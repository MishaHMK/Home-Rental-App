INSERT INTO payments (
    id, status, booking_id, session_url, session_id, amount, is_deleted
)
VALUES (
           1,
           'PENDING',
           1,
           'https://checkout.stripe.com/c/pay/cs_test_a1h5',
           'cs_test_a1h5',
           125.55,
           false
       );