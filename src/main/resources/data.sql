-- DROP TABLE IF exists bookings, customers, trips;

CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    first_name VARCHAR(20),
    last_name VARCHAR(35),
    address VARCHAR(35)
    );

CREATE TABLE IF NOT EXISTS trips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    price_per_week DECIMAL(10, 2),
    hotel_name VARCHAR(100),
    country VARCHAR(50),
    city VARCHAR(30)
    );

CREATE TABLE IF NOT EXISTS bookings (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    departure_date VARCHAR(50),
    trip_id BIGINT,
    customer_id BIGINT,
    CONSTRAINT fk_trip FOREIGN KEY (trip_id) REFERENCES trips(id),
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
    );

CREATE TABLE IF NOT EXISTS tripitems (

    id BIGINT PRIMARY KEY,
    price_per_week DECIMAL(10, 2),
    hotel_name VARCHAR(100),
    country VARCHAR(50),
    city VARCHAR(30)
    );

CREATE TABLE IF NOT EXISTS bookingitems (

    id BIGINT PRIMARY KEY,
    departure_date VARCHAR(50),
    tripitem_id BIGINT,
    customer_id BIGINT,
    CONSTRAINT tk_tripItem FOREIGN KEY (tripItem_id) REFERENCES tripitems(id),
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

INSERT INTO tripitems (id, price_per_week, hotel_name, country, city)
VALUES
    (1, 16499, 'El Hotelo', 'Spain', 'Madrid'),
    (2, 23200, 'Hotel Lisbon', 'Portugal', 'Lisbon');

INSERT INTO bookings (departure_date, trip_id, customer_id)
VALUES
    (DATE_ADD(NOW(), INTERVAL 3 MONTH), 1, 1),
    (DATE_ADD(NOW(), INTERVAL 2 MONTH), 2, 2);

INSERT INTO bookingitems (id, departure_date, tripitem_id, customer_id)
VALUES
    (1, DATE_ADD(NOW(), INTERVAL 3 MONTH), 1, 1),
    (2, DATE_ADD(NOW(), INTERVAL 2 MONTH), 2, 2);