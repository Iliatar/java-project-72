DROP TABLE IF EXISTS url_checks;
DROP TABLE IF EXISTS urls;

CREATE TABLE urls (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(500),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE url_checks (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    url_id BIGINT REFERENCES urls(id) NOT NULL,
    status_code INT,
    h1 VARCHAR(500),
    title VARCHAR(500),
    description text,
    created_at TIMESTAMP DEFAULT NOW()
);

