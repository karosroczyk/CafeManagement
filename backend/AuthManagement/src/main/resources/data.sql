-- USER --
INSERT IGNORE INTO users (id, first_name, last_name, email, password, enabled, date_of_birth, account_locked, created_date, last_modified_date)
VALUES
(1, 'client', 'client', 'client@gmail.com', '$2a$12$4Wbq6acQAeLTR5jPv6hwl.13LshFTZku5/fTtsKeViynOScattH0G', 1, '2000-08-05', 0, '2002-08-05', '2002-08-05');
INSERT IGNORE INTO users (id, first_name, last_name, email, password, enabled, date_of_birth, account_locked, created_date, last_modified_date)
VALUES
(2, 'employee', 'employee', 'employee@gmail.com', '$2a$12$CO8FI/khi7s8IylTF5tjJemz8j6iF.z/Tq/qLIoKm.Qpg3ndftTjm', 1, '2000-08-05', 0, '2002-08-05', '2002-08-05');
INSERT IGNORE INTO users (id, first_name, last_name, email, password, enabled, date_of_birth, account_locked, created_date, last_modified_date)
VALUES
(3, 'admin', 'admin', 'admin@gmail.com', '$2a$12$OkazfzU4Ux7.MuVKEknbFuoSMzAb.0gN7O4o7AqBN8dgxJXacKKfq', 1, '2000-08-05', 0, '2002-08-05', '2002-08-05');

INSERT IGNORE INTO role (id, created_date, last_modified_date, name) VALUES (1, '2000-08-05', '2002-08-05', 'ROLE_CLIENT');
INSERT IGNORE INTO role (id, created_date, last_modified_date, name) VALUES (2, '2000-08-05', '2002-08-05', 'ROLE_EMPLOYEE');
INSERT IGNORE INTO role (id, created_date, last_modified_date, name) VALUES (3, '2000-08-05', '2002-08-05', 'ROLE_ADMIN');

INSERT IGNORE INTO users_roles (user_id, roles_id) VALUES (1, 1);
INSERT IGNORE INTO users_roles (user_id, roles_id) VALUES (2, 2);
INSERT IGNORE INTO users_roles (user_id, roles_id) VALUES (3, 3);
