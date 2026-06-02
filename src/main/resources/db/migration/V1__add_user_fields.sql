-- V4__add_user_fields.sql

ALTER TABLE users
ADD COLUMN phone_number VARCHAR(255),
ADD COLUMN email_verified BOOLEAN DEFAULT FALSE,
ADD COLUMN email_verification_token VARCHAR(512),
ADD COLUMN email_verification_token_expiry TIMESTAMP,
ADD COLUMN password_reset_token VARCHAR(512),
ADD COLUMN password_reset_token_expiry TIMESTAMP;

CREATE UNIQUE INDEX idx_users_phone_number
ON users(phone_number);
-- phone_number IS NOT NULL;