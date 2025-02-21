-- Insertar usuarios
INSERT INTO user_entity(id, username, password, email, avatar, is_verified, enabled)
values ('550e8400-e29b-41d4-a716-446655440000', 'usuario1', '{noop}contrasena1234', 'usuario1@example.com', 'image.jpg', true, true);
INSERT INTO user_entity(id, username, password, email, avatar, is_verified, enabled)
values ('6ba7b810-9dad-11d1-80b4-00c04fd430c8', 'usuario2', '{noop}contrasena5678', 'usuario2@example.com', 'image.jpg', true, true);

-- Insertar roles en la tabla intermedia user_roles (@ElementCollection)
INSERT INTO user_roles (user_id, role)
values('550e8400-e29b-41d4-a716-446655440000', 'USER');
INSERT INTO user_roles (user_id, role)
values('6ba7b810-9dad-11d1-80b4-00c04fd430c8', 'USER');