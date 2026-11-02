INSERT INTO accounts (id, account_number, customer_id, status) VALUES (1, '001-100001', '100', 'ACTIVE');
INSERT INTO accounts (id, account_number, customer_id, status) VALUES (2, '001-100002', '101', 'ACTIVE');
INSERT INTO accounts (id, account_number, customer_id, status) VALUES (3, '001-200001', '102', 'ACTIVE');
INSERT INTO accounts (id, account_number, customer_id, status) VALUES (4, '001-200002', '103', 'ACTIVE');
INSERT INTO accounts (id, account_number, customer_id, status) VALUES (5, '001-200005', '104', 'BLOCKED');
INSERT INTO accounts (id, account_number, customer_id, status) VALUES (6, '001-200006', '104', 'BLOCKED');

INSERT INTO transfers (id, account_number, account_destination, customer_id, status, amount, transfer_date)
VALUES (1,'001-100001','001-200001','12345678','successful',500.00,'2026-10-01 08:15:20.277140');

INSERT INTO transfers (
    id,
    account_number,
    account_destination,
    customer_id,
    status,
    amount,
    transfer_date
) VALUES (
    2,
    '001-100001',
    '001-200002',
    '12345678',
    'successful',
    1200.50,
    '2026-10-01 09:30:10.254171'
);

INSERT INTO transfers (
    id,
    account_number,
    account_destination,
    customer_id,
    status,
    amount,
    transfer_date
) VALUES (
    3,
    '001-100001',
    '001-200003',
    '12345678',
    'successful',
    3000.00,
    '2026-10-01 11:45:30.167191'
);

ALTER TABLE transfers ALTER COLUMN id RESTART WITH 4;
