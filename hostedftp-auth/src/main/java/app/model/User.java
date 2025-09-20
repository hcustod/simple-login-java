package app.model;

import java.time.Instant;

public final class User {
  private final int id;
  private final String username;
  private final Instant createdAt;

  public User(int id, String username, Instant createdAt) {
    this.id = id;
    this.username = username;
    this.createdAt = createdAt;
  }

  public int getId() { return id; }
  public String getUsername() { return username; }
  public Instant getCreatedAt() { return createdAt; }
}
