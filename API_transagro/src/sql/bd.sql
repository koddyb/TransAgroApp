-- Création de la base de données TransAgro
CREATE DATABASE IF NOT EXISTS transagro_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE transagro_db;


-- Table pour les chauffeurs (drivers)
CREATE TABLE IF NOT EXISTS drivers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'driver') NOT NULL DEFAULT 'driver',
    ville VARCHAR(100) NOT NULL
);


-- Table pour les livraisons (deliveries)
CREATE TABLE IF NOT EXISTS deliveries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    address VARCHAR(255) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    delivered TINYINT(1) NOT NULL DEFAULT 0,
    driver_id INT DEFAULT NULL,
    ville VARCHAR(100) NOT NULL,
    FOREIGN KEY (driver_id) REFERENCES drivers(id)
);


-- -- Table pour les véhicules
-- CREATE TABLE IF NOT EXISTS vehicles (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     name VARCHAR(100) NOT NULL
-- );

-- -- Table pour l'assignation véhicule/chauffeur
-- CREATE TABLE IF NOT EXISTS driver_vehicles (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     driver_id INT NOT NULL,
--     vehicle_id INT NOT NULL,
--     FOREIGN KEY (driver_id) REFERENCES drivers(id),
--     FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
-- );

INSERT INTO drivers (name, email, password, role, ville) VALUES
('Jean Dupont', 'jean.dupont@example.com', 'password', 'driver', 'Paris'),
('Alice Martin', 'alice.martin@example.com', 'password', 'admin', 'Lyon'),
('Bob Leroy', 'bob.leroy@example.com', 'password', 'driver', 'Lyon'),
('Marie Curie', 'marie.curie@example.com', 'password', 'driver', 'Marseille'),
('Luc Durant', 'luc.durant@example.com', 'password', 'driver', 'Paris');

-- Livraisons non affectées
INSERT INTO deliveries (address, latitude, longitude, delivered, driver_id, ville) VALUES
('10 Rue de Paris, 75001 Paris', 48.8566, 2.3522, 0, NULL, 'Paris'),
('15 Rue de Rivoli, 75004 Paris', 48.8570, 2.3525, 0, NULL, 'Paris'),
('100 Rue de la République, 69002 Lyon', 45.7597, 4.8422, 0, NULL, 'Lyon'),
('50 Avenue Jean Jaurès, 69007 Lyon', 45.7578, 4.8357, 0, NULL, 'Lyon'),
('20 Boulevard de Marseille, 13001 Marseille', 43.2965, 5.3698, 0, NULL, 'Marseille'),
('30 Rue de la Liberté, 13002 Marseille', 43.2965, 5.3698, 0, NULL, 'Marseille'),
('25 Avenue des Champs-Élysées, 75008 Paris', 48.8698, 2.3073, 0, NULL, 'Paris'),
('40 Rue de la Paix, 75002 Paris', 48.8688, 2.3314, 0, NULL, 'Paris'),
('60 Rue de la République, 69001 Lyon', 45.7640, 4.8357, 0, NULL, 'Lyon'),
('70 Boulevard de la Libération, 13003 Marseille', 43.2986, 5.3759, 0, NULL, 'Marseille');

-- Livraison déjà affectée (pour vérification)
INSERT INTO deliveries (address, latitude, longitude, delivered, driver_id, ville) VALUES
('5 Avenue de la Liberté, 75008 Paris', 48.8708, 2.3078, 0, 1, 'Paris'),
('10 Rue de la République, 69001 Lyon', 45.7640, 4.8357, 0, 3, 'Lyon'),
('15 Boulevard de la Liberté, 13001 Marseille', 43.2965, 5.3698, 0, 4, 'Marseille'),
('20 Avenue des Champs-Élysées, 75008 Paris', 48.8698, 2.3073, 0, 1, 'Paris'),
('25 Rue de la Paix, 75002 Paris', 48.8688, 2.3314, 0, 1, 'Paris');
