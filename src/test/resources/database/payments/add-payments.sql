INSERT INTO payments (id, status, type, rental_id, session_url, session_id, amount_to_pay, is_deleted)
VALUES
    (1, 'PAID', 'PAYMENT', 10, 'https://checkout.stripe.com/c/pay/cs_test_1', 'session_11111', 50.00, false),
    (2, 'PENDING', 'PAYMENT', 20, 'https://checkout.stripe.com/c/pay/cs_test_2', 'session_22222', 75.00, false),
    (3, 'PAID', 'PAYMENT', 30, 'https://checkout.stripe.com/c/pay/cs_test_3', 'session_33333', 100.00, false);