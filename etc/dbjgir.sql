CREATE DATABASE jgir;
USE jgir;

CREATE TABLE page 
(
        page_id     BIGINT NOT NULL AUTO_INCREMENT,
        sender      VARCHAR(255),
		page_time   VARCHAR(255),
        msg         TEXT,
        PRIMARY KEY(page_id)
);

CREATE TABLE users 
(
        user_id BIGINT NOT NULL AUTO_INCREMENT,
        user_name VARCHAR(60),
        user_password VARCHAR(80),
        user_email VARCHAR(80),
        user_date VARCHAR(80),
        PRIMARY KEY(user_id)
); 
