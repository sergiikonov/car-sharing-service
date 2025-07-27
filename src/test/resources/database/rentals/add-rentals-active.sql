INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted)
VALUES
    (301, CURRENT_DATE - 2, CURRENT_DATE + 5, NULL, 1, 10, false),
    (302, CURRENT_DATE - 1, CURRENT_DATE + 3, NULL, 1, 20, false),
    (303, CURRENT_DATE - 3, CURRENT_DATE + 2, NULL, 2, 30, false);