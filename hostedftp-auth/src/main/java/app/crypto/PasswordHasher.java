package app.crypto;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Arrays;

public final class PasswordHasher {
  // Canonical parameters for new hashes
  private static final int DEFAULT_ITERATIONS = 210_000; // tune to ~150–250ms on prod
  private static final int KEY_LEN_BITS       = 256;     // 32 bytes
  private static final int SALT_LEN_BYTES     = 16;

  // Defensive bounds for parsed/stored hashes (prevents DoS via manual injection)
  private static final int MIN_ITERATIONS = 50_000;
  private static final int MAX_ITERATIONS = 1_000_000;
  private static final int MIN_SALT_LEN   = 8;
  private static final int MAX_SALT_LEN   = 64;
  private static final int MIN_DK_LEN     = 16;  // 128-bit minimum accepted
  private static final int MAX_DK_LEN     = 64;  // cap accepted DK size

  private static final SecureRandom RNG = new SecureRandom();

  private PasswordHasher() {}

  /** Hash a new password; returns PHC-style string: $pbkdf2-sha256$iterations=N$saltB64$dkB64 */
  public static String hash(char[] password) {
    byte[] salt = new byte[SALT_LEN_BYTES];
    RNG.nextBytes(salt);
    try {
      byte[] effSalt = mixPepper(salt, pepper());
      byte[] dk = pbkdf2(password, effSalt, DEFAULT_ITERATIONS, KEY_LEN_BITS);
      return format(DEFAULT_ITERATIONS, salt, dk);
    } finally {
      zero(password);
      zero(salt);
    }
  }

  /** Verify a password attempt against stored PHC string. */
  public static boolean verify(char[] attempt, String stored) {
    byte[] got = null;
    try {
      Parsed p = parse(stored); // validates ranges and base64
      // Require canonical DK length (prevents oversized DK-driven DoS)
      if (p.dk.length != KEY_LEN_BITS / 8) return false;

      byte[] effSalt = mixPepper(p.salt, pepper());
      got = pbkdf2(attempt, effSalt, p.iterations, KEY_LEN_BITS);
      return constantTimeEquals(p.dk, got);
    } finally {
      zero(attempt);
      zero(got);
    }
  }

  /** Signal that parameters should be upgraded (rehash-on-login). */
  public static boolean needsRehash(String stored) {
    Parsed p = parse(stored); // already validated bounds
    boolean iterTooLow = p.iterations < DEFAULT_ITERATIONS;
    boolean wrongLen   = p.dk.length != (KEY_LEN_BITS / 8);
    return iterTooLow || wrongLen;
  }

  /* ==== main ==== */

  private static String format(int iters, byte[] salt, byte[] dk) {
    return String.format("$pbkdf2-sha256$iterations=%d$%s$%s",
        iters, Base64.getEncoder().encodeToString(salt), Base64.getEncoder().encodeToString(dk));
  }

  // Hardened parser with strict bounds to prevent parameter-injection DoS
  private static Parsed parse(String phc) {
    if (phc == null) throw new IllegalArgumentException("Null hash");
    // Expected tokens: ["", "pbkdf2-sha256", "iterations=N", "<saltB64>", "<dkB64>"]
    String[] parts = phc.split("\\$", -1); // keep empty tokens
    if (parts.length != 5 || !"pbkdf2-sha256".equals(parts[1])) {
      throw new IllegalArgumentException("Unsupported or malformed hash format");
    }

    // iterations=...
    String iterPart = parts[2].trim();
    if (!iterPart.startsWith("iterations=")) {
      throw new IllegalArgumentException("Missing iterations parameter");
    }
    int iterations;
    try {
      iterations = Integer.parseInt(iterPart.substring("iterations=".length()));
    } catch (NumberFormatException nfe) {
      throw new IllegalArgumentException("Invalid iterations value", nfe);
    }
    if (iterations < MIN_ITERATIONS || iterations > MAX_ITERATIONS) {
      throw new IllegalArgumentException("Iterations out of allowed range");
    }

    // salt
    byte[] salt;
    try {
      salt = Base64.getDecoder().decode(parts[3]);
    } catch (IllegalArgumentException iae) {
      throw new IllegalArgumentException("Invalid base64 salt", iae);
    }
    if (salt.length < MIN_SALT_LEN || salt.length > MAX_SALT_LEN) {
      throw new IllegalArgumentException("Salt length out of range");
    }

    // dk
    byte[] dk;
    try {
      dk = Base64.getDecoder().decode(parts[4]);
    } catch (IllegalArgumentException iae) {
      throw new IllegalArgumentException("Invalid base64 dk", iae);
    }
    if (dk.length < MIN_DK_LEN || dk.length > MAX_DK_LEN) {
      throw new IllegalArgumentException("Derived key length out of range");
    }

    return new Parsed(iterations, salt, dk);
  }

  private static byte[] pbkdf2(char[] pw, byte[] salt, int iters, int keyBits) {
    PBEKeySpec spec = null;
    try {
      spec = new PBEKeySpec(pw, salt, iters, keyBits);
      SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
      return skf.generateSecret(spec).getEncoded();
    } catch (Exception e) {
      throw new IllegalStateException("PBKDF2 failure", e);
    } finally {
      if (spec != null) spec.clearPassword();
    }
  }

  private static boolean constantTimeEquals(byte[] a, byte[] b) {
    if (a == null || b == null || a.length != b.length) return false;
    // Either approach is fine; using MessageDigest.isEqual to avoid JIT shenanigans.
    return MessageDigest.isEqual(a, b);
  }

  private static byte[] pepper() {
    String b64 = System.getenv("AUTH_PEPPER_B64");
    if (b64 == null || b64.isEmpty()) return null;
    return Base64.getDecoder().decode(b64);
  }

  private static byte[] mixPepper(byte[] salt, byte[] pepper) {
    if (pepper == null) return salt;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(salt);
      md.update(pepper);
      return md.digest(); // 32B effective salt
    } catch (Exception e) {
      throw new IllegalStateException("Pepper mix failure", e);
    }
  }

  private static void zero(char[] data) {
    if (data != null) Arrays.fill(data, '\0');
  }

  private static void zero(byte[] data) {
    if (data != null) Arrays.fill(data, (byte) 0);
  }

  private static final class Parsed {
    final int iterations; final byte[] salt; final byte[] dk;
    Parsed(int i, byte[] s, byte[] d) { this.iterations = i; this.salt = s; this.dk = d; }
  }
}
