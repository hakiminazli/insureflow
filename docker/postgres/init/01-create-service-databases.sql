SELECT 'CREATE DATABASE member_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'member_db')\gexec
