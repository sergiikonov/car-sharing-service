INSERT INTO payments (status, type, rental_id, session_url, session_id, amount_to_pay, is_deleted)
VALUES
    ('PAID', 'PAYMENT', 10, 'https://checkout.stripe.com/c/pay/cs_test_a1dHgl', 'session_12345', 50.00, false);