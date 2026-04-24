CREATE DATABASE IF NOT EXISTS lms_db;
USE lms_db;

-- Department first (FK references)
CREATE TABLE IF NOT EXISTS department (
    department_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Users table
CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(10) PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    contact_number VARCHAR(20),
    profile_picture VARCHAR(255),
    role ENUM('Student','Lecturer','Dean','Tech_Officer','Admin') NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
    );

-- Students table
CREATE TABLE IF NOT EXISTS students (
    user_id VARCHAR(10) PRIMARY KEY,
    reg_no VARCHAR(20) UNIQUE NOT NULL,
    batch VARCHAR(20) NOT NULL,
    academic_level INT CHECK (academic_level BETWEEN 1 AND 4),
    department_id VARCHAR(10),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES department(department_id)
    );


-- Tech Officers
CREATE TABLE IF NOT EXISTS tech_officers (
    user_id VARCHAR(10) PRIMARY KEY,
    department_id VARCHAR(10),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES department(department_id)
    );

-- Lecturers
CREATE TABLE IF NOT EXISTS lecturers (
    user_id VARCHAR(10) PRIMARY KEY,
    specialization VARCHAR(100),
    designation VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
    );

-- Courses
CREATE TABLE IF NOT EXISTS course (
    course_id VARCHAR(10) PRIMARY KEY,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(150) NOT NULL,
    course_credit INT NOT NULL,
    academic_level INT CHECK (academic_level BETWEEN 1 AND 4),
    semester ENUM('1','2') NOT NULL,
    department_id VARCHAR(10) NOT NULL,

    FOREIGN KEY (department_id)
    REFERENCES department(department_id)
    ON DELETE CASCADE
);
-- Course Registration
CREATE TABLE IF NOT EXISTS course_registration (
    student_id VARCHAR(10),
    course_id VARCHAR(10),
    academic_year INT NOT NULL,
    semester ENUM('1','2') NOT NULL,
    registration_type ENUM('Proper','Repeat','Suspend') DEFAULT 'Proper',
    PRIMARY KEY (student_id, course_id, academic_year, semester),
    FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE
);

-- Assessment Type
CREATE TABLE IF NOT EXISTS assessment_type (
    assessment_type_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id VARCHAR(10) NOT NULL,
    name VARCHAR(50) NOT NULL,       -- Quiz1, Quiz2, Mid, Final
    weight DECIMAL(5,2) NOT NULL,    -- Percentage
    component ENUM('CA','Final') NOT NULL,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE
    );

-- Student Marks
CREATE TABLE IF NOT EXISTS student_marks (
    mark_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(10) NOT NULL,
    assessment_type_id INT NOT NULL,
    marks DECIMAL(5,2) CHECK (marks BETWEEN 0 AND 100),
    UNIQUE (student_id, assessment_type_id),
    FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    FOREIGN KEY (assessment_type_id) REFERENCES assessment_type(assessment_type_id) ON DELETE CASCADE
    );

-- Session
CREATE TABLE IF NOT EXISTS session (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id VARCHAR(10) NOT NULL,
    session_date DATE NOT NULL,
    session_hours DECIMAL(4,2) NOT NULL,
    type ENUM('Theory','Practical') NOT NULL,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE
    );

-- Course Result
CREATE TABLE IF NOT EXISTS course_result (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(10) NOT NULL,
    course_id VARCHAR(10) NOT NULL,
    academic_year INT NOT NULL,
    academic_level INT NOT NULL,
    semester ENUM('1','2') NOT NULL,
    total_marks DECIMAL(5,2),
    grade VARCHAR(5),
    UNIQUE (student_id, course_id, academic_year, semester),
    FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE
    );

-- Semester Result
CREATE TABLE IF NOT EXISTS semester_result (
    semester_result_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(10) NOT NULL,
    academic_year INT NOT NULL,
    academic_level INT,
    semester ENUM('1','2'),
    total_credits INT,
    sgpa DECIMAL(3,2),
    cgpa DECIMAL(3,2),
    FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE
    );

