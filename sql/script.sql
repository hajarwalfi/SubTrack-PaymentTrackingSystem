CREATE TABLE subscription (
    id VARCHAR(36) PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    monthly_amount DECIMAL(10,2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(20) NOT NULL,
    subscription_type VARCHAR(20) NOT NULL,
    commitment_duration_months INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Creation timestamp
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Will be updated manually or via trigger
);


CREATE TABLE payment (
    id_payment VARCHAR(36) PRIMARY KEY,
    subscription_id VARCHAR(36) NOT NULL,
    due_date DATE NOT NULL,
    payment_date DATE,
    payment_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_subscription
        FOREIGN KEY (subscription_id) REFERENCES subscription(id)
        ON DELETE CASCADE
);
