INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted)
VALUES
    (401, CURRENT_DATE - INTERVAL 10 DAY, CURRENT_DATE - INTERVAL 5 DAY, CURRENT_DATE - INTERVAL 5 DAY, 2, 10, false),
    (402, CURRENT_DATE - INTERVAL 15 DAY, CURRENT_DATE - INTERVAL 10 DAY, CURRENT_DATE - INTERVAL 10 DAY, 3, 20, false);
