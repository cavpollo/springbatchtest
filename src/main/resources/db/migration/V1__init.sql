CREATE TABLE warehouse_orders
(
    id                       BIGSERIAL   NOT NULL PRIMARY KEY,
    customer_order_reference VARCHAR(16) NOT NULL,
    transporter_name         VARCHAR(64) NOT NULL,
    delivered_at             TIMESTAMP   NOT NULL,
    created_at               TIMESTAMP   NOT NULL
)