-- Lecturer-Course
CREATE TABLE IF NOT EXISTS lecturer_course (
    lecturer_id VARCHAR(10),
    course_id VARCHAR(10),
    PRIMARY KEY (lecturer_id, course_id),
    FOREIGN KEY (lecturer_id) REFERENCES lecturers(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE
);

-- Medical
CREATE TABLE IF NOT EXISTS medical (
    medical_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(10) NOT NULL,
    course_id VARCHAR(10),
    exam_type ENUM('Mid','Final','Attendance') NOT NULL,
    date_submitted DATE NOT NULL,
    medical_copy VARCHAR(255),
    status ENUM('Pending','Approved','Rejected') DEFAULT 'Pending',
    FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE
    );

-- Notice
CREATE TABLE IF NOT EXISTS notice (
    notice_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    pdf_file_path VARCHAR(255),
    created_by VARCHAR(10),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
);
-- Course Material
CREATE TABLE IF NOT EXISTS course_material (
    material_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id VARCHAR(10),
    lecturer_id VARCHAR(10),
    title VARCHAR(150),
    file_path VARCHAR(255),
    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE,
    FOREIGN KEY (lecturer_id) REFERENCES lecturers(user_id) ON DELETE CASCADE
    );

-- Time Table
CREATE TABLE IF NOT EXISTS timetable (
    timetable_id INT AUTO_INCREMENT PRIMARY KEY,
    department_id VARCHAR(10) NOT NULL,
    academic_level INT NOT NULL CHECK (academic_level BETWEEN 1 AND 4),
    semester ENUM('1','2') NOT NULL,
    title VARCHAR(100),
    pdf_file_path VARCHAR(255) NOT NULL,
    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (department_id, academic_level, semester),

    FOREIGN KEY (department_id) REFERENCES department(department_id) ON DELETE CASCADE
);

CREATE TABLE attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(10) NOT NULL,
    session_id INT NOT NULL,
    status ENUM('Present','Absent') NOT NULL,
    hours_attended DECIMAL(4,2) DEFAULT 0,
    UNIQUE(student_id, session_id),
    FOREIGN KEY(student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    FOREIGN KEY(session_id) REFERENCES session(session_id) ON DELETE CASCADE
);

-- Dummy Data for Testing Attendance
INSERT IGNORE INTO department (department_id, name) VALUES ('DPT01', 'Computer Science');
INSERT IGNORE INTO users (user_id, username, email, password, contact_number, role) VALUES ('STU001', 'John Doe', 'john@example.com', 'password', '1234567890', 'Student');
INSERT IGNORE INTO students (user_id, reg_no, batch, academic_level, department_id) VALUES ('STU001', 'REG001', '2023', 1, 'DPT01');
INSERT IGNORE INTO course (course_id, course_code, name, course_credit, academic_level, semester, department_id) VALUES ('CRS001', 'CS101', 'Programming I', 3, 1, '1', 'DPT01');
INSERT IGNORE INTO course (course_id, course_code, name, course_credit, academic_level, semester, department_id) VALUES ('CRS002', 'CS102', 'Database Systems', 3, 1, '1', 'DPT01');
INSERT IGNORE INTO course_registration (student_id, course_id, academic_year, semester) VALUES ('STU001', 'CRS001', 2023, '1');
INSERT IGNORE INTO course_registration (student_id, course_id, academic_year, semester) VALUES ('STU001', 'CRS002', 2023, '1');
INSERT IGNORE INTO session (session_id, course_id, session_date, session_hours, type) VALUES (1, 'CRS001', '2023-01-10', 2.0, 'Theory');
INSERT IGNORE INTO session (session_id, course_id, session_date, session_hours, type) VALUES (2, 'CRS001', '2023-01-12', 2.0, 'Practical');
INSERT IGNORE INTO session (session_id, course_id, session_date, session_hours, type) VALUES (3, 'CRS002', '2023-01-11', 2.0, 'Theory');
INSERT IGNORE INTO session (session_id, course_id, session_date, session_hours, type) VALUES (4, 'CRS002', '2023-01-13', 2.0, 'Practical');
INSERT IGNORE INTO attendance (student_id, session_id, status, hours_attended) VALUES ('STU001', 1, 'Present', 2.0);
INSERT IGNORE INTO attendance (student_id, session_id, status, hours_attended) VALUES ('STU001', 2, 'Present', 2.0);
INSERT IGNORE INTO attendance (student_id, session_id, status, hours_attended) VALUES ('STU001', 3, 'Present', 2.0);
INSERT IGNORE INTO attendance (student_id, session_id, status, hours_attended) VALUES ('STU001', 4, 'Absent', 0.0);

-- Seed data for U001 (nimal) attendance
INSERT IGNORE INTO department (department_id, name) VALUES ('DPT02', 'Information Technology');

-- Courses for U001
INSERT IGNORE INTO course (course_id, course_code, name, course_credit, academic_level, semester, department_id)
    VALUES ('CRS101', 'IT101', 'Introduction to Programming', 3, 1, '1', 'DPT02');
INSERT IGNORE INTO course (course_id, course_code, name, course_credit, academic_level, semester, department_id)
    VALUES ('CRS102', 'IT102', 'Database Management', 3, 1, '1', 'DPT02');
INSERT IGNORE INTO course (course_id, course_code, name, course_credit, academic_level, semester, department_id)
    VALUES ('CRS103', 'IT103', 'Web Technologies', 3, 1, '1', 'DPT02');

-- Sessions for CRS101
INSERT IGNORE INTO session (session_id, course_id, session_date, session_hours, type) VALUES (10, 'CRS101', '2024-01-08', 2.0, 'Theory');
INSERT IGNORE INTO session (session_id, course_id, session_date, session_hours, type) VALUES (11, 'CRS101', '2024-01-10', 2.0, 'Theory');
INSERT IGNORE INTO session (session_id, course_id, session_date, session_hours, type) VALUES (12, 'CRS101', '2024-01-15', 3.0, 'Practical');
INSERT IGNORE INTO session (session_id, course_id, session_date, session_hours, type) VALUES (13, 'CRS101', '2024-01-17', 2.0, 'Theory');
INSERT IGNORE INTO session (session_id, course_id, session_date, session_hours, type) VALUES (14, 'CRS101', '2024-01-22', 3.0, 'Practical');

-- Sessions for CRS102
INSERT IGNORE INTO session (session_id, course_id, session_date, session_hours, type) VALUES (15, 'CRS102', '2024-01-09', 2.0, 'Theory');
INSERT IGNORE INTO session (session_id, course_id, session_date, session_hours, type) VALUES (16, 'CRS102', '2024-01-11', 2.0, 'Theory');
INSERT IGNORE INTO session (session_id, course_id, session_date, session_hours, type) VALUES (17, 'CRS102', '2024-01-16', 3.0, 'Practical');
INSERT IGNORE INTO session (session_id, course_id, session_date, session_hours, type) VALUES (18, 'CRS102', '2024-01-18', 2.0, 'Theory');

-- Sessions for CRS103
INSERT IGNORE INTO session (session_id, course_id, session_date, session_hours, type) VALUES (19, 'CRS103', '2024-01-12', 2.0, 'Theory');
INSERT IGNORE INTO session (session_id, course_id, session_date, session_hours, type) VALUES (20, 'CRS103', '2024-01-19', 2.0, 'Theory');
INSERT IGNORE INTO session (session_id, course_id, session_date, session_hours, type) VALUES (21, 'CRS103', '2024-01-26', 3.0, 'Practical');

-- Attendance for U001 (nimal) — CRS101: 4/5 Present = 80%
INSERT IGNORE INTO attendance (student_id, session_id, status, hours_attended) VALUES ('U001', 10, 'Present', 2.0);
INSERT IGNORE INTO attendance (student_id, session_id, status, hours_attended) VALUES ('U001', 11, 'Present', 2.0);
INSERT IGNORE INTO attendance (student_id, session_id, status, hours_attended) VALUES ('U001', 12, 'Present', 3.0);
INSERT IGNORE INTO attendance (student_id, session_id, status, hours_attended) VALUES ('U001', 13, 'Absent',  0.0);
INSERT IGNORE INTO attendance (student_id, session_id, status, hours_attended) VALUES ('U001', 14, 'Present', 3.0);

-- Attendance for U001 (nimal) — CRS102: 3/4 Present = 75%
INSERT IGNORE INTO attendance (student_id, session_id, status, hours_attended) VALUES ('U001', 15, 'Present', 2.0);
INSERT IGNORE INTO attendance (student_id, session_id, status, hours_attended) VALUES ('U001', 16, 'Absent',  0.0);
INSERT IGNORE INTO attendance (student_id, session_id, status, hours_attended) VALUES ('U001', 17, 'Present', 3.0);
INSERT IGNORE INTO attendance (student_id, session_id, status, hours_attended) VALUES ('U001', 18, 'Present', 2.0);

-- Attendance for U001 (nimal) — CRS103: 3/3 Present = 100%
INSERT IGNORE INTO attendance (student_id, session_id, status, hours_attended) VALUES ('U001', 19, 'Present', 2.0);
INSERT IGNORE INTO attendance (student_id, session_id, status, hours_attended) VALUES ('U001', 20, 'Present', 2.0);
INSERT IGNORE INTO attendance (student_id, session_id, status, hours_attended) VALUES ('U001', 21, 'Present', 3.0);