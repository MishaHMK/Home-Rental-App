INSERT INTO accommodations (
    id, type, size, street, city, country, state, postal_code, latitude, longitude, amenities,
    daily_rate, availability, is_deleted
)
VALUES
    (
        1, 'HOUSE', 'Test size info', 'Test Street, 22', 'Test City', 'Test Country',
        'Test Region', '80001', 50.0, 15.0, 'amenity 1, amenity 2, amenity 3',
        89.99, 2, 0
    ),
    (
        2, 'HOUSE', 'Test size info 2', 'Test Street, 52', 'Test City 2', 'Test Country 2',
        'Test Region 2', '80002', 18.5, 14.32, 'amenity 1, amenity 2, amenity 3',
        119.99, 2, 0
    ),
    (
        3, 'APARTMENT', 'Test size info 3', 'Test Street, 52', 'Test City 2', 'Test Country 2',
        'Test Region 2', '80002', 18.5, 14.32, 'amenity 1, amenity 2, amenity 3',
        49.99, 3, 0
    );