INSERT INTO payments (
    id, status, booking_id, session_url, session_id, amount, is_deleted
)
VALUES (
           4,
           'CANCELED',
           2,
           'https://checkout.stripe.com/c/pay/cs_test_a1h7',
           'cs_test_a1h7',
           125.55,
           false
       );