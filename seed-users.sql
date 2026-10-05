-- =========================================================================
-- Seed Mock Users for hustle-dev database (PostgreSQL 18)
-- Schema matches public.users from pgAdmin 4 ERD diagram:
--   id: uuid
--   email: varchar(255)
--   password_hash: varchar(255)
--   full_name: varchar(255)
--   role: varchar(20)
--   created_at: timestamptz
--   updated_at: timestamptz
--   deleted_at: timestamptz
-- =========================================================================

-- Ensure pgcrypto extension is available for UUID generation (if needed)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Insert or update mock users with Spring Security BCrypt-encoded passwords:
-- Admin: admin@hustle.com / admin123
-- User:  user@hustle.com  / password123

INSERT INTO public.users (
    id,
    email,
    password_hash,
    full_name,
    role,
    created_at,
    updated_at,
    deleted_at
)
VALUES
(
    gen_random_uuid(),
    'admin@hustle.com',
    '$2a$10$ERxrF3nmQCygo3WoY9qPierUKRtwt.F7CA41QkBr6aa.Sp3mqryYe',
    'Admin User',
    'ROLE_ADMIN',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    NULL
),
(
    gen_random_uuid(),
    'user@hustle.com',
    '$2a$10$Wo6Ixq0bnBgDlkiwhr9M4OcL5IL8zdR9j1hybP2N0cDj9esNETO8C',
    'Demo User',
    'ROLE_USER',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    NULL
)
ON CONFLICT (email) DO UPDATE
SET
    password_hash = EXCLUDED.password_hash,
    full_name     = EXCLUDED.full_name,
    role          = EXCLUDED.role,
    updated_at    = CURRENT_TIMESTAMP,
    deleted_at    = NULL;

-- Verification query
SELECT id, email, full_name, role, created_at, deleted_at FROM public.users;
