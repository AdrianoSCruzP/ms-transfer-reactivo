DROP TABLE IF EXISTS accounts;

CREATE TABLE accounts (
    id INT PRIMARY KEY,
    account_number VARCHAR(100) NOT NULL UNIQUE,
    customer_id VARCHAR(100) NOT NULL,
    status VARCHAR(150) NOT NULL
);

DROP TABLE IF EXISTS transfers;

CREATE TABLE transfers (
    id INT PRIMARY KEY,
    account_number VARCHAR(30) NOT NULL,
    account_destination VARCHAR(30) NOT NULL,
    customer_id VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    transfer_date TIMESTAMP NOT NULL
);