-- Aurorah CAN Digital Hub - Relational Database Schema (Azure SQL / T-SQL)

-- Drop tables in reverse order of foreign key dependencies if they exist
IF OBJECT_ID('dbo.Donations', 'U') IS NOT NULL DROP TABLE dbo.Donations;
IF OBJECT_ID('dbo.CommunityNeeds', 'U') IS NOT NULL DROP TABLE dbo.CommunityNeeds;
IF OBJECT_ID('dbo.Beneficiaries', 'U') IS NOT NULL DROP TABLE dbo.Beneficiaries;
IF OBJECT_ID('dbo.Volunteers', 'U') IS NOT NULL DROP TABLE dbo.Volunteers;
IF OBJECT_ID('dbo.Users', 'U') IS NOT NULL DROP TABLE dbo.Users;

-- 1. Table: Users
CREATE TABLE Users (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    FullName VARCHAR(100) NOT NULL,
    Email VARCHAR(150) UNIQUE NOT NULL,
    PasswordHash VARCHAR(255) NOT NULL,
    Role VARCHAR(20) DEFAULT 'User',
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- 2. Table: Volunteers
CREATE TABLE Volunteers (
    VolunteerID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT UNIQUE NOT NULL,
    Phone VARCHAR(20) NOT NULL,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
);

-- 3. Table: Beneficiaries
CREATE TABLE Beneficiaries (
    BeneficiaryID INT IDENTITY(1,1) PRIMARY KEY,
    FullName VARCHAR(100) NOT NULL,
    Phone VARCHAR(20) NULL,
    Address VARCHAR(255) NULL,
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- 4. Table: CommunityNeeds
CREATE TABLE CommunityNeeds (
    NeedID INT IDENTITY(1,1) PRIMARY KEY,
    Title VARCHAR(150) NOT NULL,
    Description VARCHAR(MAX) NULL,
    Status VARCHAR(50) DEFAULT 'Pending',
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- 5. Table: Donations
CREATE TABLE Donations (
    DonationID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NULL,
    Amount DECIMAL(18,2) NOT NULL,
    DonationDate DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE SET NULL
);