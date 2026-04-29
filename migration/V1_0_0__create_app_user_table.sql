CREATE TABLE app_user (
    user_id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(32),
    last_name VARCHAR(32),
    patronimyc VARCHAR(32),
    username VARCHAR(24) NOT NULL,
    auth_password VARCHAR(64) NOT NULL,
    app_role VARCHAR(16)
);
