SELECT 'CREATE DATABASE auth_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'auth_db')\gexec

SELECT 'CREATE DATABASE member_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'member_db')\gexec

SELECT 'CREATE DATABASE policy_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'policy_db')\gexec
