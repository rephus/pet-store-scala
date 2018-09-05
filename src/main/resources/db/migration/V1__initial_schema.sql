CREATE TABLE "user"(
    id SERIAL PRIMARY KEY,
    email TEXT,
    password TEXT
);

CREATE TABLE "pet"(
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    photo_urls TEXT,
    status TEXT
);

CREATE TABLE "order"(
    id SERIAL PRIMARY KEY,
    complete BOOLEAN DEFAULT false,
    pet_id INTEGER,
    quantity INTEGER,
    status TEXT
);