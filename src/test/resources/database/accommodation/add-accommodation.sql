INSERT INTO accommodations (
    id, type, size, street, city, country, state, postal_code, latitude, longitude, amenities,
    daily_rate, availability, is_deleted
)
VALUES
    (
        4, 'HOUSE', 'Test size info', 'Test Street, 22', 'Test City', 'Test Country',
        'Test Region', '80001', 50.0, 15.0, 'amenity 1, amenity 2, amenity 3',
        89.99, 2, 0
    );