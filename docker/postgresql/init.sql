DO
$$
BEGIN
    IF NOT EXISTS(
        SELECT FROM pg_database WHERE datname = 'inventory_db'
    ) THEN
        CREATE DATABASE inventory_db;
    END IF;
END
$$;