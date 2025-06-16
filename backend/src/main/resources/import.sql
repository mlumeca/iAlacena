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
    -- Carnes y embutidos
    (1, 'Pollo', 1, 'KILO'),
    (2, 'Ternera', 1, 'KILO'),
    (3, 'Cerdo', 1, 'KILO'),
    (4, 'Cordero', 1, 'KILO'),
    (5, 'Conejo', 1, 'KILO'),
    (6, 'Chorizo', 1, 'KILO'),
    (7, 'Jamón serrano', 1, 'KILO'),
    (8, 'Jamón ibérico', 1, 'KILO'),
    (9, 'Salchichón', 1, 'KILO'),
    (10, 'Morcilla', 1, 'KILO'),
    (11, 'Lomo', 1, 'KILO'),
    (12, 'Costillas de cerdo', 1, 'KILO'),
    (13, 'Panceta', 1, 'KILO'),
    (14, 'Butifarra', 1, 'KILO'),
    (15, 'Pollo (pechuga)', 1, 'KILO'),
    (16, 'Pollo (muslo)', 1, 'KILO'),
    (17, 'Pavo', 1, 'KILO'),

    -- Pescados y mariscos
    (18, 'Merluza', 1, 'KILO'),
    (19, 'Bacalao', 1, 'KILO'),
    (20, 'Sardina', 1, 'KILO'),
    (21, 'Boquerón', 1, 'KILO'),
    (22, 'Atún', 1, 'KILO'),
    (23, 'Salmón', 1, 'KILO'),
    (24, 'Dorada', 1, 'KILO'),
    (25, 'Lubina', 1, 'KILO'),
    (26, 'Rape', 1, 'KILO'),
    (27, 'Calamar', 1, 'KILO'),
    (28, 'Sepia', 1, 'KILO'),
    (29, 'Pulpo', 1, 'KILO'),
    (30, 'Gamba', 1, 'KILO'),
    (31, 'Langostino', 1, 'KILO'),
    (32, 'Mejillón', 1, 'KILO'),
    (33, 'Almeja', 1, 'KILO'),
    (34, 'Chirlas', 1, 'KILO'),
    (35, 'Cangrejo', 1, 'KILO'),
    (36, 'Bogavante', 1, 'KILO'),
    (37, 'Nécora', 1, 'KILO'),

    -- Verduras y hortalizas
    (38, 'Tomate', 1, 'KILO'),
    (39, 'Cebolla', 1, 'KILO'),
    (40, 'Ajo', 1, 'KILO'),
    (41, 'Pimiento rojo', 1, 'KILO'),
    (42, 'Pimiento verde', 1, 'KILO'),
    (43, 'Pimiento amarillo', 1, 'KILO'),
    (44, 'Patata', 1, 'KILO'),
    (45, 'Lechuga', 1, 'UNIDAD'),
    (46, 'Espinaca', 1, 'KILO'),
    (47, 'Acelga', 1, 'KILO'),
    (48, 'Calabacín', 1, 'KILO'),
    (49, 'Berenjena', 1, 'KILO'),
    (50, 'Zanahoria', 1, 'KILO'),
    (51, 'Calabaza', 1, 'KILO'),
    (52, 'Coliflor', 1, 'UNIDAD'),
    (53, 'Brócoli', 1, 'KILO'),
    (54, 'Repollo', 1, 'UNIDAD'),
    (55, 'Alcachofa', 1, 'KILO'),
    (56, 'Espárrago verde', 1, 'KILO'),
    (57, 'Espárrago blanco', 1, 'KILO'),
    (58, 'Puerro', 1, 'KILO'),
    (59, 'Apio', 1, 'KILO'),
    (60, 'Nabo', 1, 'KILO'),
    (61, 'Rábano', 1, 'KILO'),
    (62, 'Judía verde', 1, 'KILO'),
    (63, 'Guisante', 1, 'KILO'),
    (64, 'Haba', 1, 'KILO'),
    (65, 'Endibia', 1, 'UNIDAD'),
    (66, 'Champiñón', 1, 'KILO'),
    (67, 'Seta de cardo', 1, 'KILO'),
    (68, 'Boletus', 1, 'KILO'),
    (69, 'Níscalo', 1, 'KILO'),

    -- Frutas
    (70, 'Naranja', 1, 'KILO'),
    (71, 'Limón', 1, 'KILO'),
    (72, 'Mandarina', 1, 'KILO'),
    (73, 'Manzana', 1, 'KILO'),
    (74, 'Pera', 1, 'KILO'),
    (75, 'Plátano', 1, 'KILO'),
    (76, 'Uva', 1, 'KILO'),
    (77, 'Fresa', 1, 'KILO'),
    (78, 'Melocotón', 1, 'KILO'),
    (79, 'Albaricoque', 1, 'KILO'),
    (80, 'Cereza', 1, 'KILO'),
    (81, 'Ciruela', 1, 'KILO'),
    (82, 'Melón', 1, 'KILO'),
    (83, 'Sandía', 1, 'KILO'),
    (84, 'Higo', 1, 'KILO'),
    (85, 'Granada', 1, 'KILO'),
    (86, 'Kiwi', 1, 'KILO'),
    (87, 'Piña', 1, 'KILO'),
    (88, 'Mango', 1, 'KILO'),
    (89, 'Aguacate', 1, 'KILO'),

    -- Cereales y legumbres
    (90, 'Arroz', 1, 'KILO'),
    (91, 'Pasta', 1, 'KILO'),
    (92, 'Lenteja', 1, 'KILO'),
    (93, 'Garbanzo', 1, 'KILO'),
    (94, 'Alubia blanca', 1, 'KILO'),
    (95, 'Alubia roja', 1, 'KILO'),
    (96, 'Quinoa', 1, 'KILO'),
    (97, 'Cuscús', 1, 'KILO'),
    (98, 'Harina de trigo', 1, 'KILO'),
    (99, 'Harina de maíz', 1, 'KILO'),
    (100, 'Pan rallado', 1, 'KILO'),
    (101, 'Sémola', 1, 'KILO'),
    (102, 'Trigo sarraceno', 1, 'KILO'),

    -- Lácteos
    (103, 'Queso manchego', 1, 'KILO'),
    (104, 'Queso fresco', 1, 'KILO'),
    (105, 'Queso de cabra', 1, 'KILO'),
    (106, 'Queso azul', 1, 'KILO'),
    (107, 'Queso curado', 1, 'KILO'),
    (108, 'Leche entera', 1, 'LITRO'),
    (109, 'Leche desnatada', 1, 'LITRO'),
    (110, 'Nata líquida', 1, 'LITRO'),
    (111, 'Yogur natural', 1, 'UNIDAD'),
    (112, 'Mantequilla', 1, 'KILO'),
    (113, 'Requesón', 1, 'KILO'),

    -- Aceites, vinagres y salsas
    (114, 'Aceite de oliva virgen extra', 1, 'LITRO'),
    (115, 'Aceite de girasol', 1, 'LITRO'),
    (116, 'Vinagre de vino', 1, 'LITRO'),
    (117, 'Vinagre de Jerez', 1, 'LITRO'),
    (118, 'Vinagre balsámico', 1, 'LITRO'),
    (119, 'Salsa de tomate', 1, 'KILO'),
    (120, 'Mayonesa', 1, 'KILO'),
    (121, 'Mostaza', 1, 'KILO'),
    (122, 'Salsa alioli', 1, 'KILO'),
    (123, 'Salsa romesco', 1, 'KILO'),
    (124, 'Miel', 1, 'KILO'),

    -- Especias y hierbas
    (125, 'Pimentón dulce', 1, 'GRAMO'),
    (126, 'Pimentón picante', 1, 'GRAMO'),
    (127, 'Azafrán', 1, 'GRAMO'),
    (128, 'Perejil', 1, 'GRAMO'),
    (129, 'Albahaca', 1, 'GRAMO'),
    (130, 'Orégano', 1, 'GRAMO'),
    (131, 'Tomillo', 1, 'GRAMO'),
    (132, 'Romero', 1, 'GRAMO'),
    (133, 'Laurel', 1, 'GRAMO'),
    (134, 'Cilantro', 1, 'GRAMO'),
    (135, 'Canela', 1, 'GRAMO'),
    (136, 'Comino', 1, 'GRAMO'),
    (137, 'Nuez moscada', 1, 'GRAMO'),
    (138, 'Pimienta negra', 1, 'GRAMO'),
    (139, 'Pimienta blanca', 1, 'GRAMO'),
    (140, 'Clavo', 1, 'GRAMO'),
    (141, 'Curry', 1, 'GRAMO'),
    (142, 'Cúrcuma', 1, 'GRAMO'),
    (143, 'Jengibre', 1, 'GRAMO'),

    -- Frutos secos y semillas
    (144, 'Almendra', 1, 'KILO'),
    (145, 'Avellana', 1, 'KILO'),
    (146, 'Nuez', 1, 'KILO'),
    (147, 'Piñón', 1, 'KILO'),
    (148, 'Cacahuete', 1, 'KILO'),
    (149, 'Semilla de sésamo', 1, 'KILO'),
    (150, 'Semilla de chía', 1, 'KILO'),
    (151, 'Semilla de lino', 1, 'KILO'),
    (152, 'Anacardo', 1, 'KILO'),
    (153, 'Pistacho', 1, 'KILO'),

    -- Bebidas y caldos
    (154, 'Vino blanco', 1, 'LITRO'),
    (155, 'Vino tinto', 1, 'LITRO'),
    (156, 'Cava', 1, 'LITRO'),
    (157, 'Caldo de pollo', 1, 'LITRO'),
    (158, 'Caldo de pescado', 1, 'LITRO'),
    (159, 'Caldo de carne', 1, 'LITRO'),
    (160, 'Caldo de verduras', 1, 'LITRO'),
    (161, 'Agua', 1, 'LITRO'),

    -- Panes y masas
    (162, 'Pan blanco', 1, 'KILO'),
    (163, 'Pan integral', 1, 'KILO'),
    (164, 'Pan de centeno', 1, 'KILO'),
    (165, 'Masa de hojaldre', 1, 'KILO'),
    (166, 'Masa de pizza', 1, 'KILO'),
    (167, 'Masa quebrada', 1, 'KILO'),

    -- Azúcares y edulcorantes
    (168, 'Azúcar blanco', 1, 'KILO'),
    (169, 'Azúcar moreno', 1, 'KILO'),
    (170, 'Azúcar glas', 1, 'KILO'),
    (171, 'Stevia', 1, 'GRAMO'),
    (172, 'Sacarina', 1, 'GRAMO'),

    -- Otros
    (173, 'Huevo', 1, 'UNIDAD'),
    (174, 'Clara de huevo', 1, 'UNIDAD'),
    (175, 'Yema de huevo', 1, 'UNIDAD'),
    (176, 'Levadura fresca', 1, 'GRAMO'),
    (177, 'Levadura en polvo', 1, 'GRAMO'),
    (178, 'Bicarbonato sódico', 1, 'GRAMO'),
    (179, 'Chocolate negro', 1, 'KILO'),
    (180, 'Chocolate con leche', 1, 'KILO'),
    (181, 'Cacao en polvo', 1, 'KILO'),
    (182, 'Maicena', 1, 'KILO'),
    (183, 'Gelatina', 1, 'GRAMO'),
    (184, 'Vainilla', 1, 'GRAMO'),
    (185, 'Anís', 1, 'GRAMO'),
    (186, 'Sal', 1, 'KILO'),
    (187, 'Sal gorda', 1, 'KILO'),
    (188, 'Kétchup', 1, 'KILO'),
    (189, 'Salsa de soja', 1, 'LITRO'),
    (190, 'Pasta de pimiento choricero', 1, 'KILO'),
    (191, 'Aceituna verde', 1, 'KILO'),
    (192, 'Aceituna negra', 1, 'KILO'),
    (193, 'Alcaparra', 1, 'KILO'),
    (194, 'Pepinillo', 1, 'KILO'),
    (195, 'Cebolla frita crujiente', 1, 'KILO'),
    (196, 'Mermelada de fresa', 1, 'KILO'),
    (197, 'Mermelada de melocotón', 1, 'KILO'),
    (198, 'Dulce de leche', 1, 'KILO'),
    (199, 'Crema de cacao', 1, 'KILO'),
    (200, 'Crema catalana en polvo', 1, 'KILO');

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
INSERT INTO recipe_ingredient (recipe_id, ingredient_id, quantity) VALUES
    (1, 1, 1), (1, 2, 1),
    (2, 1, 1), (2, 7, 1),
    (3, 5, 1), (3, 3, 1),
    (4, 9, 1), (4, 10, 1),
    (5, 2, 1), (5, 3, 1),
    (6, 4, 1), (6, 6, 1),
    (7, 10, 1),
    (8, 7, 1), (8, 3, 1),
    (9, 1, 1),
    (10, 5, 1), (10, 6, 1);
    
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

ALTER SEQUENCE recipe_seq RESTART WITH 100;
ALTER SEQUENCE ingredient_seq RESTART WITH 100;
ALTER SEQUENCE category_seq RESTART WITH 100;
ALTER SEQUENCE shopping_cart_seq RESTART WITH 100;
ALTER SEQUENCE favorites_seq RESTART WITH 100;
