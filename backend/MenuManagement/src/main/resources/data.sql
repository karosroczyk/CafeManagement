-- MENU --
INSERT IGNORE INTO menuitems (item_id, category_id, created_at, description, image, name, price, updated_at)
VALUES
(1, 1, '2000-08-05', 'Fluffy pastry', '', 'Croissant', 3.5, '2002-08-05');
INSERT IGNORE INTO menuitems (item_id, category_id, created_at, description, image, name, price, updated_at)
VALUES
(2, 1, '2000-08-05', 'Fluffy pastry with cinamon', '', 'Cinamon Roll', 2.5, '2002-08-05');
INSERT IGNORE INTO menuitems (item_id, category_id, created_at, description, image, name, price, updated_at)
VALUES
(3, 2, '2000-08-05', 'Healthy carrot cake with a pinch of pumpkin', '', 'Carrot Cake', 4.5, '2002-08-05');
INSERT IGNORE INTO menuitems (item_id, category_id, created_at, description, image, name, price, updated_at)
VALUES
(4, 2, '2000-08-05', 'New york style cheesecake', '', 'Cheesecake', 5, '2002-08-05');

INSERT IGNORE INTO categories (category_id, description, category_name) VALUES (1, 'Hot pastries', 'Pastries');
INSERT IGNORE INTO categories (category_id, description, category_name) VALUES (2, 'Sweet cakes', 'Cakes');
