DROP TABLE IF EXISTS travel_bookingitems;
DROP TABLE IF EXISTS travel_bookings;
DROP TABLE IF EXISTS travel_tripitems;
DROP TABLE IF EXISTS travel_trips;
DROP TABLE IF EXISTS travel_customers;

CREATE TABLE IF NOT EXISTS travel_customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    first_name VARCHAR(20),
    last_name VARCHAR(35),
    address VARCHAR(35)
    );

CREATE TABLE IF NOT EXISTS travel_trips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    price_per_week DECIMAL(10, 2),
    hotel_name VARCHAR(100),
    country VARCHAR(50),
    city VARCHAR(30),
    available_tickets INT
    );

CREATE TABLE IF NOT EXISTS travel_tripitems (
    id BIGINT PRIMARY KEY,
    price_per_week DECIMAL(10, 2),
    hotel_name VARCHAR(100),
    country VARCHAR(50),
    city VARCHAR(30),
    available_tickets INT
    );

CREATE TABLE IF NOT EXISTS travel_bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    departure_date VARCHAR(50),
    trip_id BIGINT,
    customer_id BIGINT,
    tickets INT,
    CONSTRAINT fk_trip FOREIGN KEY (trip_id) REFERENCES travel_trips(id),
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES travel_customers(id)
    );

CREATE TABLE IF NOT EXISTS travel_bookingitems (
    id BIGINT PRIMARY KEY,
    departure_date VARCHAR(50),
    tripitem_id BIGINT,
    customer_id BIGINT,
    tickets INT,
    CONSTRAINT tk_tripItem FOREIGN KEY (tripItem_id) REFERENCES travel_tripitems(id),
    CONSTRAINT fk_booking_customer FOREIGN KEY (customer_id) REFERENCES travel_customers(id)
    );

INSERT INTO travel_customers (username, first_name, last_name, address)
VALUES
    ('Freddan', 'Fredrik', 'Lundell', 'Prästgårdsängen 8'),
    ('Sly', 'Sylvester', 'Stallone', 'Amerikanskagatan 2'),
    ('Iron Mike', 'Mike', 'Tyson', 'Amerikanskagatan 3'),
    ('Mackan', 'Marcus', 'Andersson', 'Smörgåsbordet 35');

INSERT INTO travel_trips (price_per_week, hotel_name, country, city, available_tickets)
VALUES
    (16499, 'El Hotelo', 'Spain', 'Madrid', 49),
    (23200, 'Hotel Lisbon', 'Portugal', 'Lisbon', 49);

INSERT INTO travel_tripitems (id, price_per_week, hotel_name, country, city, available_tickets)
VALUES
    (1, 16499, 'El Hotelo', 'Spain', 'Madrid', 49),
    (2, 23200, 'Hotel Lisbon', 'Portugal', 'Lisbon', 49);

INSERT INTO travel_bookings (departure_date, trip_id, customer_id, tickets)
VALUES
    (DATE_ADD(NOW(), INTERVAL 3 MONTH), 1, 1, 1),
    (DATE_ADD(NOW(), INTERVAL 2 MONTH), 2, 2, 1);

INSERT INTO travel_bookingitems (id, departure_date, tripitem_id, customer_id, tickets)
VALUES
    (1, DATE_ADD(NOW(), INTERVAL 3 MONTH), 1, 1, 1),
    (2, DATE_ADD(NOW(), INTERVAL 2 MONTH), 2, 2, 1);