-- Insert Users
INSERT INTO user_entity (id, username, email, password, role, created_at, is_verified, activation_token, avatar)
VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 'user1', 'user1@example.com', '$2a$12$prB7vZCNs7uhHWOZlVZSUe6iGCPrhGzO.gni6IvqJIlj7Hyj0mS8u', 'USER', '2025-02-24 12:00:00', true, null, null),
    ('550e8400-e29b-41d4-a716-446655440002', 'user2', 'user2@example.com', '$2a$12$prB7vZCNs7uhHWOZlVZSUe6iGCPrhGzO.gni6IvqJIlj7Hyj0mS8u', 'USER', '2025-02-24 12:00:00', true, null, null),
    ('550e8400-e29b-41d4-a716-446655440003', 'user3', 'user3@example.com', '$2a$12$prB7vZCNs7uhHWOZlVZSUe6iGCPrhGzO.gni6IvqJIlj7Hyj0mS8u', 'USER', '2025-02-24 12:00:00', true, null, null),
    ('03453eb8-4c4a-42fb-98c7-302e9b81acc6', 'admin1', 'maluisa2000.30.3@gmail.com', '$2a$12$prB7vZCNs7uhHWOZlVZSUe6iGCPrhGzO.gni6IvqJIlj7Hyj0mS8u', 'ADMIN', '2025-02-24 12:00:00', true, null, null),
    ('550e8400-e29b-41d4-a716-446655440005', 'user5', 'user5@example.com', '$2a$12$prB7vZCNs7uhHWOZlVZSUe6iGCPrhGzO.gni6IvqJIlj7Hyj0mS8u', 'USER', '2025-02-24 12:00:00', true, null, null);

-- Insert Shopping Carts for Each User
INSERT INTO shopping_cart (id, user_id, created_at)
VALUES
    (1, '550e8400-e29b-41d4-a716-446655440001', '2025-02-24 12:00:00'),
    (2, '550e8400-e29b-41d4-a716-446655440002', '2025-02-24 12:00:00'),
    (3, '550e8400-e29b-41d4-a716-446655440003', '2025-02-24 12:00:00'),
    (4, '03453eb8-4c4a-42fb-98c7-302e9b81acc6', '2025-02-24 12:00:00'),
    (5, '550e8400-e29b-41d4-a716-446655440005', '2025-02-24 12:00:00');

-- Insert Categories
INSERT INTO category (id, name, parent_category_id)
VALUES
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

-- Insert Ingredients
INSERT INTO ingredient (id, name, quantity, unit_of_measure)
VALUES
    (1, 'Pollo', 1, 'KILO'),
    (2, 'Arroz', 1, 'KILO'),
    (3, 'Tomate', 1, 'UNIDAD'),
    (4, 'Cebolla', 1, 'UNIDAD'),
    (5, 'Pasta', 1, 'KILO'),
    (6, 'Queso', 1, 'GRAMO'),
    (7, 'Lechuga', 1, 'UNIDAD'),
    (8, 'Aceite de oliva', 1, 'LITRO'),
    (9, 'Pescado', 1, 'KILO'),
    (10, 'Patata', 1, 'KILO');

-- Link Ingredients to Categories
INSERT INTO ingredient_category (ingredient_id, category_id)
VALUES
    (1, 1),  -- Pollo -> Carnes
    (2, 2),  -- Arroz -> Carbohidratos (assuming category 2 is Carbohidratos)
    (3, 3),  -- Tomate -> Vegetariana (assuming category 3 is Vegetariana)
    (4, 3),  -- Cebolla -> Vegetariana
    (5, 2),  -- Pasta -> Carbohidratos
    (6, 2),  -- Queso -> Carbohidratos
    (7, 3),  -- Lechuga -> Vegetariana
    (8, 3),  -- Aceite de oliva -> Vegetariana
    (9, 1),  -- Pescado -> Carnes
    (10, 2); -- Patata -> Carbohidratos

