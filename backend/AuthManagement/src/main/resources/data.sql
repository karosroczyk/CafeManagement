-- USER --
INSERT IGNORE INTO user (id, first_name, last_name, email, password, enabled, date_of_birth, account_locked, created_date, last_modified_date)
VALUES
(1, 'client', 'client', 'client@gmail.com', 'Test123!', 1, '2000-08-05', 1, '2002-08-05', '2002-08-05');
INSERT IGNORE INTO user (id, first_name, last_name, email, password, enabled, date_of_birth, account_locked, created_date, last_modified_date)
VALUES
(2, 'employee', 'employee', 'employee@gmail.com', 'Test123!', 1, '2000-08-05', 1, '2002-08-05', '2002-08-05');
INSERT IGNORE INTO user (id, first_name, last_name, email, password, enabled, date_of_birth, account_locked, created_date, last_modified_date)
VALUES
(3, 'admin', 'admin', 'admin@gmail.com', 'Test123!', 1, '2000-08-05', 1, '2002-08-05', '2002-08-05');

INSERT IGNORE INTO role (id, created_date, last_modified_date, name) VALUES (1, '2000-08-05', '2002-08-05', 'ROLE_CLIENT');
INSERT IGNORE INTO role (id, created_date, last_modified_date, name) VALUES (2, '2000-08-05', '2002-08-05', 'ROLE_EMPLOYEE');
INSERT IGNORE INTO role (id, created_date, last_modified_date, name) VALUES (3, '2000-08-05', '2002-08-05', 'ROLE_ADMIN');

INSERT IGNORE INTO user_roles (user_id, roles_id) VALUES (1, 1);
INSERT IGNORE INTO user_roles (user_id, roles_id) VALUES (2, 2);
INSERT IGNORE INTO user_roles (user_id, roles_id) VALUES (3, 3);
