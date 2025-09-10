-- =============================================
--  DROP TABLE STATEMENTS
-- =============================================
DROP TABLE IF EXISTS customer_types CASCADE;
DROP TABLE IF EXISTS pet_types CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS booking_statuses CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS pets CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;

-- =============================================
--  CREATE TABLE STATEMENTS
-- =============================================
-- 1. Customer Types Table
CREATE TABLE customer_types (
    id BIGSERIAL PRIMARY KEY,
    type_name VARCHAR(20) UNIQUE NOT NULL -- 'OWNER', 'SITTER', 'BOTH'
);

-- 2. Pet Types Table
CREATE TABLE pet_types (
    id BIGSERIAL PRIMARY KEY,
    type_name VARCHAR(50) UNIQUE NOT NULL
);

-- 3. Roles Table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(20) UNIQUE NOT NULL -- e.g. 'CUSTOMER', 'ADMIN'
);

-- 4. Booking Statuses Table
CREATE TABLE booking_statuses (
    id BIGSERIAL PRIMARY KEY,
    status_name VARCHAR(20) UNIQUE NOT NULL -- 'PENDING', 'CONFIRMED', etc.
);

-- 5. Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE RESTRICT,
    customer_type_id BIGINT REFERENCES customer_types(id) ON DELETE RESTRICT,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    address TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT role_customer_type_check CHECK (
        (customer_type_id IS NOT NULL AND role_id = 1) OR -- assuming role_id 1 = 'CUSTOMER'
        (customer_type_id IS NULL AND role_id != 1)
    )
);

-- 6. Pets Table
CREATE TABLE pets (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INTEGER CHECK (age >= 0),
    breed VARCHAR(100),
    weight DECIMAL(5,2),
    special_instructions TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type_id BIGINT NOT NULL REFERENCES pet_types(id) ON DELETE RESTRICT
);

-- 7. Bookings Table
CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status_id BIGINT NOT NULL REFERENCES booking_statuses(id),
    total_cost DECIMAL(10, 2),
    special_requests TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    pet_id BIGINT NOT NULL REFERENCES pets(id) ON DELETE CASCADE,
    owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    sitter_id BIGINT REFERENCES users(id) ON DELETE RESTRICT
);

-- =============================================
--  INSERT STATEMENTS FOR PET SITTING DATABASE
-- =============================================

-- 1. Insert Customer Types
INSERT INTO customer_types (id, type_name) VALUES
(1, 'OWNER'),
(2, 'SITTER');

-- 2. Insert Pet Types
INSERT INTO pet_types (id, type_name) VALUES
(1, 'Dog'),
(2, 'Cat'),
(3, 'Bird'),
(4, 'Fish'),
(5, 'Rabbit'),
(6, 'Hamster'),
(7, 'Guinea Pig'),
(8, 'Reptile'),
(9, 'Ferret'),
(10, 'Chinchilla');

-- 3. Insert Roles
INSERT INTO roles (id, role_name) VALUES
(1, 'CUSTOMER'),
(2, 'ADMIN');

-- 4. Insert Booking Statuses
INSERT INTO booking_statuses (id, status_name) VALUES
(1, 'PENDING'),
(2, 'CONFIRMED'),
(3, 'IN_PROGRESS'),
(4, 'COMPLETED'),
(5, 'CANCELLED'),
(6, 'REJECTED');

