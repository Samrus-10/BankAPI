CREATE TABLE Partners
(
    id           BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255),
    partner_bill BIGINT UNIQUE,
    CHECK (partner_bill >= 2000000000000000000)
);