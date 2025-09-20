package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

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
}