-- 5. Insert Users 
-- customer: password
-- admin: adminpassword
INSERT INTO users (id, username, email, password, role_id, customer_type_id, first_name, last_name, phone_number, address) VALUES
(1, 'sarah_jones', 'sarah.jones@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 1, 'Sarah', 'Jones', '555-0101', '123 Oak Street, Toronto, ON M4B 1Y2'),
(2, 'mike_smith', 'mike.smith@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 2, 'Mike', 'Smith', '555-0102', '456 Maple Ave, Toronto, ON M5V 3A8'),
(3, 'emma_wilson', 'emma.wilson@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 2, 'Emma', 'Wilson', '555-0103', '789 Pine Road, Mississauga, ON L5B 4K9'),
(4, 'david_brown', 'david.brown@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 1, 'David', 'Brown', '555-0104', '321 Elm Street, Toronto, ON M6H 2X5'),
(5, 'lisa_davis', 'lisa.davis@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 2, 'Lisa', 'Davis', '555-0105', '654 Cedar Lane, Brampton, ON L6T 5R3'),
(6, 'james_miller', 'james.miller@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 2, 'James', 'Miller', '555-0106', '987 Birch Circle, Toronto, ON M4C 5E7'),
(7, 'anna_garcia', 'anna.garcia@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 1, 'Anna', 'Garcia', '555-0107', '147 Spruce Way, Vaughan, ON L4J 8M2'),
(8, 'robert_martinez', 'robert.martinez@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 2, 'Robert', 'Martinez', '555-0108', '258 Willow Drive, Richmond Hill, ON L4B 3N9'),
(9, 'jessica_anderson', 'jessica.anderson@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 1, 'Jessica', 'Anderson', '555-0109', '369 Ash Boulevard, Markham, ON L3P 7K5'),
(10, 'kevin_taylor', 'kevin.taylor@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 2, 'Kevin', 'Taylor', '555-0110', '741 Poplar Street, Toronto, ON M6P 1R8'),
(11, 'michelle_thomas', 'michelle.thomas@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 2, 'Michelle', 'Thomas', '555-0111', '852 Cherry Lane, Etobicoke, ON M9C 4T6'),
(12, 'christopher_white', 'christopher.white@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 1, 'Christopher', 'White', '555-0112', '963 Walnut Avenue, North York, ON M2N 6H3'),
(13, 'amanda_harris', 'amanda.harris@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 2, 'Amanda', 'Harris', '555-0113', '159 Hickory Road, Scarborough, ON M1B 5G7'),
(14, 'daniel_clark', 'daniel.clark@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 1, 'Daniel', 'Clark', '555-0114', '357 Chestnut Drive, Toronto, ON M5R 2L4'),
(15, 'stephanie_lewis', 'stephanie.lewis@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 2, 'Stephanie', 'Lewis', '555-0115', '486 Beech Street, Mississauga, ON L5H 3J8'),
(16, 'matthew_walker', 'matthew.walker@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 2, 'Matthew', 'Walker', '555-0116', '672 Sycamore Place, Burlington, ON L7M 4P2'),
(17, 'nicole_hall', 'nicole.hall@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 1, 'Nicole', 'Hall', '555-0117', '793 Magnolia Court, Oakville, ON L6H 6W1'),
(18, 'ryan_allen', 'ryan.allen@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 2, 'Ryan', 'Allen', '555-0118', '814 Dogwood Trail, Milton, ON L9T 7X5'),
(19, 'lauren_young', 'lauren.young@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 1, 'Lauren', 'Young', '555-0119', '925 Redwood Heights, Georgetown, ON L7G 4R9'),
(20, 'brandon_king', 'brandon.king@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 2, 'Brandon', 'King', '555-0120', '136 Cypress Gardens, Ajax, ON L1S 3M7'),
(21, 'samantha_wright', 'samantha.wright@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 2, 'Samantha', 'Wright', '555-0121', '247 Fir Ridge, Pickering, ON L1V 2K4'),
(22, 'tyler_lopez', 'tyler.lopez@email.com', '$2a$06$L2s.dzicmhNs4unAFZP1ZOKqTxAqOsw.tkKBLDfq8TDDr8SjQb9ta', 1, 1, 'Tyler', 'Lopez', '555-0122', '358 Hemlock Valley, Whitby, ON L1N 8P6'),
(23, 'admin_user1', 'admin1@petsitting.com', '$2a$06$.Uagk0RgXqvzoFwfE2c0BOIwC8dKtLdJQDR4m67VGU/0G1L7llI/q', 2, NULL, 'Admin1', 'Admin1', '555-0001', '1 Admin Plaza, Toronto, ON M5H 2N2'),
(24, 'admin_user2', 'admin2@petsitting.com', '$2a$06$.Uagk0RgXqvzoFwfE2c0BOIwC8dKtLdJQDR4m67VGU/0G1L7llI/q', 2, NULL, 'Admin2', 'Admin2', '555-0002', '1 Admin Plaza, Toronto, ON M5H 2N2');

-- 6. Insert Pets (20+ records)
INSERT INTO pets (id, name, age, breed, weight, special_instructions, owner_id, type_id) VALUES
(1, 'Buddy', 3, 'Golden Retriever', 65.50, 'Needs medication twice daily. Very friendly with other dogs.', 1, 1),
(2, 'Whiskers', 2, 'Persian Cat', 8.20, 'Indoor cat only. Loves to hide under beds.', 4, 2),
(3, 'Charlie', 5, 'Labrador Mix', 55.30, 'Energetic, needs long walks. Good with children.', 7, 1),
(4, 'Fluffy', 1, 'Maine Coon', 12.80, 'Requires special diet food. Very social.', 9, 2),
(5, 'Max', 4, 'German Shepherd', 75.00, 'Well-trained, knows basic commands. Protective but gentle.', 14, 1),
(6, 'Luna', 6, 'Siamese Cat', 7.50, 'Vocal cat, likes to "talk". Prefers quiet environments.', 17, 2),
(7, 'Rocky', 2, 'Bulldog', 45.20, 'Breathing issues, avoid overexertion. Very gentle.', 19, 1),
(8, 'Mittens', 3, 'Tabby Cat', 9.10, 'Outdoor/indoor cat. Comes when called.', 22, 2),
(9, 'Duke', 7, 'Rottweiler', 85.60, 'Senior dog, needs gentle exercise. Great temperament.', 1, 1),
(10, 'Princess', 1, 'Ragdoll Cat', 6.80, 'Very docile, loves being held. Indoor only.', 4, 2),
(11, 'Pip', 2, 'Canary', 0.05, 'Sings in the morning. Cage needs daily cleaning.', 7, 3),
(12, 'Goldie', 1, 'Goldfish', 0.10, 'Feed twice daily, small portions. Tank temperature important.', 9, 4),
(13, 'Buster', 4, 'Beagle', 28.40, 'Food motivated, great for training. Loves walks.', 14, 1),
(14, 'Shadow', 5, 'Black Cat', 10.20, 'Very independent, low maintenance. Indoor/outdoor.', 17, 2),
(15, 'Rex', 3, 'German Pointer', 62.70, 'High energy, needs lots of exercise. Well-behaved.', 19, 1),
(16, 'Coco', 2, 'Cockatiel', 0.08, 'Talks and whistles. Likes interaction and music.', 22, 3),
(17, 'Nemo', 2, 'Betta Fish', 0.02, 'Aggressive to other fish. Keep alone. Weekly water changes.', 1, 4),
(18, 'Daisy', 6, 'Cocker Spaniel', 32.10, 'Senior dog, arthritis medication needed. Very sweet.', 4, 1),
(19, 'Tiger', 4, 'Orange Tabby', 11.50, 'Playful and active. Loves laser pointers and toys.', 7, 2),
(20, 'Ace', 1, 'Husky Puppy', 25.30, 'Very energetic puppy, needs constant supervision.', 9, 1),
(21, 'Snowball', 3, 'White Rabbit', 3.20, 'Litter trained. Needs fresh vegetables daily.', 14, 5),
(22, 'Peanut', 2, 'Hamster', 0.15, 'Nocturnal, active at night. Clean cage weekly.', 17, 6),
(23, 'Bella', 5, 'Border Collie', 48.90, 'Extremely intelligent, needs mental stimulation.', 19, 1),
(24, 'Smokey', 7, 'Russian Blue', 8.90, 'Quiet and reserved. Prefers routine and calm environment.', 22, 2);

-- 7. Insert Bookings (20+ records)
INSERT INTO bookings (id, booking_date, start_time, end_time, status_id, total_cost, special_requests, pet_id, owner_id, sitter_id) VALUES
(1, '2025-08-05', '09:00:00', '12:00:00', 2, 75.00, 'Please give Buddy his medication at 10 AM', 1, 1, 2),
(2, '2025-08-05', '14:00:00', '18:00:00', 2, 120.00, 'Whiskers likes to hide - please be patient', 2, 4, 3),
(3, '2025-08-06', '10:00:00', '15:00:00', 1, 125.00, 'Charlie needs a long walk in the park', 3, 7, 2),
(4, '2025-08-06', '16:00:00', '19:00:00', 2, 96.00, 'Fluffy is very social and loves attention', 4, 9, 8),
(5, '2025-08-07', '08:00:00', '11:00:00', 4, 105.00, 'Max knows sit, stay, and come commands', 5, 14, 6),
(6, '2025-08-07', '13:00:00', '16:00:00', 2, 78.00, 'Luna will talk to you - she is very vocal', 6, 17, 11),
(7, '2025-08-08', '09:00:00', '13:00:00', 3, 112.00, 'Rocky gets tired easily, short walks only', 7, 19, 8),
(8, '2025-08-08', '15:00:00', '18:00:00', 2, 78.00, 'Mittens comes when you call her name', 8, 22, 13),
(9, '2025-08-09', '08:00:00', '11:00:00', 1, 60.00, 'Duke is gentle but moves slowly due to age', 9, 1, 5),
(10, '2025-08-09', '14:00:00', '17:00:00', 2, 96.00, 'Princess loves being petted and held', 10, 4, 10),
(11, '2025-08-10', '10:00:00', '12:00:00', 2, 48.00, 'Clean Pips cage and refill food/water', 11, 7, 5),
(12, '2025-08-10', '15:00:00', '18:00:00', 4, 81.00, 'Feed Goldie small amount twice during visit', 12, 9, 20),
(13, '2025-08-11', '09:00:00', '14:00:00', 2, 160.00, 'Buster is food motivated - great for training', 13, 14, 6),
(14, '2025-08-11', '16:00:00', '19:00:00', 1, 96.00, 'Shadow is independent but likes some attention', 14, 17, 13),
(15, '2025-08-12', '11:00:00', '16:00:00', 2, 135.00, 'Rex needs lots of exercise and playtime', 15, 19, 8),
(16, '2025-08-12', '08:00:00', '11:00:00', 3, 63.00, 'Talk to Coco and play some music for her', 16, 22, 15),
(17, '2025-08-13', '10:00:00', '12:00:00', 2, 44.00, 'Feed Nemo and check water temperature', 17, 1, 10),
(18, '2025-08-13', '14:00:00', '17:00:00', 1, 81.00, 'Daisy needs her arthritis medication', 18, 4, 20),
(19, '2025-08-14', '09:00:00', '13:00:00', 2, 124.00, 'Tiger loves to play - bring laser pointer', 19, 7, 11),
(20, '2025-08-14', '15:00:00', '18:00:00', 5, 75.00, 'Ace is a puppy - needs constant watching', 20, 9, 2),
(21, '2025-08-15', '12:00:00', '15:00:00', 2, 78.00, 'Give Snowball fresh vegetables and hay', 21, 14, 13),
(22, '2025-08-15', '17:00:00', '19:00:00', 1, 48.00, 'Clean Peanuts cage - he is most active evening', 22, 17, 15),
(23, '2025-08-16', '08:00:00', '12:00:00', 2, 116.00, 'Bella needs mental stimulation - puzzle toys', 23, 19, 16),
(24, '2025-08-16', '14:00:00', '17:00:00', 4, 87.00, 'Smokey prefers quiet - no loud noises', 24, 22, 20),
(25, '2025-08-17', '10:00:00', '14:00:00', 1, 84.00, 'Second visit for Buddy - same medication schedule', 1, 1, 18),
(26, '2025-08-17', '16:00:00', '19:00:00', 2, 90.00, 'Charlie has been good - reward with treats', 3, 7, 21),
(27, '2025-08-18', '09:00:00', '15:00:00', 1, 180.00, 'Long day care for Max - include feeding', 5, 14, 20),
(28, '2025-08-18', '11:00:00', '13:00:00', 3, 54.00, 'Luna is getting used to you now', 6, 17, 8),
(29, '2025-08-19', '07:00:00', '11:00:00', 2, 124.00, 'Early morning care for Duke before work', 9, 1, 21),
(30,'2025-08-19', '15:00:00', '19:00:00', 1, 124.00, 'Extended play session for Rex - he loves fetch', 15, 19, 21);

-- Reset sequences to match the inserted data
SELECT setval('customer_types_id_seq', (SELECT MAX(id) FROM customer_types));
SELECT setval('pet_types_id_seq', (SELECT MAX(id) FROM pet_types));
SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));
SELECT setval('booking_statuses_id_seq', (SELECT MAX(id) FROM booking_statuses));
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('pets_id_seq', (SELECT MAX(id) FROM pets));
SELECT setval('bookings_id_seq', (SELECT MAX(id) FROM bookings));

-- Update constraint
ALTER TABLE bookings
    DROP CONSTRAINT IF EXISTS bookings_sitter_id_fkey;

ALTER TABLE bookings
    ADD CONSTRAINT bookings_sitter_id_fkey
        FOREIGN KEY (sitter_id) REFERENCES users(id) ON DELETE CASCADE;

--select
select * from users;