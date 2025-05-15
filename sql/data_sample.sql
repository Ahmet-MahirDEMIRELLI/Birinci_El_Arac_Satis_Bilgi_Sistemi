INSERT INTO customer (email, password_customer, first_name, last_name, phone_number, gender, age, profession, income_level, city)
VALUES 
('ayse@example.com', 'hashed_pass1', 'Ayşe', 'Demir', '05551112233', 'female', 30, 'Mühendis', 'medium', 'İstanbul'),
('mehmet@example.com', 'hashed_pass2', 'Mehmet', 'Yılmaz', '05553334455', 'male', 40, 'Doktor', 'high', 'Ankara'),
('zeynep@example.com', 'hashed_pass3', 'Zeynep', 'Kaya', '05552223344', 'female', 25, 'Öğrenci', 'low', 'İzmir'),
('ahmet@example.com', 'hashed_pass4', 'Ahmet', 'Çetin', '05443334455', 'male', 35, 'Avukat', 'medium', 'Bursa'),
('elif@example.com', 'hashed_pass5', 'Elif', 'Koç', '05441112233', 'female', 28, 'Grafiker', 'medium', 'Antalya');

/*
INSERT INTO admin (email, password_admin, first_name, last_name)
VALUES 
('admin1@example.com', 'hashed_admin1', 'Zeynep', 'Yılmaz'),
('admin2@example.com', 'hashed_admin2', 'Ali', 'Toprak'),
('admin3@example.com', 'hashed_admin3', 'Selin', 'Acar'),
('admin4@example.com', 'hashed_admin4', 'Murat', 'Demir'),
('admin5@example.com', 'hashed_admin5', 'Esra', 'Yıldız');
*/

INSERT INTO warehouse_or_dealer (email, password, type)
VALUES 
('dealer@example.com', 'hashed_dealer', 'DEALER'),
('warehouse@example.com', 'hashed_warehouse', 'WAREHOUSE');

/* Birden fazla bayi veya depo olucaksa stock tablosunda location_type değil warehouse_or_dealer'I işararet eden location_id foreignkey i tutulmalı
('dealer2@example.com', 'hashed_dealer2', 'DEALER'),
('warehouse2@example.com', 'hashed_warehouse2', 'WAREHOUSE');
*/

INSERT INTO vehicle (brand, model, year, package, price)
VALUES 
('Renault', 'Clio', 2023, 'Touch', 450000.00),
('Toyota', 'Corolla', 2024, 'Flame', 650000.00),
('Hyundai', 'i20', 2023, 'Elite', 520000.00),
('Volkswagen', 'Golf', 2022, 'Style', 780000.00),
('Peugeot', '208', 2024, 'Active', 490000.00);

INSERT INTO stock (vehicle_id, location_type, quantity)
VALUES 
(1, 'warehouse', 10),
(2, 'dealer', 5),
(3, 'warehouse', 8),
(4, 'dealer', 4),
(5, 'warehouse', 1);

INSERT INTO sales (vehicle_id, user_id, sale_date, sale_price)
VALUES 
-- 2025 2. dönem (Nis-May-Haz)
(1, 1, '2025-04-10', 460000.00),
(2, 2, '2025-05-05', 625000.00),
(3, 3, '2025-05-15', 530000.00),
-- 2025 1. dönem (Oca-Şub-Mar)
(4, 4, '2025-01-20', 780000.00),
(5, 5, '2025-02-14', 495000.00),
(1, 3, '2025-03-22', 455000.00),
-- 2024 4. dönem (Eki-Kas-Ara)
(2, 1, '2024-10-30', 610000.00),
(3, 5, '2024-11-11', 520000.00),
(4, 4, '2024-12-05', 790000.00),
-- 2024 3. dönem (Tem-Ağu-Eyl)
(5, 2, '2024-07-22', 490000.00),
(1, 1, '2024-08-18', 460000.00),
(2, 3, '2024-09-09', 630000.00),
-- 2024 2. dönem (Nis-May-Haz)
(3, 4, '2024-05-05', 525000.00),
(4, 5, '2024-06-10', 785000.00),
(5, 2, '2024-06-25', 495000.00),
-- 2024 1. dönem (Oca-Şub-Mar)
(4, 4, '2024-01-20', 780000.00),
(5, 5, '2024-02-14', 495000.00),
(1, 3, '2024-03-22', 455000.00);


INSERT INTO requests (user_id, request_type, vehicle_id, request_date, status)
VALUES 
(1, 'test_drive', 1, '2025-04-10', 'accepted'),
(2, 'price_offer', 2, '2025-04-11', 'pending'),
(3, 'test_drive', 3, '2025-04-12', 'rejected'),
(4, 'price_offer', 4, '2025-04-13', 'accepted'),
(5, 'test_drive', 5, '2025-04-14', 'pending');

INSERT INTO requests (user_id, request_type, vehicle_id, request_date, status)
VALUES 
(2, 'price_offer', 2, '2025-05-20', 'accepted'),
(1, 'price_offer', 1, '2025-04-05', 'accepted'),
(4, 'price_offer', 4, '2025-03-15', 'accepted'),
(3, 'price_offer', 3, '2025-04-20', 'accepted'),
(5, 'price_offer', 5, '2025-04-28', 'accepted'),
(2, 'order', 2, '2025-05-01', 'accepted'),
(1, 'order', 1, '2025-04-12', 'accepted'),
(4, 'order', 4, '2025-04-17', 'accepted'),
(3, 'order', 3, '2025-04-22', 'accepted'),
(5, 'order', 5, '2025-05-03', 'accepted');

INSERT INTO price_offers (request_id, user_id, vehicle_id, offer_date, offered_price)
VALUES 
(4, 4, 4, '2025-04-25', 800000.00),
(6, 2, 2, '2025-04-26', 640000.00),
(7, 1, 1, '2025-04-08', 445000.00),
(8, 4, 4, '2025-03-20', 770000.00),
(9, 3, 3, '2025-04-21', 510000.00),
(10, 5, 5, '2025-05-04', 485000.00);


