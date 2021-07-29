DROP TABLE IF EXISTS Cards CASCADE;
DROP TABLE IF EXISTS Bills CASCADE;
DROP TABLE IF EXISTS Users CASCADE;
DROP TABLE IF EXISTS Replenishments CASCADE;
DROP TABLE IF EXISTS Partners ;
DROP TABLE IF EXISTS Operations ;




CREATE TABLE Bills(

    id           serial PRIMARY KEY,
    bill_number BIGINT NOT NULL AUTO_INCREMENT (1000000000000000000, 1),
    balance     DECIMAL DEFAULT 0.0,
    user_id     BIGINT NOT NULL,
    CHECK (bill_number < 2000000000000000000)
);

CREATE TABLE Users(

    id           serial PRIMARY KEY,
    login        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    first_name   VARCHAR(255) NOT NULL,
    last_name    VARCHAR(255) NOT NULL,
    middle_name  VARCHAR(255),
    passport     VARCHAR(255) NOT NULL,
    mobile_phone VARCHAR(12)  NOT NULL,
    role         VARCHAR(255) NOT NULL DEFAULT 'USER'
);

CREATE TABLE Cards(

    id           serial PRIMARY KEY,
    card_number BIGINT       NOT NULL AUTO_INCREMENT (125155000000000, 1),
    expires     VARCHAR(5),
    first_name  VARCHAR(255),
    last_name   VARCHAR(255),
    bill_id     BIGINT       NOT NULL,
    status      VARCHAR(255) NOT NULL DEFAULT 'NOT_ACTIVE'
);

CREATE TABLE Replenishments(

    id       serial PRIMARY KEY,
    sum     DECIMAL NOT NULL,
    bill_id BIGINT  NOT NULL,
    CHECK (sum % 0.01 = 0)
);

CREATE TABLE Partners(

    id            serial PRIMARY KEY,
    name         VARCHAR(255),
    partner_bill BIGINT UNIQUE,
    CHECK (partner_bill >= 2000000000000000000)
);

CREATE TABLE Operations(

    id      serial PRIMARY KEY,
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

-- INSERT INTO USERS(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, MIDDLE_NAME, PASSPORT, MOBILE_PHONE, ROLE)
-- VALUES ('admin', '$2a$10$ByXXAs0sCoDlcnhwkyR7U.cg5UVRa9B12ZQovtPajTa7aTXqFts8q', 'Sam', 'Rusanov', 'Alekseevich','1', '1', 'EMPLOYEE');
-- INSERT INTO USERS(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, MIDDLE_NAME, PASSPORT, MOBILE_PHONE, ROLE)
-- VALUES ('user1', '$2a$10$ByXXAs0sCoDlcnhwkyR7U.cg5UVRa9B12ZQovtPajTa7aTXqFts8q', 'Samy', 'Rusanov', 'Alekseevich','1', '1', 'USER');
-- INSERT INTO USERS(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, MIDDLE_NAME, PASSPORT, MOBILE_PHONE, ROLE)
-- VALUES ('user', '$2a$10$ByXXAs0sCoDlcnhwkyR7U.cg5UVRa9B12ZQovtPajTa7aTXqFts8q', 'SamRus', 'Ivanov', 'Alekseevich','1', '1', 'USER');

INSERT INTO USERS(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, MIDDLE_NAME, PASSPORT, MOBILE_PHONE, ROLE)
VALUES ('admin', '1111', 'Sam', 'Rusanov', 'Alekseevich','1', '1', 'EMPLOYEE');
INSERT INTO USERS(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, MIDDLE_NAME, PASSPORT, MOBILE_PHONE, ROLE)
VALUES ('user1', '2222', 'Samy', 'Rusanov', 'Alekseevich','1', '1', 'USER');
INSERT INTO USERS(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, MIDDLE_NAME, PASSPORT, MOBILE_PHONE, ROLE)
VALUES ('user2', '3333', 'SamRus', 'Ivanov', 'Alekseevich','1', '1', 'USER');

INSERT INTO Bills(bill_number, balance, user_id)
VALUES (8885346675400000001, 100.54, 1 );
INSERT INTO Bills(bill_number, balance, user_id)
VALUES (8885346675400000002, 100000.27, 2 );

INSERT INTO Cards(card_number, expires, first_name, last_name, bill_id, status)
VALUES (5469510010000001, '09/23', 'Samy', 'Rusanov', 1, 'ACTIVE');
INSERT INTO Cards(card_number, expires, first_name, last_name, bill_id, status)
VALUES (5469510010000001, '09/25', 'Samy_Second', 'Rusanov', 1, 'ACTIVE');
INSERT INTO Cards(card_number, expires, first_name, last_name, bill_id, status)
VALUES (5469510020000001, '09/23', 'SamRus', 'Ivanov', 2, 'ACTIVE');

INSERT INTO Partners(name, partner_bill)
VALUES ('SamRus', 8885346675400000002);
INSERT INTO Partners(name, partner_bill)
VALUES ('Sam', 8885346675400000001);




