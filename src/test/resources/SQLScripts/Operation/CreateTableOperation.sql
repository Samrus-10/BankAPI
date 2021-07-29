CREATE TABLE Operations
(
    id     BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    source BIGINT       NOT NULL,
    target BIGINT       NOT NULL,
    sum    DECIMAL      NOT NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'UNAPPROVED',
    CHECK (sum % 0.01 = 0)
);