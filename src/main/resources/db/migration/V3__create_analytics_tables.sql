CREATE TABLE order_daily_analytics (
    date            DATE            PRIMARY KEY,
    total_orders    BIGINT          NOT NULL DEFAULT 0,
    total_revenue   NUMERIC(18,2)   NOT NULL DEFAULT 0.00,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE TABLE order_customer_analytics (
    customer_id     UUID            PRIMARY KEY,
    total_orders    BIGINT          NOT NULL DEFAULT 0,
    total_spent     NUMERIC(18,2)   NOT NULL DEFAULT 0.00,
    last_order_at   TIMESTAMP WITH TIME ZONE,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE TABLE processed_analytics_events (
    event_id        UUID            PRIMARY KEY,
    processed_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_processed_analytics_events_processed_at
    ON processed_analytics_events (processed_at);

