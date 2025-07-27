INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted)
VALUES
    (201, CURRENT_DATE - 2, CURRENT_DATE + 5, NULL, 1, 20, false),
    (202, CURRENT_DATE - 10, CURRENT_DATE - 5, CURRENT_DATE - 5, 2, 20, false);