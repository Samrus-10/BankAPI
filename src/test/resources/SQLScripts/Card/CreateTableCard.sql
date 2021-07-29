CREATE TABLE Cards
(
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    card_number BIGINT       NOT NULL AUTO_INCREMENT (125155000000000, 1),
    expires     VARCHAR(5),
    first_name  VARCHAR(255),
    last_name   VARCHAR(255),
    bill_id     BIGINT       NOT NULL,
    status      VARCHAR(255) NOT NULL DEFAULT 'NOT_ACTIVE'
);