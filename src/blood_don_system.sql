-- blood_don_system.sql
CREATE DATABASE IF NOT EXISTS blood_don_system;
USE blood_don_system;

-- Admins table
CREATE TABLE IF NOT EXISTS admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL
);

-- Donors table
CREATE TABLE IF NOT EXISTS donors (
    donor_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    blood_group VARCHAR(5) NOT NULL,
    location VARCHAR(100) NOT NULL,
    contact_info VARCHAR(100) NOT NULL,
    health_status VARCHAR(200) NOT NULL,
    availability BOOLEAN DEFAULT TRUE,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Patients table
CREATE TABLE IF NOT EXISTS patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    blood_group VARCHAR(5) NOT NULL,
    location VARCHAR(100) NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Requests table
CREATE TABLE IF NOT EXISTS requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    donor_id INT NOT NULL,
    status ENUM('Pending', 'Accepted', 'Rejected') DEFAULT 'Pending',
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    response_date TIMESTAMP NULL,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (donor_id) REFERENCES donors(donor_id)
);

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    user_type ENUM('donor', 'patient') NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample admin
INSERT INTO admins (username, password, name) VALUES 
('admin', 'admin123', 'System Administrator');

-- Insert sample donors
INSERT INTO donors (username, password, name, age, blood_group, location, contact_info, health_status) VALUES 
('john_doe', 'password123', 'John Doe', 25, 'A+', 'New York', 'john@email.com', 'Excellent health'),
('jane_smith', 'password123', 'Jane Smith', 30, 'O-', 'New York', 'jane@email.com', 'Good health'),
('mike_wilson', 'password123', 'Mike Wilson', 28, 'B+', 'Los Angeles', 'mike@email.com', 'Very good health'),
('sarah_brown', 'password123', 'Sarah Brown', 22, 'AB+', 'Chicago', 'sarah@email.com', 'Excellent health');

-- Insert sample patients
INSERT INTO patients (username, password, name, blood_group, location) VALUES 
('patient1', 'password123', 'Robert Johnson', 'A+', 'New York'),
('patient2', 'password123', 'Emily Davis', 'O-', 'Boston'),
('patient3', 'password123', 'David Wilson', 'B+', 'Los Angeles');