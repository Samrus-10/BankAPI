CREATE TABLE Replenishments
(
    id      BIGINT  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sum     DECIMAL NOT NULL,
    bill_id BIGINT  NOT NULL,
    CHECK (sum % 0.01 = 0)
);