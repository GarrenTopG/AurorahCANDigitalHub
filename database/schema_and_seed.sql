-- =============================================================================
-- Aurorah CAN Digital Hub - Relational Database Schema & Sample Data (Phase 2)
-- Database Engine: MySQL / MariaDB / Compatible Relational DB
-- =============================================================================

CREATE DATABASE IF NOT EXISTS aurorah_can_db;
USE aurorah_can_db;

-- Drop tables in reverse order of foreign key dependencies if they exist
DROP TABLE IF EXISTS Donations;
DROP TABLE IF EXISTS CommunityNeeds;
DROP TABLE IF EXISTS Beneficiaries;
DROP TABLE IF EXISTS Volunteers;
DROP TABLE IF EXISTS Users;

-- -----------------------------------------------------------------------------
-- 1. Table: Users
-- Description: Core authentication and user profile table with encrypted passwords
-- -----------------------------------------------------------------------------
CREATE TABLE Users (
    UserID INT PRIMARY KEY AUTO_INCREMENT,
    FullName VARCHAR(100) NOT NULL,
    Email VARCHAR(150) UNIQUE NOT NULL,
    PasswordHash VARCHAR(255) NOT NULL,
    Role VARCHAR(20) DEFAULT 'User',
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- 2. Table: Volunteers
-- Description: Volunteer profiles linked 1:1 with Users
-- -----------------------------------------------------------------------------
CREATE TABLE Volunteers (
    VolunteerID INT PRIMARY KEY AUTO_INCREMENT,
    UserID INT UNIQUE NOT NULL,
    Phone VARCHAR(20) NOT NULL,
    Skills TEXT NOT NULL,
    Availability VARCHAR(50) NOT NULL,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- 3. Table: Beneficiaries
-- Description: Beneficiary profiles linked 1:1 with Users
-- -----------------------------------------------------------------------------
CREATE TABLE Beneficiaries (
    BeneficiaryID INT PRIMARY KEY AUTO_INCREMENT,
    UserID INT UNIQUE NOT NULL,
    HouseholdSize INT NOT NULL,
    Address TEXT NOT NULL,
    NeedCategory VARCHAR(50) NOT NULL,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- 4. Table: CommunityNeeds
-- Description: Community aid requests and social project initiatives
-- -----------------------------------------------------------------------------
CREATE TABLE CommunityNeeds (
    NeedID INT PRIMARY KEY AUTO_INCREMENT,
    Title VARCHAR(100) NOT NULL,
    Description TEXT NOT NULL,
    Location VARCHAR(100) NOT NULL,
    Priority VARCHAR(20) NOT NULL,
    Status VARCHAR(20) DEFAULT 'Open',
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- 5. Table: Donations
-- Description: Financial donation records linked to Users
-- -----------------------------------------------------------------------------
CREATE TABLE Donations (
    DonationID INT PRIMARY KEY AUTO_INCREMENT,
    UserID INT NOT NULL,
    Amount DECIMAL(10,2) NOT NULL,
    PaymentMethod VARCHAR(50) NOT NULL,
    TransactionRef VARCHAR(100) UNIQUE NOT NULL,
    DonatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- =============================================================================
-- SAMPLE DATA INSERTIONS (At least 10 records per table)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Populating Users Table (30 Users total)
-- Note: All PasswordHash entries are generated using standard BCrypt hashing.
-- -----------------------------------------------------------------------------
INSERT INTO Users (UserID, FullName, Email, PasswordHash, Role, CreatedAt) VALUES
(1, 'Sarah Jenkins', 'sarah.jenkins@aurorahcan.org', '$2b$12$PMfeQcsbwNoUo6fJicyXCu/im/4XVHKchQkCehNAJ/TuRxiBfarWK', 'Admin', '2025-01-15 08:00:00'),
(2, 'Marcus Vance', 'marcus.vance@aurorahcan.org', '$2b$12$LIwKT50jHuc/YkD/aK7SyeZ9APmHKJIa115pR6iH/ZtM5XcLL8oV.', 'Admin', '2025-01-15 08:00:00'),
(3, 'Elena Rostova', 'elena.rostova@volunteer.org', '$2b$12$NOTSsKBb6X0sW9ocHqt5rO546gcLK2z1lwFuOzdnghTSQQ3agQpq6', 'Volunteer', '2025-01-15 08:00:00'),
(4, 'David Kim', 'david.kim@volunteer.org', '$2b$12$fIx113rC3FGjwlSiq2ngSumpDWHC40.KYN5O7Fazsu3D6C3joh1OO', 'Volunteer', '2025-01-15 08:00:00'),
(5, 'Fatima Al-Mansoor', 'fatima.almansoor@volunteer.org', '$2b$12$VOkLZvanRegn2HT2SqUD0eSdZClZi8p5DTrVgPkWagY8bSILd2UJq', 'Volunteer', '2025-01-15 08:00:00'),
(6, 'James O''Connor', 'james.oconnor@volunteer.org', '$2b$12$5o9IdVyJ.1WVUJ.Ro4ihxufqpR9H7nOWsgGaADs9EAVwRqtycn.M6', 'Volunteer', '2025-01-15 08:00:00'),
(7, 'Aisha Patel', 'aisha.patel@volunteer.org', '$2b$12$NHlvDZXnaIj82bRBxGO7dOrYW4A7V/BWmvhoQ4xAP7E8LZjQSEvui', 'Volunteer', '2025-01-15 08:00:00'),
(8, 'Carlos Mendez', 'carlos.mendez@volunteer.org', '$2b$12$9QChQfNWQU/1v08rkf4PFOodr3FKC0C3psFtD0wCgZcu1K/cJhByC', 'Volunteer', '2025-01-15 08:00:00'),
(9, 'Chloe Bennett', 'chloe.bennett@volunteer.org', '$2b$12$R75FH2LwUa.sNhP98lyU.elllEwWtcDj6wucWcrrzgZluXH6RX2Yq', 'Volunteer', '2025-01-15 08:00:00'),
(10, 'Samuel Jackson', 'samuel.jackson@volunteer.org', '$2b$12$ImzarmSi65RPh5nQzrxsc.rTp9hCerTJdB.CpzHpD4tXl.t09JWcq', 'Volunteer', '2025-01-15 08:00:00'),
(11, 'Maya Lin', 'maya.lin@volunteer.org', '$2b$12$yvP/m3V0zibD00UnBOXN2uwNkaa7kElMWwnBgHPfBghblbW6.r6Qi', 'Volunteer', '2025-01-15 08:00:00'),
(12, 'Robert Chen', 'robert.chen@volunteer.org', '$2b$12$QuShEtsxVGNj7WJErhfdluhqcvwywHXpex9dTTVaq9vSQu.fcaDIi', 'Volunteer', '2025-01-15 08:00:00'),
(13, 'Grace Ndirangu', 'grace.ndirangu@beneficiary.org', '$2b$12$HFXzQqv/59lX68qhxPJF7OLAAJ4Hp8PW3OeRWog6VAo89t2DNF6J2', 'Beneficiary', '2025-01-15 08:00:00'),
(14, 'Joseph Mwangi', 'joseph.mwangi@beneficiary.org', '$2b$12$PMVUuwTuIZrLv4Gcckys3Oe9CckLGMDkwplSw0Tx6JyZ2sDyICFJ6', 'Beneficiary', '2025-01-15 08:00:00'),
(15, 'Maria Santos', 'maria.santos@beneficiary.org', '$2b$12$0qndIN4kIoDumTG9nsbceOIXS8RD62xtD/trVuY/1FLHAb2gCWHZ.', 'Beneficiary', '2025-01-15 08:00:00'),
(16, 'Kwame Mensah', 'kwame.mensah@beneficiary.org', '$2b$12$UR/SEglqn0tPAuWTwFug4.JIrNzvF0LAxLXi1UQSAgEJ/OdNsvXJ.', 'Beneficiary', '2025-01-15 08:00:00'),
(17, 'Amara Okafor', 'amara.okafor@beneficiary.org', '$2b$12$025u3BUGI7WDwvWjNbMpqe0xhH1zc9K9Wj00pACDo.6scmU2FLe3u', 'Beneficiary', '2025-01-15 08:00:00'),
(18, 'Ibrahim Diallo', 'ibrahim.diallo@beneficiary.org', '$2b$12$12IejllSb/k2Msa1/FbdweMcKuTniiqlvbODhGUMwM8mtbQXFej8m', 'Beneficiary', '2025-01-15 08:00:00'),
(19, 'Beatrice Wanjiku', 'beatrice.wanjiku@beneficiary.org', '$2b$12$Bdiir8OGqtlQnPnQYGhqDO2gVQeESLdgj/oTjajKC5y0mgoDf5JUu', 'Beneficiary', '2025-01-15 08:00:00'),
(20, 'Emmanuel Kiprop', 'emmanuel.kiprop@beneficiary.org', '$2b$12$aDoVj/ZVzlWkWTSH1QyqZutNHeLkMHgzMsLEe8ktQOJ0gLpQenO/a', 'Beneficiary', '2025-01-15 08:00:00'),
(21, 'Mercy Chebet', 'mercy.chebet@beneficiary.org', '$2b$12$p4RjxUtCWKAI.jNk/JnkK.he.RT5LzV1l3RbEpV/u03qZoOkGOvTO', 'Beneficiary', '2025-01-15 08:00:00'),
(22, 'Daniel Omondi', 'daniel.omondi@beneficiary.org', '$2b$12$R0qFf/UcfPoih/xWXpHVQOWer9.4em.dcY8JIvyuvF24fMNLLvRmK', 'Beneficiary', '2025-01-15 08:00:00'),
(23, 'Hope Foundation Trust', 'contact@hopefoundation.org', '$2b$12$vEZYkkH3XZ98txG3iIXDI.e1TfPAv72/wqxF7ZYG9kCCEEIUYW5eq', 'Donor', '2025-01-15 08:00:00'),
(24, 'Apex Capital Group', 'csr@apexcapital.com', '$2b$12$XIrGtqABpF7peS2o1oXzs.awN3rMzd/7l0CHLF2rVt4ApiqAhJDrS', 'Donor', '2025-01-15 08:00:00'),
(25, 'Dr. Elizabeth Taylor', 'elizabeth.taylor@medical.org', '$2b$12$cEIm5UlfvWGKVB9q2iNwMu7GE7UXm5g19mVpxf/GmF8rGffNDc5Qe', 'Donor', '2025-01-15 08:00:00'),
(26, 'Michael Chang', 'michael.chang@techdonor.io', '$2b$12$L5xdlVc53U3jFOA/MfGqjOJgNddQnDPnZYHZ4yM5Cz4c8zyVM2yOS', 'Donor', '2025-01-15 08:00:00'),
(27, 'Sophia Rodriguez', 'sophia.rodriguez@globalcharity.org', '$2b$12$0VthWWOv6DgkXt7foaNlVujKv5YMscrfhchs98y.3ivVCmEG.RC5G', 'Donor', '2025-01-15 08:00:00'),
(28, 'Alexander Wright', 'alex.wright@wrightventures.com', '$2b$12$g9sWm6b6izcFedXp.zzkGOsRO8QPufreQRTbt0Ofq2XZIZIZkXSAy', 'Donor', '2025-01-15 08:00:00'),
(29, 'Linda Scott', 'linda.scott@communityfund.org', '$2b$12$4yoi2VO4Yr2FK9zYNUI6AOJhwAVKK.iAsyWPnScd1N0RO1mtC7aVm', 'Donor', '2025-01-15 08:00:00'),
(30, 'TechForGood Org', 'grants@techforgood.org', '$2b$12$AejsMOpIKY6jPVJ8jlGE2eBNYhr7nUyN4jY4pBSxfpsr8WrW1zOHO', 'Donor', '2025-01-15 08:00:00');

-- -----------------------------------------------------------------------------
-- Populating Volunteers Table (10 Records)
-- -----------------------------------------------------------------------------
INSERT INTO Volunteers (VolunteerID, UserID, Phone, Skills, Availability) VALUES
(1, 3, '+254712345678', 'Emergency First Aid, Disaster Relief', 'Weekends'),
(2, 4, '+254723456789', 'Logistics, Cargo Transport, Inventory', 'Weekdays Morning'),
(3, 5, '+254734567890', 'Youth Mentoring, STEM Tutoring, Counseling', 'Flexible'),
(4, 6, '+254745678901', 'Medical Assistance, Nursing, Patient Care', 'Evenings'),
(5, 7, '+254756789012', 'Food Preparation, Nutritional Planning', 'Saturdays'),
(6, 8, '+254767890123', 'Technical Support, IT & Network Setup', 'Weekends'),
(7, 9, '+254778901234', 'Translation (Swahili, English, French)', 'On-call'),
(8, 10, '+254789012345', 'Heavy Vehicle Driving, Equipment Operator', 'Full-time'),
(9, 11, '+254790123456', 'Social Work, Community Outreach, Surveying', 'Weekdays Afternoon'),
(10, 12, '+254701234567', 'Event Coordination, Fundraising, PR', 'Sundays');

-- -----------------------------------------------------------------------------
-- Populating Beneficiaries Table (10 Records)
-- -----------------------------------------------------------------------------
INSERT INTO Beneficiaries (BeneficiaryID, UserID, HouseholdSize, Address, NeedCategory) VALUES
(1, 13, 5, '124 Kibera Drive, Sector 4, Nairobi', 'Food Security'),
(2, 14, 4, '88 Mathare North Area B, Nairobi', 'Educational Support'),
(3, 15, 6, '45 Mukuru Kwa Njenga, Zone A, Nairobi', 'Healthcare & Medicine'),
(4, 16, 3, '12 Korogocho Village, Block C, Nairobi', 'Shelter & Housing'),
(5, 17, 7, '305 Kawangware Ridge, Nairobi', 'Clean Water & Sanitation'),
(6, 18, 5, '71 Dagoretti Corner, Nairobi', 'Winter Warmth & Clothing'),
(7, 19, 4, '19 Dandora Phase 2, Nairobi', 'Child Nutrition'),
(8, 20, 8, '54 Baba Dogo Estate, Nairobi', 'Disaster Relief'),
(9, 21, 2, '202 Eastleigh 3rd Street, Nairobi', 'Elderly Care'),
(10, 22, 5, '11 Kangemi Central, Nairobi', 'Vocational Training');

-- -----------------------------------------------------------------------------
-- Populating CommunityNeeds Table (10 Records)
-- -----------------------------------------------------------------------------
INSERT INTO CommunityNeeds (NeedID, Title, Description, Location, Priority, Status, CreatedAt) VALUES
(1, 'Emergency Food Distribution Drive', 'Providing essential dry food rations, flour, oil, and clean water packets to 200 vulnerable households affected by seasonal droughts.', 'Kibera Community Center', 'High', 'Open', '2025-01-20 10:30:00'),
(2, 'Mobile Medical & First Aid Clinic', 'Setting up a temporary mobile health unit with volunteer nurses to provide free medical checkups and basic medications.', 'Mathare Health Hub', 'Critical', 'In Progress', '2025-01-20 10:30:00'),
(3, 'After-School STEM & Literacy Tutoring', 'Weekly educational support and learning materials for primary school students needing homework assistance.', 'Mukuru Resource Center', 'Medium', 'In Progress', '2025-01-20 10:30:00'),
(4, 'Clean Drinking Water Filtration Station', 'Installation of two commercial-grade water filtration tanks to supply clean, safe drinking water to local residents.', 'Korogocho Community School', 'High', 'Open', '2025-01-20 10:30:00'),
(5, 'Winter Blankets & Warm Clothing Relief', 'Collecting and distributing heavy winter coats, thermal blankets, and shoes for children and senior citizens.', 'Kawangware Youth Hall', 'Medium', 'Open', '2025-01-20 10:30:00'),
(6, 'Maternal and Child Health Supplies', 'Emergency delivery kits, infant formula, prenatal vitamins, and hygiene supplies for expectant and new mothers.', 'Dagoretti Care Center', 'Critical', 'Open', '2025-01-20 10:30:00'),
(7, 'Community Shelter Roof Repair', 'Urgent structural repairs and waterproofing for the primary community emergency shelter ahead of heavy rains.', 'Dandora Social Hall', 'High', 'Open', '2025-01-20 10:30:00'),
(8, 'Elderly Companion & Meal Delivery Service', 'Daily hot meal delivery and basic wellness visits for homebound elderly community members.', 'Baba Dogo Health Post', 'Low', 'Open', '2025-01-20 10:30:00'),
(9, 'Flood Disaster Relief Care Packages', 'Distributing emergency tents, water purification tablets, and sanitation kits for families displaced by flash floods.', 'Eastleigh Resource Center', 'Critical', 'In Progress', '2025-01-20 10:30:00'),
(10, 'Youth Digital Literacy & Computer Lab Setup', 'Establishing a 10-workstation refurbished computer room to teach basic coding and digital skills to teenagers.', 'Kangemi Tech Center', 'Medium', 'Open', '2025-01-20 10:30:00');

-- -----------------------------------------------------------------------------
-- Populating Donations Table (10 Records)
-- -----------------------------------------------------------------------------
INSERT INTO Donations (DonationID, UserID, Amount, PaymentMethod, TransactionRef, DonatedAt) VALUES
(1, 23, 1250.00, 'Credit Card', 'TXN-2025-88401', '2025-02-01 14:15:00'),
(2, 24, 5000.00, 'Bank Transfer', 'TXN-2025-88402', '2025-02-01 14:15:00'),
(3, 25, 750.00, 'PayPal', 'TXN-2025-88403', '2025-02-01 14:15:00'),
(4, 26, 2000.00, 'Stripe', 'TXN-2025-88404', '2025-02-01 14:15:00'),
(5, 27, 300.00, 'Mobile Money', 'TXN-2025-88405', '2025-02-01 14:15:00'),
(6, 28, 3500.00, 'Bank Transfer', 'TXN-2025-88406', '2025-02-01 14:15:00'),
(7, 29, 450.00, 'Credit Card', 'TXN-2025-88407', '2025-02-01 14:15:00'),
(8, 30, 10000.00, 'PayPal', 'TXN-2025-88408', '2025-02-01 14:15:00'),
(9, 1, 150.00, 'Mobile Money', 'TXN-2025-88409', '2025-02-01 14:15:00'),
(10, 2, 500.00, 'Mobile Money', 'TXN-2025-88410', '2025-02-01 14:15:00');

