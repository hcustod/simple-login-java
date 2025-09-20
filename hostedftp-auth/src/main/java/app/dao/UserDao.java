package app.dao;

import app.Db;
import app.model.User;

import java.sql.*;

public final class UserDao {

  public User getByUsername(String username) throws SQLException {
    String sql = "SELECT id, username, created_at FROM users WHERE username = ?";
    try (Connection c = Db.conn(); PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, username);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return null;
        int id = rs.getInt("id");
        String u = rs.getString("username");
        Timestamp ts = rs.getTimestamp("created_at");
        return new User(id, u, ts == null ? null : ts.toInstant());
      }
    }
  }

  public String getPasswordHashByUsername(String username) throws SQLException {
    String sql = "SELECT password_hash FROM users WHERE username = ?";
    try (Connection c = Db.conn(); PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, username);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getString(1) : null;
      }
    }
  }

  public void create(String username, String passwordHash) throws SQLException {
    String sql = "INSERT INTO users(username, password_hash) VALUES(?, ?)";
    try (Connection c = Db.conn(); PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, username);
      ps.setString(2, passwordHash);
      ps.executeUpdate();
    }
  }

  public void updatePasswordHash(int userId, String newHash) throws SQLException {
    String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
    try (Connection c = Db.conn(); PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, newHash);
      ps.setInt(2, userId);
      ps.executeUpdate();
    }
  }

  public int countUsers() throws SQLException {
    try (Connection c = Db.conn();
         Statement st = c.createStatement();
         ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
      rs.next();
      return rs.getInt(1);
    }
  }
}
