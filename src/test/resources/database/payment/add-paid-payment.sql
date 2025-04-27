INSERT INTO payments (
    id, status, booking_id, session_url, session_id, amount, is_deleted
)
VALUES (
           2,
           'PAID',
           2,
           'https://checkout.stripe.com/c/pay/cs_test_a1h6',
           'cs_test_a1h6',
           125.55,
           false
       );