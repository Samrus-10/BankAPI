CREATE TABLE Bills
(
    id          BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    bill_number BIGINT NOT NULL AUTO_INCREMENT (1000000000000000000, 1),
    balance     DECIMAL DEFAULT 0.0,
    user_id     BIGINT NOT NULL,
    CHECK (bill_number < 2000000000000000000)
);

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

CREATE TABLE Replenishments
(
    id      BIGINT  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sum     DECIMAL NOT NULL,
    bill_id BIGINT  NOT NULL,
    CHECK (sum % 0.01 = 0)
);

CREATE TABLE Partners
(
    id           BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255),
    partner_bill BIGINT UNIQUE,
    CHECK (partner_bill >= 2000000000000000000)
);

CREATE TABLE Operations
(
    id     BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    source BIGINT       NOT NULL,
    target BIGINT       NOT NULL,
    sum    DECIMAL      NOT NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'UNAPPROVED',
    CHECK (sum % 0.01 = 0)
);


ALTER TABLE Bills
    ADD FOREIGN KEY (user_id) REFERENCES Users (id);
ALTER TABLE Cards
    ADD FOREIGN KEY (bill_id) REFERENCES Bills (id);
ALTER TABLE Replenishments
    ADD FOREIGN KEY (bill_id) REFERENCES Bills (id);
ALTER TABLE Operations
    ADD FOREIGN KEY (source) REFERENCES Bills (id);
ALTER TABLE Operations
    ADD FOREIGN KEY (target) REFERENCES Partners (id);