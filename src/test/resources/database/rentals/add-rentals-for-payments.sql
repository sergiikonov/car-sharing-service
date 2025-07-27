INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted)
VALUES
    (10, CURRENT_DATE - 2, CURRENT_DATE + 5, NULL, 1, 10, false),
    (20, CURRENT_DATE - 1, CURRENT_DATE + 3, CURRENT_DATE - 1, 1, 20, false),
    (30, CURRENT_DATE - 3, CURRENT_DATE + 2, NULL, 1, 30, false);
