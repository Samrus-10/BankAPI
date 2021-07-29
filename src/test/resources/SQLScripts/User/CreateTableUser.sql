CREATE TABLE Users
(
    id           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    login        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    first_name   VARCHAR(255) NOT NULL,
    last_name    VARCHAR(255) NOT NULL,
    middle_name  VARCHAR(255),
    passport     VARCHAR(255) NOT NULL,
    mobile_phone VARCHAR(12)  NOT NULL,
    role         VARCHAR(255) NOT NULL DEFAULT 'USER'
);