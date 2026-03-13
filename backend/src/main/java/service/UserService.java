package service;

import dao.UserDAO;
import dto.requestDto.UserRequestDTO;
import dto.responseDto.UserResponseDTO;
import model.User;

import java.util.List;
import java.util.UUID;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // Service method to get all users
    public List<User> getAllUsers() {
        return userDAO.findAllUsers();
    }

    public boolean createUser(UserRequestDTO dto) {

        // Generate user ID
        String userId = UUID.randomUUID().toString().substring(0, 8);

        User user = new User(
                userId,
                dto.getUsername(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getContactNumber(),
                dto.getProfilePicture(),
                dto.getRole()
        );

        User savedUser = userDAO.createUser(user);

        // return true if creation succeeded
        return savedUser != null;
    }

    public User getUserById(String userId) {
        return userDAO.getUserById(userId);
    }
}