package app.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class Db {

  private static String env(String key) {
    String v = System.getProperty(key);
    if (v == null || v.isBlank()) v = System.getenv(key);
    return v == null ? "" : v.trim();
  }

  private static String required(String key) {
    String v = env(key);
    if (v.isBlank()) throw new IllegalStateException(key + " is not set");
    return v;
  }

  private static final String URL  = required("DB_URL");
  private static final String USER = env("DB_USER");
  private static final String PASS = resolvePass();

  private static String resolvePass() {
    String p = env("DB_PASS");
    if (p.isBlank()) p = env("DB_PASSWORD");
    return p;
  }

  static {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("MySQL JDBC driver missing", e);
    }
  }

  public static Connection conn() throws SQLException {
    Properties props = new Properties();
    if (!USER.isBlank()) props.setProperty("user", USER);
    if (!PASS.isBlank()) props.setProperty("password", PASS);
    return DriverManager.getConnection(URL, props);
  }

  private Db() {}
}
