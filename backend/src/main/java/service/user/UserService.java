package service.user;

import dao.user.UserDAO;
import dto.requestDto.user.UserRequestDTO;
import model.Lecturer;
import model.Student;
import model.TechOfficer;
import model.User;

import java.util.List;
import java.util.UUID;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // Get all users
    public List<User> getAllUsers() {
        return userDAO.findAllUsers();
    }

    // Create user (handles all roles)
    public boolean createUser(UserRequestDTO dto) {

        String userId = UUID.randomUUID().toString().substring(0, 8); // generate 8-char ID
        User user;

        switch (dto.getRole()) {
            case "Student":
                Student student = new Student();
                student.setUserId(userId);
                student.setUsername(dto.getUsername());
                student.setEmail(dto.getEmail());
                student.setPassword(dto.getPassword());
                student.setContactNumber(dto.getContactNumber());
                student.setRole("Student");

                student.setRegNo(dto.getRegNo());
                student.setBatch(dto.getBatch());
                student.setAcademicLevel(dto.getAcademicLevel());
                student.setDepartmentId(dto.getDepartmentId());

                user = student;
                break;

            case "Lecturer":
                Lecturer lecturer = new Lecturer();
                lecturer.setUserId(userId);
                lecturer.setUsername(dto.getUsername());
                lecturer.setEmail(dto.getEmail());
                lecturer.setPassword(dto.getPassword());
                lecturer.setContactNumber(dto.getContactNumber());
                lecturer.setRole("Lecturer");

                lecturer.setSpecialization(dto.getSpecialization());
                lecturer.setDesignation(dto.getDesignation());

                user = lecturer;
                break;

            case "Tech_Officer":
                TechOfficer tech = new TechOfficer();
                tech.setUserId(userId);
                tech.setUsername(dto.getUsername());
                tech.setEmail(dto.getEmail());
                tech.setPassword(dto.getPassword());
                tech.setContactNumber(dto.getContactNumber());
                tech.setRole("Tech_Officer");

                tech.setDepartmentId(dto.getTechDepartmentId());

                user = tech;
                break;

            case "Admin":
            case "Dean":
                user = new User(
                        userId,
                        dto.getUsername(),
                        dto.getEmail(),
                        dto.getPassword(),
                        dto.getContactNumber(),
                        dto.getProfilePicture(),
                        dto.getRole()
                );
                break;

            default:
                throw new IllegalArgumentException("Unknown role: " + dto.getRole());
        }

        // Insert into users + role-specific table (if any)
        return userDAO.createUser(user) != null;
    }

    // Get user by ID
    public User getUserById(String userId) {
        return userDAO.getUserById(userId);
    }
}