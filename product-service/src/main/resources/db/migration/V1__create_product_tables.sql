CREATE TABLE products
(
    id          UUID           NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    description TEXT,
    price       NUMERIC(19, 2) NOT NULL,
    sku         VARCHAR(100)   NOT NULL UNIQUE,
    category    VARCHAR(50)    NOT NULL,
    status      VARCHAR(50)    NOT NULL DEFAULT 'ACTIVE',
    seller_id   UUID           NOT NULL,
    created_at  TIMESTAMP      NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP      NOT NULL DEFAULT now()
);

CREATE TABLE stocks
(
    id                 UUID    NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    product_id         UUID    NOT NULL UNIQUE REFERENCES products (id) ON DELETE CASCADE,
    quantity_available INTEGER NOT NULL DEFAULT 0,
    quantity_reserved  INTEGER NOT NULL DEFAULT 0,
    updated_at         TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT chk_quantity_available CHECK (quantity_available >= 0),
    CONSTRAINT chk_quantity_reserved CHECK (quantity_reserved >= 0)
);

CREATE TABLE stock_movements
(
    id         UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    stock_id   UUID        NOT NULL REFERENCES stocks (id) ON DELETE CASCADE,
    type       VARCHAR(50) NOT NULL,
    quantity   INTEGER     NOT NULL,
    reason     VARCHAR(255),
    order_id   UUID,
    created_at TIMESTAMP   NOT NULL DEFAULT now()
);

CREATE INDEX idx_products_sku ON products (sku);
CREATE INDEX idx_products_category ON products (category);
CREATE INDEX idx_products_status ON products (status);
CREATE INDEX idx_products_seller_id ON products (seller_id);
CREATE INDEX idx_stock_movements_stock_id ON stock_movements (stock_id);
CREATE INDEX idx_stock_movements_order_id ON stock_movements (order_id);
CREATE INDEX idx_stock_movements_type ON stock_movements (type);