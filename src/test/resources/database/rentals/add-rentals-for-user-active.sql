INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted)
VALUES
    (101, CURRENT_DATE - INTERVAL 2 DAY, CURRENT_DATE + INTERVAL 5 DAY, NULL, 1, 10, false);
