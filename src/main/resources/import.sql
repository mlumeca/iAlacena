INSERT INTO user_entity (id, username, email, password, role, created_at, is_verified, activation_token, avatar)
values('550e8400-e29b-41d4-a716-446655440001', 'user1', 'user1@example.com', '$2a$12$prB7vZCNs7uhHWOZlVZSUe6iGCPrhGzO.gni6IvqJIlj7Hyj0mS8u', 'USER', '2025-02-24 12:00:00', true, null, null);
INSERT INTO user_entity (id, username, email, password, role, created_at, is_verified, activation_token, avatar)
values('550e8400-e29b-41d4-a716-446655440002', 'user2', 'user2@example.com', '$2a$12$prB7vZCNs7uhHWOZlVZSUe6iGCPrhGzO.gni6IvqJIlj7Hyj0mS8u', 'USER', '2025-02-24 12:00:00', true, null, null);
INSERT INTO user_entity (id, username, email, password, role, created_at, is_verified, activation_token, avatar)
values('550e8400-e29b-41d4-a716-446655440003', 'user3', 'user3@example.com', '$2a$12$prB7vZCNs7uhHWOZlVZSUe6iGCPrhGzO.gni6IvqJIlj7Hyj0mS8u', 'USER', '2025-02-24 12:00:00', true, null, null);
INSERT INTO user_entity (id, username, email, password, role, created_at, is_verified, activation_token, avatar)
values('550e8400-e29b-41d4-a716-446655440004', 'admin1', 'maluisa2000.30.3@gmail.com', '$2a$12$prB7vZCNs7uhHWOZlVZSUe6iGCPrhGzO.gni6IvqJIlj7Hyj0mS8u', 'ADMIN', '2025-02-24 12:00:00', true, null, null);
INSERT INTO user_entity (id, username, email, password, role, created_at, is_verified, activation_token, avatar)
values('550e8400-e29b-41d4-a716-446655440005', 'user5', 'user5@example.com', '$2a$12$prB7vZCNs7uhHWOZlVZSUe6iGCPrhGzO.gni6IvqJIlj7Hyj0mS8u', 'USER', '2025-02-24 12:00:00', true, null, null);

INSERT INTO category (id, name, parent_category_id) VALUES
                                                        (1, 'Carnes', NULL),
                                                        (2, 'Carnes Rojas', 1),
                                                        (3, 'Carnes Blancas', 1),
                                                        (4, 'Pescados', NULL),
                                                        (5, 'Pescados Azules', 4),
                                                        (6, 'Pescados Blancos', 4),
                                                        (7, 'Vegetales', NULL),
                                                        (8, 'Verduras', 7),
                                                        (9, 'Frutas', 7),
                                                        (10, 'Lácteos', NULL),
                                                        (11, 'Quesos', 10);