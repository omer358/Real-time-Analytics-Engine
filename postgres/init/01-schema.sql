CREATE TABLE IF NOT EXISTS category
(
    id                 SERIAL PRIMARY KEY,
    name               VARCHAR(50) NOT NULL,
    parent_category_id INT REFERENCES category (id)
);

CREATE TABLE IF NOT EXISTS brand
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS product
(
    id          serial PRIMARY KEY,
    name        varchar(255)   NOT NULL,
    description TEXT,
    brand_id    INT REFERENCES brand,
    price       NUMERIC(10, 2) NOT NULL,
    category_id INT REFERENCES category
);

CREATE TABLE IF NOT EXISTS users
(
    id         SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    email      VARCHAR(50) UNIQUE
);

COPY category(id, name, parent_category_id)
    FROM '/data/categories.csv'
    DELIMITER ',' CSV HEADER;

COPY brand(id, name)
    FROM '/data/brands.csv'
    DELIMITER ',' CSV HEADER;

COPY product(id, name, description, brand_id, price, category_id)
    FROM '/data/products.csv'
    DELIMITER ',' CSV HEADER;

COPY users(id,first_name,last_name,email)
    FROM '/data/users.csv'
DELIMITER ',' CSV HEADER;
