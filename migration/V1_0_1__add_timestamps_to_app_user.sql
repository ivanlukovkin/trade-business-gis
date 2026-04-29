DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_name = 'app_user'
            AND table_schema = 'public'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'app_user'
            AND table_schema = 'public'
            AND column_name = 'created_at'
    ) THEN
        ALTER TABLE public.app_user
            ADD COLUMN created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;
    END IF;
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_name = 'app_user'
            AND table_schema = 'public'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'app_user'
            AND table_schema = 'public'
            AND column_name = 'updated_at'
    ) THEN
        ALTER TABLE public.app_user
            ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;
    END IF;
END $$;
