INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted)
VALUES
    (201, CURRENT_DATE - INTERVAL 2 DAY, CURRENT_DATE + INTERVAL 5 DAY, NULL, 1, 20, false),
    (202, CURRENT_DATE - INTERVAL 10 DAY, CURRENT_DATE - INTERVAL 5 DAY, CURRENT_DATE - INTERVAL 5 DAY, 2, 20, false);
