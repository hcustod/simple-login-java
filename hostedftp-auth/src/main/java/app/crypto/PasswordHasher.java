package app.crypto;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordHasher {
  private static final int DEFAULT_ITERATIONS = 210_000; // tune to ~150–250ms on prod
  private static final int KEY_LEN_BITS = 256;           // 32 bytes
  private static final int SALT_LEN_BYTES = 16;
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
    }
  }

  /** Verify a password attempt against stored PHC string. */
  public static boolean verify(char[] attempt, String stored) {
    try {
      Parsed p = parse(stored);
      byte[] effSalt = mixPepper(p.salt, pepper());
      byte[] got = pbkdf2(attempt, effSalt, p.iterations, p.dk.length * 8);
      return constantTimeEquals(p.dk, got);
    } finally {
      zero(attempt);
    }
  }

  /** Signal that parameters should be upgraded (rehash-on-login). */
  public static boolean needsRehash(String stored) {
    Parsed p = parse(stored);
    return p.iterations < DEFAULT_ITERATIONS;
  }

  /* ==== main ==== */

  private static String format(int iters, byte[] salt, byte[] dk) {
    return String.format("$pbkdf2-sha256$iterations=%d$%s$%s",
        iters, Base64.getEncoder().encodeToString(salt), Base64.getEncoder().encodeToString(dk));
  }

  private static Parsed parse(String phc) {
    String[] parts = phc.split("\\$");
    if (parts.length != 5 || !parts[1].equals("pbkdf2-sha256")) {
      throw new IllegalArgumentException("Unsupported hash format");
    }
    int iterations = Integer.parseInt(parts[2].split("=")[1]);
    byte[] salt = Base64.getDecoder().decode(parts[3]);
    byte[] dk   = Base64.getDecoder().decode(parts[4]);
    return new Parsed(iterations, salt, dk);
  }

  private static byte[] pbkdf2(char[] pw, byte[] salt, int iters, int keyBits) {
    try {
      PBEKeySpec spec = new PBEKeySpec(pw, salt, iters, keyBits);
      SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
      return skf.generateSecret(spec).getEncoded();
    } catch (Exception e) {
      throw new IllegalStateException("PBKDF2 failure", e);
    }
  }

  private static boolean constantTimeEquals(byte[] a, byte[] b) {
    if (a.length != b.length) return false;
    int r = 0;
    for (int i = 0; i < a.length; i++) r |= (a[i] ^ b[i]);
    return r == 0;
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
    if (data != null) java.util.Arrays.fill(data, '\0');
  }

  private static final class Parsed {
    final int iterations; final byte[] salt; final byte[] dk;
    Parsed(int i, byte[] s, byte[] d) { this.iterations = i; this.salt = s; this.dk = d; }
  }
}