-- Insert Recipes (Fixed with created_at and updated_at)
INSERT INTO recipe (id, name, description, portions, user_id, created_at, updated_at)
VALUES
    (1, 'Pollo al Curry', 'Un delicioso plato de pollo con curry y arroz.', 4, '550e8400-e29b-41d4-a716-446655440001', '2025-02-24 12:00:00', '2025-02-24 12:00:00'),
    (2, 'Ensalada César', 'Ensalada fresca con pollo y aderezo César.', 2, '550e8400-e29b-41d4-a716-446655440001', '2025-02-24 12:00:00', '2025-02-24 12:00:00'),
    (3, 'Pasta con Tomate', 'Pasta sencilla con salsa de tomate casera.', 3, '550e8400-e29b-41d4-a716-446655440001', '2025-02-24 12:00:00', '2025-02-24 12:00:00'),
    (4, 'Pescado al Horno', 'Pescado asado con patatas y aceite de oliva.', 2, '550e8400-e29b-41d4-a716-446655440001', '2025-02-24 12:00:00', '2025-02-24 12:00:00'),
    (5, 'Arroz con Verduras', 'Arroz salteado con verduras frescas.', 4, '550e8400-e29b-41d4-a716-446655440001', '2025-02-24 12:00:00', '2025-02-24 12:00:00'),
    (6, 'Sopa de Cebolla', 'Sopa caliente con cebolla y queso gratinado.', 3, '550e8400-e29b-41d4-a716-446655440001', '2025-02-24 12:00:00', '2025-02-24 12:00:00'),
    (7, 'Patatas Fritas', 'Patatas crujientes fritas en aceite de oliva.', 2, '550e8400-e29b-41d4-a716-446655440001', '2025-02-24 12:00:00', '2025-02-24 12:00:00'),
    (8, 'Ensalada Verde', 'Ensalada ligera con lechuga y tomate.', 1, '550e8400-e29b-41d4-a716-446655440001', '2025-02-24 12:00:00', '2025-02-24 12:00:00'),
    (9, 'Pollo Asado', 'Pollo jugoso asado con especias.', 4, '550e8400-e29b-41d4-a716-446655440001', '2025-02-24 12:00:00', '2025-02-24 12:00:00'),
    (10, 'Pasta con Queso', 'Pasta cremosa con queso fundido.', 3, '550e8400-e29b-41d4-a716-446655440001', '2025-02-24 12:00:00', '2025-02-24 12:00:00');

-- Link Recipes to Categories
INSERT INTO recipe_category (recipe_id, category_id)
VALUES
    (1, 1), (1, 2),  -- Pollo al Curry -> Carnes, Carbohidratos
    (2, 1), (2, 3),  -- Ensalada César -> Carnes, Vegetariana
    (3, 2), (3, 3),  -- Pasta con Tomate -> Carbohidratos, Vegetariana
    (4, 1), (4, 2),  -- Pescado al Horno -> Carnes, Carbohidratos
    (5, 2), (5, 3),  -- Arroz con Verduras -> Carbohidratos, Vegetariana
    (6, 3), (6, 2),  -- Sopa de Cebolla -> Vegetariana, Carbohidratos
    (7, 2),          -- Patatas Fritas -> Carbohidratos
    (8, 3),          -- Ensalada Verde -> Vegetariana
    (9, 1),          -- Pollo Asado -> Carnes
    (10, 2);         -- Pasta con Queso -> Carbohidratos

-- Insert Recipe Ingredients
INSERT INTO recipe_ingredient (recipe_id, ingredient_id)
VALUES
    (1, 1), (1, 2),       -- Pollo al Curry: Pollo, Arroz
    (2, 1), (2, 7),       -- Ensalada César: Pollo, Lechuga
    (3, 5), (3, 3),       -- Pasta con Tomate: Pasta, Tomate
    (4, 9), (4, 10),      -- Pescado al Horno: Pescado, Patata
    (5, 2), (5, 3),       -- Arroz con Verduras: Arroz, Tomate
    (6, 4), (6, 6),       -- Sopa de Cebolla: Cebolla, Queso
    (7, 10),              -- Patatas Fritas: Patata
    (8, 7), (8, 3),       -- Ensalada Verde: Lechuga, Tomate
    (9, 1),               -- Pollo Asado: Pollo
    (10, 5), (10, 6);     -- Pasta con Queso: Pasta, Queso

-- Insert Inventory Items (Example for user1)
INSERT INTO inventory (id, user_id, ingredient_id, quantity, added_at)
VALUES
    (1, '550e8400-e29b-41d4-a716-446655440001', 1, 2, '2025-02-24 12:00:00'),
    (2, '550e8400-e29b-41d4-a716-446655440001', 2, 3, '2025-02-24 12:00:00');

-- Insert Favorites (Example for user1)
INSERT INTO favorites (id, user_id, recipe_id, added_at)
VALUES
    (1, '550e8400-e29b-41d4-a716-446655440001', 1, '2025-02-24 12:00:00'),
    (2, '550e8400-e29b-41d4-a716-446655440001', 2, '2025-02-24 12:00:00');

-- Insert Shopping Cart Items (Example for user1's cart)
INSERT INTO shopping_cart_item (id, shopping_cart_id, ingredient_id, quantity)
VALUES
    (1, 1, 1, 1),  -- Pollo in user1's cart
    (2, 1, 2, 2);  -- Arroz in user1's cart

-- Insert Refresh Tokens (Example for user1)
INSERT INTO refresh_token (id, user_id, created_at, expire_at)
VALUES
    ('550e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440001', '2025-02-24 12:00:00', '2025-02-25 12:00:00');