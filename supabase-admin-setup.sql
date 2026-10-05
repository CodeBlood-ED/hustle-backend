-- ==============================================================================
-- Hustle Ecosystem: Supabase PostgreSQL Admin Provisioning Script
-- Run this script in the Supabase SQL Editor: https://supabase.com/dashboard/project/driolkmtgrtusrghpcuq/sql
-- ==============================================================================

-- 1. Enable pgcrypto extension for BCrypt password hashing
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 2. Insert or Update Admin User
-- Replace 'admin@hustle.com' and 'admin123' with your desired admin credentials.
INSERT INTO users (name, email, password, contact, role, created_at, updated_at)
VALUES (
    'Admin User',
    'admin@hustle.com',
    crypt('admin123', gen_salt('bf', 10)),
    '9876543210',
    'ROLE_ADMIN',
    NOW(),
    NOW()
)
ON CONFLICT (email) 
DO UPDATE SET 
    role = 'ROLE_ADMIN',
    password = crypt('admin123', gen_salt('bf', 10)),
    updated_at = NOW();

-- 3. Promote an existing registered user to ROLE_ADMIN (Optional alternative):
-- UPDATE users 
-- SET role = 'ROLE_ADMIN', updated_at = NOW() 
-- WHERE email = 'your-existing-user@example.com';

-- 4. Verification Query: List all administrators
SELECT id, name, email, contact, role, created_at, updated_at 
FROM users 
WHERE role = 'ROLE_ADMIN';
