package app;

import java.sql.*;
import java.util.Properties;
import org.mindrot.jbcrypt.BCrypt;

public class Db {

  private static String pick(String k, String def) {
    String v = System.getProperty(k);
    if (v == null || v.isBlank()) v = System.getenv(k);
    return (v == null || v.isBlank()) ? def : v;
  }

  private static final String URL  = pick("DB_URL",
      "jdbc:mysql://localhost:3306/hostedftp?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
  private static final String USER = pick("DB_USER", "root");
  private static final String PASS = pick("DB_PASS", "");

  static {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("MySQL JDBC driver missing", e);
    }
  }

  public static Connection conn() throws SQLException {
    Properties props = new Properties();
    props.setProperty("user", USER);
    props.setProperty("password", PASS == null ? "" : PASS);
    return DriverManager.getConnection(URL, props);
  }

  private static String normalizeBcrypt(String hash) {
    if (hash == null) return null;
    hash = hash.trim();
    if (hash.startsWith("$2y$") || hash.startsWith("$2x$")) {
      return "$2a$" + hash.substring(4);
    }
    return hash;
  }

  public record User(int id, String username) {}

  public static User authenticate(String username, String password) throws SQLException {
    final String sql = "SELECT id, username, password FROM users WHERE username = ?";
    try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, username);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return null;

        String stored = rs.getString("password");
        String hash = normalizeBcrypt(stored);

        if (hash == null || !BCrypt.checkpw(password, hash)) {
          return null;
        }
        return new User(rs.getInt("id"), rs.getString("username"));
      }
    }
  }
}
