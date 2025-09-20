CREATE DATABASE IF NOT EXISTS hostedftp
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE hostedftp;

DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  username      VARCHAR(100) NOT NULL UNIQUE,            
  password_hash TEXT         NOT NULL,                  
  created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

