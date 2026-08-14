CREATE TABLE usuarios (
  id             VARCHAR(50)  PRIMARY KEY,
  username       VARCHAR(100) NOT NULL UNIQUE,
  password_hash  VARCHAR(100) NOT NULL,
  totp_secret    TEXT,
  totp_enabled   BOOLEAN      NOT NULL DEFAULT FALSE,
  backup_codes   TEXT,
  created_at     TIMESTAMP    DEFAULT NOW()
);
