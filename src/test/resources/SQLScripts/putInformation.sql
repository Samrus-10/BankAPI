INSERT INTO USERS(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, MIDDLE_NAME, PASSPORT, MOBILE_PHONE, ROLE)
VALUES ('admin', '1111', 'Sam', 'Rusanov', 'Alekseevich','1', '1', 'EMPLOYEE');
INSERT INTO USERS(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, MIDDLE_NAME, PASSPORT, MOBILE_PHONE, ROLE)
VALUES ('user1', '2222', 'Samy', 'Rusanov', 'Alekseevich','1', '1', 'USER');
INSERT INTO USERS(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, MIDDLE_NAME, PASSPORT, MOBILE_PHONE, ROLE)
VALUES ('user2', '3333', 'SamRus', 'Ivanov', 'Alekseevich','1', '1', 'USER');
INSERT INTO USERS(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, MIDDLE_NAME, PASSPORT, MOBILE_PHONE, ROLE)
VALUES ('user3', '4444', 'S', 'Ivanov', 'Alekseevich','1', '1', 'USER');

INSERT INTO Bills( user_id, balance)
VALUES (2 , 1000);
INSERT INTO Bills( user_id, balance)
VALUES ( 3 , 1000);
INSERT INTO Bills( user_id, balance)
VALUES ( 4 , 1000);

INSERT INTO Cards(card_number, expires, first_name, last_name, bill_id, status)
VALUES (5469510010000001, '09/23', 'Samy', 'Rusanov', 1, 'ACTIVE');
INSERT INTO Cards(card_number, expires, first_name, last_name, bill_id, status)
VALUES (5469510010000001, '09/25', 'Samy_Second', 'Rusanov', 1, 'ACTIVE');
INSERT INTO Cards(card_number, expires, first_name, last_name, bill_id, status)
VALUES (5469510020000001, '09/23', 'SamRus', 'Ivanov', 2, 'ACTIVE');
INSERT INTO Cards(card_number, expires, first_name, last_name, bill_id, status)
VALUES (5469510020000001, '09/23', 'K', 'Ivanov', 3, 'ACTIVE');

INSERT INTO Partners(name, partner_bill)
VALUES ('SamRus', 5346675400000002);
INSERT INTO Partners(name, partner_bill)
VALUES ('Sam', 5346675400000001);
