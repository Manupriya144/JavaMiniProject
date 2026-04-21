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
    created_by INT,
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