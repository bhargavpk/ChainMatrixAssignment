CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(40) NOT NULL,
    mailid VARCHAR(20) NOT NULL,
    contact_number VARCHAR(20) NOT NULL
);

CREATE TABLE seller (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    company_name VARCHAR(255) NOT NULL,
    company_mail VARCHAR(20) NOT NULL,
    company_contact VARCHAR(20) NOT NULL,
    company_address VARCHAR(255) NOT NULL,
    FOREIGN KEY(id) REFERENCES users(id)
);

-- TODO: description to be later changed to desc
CREATE TABLE category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255)
);

-- TODO: description to be later changed to desc
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    category_id BIGINT NOT NULL,
    price DOUBLE PRECISION(6, 2) NOT NULL,
    available_quantity INT,
    description VARCHAR(255),
    seller_id BIGINT NOT NULL,
    FOREIGN KEY(category_id) REFERENCES category(id),
    FOREIGN KEY(seller_id) REFERENCES seller(id)
);

CREATE TABLE bookmark (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(product_id) REFERENCES product(id)
);
-- Optional: Try adding more fields
CREATE TABLE bank_account (
    account_no VARCHAR(20) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    amount INT
);

-- Optional: Create table for wallet and online payment companies
CREATE TABLE wallet_info (
    company_name VARCHAR(20) NOT NULL,
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_no VARCHAR(20),
    FOREIGN KEY(account_no) REFERENCES bank_account(account_no)
);
CREATE TABLE online_payment_info (
    company_name VARCHAR(20) NOT NULL,
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_no VARCHAR(20),
    FOREIGN KEY(account_no) REFERENCES bank_account(account_no)
);

CREATE TABLE wallet_user (
    company_name VARCHAR(20) NOT NULL,
    user_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(client_id) REFERENCES wallet_info(id)
);
CREATE TABLE online_payment_user (
    company_name VARCHAR(20) NOT NULL,
    user_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(client_id) REFERENCES online_payment_info(id)
);

-- Info: No need to store wallet info here as 1-to-1 relation 'wallet_user' exists
CREATE TABLE payment_info (
    user_id BIGINT PRIMARY KEY,
    address VARCHAR(255) NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id)
);

-- User can have a single cart
CREATE TABLE cart (
    id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    checkout_status CHAR(1) DEFAULT 'U',
    checkout_time DATETIME,
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(product_id) REFERENCES product(id)
);

CREATE TABLE checkout_history (
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    checkout_time DATETIME DEFAULT CURRENT_TIMESTAMP(),
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(product_id) REFERENCES product(id)
);

delimiter //
CREATE TRIGGER product_quantity BEFORE UPDATE ON product
FOR EACH ROW
BEGIN
    IF NEW.available_quantity < 0 THEN
        SIGNAL sqlstate '45001' SET message_text = 'Product unavailable';
    END IF;
END;//
delimiter ;

delimiter //
CREATE TRIGGER account_update BEFORE UPDATE ON bank_account
FOR EACH ROW
BEGIN
    IF NEW.amount < 0 THEN
        SIGNAL sqlstate '45000' SET message_text = 'Balance insufficient';
    END IF;
END;//
delimiter ;

INSERT INTO users(name, mailid, contact_number) VALUES ('Bhargav Kulkarni', 'bpk10@iitbbs.ac.in', '9535688271');
INSERT INTO users(name, mailid, contact_number) VALUES ('Sam Billings', 'sam10@iitbbs.ac.in', '9535688271');
INSERT INTO users(name, mailid, contact_number) VALUES ('Luke Shaw', 'luke@iitbbs.ac.in', '9535688271');
INSERT INTO seller VALUES (2, 'DRIP sports', 'dripsports@gmail.com', '9999999999', 'Some street at India');
INSERT INTO seller VALUES (3, 'Bata showroom', 'bata@gmail.com', '9999999999', 'Some street at India');
INSERT INTO category(name, description) VALUES ('Sports', 'Cricket, football goods');
INSERT INTO category(name, description) VALUES ('Footwear', 'Casual, formal and sport footwears');
INSERT INTO product VALUES (1, 'Cricket bat', 1, 2000, 2, 'DRIP cricket bat', 2);
INSERT INTO product VALUES (2, 'Slippers', 2, 200, 5, 'Bata daily-wear slippers', 3);

INSERT INTO bank_account VALUES ('123123123', 'Bhargav Padmakar Kulkarni', 2500);
INSERT INTO wallet_info VALUES ('Google Pay' ,1, '123123123');
INSERT INTO online_payment_info VALUES ('Razorpay', 1, '123123123');
INSERT INTO wallet_user VALUES ('Google Pay', 1, 1);
INSERT INTO online_payment_user VALUES ('Razorpay', 1, 1);
INSERT INTO payment_info VALUES (1, 'Some home, some street, Gulbarga');
