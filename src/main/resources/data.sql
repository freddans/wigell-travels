DROP TABLE IF exists bookings, customers, trips;

CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    first_name VARCHAR(20),
    last_name VARCHAR(35),
    address VARCHAR(35)
);

CREATE TABLE trips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    price_per_week DECIMAL(10, 2),
    hotel_name VARCHAR(100),
    country VARCHAR(50),
    city VARCHAR(30)
);

CREATE TABLE bookings (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    departure_date VARCHAR(50),
    trip_id BIGINT,
    total_cost DECIMAL(10, 2),
    customer_id BIGINT,
    CONSTRAINT fk_trip FOREIGN KEY (trip_id) REFERENCES trips(id),
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

INSERT INTO customers (username, first_name, last_name, address)
VALUES
    ('Freddan', 'Fredrik', 'Lundell', 'Prästgårdsängen 8'),
    ('Sly', 'Sylvester', 'Stallone', 'Amerikanskagatan 2'),
    ('Iron Mike', 'Mike', 'Tyson', 'Amerikanskagatan 3'),
    ('Mackan', 'Marcus', 'Andersson', 'Smörgåsbordet 35');

INSERT INTO trips (price_per_week, hotel_name, country, city)
VALUES
    (16499, 'El Hotelo', 'Spain', 'Madrid'),
    (23200, 'Hotel Lisbon', 'Portugal', 'Lisbon');

INSERT INTO bookings (departure_date, trip_id, total_cost, customer_id)
VALUES
    (DATE_ADD(NOW(), INTERVAL 3 MONTH), 1, 400, 1),
    (DATE_ADD(NOW(), INTERVAL 2 MONTH), 2, 500, 2);