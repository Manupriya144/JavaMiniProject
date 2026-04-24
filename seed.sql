-- Insert into users if not exists
INSERT IGNORE INTO users (user_id, username, email, password, contact_number, profile_picture, role) 
VALUES ('U002', 'nuwangi', 'nuwangi@gmail.com', '1234', '0771234567', '', 'Student');

-- Insert into department if not exists
INSERT IGNORE INTO department (department_id, name) VALUES ('D001', 'IT');

-- Insert into students
INSERT IGNORE INTO students (user_id, reg_no, batch, academic_level, department_id)
VALUES ('U002', 'TG/2021/1000', '2021', 2, 'D001');

-- Insert into course
INSERT IGNORE INTO course (course_id, department_id, name, course_code, course_credit)
VALUES 
('C001', 'D001', 'Object Oriented Programming', 'ICT2112', 3),
('C002', 'D001', 'OOP Practicum', 'ICT2132', 2),
('C003', 'D001', 'Data Structures', 'ICT2142', 3),
('C004', 'D001', 'Web Technologies', 'ICT2152', 3);

-- Insert into course_registration
INSERT IGNORE INTO course_registration (student_id, course_id, academic_year, semester, registration_type)
VALUES 
('U001', 'C001', 2023, '1', 'Proper'),
('U001', 'C002', 2023, '1', 'Proper'),
('U001', 'C003', 2023, '1', 'Proper'),
('U001', 'C004', 2023, '1', 'Proper'),
('U002', 'C001', 2023, '1', 'Proper'),
('U002', 'C002', 2023, '1', 'Proper'),
('U002', 'C003', 2023, '1', 'Proper'),
('U002', 'C004', 2023, '1', 'Proper');
