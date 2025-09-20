package app.service;

import app.crypto.PasswordHasher;
import app.dao.UserDao;
import app.model.User;

import java.sql.SQLException;

public final class AuthService {
  private final UserDao users;
  

  private static final int MIN_PW_LEN = Integer.parseInt(
    System.getenv().getOrDefault("AUTH_MIN_PW_LEN", "12")
    );

  public static int getMinPasswordLength() { return MIN_PW_LEN; }

  public AuthService(UserDao users) { this.users = users; }

  /** Returns User on success, null on failure. Upgrades hash if parameters are outdated. */
  public User login(String username, char[] password) throws SQLException {
    String stored = users.getPasswordHashByUsername(username);
    if (stored == null) return null;

    boolean ok = PasswordHasher.verify(password, stored);
    if (!ok) return null;

    if (PasswordHasher.needsRehash(stored)) {
      String upgraded = PasswordHasher.hash(password);
      User u = users.getByUsername(username);
      if (u != null) users.updatePasswordHash(u.getId(), upgraded);
    }
    return users.getByUsername(username);
  }

  /** Throws IllegalArgumentException for validation issues; SQLException for DB errors. */
  public User register(String username, char[] password) throws SQLException {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("Username is required");
    }
    if (password == null || password.length < MIN_PW_LEN) {
      throw new IllegalArgumentException("Password must be at least" + MIN_PW_LEN + "characters");
    }
    if (users.getByUsername(username) != null) {
      throw new IllegalArgumentException("Username already taken");
    }
    String phc = PasswordHasher.hash(password);
    users.create(username, phc);
    return users.getByUsername(username);
  }
}
