CREATE TABLE warehouse_orders
(
    id                       BIGSERIAL   NOT NULL PRIMARY KEY,
    customer_order_reference VARCHAR(32) NOT NULL,
    transporter_code         VARCHAR(16) NOT NULL,
    quantity                 SMALLINT    NOT NULL,
    delivered_at             TIMESTAMP   NOT NULL,
    created_at               TIMESTAMP   NOT NULL DEFAULT now()
)
