-- =============================================
--  DROP TABLE STATEMENTS
-- =============================================
DROP TABLE IF EXISTS customer_types CASCADE;
DROP TABLE IF EXISTS pet_types CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS pets CASCADE;
DROP TABLE IF EXISTS sitter_profiles CASCADE;
DROP TABLE IF EXISTS sitter_availability CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;

-- =============================================
--  CREATE TABLE STATEMENTS
-- =============================================
-- 1. Customer Types Table
CREATE TABLE customer_types (
    id SERIAL PRIMARY KEY,
    type_name VARCHAR(20) UNIQUE NOT NULL, -- 'OWNER', 'SITTER', 'BOTH'
    description TEXT
);

-- 2. Pet Types Table
CREATE TABLE pet_types (
    id SERIAL PRIMARY KEY,
    type_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT
);

-- 3. Users Table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    user_name VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) CHECK (role IN ('CUSTOMER', 'ADMIN')) NOT NULL,
    customer_type VARCHAR(20) CHECK (customer_type IN ('OWNER', 'SITTER', 'BOTH')),
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    address TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,

    CONSTRAINT customer_type_check CHECK (
        (role = 'CUSTOMER' AND customer_type IS NOT NULL) OR
        (role = 'ADMIN' AND customer_type IS NULL)
    )
);

-- 4. Pets Table
CREATE TABLE pets (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INTEGER CHECK (age >= 0),
    breed VARCHAR(100),
    weight DECIMAL(5,2),
    special_instructions TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    owner_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type_id INTEGER NOT NULL REFERENCES pet_types(id) ON DELETE RESTRICT
);

