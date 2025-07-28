INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted)
VALUES
    (301, CURRENT_DATE - INTERVAL 2 DAY, CURRENT_DATE + INTERVAL 5 DAY, NULL, 1, 10, false),
    (302, CURRENT_DATE - INTERVAL 1 DAY, CURRENT_DATE + INTERVAL 3 DAY, NULL, 1, 20, false),
    (303, CURRENT_DATE - INTERVAL 3 DAY, CURRENT_DATE + INTERVAL 2 DAY, NULL, 2, 30, false);
