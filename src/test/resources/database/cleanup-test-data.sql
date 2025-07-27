DELETE FROM payments;

DELETE FROM rentals;
ALTER TABLE rentals AUTO_INCREMENT = 100;

DELETE FROM users_roles;
DELETE FROM users WHERE email LIKE '%@example.com%';
ALTER TABLE users AUTO_INCREMENT = 10;

DELETE FROM cars WHERE brand IN ('Toyota', 'Tesla', 'Fiat');
ALTER TABLE cars AUTO_INCREMENT = 1;
