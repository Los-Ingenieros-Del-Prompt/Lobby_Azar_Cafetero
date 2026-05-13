
CREATE TABLE balances (
    user_id         VARCHAR(255) PRIMARY KEY,
    amount          NUMERIC(15, 2) NOT NULL DEFAULT 500.00,
    last_bonus_date DATE
);

CREATE TABLE transactions (
    transaction_id  VARCHAR(36) PRIMARY KEY,
    user_id         VARCHAR(255) NOT NULL,
    amount          NUMERIC(15, 2) NOT NULL,
    type            VARCHAR(20) NOT NULL,
    description     VARCHAR(255),
    created_at      TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_transactions_user_id ON transactions(user_id);