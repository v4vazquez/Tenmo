BEGIN TRANSACTION;

DROP TABLE IF EXISTS tenmo_user, account, transfer;

DROP SEQUENCE IF EXISTS seq_user_id, seq_account_id, seq_transfer_id;

-- Sequence to start user_id values at 1001 instead of 1
CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;

CREATE TABLE tenmo_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(50) NOT NULL,
	password_hash varchar(200) NOT NULL,
	CONSTRAINT PK_tenmo_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
);

-- Sequence to start account_id values at 2001 instead of 1
-- Note: Use similar sequences with unique starting values for additional tables
CREATE SEQUENCE seq_account_id
  INCREMENT BY 1
  START WITH 2001
  NO MAXVALUE;

CREATE TABLE account (
	account_id int NOT NULL DEFAULT nextval('seq_account_id'),
	user_id int NOT NULL,
	balance numeric(13, 2) NOT NULL DEFAULT 1000,
	CONSTRAINT PK_account PRIMARY KEY (account_id),
	CONSTRAINT FK_account_tenmo_user FOREIGN KEY (user_id) REFERENCES tenmo_user (user_id)
);

-- Creating Transfer Table
CREATE SEQUENCE seq_transfer_id
  INCREMENT BY 1
  START WITH 3001
  NO MAXVALUE;

CREATE TABLE transfer (
	transfer_id int NOT NULL DEFAULT nextval('seq_transfer_id'),
	sender_id int NOT NULL,
	receiver_id int NOT NULL,
	transfer_type varchar(20), -- sender or reciever or maybe receiver?
	transfer_amount numeric(13,2) NOT NULL,
	transfer_status varchar(20) NOT NULL DEFAULT 'Pending',  -- pending, approved, rejected
	CONSTRAINT PK_transfer PRIMARY KEY (transfer_id),
	CONSTRAINT FK_transfer_tenmo_user_sender FOREIGN KEY (sender_id) REFERENCES tenmo_user (user_id),
	CONSTRAINT FK_transfer_tenmo_user_receiver FOREIGN KEY (receiver_id) REFERENCES tenmo_user (user_id)
);



INSERT INTO tenmo_user (username, password_hash)
VALUES ('bob', '$2a$10$G/MIQ7pUYupiVi72DxqHquxl73zfd7ZLNBoB2G6zUb.W16imI2.W2'),
       ('user', '$2a$10$Ud8gSvRS4G1MijNgxXWzcexeXlVs4kWDOkjE7JFIkNLKEuE57JAEy');

--INSERT INTO account (user_id, balance)
--VALUES (1001, 1000),
--       (1002, 5000);
INSERT INTO account (user_id, balance)
VALUES (1001, 1000),
       (1002, 1000);

INSERT INTO transfer (sender_id, receiver_id, transfer_type,
transfer_amount, transfer_status)
VALUES(1001,1002,'send',500,'approved'),
       (1002,1001,'send',600,'pending'),
       (1002,1001,'send',6000,'pending');;

COMMIT;