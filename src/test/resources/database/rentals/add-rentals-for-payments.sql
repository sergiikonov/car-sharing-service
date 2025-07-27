INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted)
VALUES
    (10, CURRENT_DATE - INTERVAL 2 DAY, CURRENT_DATE + INTERVAL 5 DAY, NULL, 1, 10, false),
    (20, CURRENT_DATE - INTERVAL 1 DAY, CURRENT_DATE + INTERVAL 3 DAY, CURRENT_DATE - INTERVAL 1 DAY, 1, 20, false),
    (30, CURRENT_DATE - INTERVAL 3 DAY, CURRENT_DATE + INTERVAL 2 DAY, NULL, 1, 30, false);
