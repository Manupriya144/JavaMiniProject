package com.example.frontend.controller.admin;

import com.example.frontend.model.Student;
import com.example.frontend.model.User;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.AuthService;
import com.example.frontend.service.StudentService;
import com.example.frontend.session.SessionManager;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    public static final ServerClient client = ServerClient.getInstance();

    public static String username = "";
    public static String userId = "";
    public static String reNo = "";
    public static String password = "";

    @FXML
    public void initialize() {
        usernameField.setOnAction(event -> passwordField.requestFocus());
        passwordField.setOnAction(event -> login());
    }

    @FXML
    public void login() {

        String inputUsername = usernameField.getText().trim();
        String inputPassword = passwordField.getText();

        if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
            System.out.println("Username and password required");
            return;
        }

        usernameField.setDisable(true);
        passwordField.setDisable(true);

        Task<User> loginTask = new Task<>() {
            @Override
            protected User call() throws Exception {
                client.connect();

                AuthService authService = new AuthService(client);

                return authService.login(inputUsername, inputPassword);
            }
        };

        loginTask.setOnSucceeded(event -> {
            User user = loginTask.getValue();

            if (user == null) {
                usernameField.setDisable(false);
                passwordField.setDisable(false);
                System.out.println("Login failed");
                return;
            }

            SessionManager.setToken(user.getToken());
            SessionManager.setRole(user.getRole());
            SessionManager.setUserId(user.getUserId());

            LoginController.username = user.getUsername();
            LoginController.password = user.getPassword();
            LoginController.userId = user.getUserId();

            System.out.println("Login success!");
            System.out.println("Token: " + user.getToken());
            System.out.println("Role: " + user.getRole());
            System.out.println("User id: " + user.getUserId());

            if ("Student".equals(user.getRole())) {
                loadStudentDataAndOpenDashboard(user);
            } else {
                openDashboardByRole(user);
            }
        });

        loginTask.setOnFailed(event -> {
            usernameField.setDisable(false);
            passwordField.setDisable(false);

            loginTask.getException().printStackTrace();
            System.out.println("Server connection failed");
        });

        Thread thread = new Thread(loginTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadStudentDataAndOpenDashboard(User user) {

        Task<Student> studentTask = new Task<>() {
            @Override
            protected Student call() throws Exception {
                StudentService studentService = new StudentService(client);
                return studentService.getStudentByUserId(LoginController.userId);
            }
        };

        studentTask.setOnSucceeded(event -> {
            Student student = studentTask.getValue();

            if (student != null) {
                LoginController.reNo = student.getRegNo();
            }

            openDashboardByRole(user);
        });

        studentTask.setOnFailed(event -> {
            studentTask.getException().printStackTrace();
            openDashboardByRole(user);
        });

        Thread thread = new Thread(studentTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void openDashboardByRole(User user) {

        switch (user.getRole()) {
            case "Student":
                loadDashboard("/view/student/studentDashboard.fxml");
                break;

            case "Lecturer":
                loadDashboard("/view/lecturer/lecturerDashboard.fxml");
                break;

            case "Tech_Officer":
                loadDashboard("/view/techofficer/techOfficerDashboard.fxml");
                break;

            case "Admin":
            case "Dean":
                loadDashboard("/view/admin/AdminDashboard.fxml");
                break;

            default:
                usernameField.setDisable(false);
                passwordField.setDisable(false);
                System.out.println("Unknown role: access denied");
        }
    }

    private void loadDashboard(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("FOT PORTAL");
            dashboardStage.setScene(new Scene(root));
            dashboardStage.show();

            Stage loginStage = (Stage) usernameField.getScene().getWindow();
            loginStage.close();

        } catch (IOException e) {
            usernameField.setDisable(false);
            passwordField.setDisable(false);

            e.printStackTrace();
            System.out.println("Cannot load dashboard: " + fxmlPath);
        }
    }
}