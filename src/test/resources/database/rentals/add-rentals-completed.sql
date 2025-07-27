INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted)
VALUES
    (401, CURRENT_DATE - 10, CURRENT_DATE - 5, CURRENT_DATE - 5, 2, 10, false),
    (402, CURRENT_DATE - 15, CURRENT_DATE - 10, CURRENT_DATE - 10, 3, 20, false);