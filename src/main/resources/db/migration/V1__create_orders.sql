CREATE TABLE orders (
    id          UUID            PRIMARY KEY,
    customer_id UUID            NOT NULL,
    status      VARCHAR(20)     NOT NULL,
    total       DECIMAL(19, 2)  NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE order_items (
    id          UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id    UUID            NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id  UUID            NOT NULL,
    quantity    INT             NOT NULL,
    price       DECIMAL(19, 2)  NOT NULL
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);

