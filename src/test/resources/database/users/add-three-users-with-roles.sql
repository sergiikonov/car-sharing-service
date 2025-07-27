INSERT INTO users (id, email, first_name, last_name, password, is_deleted)
VALUES
    (10, 'user1@example.com', 'First', 'LastOne', '$2a$10$JbmdOzPOOD9ihQtXOL08FecgzWdFdaSWcgUX83z1ZK6KCv9vo6NxK', FALSE),
    (20, 'user2@example.com', 'Second', 'LastTwo', '$2a$10$JbmdOzPOOD9ihQtXOL08FecgzWdFdaSWcgUX83z1ZK6KCv9vo6NxK', FALSE),
    (30, 'user3@example.com', 'Third', 'LastThree', '$2a$10$JbmdOzPOOD9ihQtXOL08FecgzWdFdaSWcgUX83z1ZK6KCv9vo6NxK', FALSE);

INSERT INTO users_roles (user_id, role_id)
VALUES
    (10, 1),
    (20, 2),
    (30, 2);