-- 5. Sitter Profiles Table (for sitters' professional info)
CREATE TABLE sitter_profiles (
    id SERIAL PRIMARY KEY,
    user_id INTEGER UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    hourly_rate DECIMAL(8,2) NOT NULL CHECK (hourly_rate > 0),
    experience_years INTEGER DEFAULT 0,
    bio TEXT,
    is_verified BOOLEAN DEFAULT FALSE,
    max_pets_per_booking INTEGER DEFAULT 1,
    available_services TEXT, -- JSON or comma-separated
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. Sitter Availability Table (for blocking availability)
CREATE TABLE sitter_availability (
    id SERIAL PRIMARY KEY,
    sitter_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    available_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_blocked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(sitter_id, available_date, start_time)
);

-- 7. Bookings Table
CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(20) CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED')) DEFAULT 'PENDING',
    total_cost DECIMAL(10, 2),
    special_requests TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    pet_id INTEGER NOT NULL REFERENCES pets(id) ON DELETE CASCADE,
    owner_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    sitter_id INTEGER NOT NULL REFERENCES users(id) ON DELETE RESTRICT
);


-- =============================================
--  INSERT STATEMENTS
-- =============================================

-- 1. CUSTOMER TYPES TABLE (owner -- sitter -- both)
INSERT INTO customer_types (id, type_name, description) VALUES
    (1, 'OWNER', 'Pet owners who need sitting services'),
    (2, 'SITTER', 'Professional pet sitters providing services'),
    (3, 'BOTH', 'Users who are both pet owners and sitters');


-- 2. PET TYPES TABLE
INSERT INTO pet_types (id, type_name, description) VALUES
    (1, 'Dog', 'Domestic canines of all breeds and sizes'),
    (2, 'Cat', 'Domestic felines including indoor and outdoor cats'),
    (3, 'Bird', 'Pet birds including parrots, canaries, and finches'),
    (4, 'Fish', 'Aquarium fish requiring feeding and tank maintenance'),
    (5, 'Rabbit', 'Domestic rabbits kept as pets'),
    (6, 'Hamster', 'Small rodent pets including Syrian and dwarf hamsters'),
    (7, 'Guinea Pig', 'Cavy pets requiring daily care and interaction'),
    (8, 'Reptile', 'Lizards, snakes, and other reptilian pets'),
    (9, 'Ferret', 'Domestic ferrets requiring specialized care'),
    (10, 'Turtle', 'Pet turtles and tortoises'),
    (11, 'Horse', 'Equine animals requiring specialized care'),
    (12, 'Goat', 'Pet goats and small livestock'),
    (13, 'Pig', 'Pot-bellied pigs and other domestic pigs'),
    (14, 'Chinchilla', 'Small furry pets with special temperature needs'),
    (15, 'Hedgehog', 'Exotic small pets with unique care requirements'),
    (16, 'Sugar Glider', 'Small marsupial pets requiring specialized diet'),
    (17, 'Parrot', 'Large speaking birds with complex social needs'),
    (18, 'Cockatiel', 'Medium-sized crested parrots'),
    (19, 'Goldfish', 'Common aquarium fish requiring basic care'),
    (20, 'Bearded Dragon', 'Popular reptilian pets requiring heat lamps'),
    (21, 'Other', 'Other types of domestic pets');


-- 3. USERS TABLE
INSERT INTO users (id, user_name, email, password, role, customer_type, first_name, last_name, phone_number, address) VALUES
    (1, 'john_doe', 'john.doe@email.com', 'password123', 'CUSTOMER', 'OWNER', 'John', 'Doe', '555-0101', '123 Main St, Toronto, ON'),
    (2, 'jane_smith', 'jane.smith@email.com', 'password123', 'CUSTOMER', 'SITTER', 'Jane', 'Smith', '555-0102', '456 Oak Ave, Toronto, ON'),
    (3, 'mike_wilson', 'mike.wilson@email.com', 'password123', 'CUSTOMER', 'BOTH', 'Mike', 'Wilson', '555-0103', '789 Pine Rd, Toronto, ON'),
    (4, 'sarah_brown', 'sarah.brown@email.com', 'password123', 'CUSTOMER', 'OWNER', 'Sarah', 'Brown', '555-0104', '321 Elm St, Toronto, ON'),
    (5, 'david_jones', 'david.jones@email.com', 'password123', 'CUSTOMER', 'SITTER', 'David', 'Jones', '555-0105', '654 Maple Dr, Toronto, ON'),
    (6, 'lisa_garcia', 'lisa.garcia@email.com', 'password123', 'CUSTOMER', 'BOTH', 'Lisa', 'Garcia', '555-0106', '987 Cedar Ln, Toronto, ON'),
    (7, 'tom_miller', 'tom.miller@email.com', 'password123', 'CUSTOMER', 'OWNER', 'Tom', 'Miller', '555-0107', '147 Birch St, Toronto, ON'),
    (8, 'amy_davis', 'amy.davis@email.com', 'password123', 'CUSTOMER', 'SITTER', 'Amy', 'Davis', '555-0108', '258 Spruce Ave, Toronto, ON'),
    (9, 'chris_taylor', 'chris.taylor@email.com', 'password123', 'CUSTOMER', 'BOTH', 'Chris', 'Taylor', '555-0109', '369 Willow Rd, Toronto, ON'),
    (10, 'emma_white', 'emma.white@email.com', 'password123', 'CUSTOMER', 'OWNER', 'Emma', 'White', '555-0110', '741 Poplar St, Toronto, ON'),
    (11, 'alex_martin', 'alex.martin@email.com', 'password123', 'CUSTOMER', 'SITTER', 'Alex', 'Martin', '555-0111', '852 Ash Dr, Toronto, ON'),
    (12, 'olivia_lee', 'olivia.lee@email.com', 'password123', 'CUSTOMER', 'BOTH', 'Olivia', 'Lee', '555-0112', '963 Walnut Ln, Toronto, ON'),
    (13, 'ryan_clark', 'ryan.clark@email.com', 'password123', 'CUSTOMER', 'OWNER', 'Ryan', 'Clark', '555-0113', '159 Cherry Ave, Toronto, ON'),
    (14, 'sophie_hall', 'sophie.hall@email.com', 'password123', 'CUSTOMER', 'SITTER', 'Sophie', 'Hall', '555-0114', '357 Hickory St, Toronto, ON'),
    (15, 'jason_young', 'jason.young@email.com', 'password123', 'CUSTOMER', 'BOTH', 'Jason', 'Young', '555-0115', '486 Sycamore Rd, Toronto, ON'),
    (16, 'natalie_king', 'natalie.king@email.com', 'password123', 'CUSTOMER', 'OWNER', 'Natalie', 'King', '555-0116', '624 Beech Dr, Toronto, ON'),
    (17, 'kevin_scott', 'kevin.scott@email.com', 'password123', 'CUSTOMER', 'SITTER', 'Kevin', 'Scott', '555-0117', '735 Fir Ln, Toronto, ON'),
    (18, 'rachel_green', 'rachel.green@email.com', 'password123', 'CUSTOMER', 'BOTH', 'Rachel', 'Green', '555-0118', '846 Redwood Ave, Toronto, ON'),
    (19, 'admin_user', 'admin@petsitting.com', 'adminpassword123', 'ADMIN', NULL, 'Admin', 'User', '555-0119', '100 Admin Plaza, Toronto, ON'),
    (20, 'super_admin', 'superadmin@petsitting.com', 'adminpassword123', 'ADMIN', NULL, 'Super', 'Admin', '555-0120', '200 System St, Toronto, ON');


-- 4. PETS TABLE (20 INSERT STATEMENTS)
INSERT INTO pets (id, name, age, breed, weight, special_instructions, owner_id, type_id) VALUES
    (1, 'Buddy', 3, 'Golden Retriever', 65.50, 'Needs medication twice daily', 1, 1),
    (2, 'Whiskers', 2, 'Persian Cat', 8.20, 'Indoor only, very shy', 4, 2),
    (3, 'Charlie', 5, 'Labrador Mix', 55.30, 'Loves walks, good with kids', 7, 1),
    (4, 'Luna', 1, 'Siamese Cat', 6.80, 'Playful, needs interactive toys', 10, 2),
    (5, 'Max', 4, 'German Shepherd', 75.40, 'Well-trained, follows commands', 13, 1),
    (6, 'Bella', 6, 'Maine Coon', 12.10, 'Senior cat, gentle handling required', 16, 2),
    (7, 'Rocky', 2, 'Bulldog', 45.60, 'Short walks only, breathing issues', 3, 1),
    (8, 'Mittens', 3, 'Domestic Shorthair', 9.50, 'Loves sunbathing by windows', 6, 2),
    (9, 'Zeus', 7, 'Great Dane', 120.80, 'Giant breed, gentle giant personality', 9, 1),
    (10, 'Princess', 4, 'Ragdoll Cat', 10.30, 'Very docile, enjoys brushing', 12, 2),
    (11, 'Scout', 1, 'Border Collie', 35.70, 'High energy, needs mental stimulation', 15, 1),
    (12, 'Shadow', 5, 'Black Cat', 7.90, 'Outdoor cat, independent nature', 18, 2),
    (13, 'Duke', 8, 'Rottweiler', 85.20, 'Senior dog, joint supplements needed', 1, 1),
    (14, 'Cleo', 2, 'Egyptian Mau', 8.40, 'Fast runner, needs secure enclosure', 4, 2),
    (15, 'Bear', 3, 'Newfoundland', 110.60, 'Water lover, drools frequently', 7, 1),
    (16, 'Ginger', 4, 'Orange Tabby', 11.20, 'Food motivated, portion control', 10, 2),
    (17, 'Thor', 2, 'Husky', 60.90, 'Escape artist, secure fencing required', 13, 1),
    (18, 'Patches', 6, 'Calico Cat', 9.80, 'Three-legged, special mobility needs', 16, 2),
    (19, 'Ace', 5, 'Pit Bull Mix', 55.40, 'Friendly but strong, experienced handler', 3, 1),
    (20, 'Smokey', 1, 'Russian Blue', 7.60, 'Quiet cat, prefers calm environments', 6, 2);


-- 5. SITTER PROFILES TABLE (20 INSERT STATEMENTS)
-- Note: Only users with customer_type 'SITTER' or 'BOTH' can have profiles
INSERT INTO sitter_profiles (id, user_id, hourly_rate, experience_years, bio, is_verified, max_pets_per_booking, available_services) VALUES
    (1, 2, 25.00, 5, 'Experienced pet sitter with veterinary background', TRUE, 3, 'Walking, Feeding, Overnight Care'),
    (2, 5, 20.00, 3, 'Dog trainer offering professional pet care', TRUE, 2, 'Walking, Training, Feeding'),
    (3, 8, 22.50, 4, 'Animal lover with flexible schedule', TRUE, 4, 'Walking, Feeding, Grooming, Playtime'),
    (4, 11, 18.00, 2, 'College student passionate about animals', FALSE, 2, 'Walking, Feeding, Playtime'),
    (5, 14, 30.00, 8, 'Certified animal behaviorist', TRUE, 5, 'Behavior Training, Walking, Feeding, Medication'),
    (6, 17, 26.00, 6, 'Professional pet care provider', TRUE, 3, 'Walking, Feeding, Overnight Care, Grooming'),
    (7, 3, 24.00, 4, 'Pet owner and sitter with great references', TRUE, 3, 'Walking, Feeding, Playtime'),
    (8, 6, 21.00, 3, 'Reliable sitter with weekend availability', FALSE, 2, 'Walking, Feeding, Overnight Care'),
    (9, 9, 27.50, 7, 'Former veterinary assistant', TRUE, 4, 'Medication, Walking, Feeding, Emergency Care'),
    (10, 12, 19.00, 2, 'Stay-at-home parent loving pets', FALSE, 3, 'Walking, Feeding, Playtime'),
    (11, 15, 23.00, 5, 'Dog daycare owner offering home services', TRUE, 6, 'Walking, Feeding, Socialization, Training'),
    (12, 18, 28.00, 9, 'Retired veterinarian providing care', TRUE, 5, 'Medical Care, Walking, Feeding, Grooming');


-- 6. SITTER AVAILABILITY TABLE (20 INSERT STATEMENTS)
-- Note: Only users with customer_type 'SITTER' or 'BOTH' can have availability
INSERT INTO sitter_availability (id, sitter_id, available_date, start_time, end_time, is_blocked) VALUES
    (1, 2, '2025-08-05', '09:00', '17:00', FALSE),
    (2, 5, '2025-08-05', '08:00', '12:00', FALSE),
    (3, 8, '2025-08-05', '13:00', '19:00', FALSE),
    (4, 11, '2025-08-05', '10:00', '16:00', FALSE),
    (5, 14, '2025-08-05', '07:00', '15:00', FALSE),
    (6, 17, '2025-08-06', '09:00', '18:00', FALSE),
    (7, 3, '2025-08-06', '08:00', '14:00', FALSE),
    (8, 6, '2025-08-06', '12:00', '20:00', FALSE),
    (9, 9, '2025-08-06', '06:00', '12:00', FALSE),
    (10, 12, '2025-08-06', '14:00', '18:00', FALSE),
    (11, 15, '2025-08-07', '10:00', '16:00', FALSE),
    (12, 18, '2025-08-07', '08:00', '17:00', FALSE),
    (13, 2, '2025-08-07', '11:00', '15:00', TRUE),
    (14, 5, '2025-08-07', '09:00', '13:00', FALSE),
    (15, 8, '2025-08-08', '14:00', '20:00', FALSE),
    (16, 11, '2025-08-08', '08:00', '12:00', FALSE),
    (17, 14, '2025-08-08', '16:00', '22:00', FALSE),
    (18, 17, '2025-08-08', '07:00', '11:00', FALSE),
    (19, 3, '2025-08-09', '13:00', '19:00', FALSE),
    (20, 6, '2025-08-09', '09:00', '15:00', TRUE);


-- 7. BOOKINGS TABLE (20 INSERT STATEMENTS)
INSERT INTO bookings (id, booking_date, start_time, end_time, status, total_cost, special_requests, pet_id, owner_id, sitter_id) VALUES
    (1, '2025-08-10', '09:00', '12:00', 'CONFIRMED', 75.00, 'Please give medication at 10 AM', 1, 1, 2),
    (2, '2025-08-11', '14:00', '18:00', 'PENDING', 80.00, 'Extra playtime needed', 3, 4, 5),
    (3, '2025-08-12', '08:00', '16:00', 'CONFIRMED', 180.00, 'Full day care, includes feeding', 5, 7, 8),
    (4, '2025-08-13', '10:00', '13:00', 'COMPLETED', 54.00, 'Short walks only', 7, 10, 11),
    (5, '2025-08-14', '07:00', '19:00', 'CONFIRMED', 360.00, 'Overnight care required', 9, 13, 14),
    (6, '2025-08-15', '15:00', '20:00', 'PENDING', 130.00, 'Evening walk and feeding', 11, 16, 17),
    (7, '2025-08-16', '09:00', '14:00', 'CANCELLED', 120.00, 'Owner cancelled due to travel change', 2, 3, 6),
    (8, '2025-08-17', '11:00', '17:00', 'CONFIRMED', 126.00, 'Grooming included', 4, 6, 9),
    (9, '2025-08-18', '08:00', '12:00', 'COMPLETED', 110.00, 'Senior dog, gentle care', 6, 9, 12),
    (10, '2025-08-19', '13:00', '16:00', 'PENDING', 57.00, 'High energy dog, long walk', 8, 12, 15),
    (11, '2025-08-20', '16:00', '21:00', 'CONFIRMED', 140.00, 'Evening care, dinner included', 10, 15, 18),
    (12, '2025-08-21', '07:00', '11:00', 'COMPLETED', 100.00, 'Morning routine established', 12, 18, 3),
    (13, '2025-08-22', '12:00', '18:00', 'CONFIRMED', 147.00, 'Multiple pets, package deal', 13, 1, 2),
    (14, '2025-08-23', '09:00', '15:00', 'PENDING', 123.00, 'Socialization with other pets', 14, 4, 5),
    (15, '2025-08-24', '14:00', '20:00', 'CONFIRMED', 132.00, 'Outdoor adventure time', 15, 7, 8),
    (16, '2025-08-25', '10:00', '14:00', 'COMPLETED', 76.00, 'Indoor playtime focus', 16, 10, 11),
    (17, '2025-08-26', '08:00', '17:00', 'CONFIRMED', 279.00, 'Full day with training session', 17, 13, 14),
    (18, '2025-08-27', '15:00', '19:00', 'PENDING', 104.00, 'Special needs attention', 18, 16, 17),
    (19, '2025-08-28', '11:00', '16:00', 'CANCELLED', 105.00, 'Sitter emergency cancellation', 19, 3, 6),
    (20, '2025-08-29', '09:00', '13:00', 'CONFIRMED', 86.00, 'Quiet indoor cat sitting', 20, 6, 9);

-- select * from customer_types;
-- select * from pet_types;
-- select * from users;
-- select * from pets;
-- select * from sitter_profiles;
-- select * from sitter_availability;
-- select * from bookings;


