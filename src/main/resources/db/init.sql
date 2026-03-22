-- Create database
CREATE DATABASE lobby_azar_cafetero;

-- Connect to the database
\c lobby_azar_cafetero;

-- Tables will be created automatically by Hibernate with spring.jpa.hibernate.ddl-auto=update

-- MANUAL
-- INSERT INTO buildings (id) VALUES ('00000000-0000-0000-0000-000000000001');
-- INSERT INTO floors (floor_id, building_id, name, icon, route, floor_order) VALUES 
--   (gen_random_uuid(), '00000000-0000-0000-0000-000000000001', 'Parques', '🎲', '/parques', 1),
--   (gen_random_uuid(), '00000000-0000-0000-0000-000000000001', 'Brisca', '🃏', '/brisca', 2);
