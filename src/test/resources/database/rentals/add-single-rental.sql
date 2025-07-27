INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted)
VALUES
    (100, CURRENT_DATE - 2, CURRENT_DATE + 5, NULL, 1, 10, false);