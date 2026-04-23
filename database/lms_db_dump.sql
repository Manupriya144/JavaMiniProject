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

CREATE TABLE IF NOT EXISTS eligibility (
    eligibility_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(10) NOT NULL,
    course_id VARCHAR(10) NOT NULL,
    academic_year INT NOT NULL,
    semester INT NOT NULL,
    is_eligible BOOLEAN NOT NULL,
    UNIQUE(student_id, course_id, academic_year, semester),
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
    deadline DATE DEFAULT NULL,
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

-- Demo data for grades/GPA screens
INSERT IGNORE INTO department (department_id, name) VALUES ('D9001', 'Information Technology');

INSERT IGNORE INTO users (user_id, username, email, password, contact_number, role) VALUES
('U9001', 'Demo Lecturer', 'demo.lecturer@example.com', '$2a$10$demoLecturerHash', '0710000001', 'Lecturer'),
('U9002', 'Demo Student', 'demo.student@example.com', '$2a$10$demoStudentHash', '0710000002', 'Student'),
('U9003', 'Demo Student 2', 'demo.student2@example.com', '$2a$10$demoStudentHash2', '0710000003', 'Student'),
('U9004', 'Demo Student 3', 'demo.student3@example.com', '$2a$10$demoStudentHash3', '0710000004', 'Student');

INSERT IGNORE INTO lecturers (user_id, specialization, designation) VALUES
('U9001', 'Software Engineering', 'Senior Lecturer');

INSERT IGNORE INTO students (user_id, reg_no, batch, academic_level, department_id) VALUES
('U9002', 'REG9002', '2026', 1, 'D9001'),
('U9003', 'REG9003', '2026', 1, 'D9001'),
('U9004', 'REG9004', '2025', 1, 'D9001');

INSERT IGNORE INTO course (course_id, course_code, name, course_credit, academic_level, semester, department_id) VALUES
('C9001', 'ITC9001', 'Software Engineering Fundamentals', 3, 1, '1', 'D9001'),
('C9002', 'ITC9002', 'Programming Practice', 2, 1, '1', 'D9001');

INSERT IGNORE INTO lecturer_course (lecturer_id, course_id) VALUES
('U9001', 'C9001'),
('U9001', 'C9002');

INSERT IGNORE INTO assessment_type (assessment_type_id, course_id, name, weight, component) VALUES
(90011, 'C9001', 'Quiz', 20.00, 'CA'),
(90012, 'C9001', 'Assignment', 20.00, 'CA'),
(90013, 'C9001', 'Final Exam', 60.00, 'Final'),
(90021, 'C9002', 'Quiz', 20.00, 'CA'),
(90022, 'C9002', 'Lab', 20.00, 'CA'),
(90023, 'C9002', 'Final Exam', 60.00, 'Final');

INSERT IGNORE INTO student_marks (student_id, assessment_type_id, marks) VALUES
('U9002', 90011, 78.00),
('U9002', 90012, 85.00),
('U9002', 90013, 81.00),
('U9002', 90021, 72.00),
('U9002', 90022, 75.00),
('U9002', 90023, 79.00),
('U9003', 90011, 66.00),
('U9003', 90012, 70.00),
('U9003', 90013, 64.00),
('U9003', 90021, 74.00),
('U9003', 90022, 76.00),
('U9003', 90023, 82.00),
('U9004', 90011, 58.00),
('U9004', 90012, 62.00),
('U9004', 90013, 55.00),
('U9004', 90021, 68.00),
('U9004', 90022, 71.00),
('U9004', 90023, 69.00);

INSERT IGNORE INTO session (session_id, course_id, session_date, session_hours, type) VALUES
(90001, 'C9001', '2026-03-10', 2.00, 'Theory'),
(90002, 'C9001', '2026-03-12', 2.00, 'Practical'),
(90003, 'C9002', '2026-03-11', 2.00, 'Theory'),
(90004, 'C9002', '2026-03-13', 2.00, 'Practical');

INSERT IGNORE INTO attendance (student_id, session_id, status, hours_attended) VALUES
('U9002', 90001, 'Present', 2.00),
('U9002', 90002, 'Present', 2.00),
('U9002', 90003, 'Present', 2.00),
('U9002', 90004, 'Absent', 0.00),
('U9003', 90001, 'Present', 2.00),
('U9003', 90002, 'Absent', 0.00),
('U9003', 90003, 'Present', 2.00),
('U9003', 90004, 'Present', 2.00),
('U9004', 90001, 'Present', 2.00),
('U9004', 90002, 'Present', 2.00),
('U9004', 90003, 'Absent', 0.00),
('U9004', 90004, 'Present', 2.00);

INSERT IGNORE INTO eligibility (student_id, course_id, academic_year, semester, is_eligible) VALUES
('U9002', 'C9001', 2026, 1, TRUE),
('U9002', 'C9002', 2026, 1, TRUE),
('U9003', 'C9001', 2026, 1, FALSE),
('U9003', 'C9002', 2026, 1, TRUE),
('U9004', 'C9001', 2025, 1, FALSE),
('U9004', 'C9002', 2025, 1, TRUE);

INSERT IGNORE INTO notice (notice_id, title, description, pdf_file_path, created_by) VALUES
(9001, 'Mid Exam Schedule', 'Mid exam schedule published for Level 1.', 'docs/mid-exam.pdf', 'U9001'),
(9002, 'Lab Submission Reminder', 'Upload lab reports before deadline.', 'docs/lab-reminder.pdf', 'U9001');

INSERT IGNORE INTO course_material (material_id, course_id, lecturer_id, title, file_path, deadline) VALUES
(9001, 'C9001', 'U9001', 'Week 1 Slides', 'materials/C9001-week1.pdf', '2026-05-20'),
(9002, 'C9002', 'U9001', 'Lab Guide 1', 'materials/C9002-lab1.pdf', '2026-05-22');

INSERT IGNORE INTO course_result (student_id, course_id, academic_year, academic_level, semester, total_marks, grade) VALUES
('U9002', 'C9001', 2026, 1, '1', 84.00, 'A'),
('U9002', 'C9002', 2026, 1, '1', 76.00, 'B+'),
('U9003', 'C9001', 2026, 1, '1', 68.00, 'B'),
('U9003', 'C9002', 2026, 1, '1', 81.00, 'A-'),
('U9004', 'C9001', 2025, 1, '1', 59.00, 'C+'),
('U9004', 'C9002', 2025, 1, '1', 72.00, 'B');

INSERT IGNORE INTO semester_result (student_id, academic_year, academic_level, semester, total_credits, sgpa, cgpa) VALUES
('U9002', 2026, 1, '1', 5, 3.72, 3.72),
('U9003', 2026, 1, '1', 5, 3.28, 3.28),
('U9004', 2025, 1, '1', 5, 2.68, 2.68);