package com.example.frontend.controller.admin;

import com.example.frontend.model.Student;
import com.example.frontend.model.User;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.AuthService;
import com.example.frontend.service.StudentService;
import com.example.frontend.session.SessionManager;
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
    public static String userId= "";
    public static String reNo = "";
    public static String password = "";

    @FXML
    public void initialize() {
        usernameField.setOnAction(actionEvent -> passwordField.requestFocus() );
        passwordField.setOnAction(event -> login());
    }

    @FXML
    public void login() {
        try {
            client.connect();
            AuthService authService = new AuthService(client);
            StudentService studentService = new StudentService(client);

            User user = authService.login(
                    usernameField.getText(),
                    passwordField.getText()
            );

            if(user != null){
                // Store token & role in session
                SessionManager.setToken(user.getToken());
                SessionManager.setRole(user.getRole());
                SessionManager.setUserId(user.getUserId());

                System.out.println("Login success!");
                System.out.println("Token: " + user.getToken());
                System.out.println("Role: " + user.getRole());
                System.out.println("Password " + user.getPassword());
                System.out.println("User id : " + user.getUserId());

                LoginController.username = user.getUsername();
                LoginController.password = user.getPassword();
                LoginController.userId = user.getUserId();


                Student student = studentService.getStudentByUserId(LoginController.userId);
                if (student != null) {
                    reNo = student.getRegNo();
                }

                // Load different pages based on role
                switch(user.getRole()) {
                    case "Student":
                        loadDashboard("/view/studentDashboard.fxml",username);
                        break;
                    case "Lecturer":
                        loadDashboard("/view/lecturerDashboard.fxml",username);
                        break;
                    case "Tech_Officer":
                        loadDashboard("/view/techOfficerDashboard.fxml",username);
                        break;
                    case "Admin":
                    case "Dean":
                        loadDashboard("/view/AdminDashboard.fxml",username);
                        break;
                    default:
                        System.out.println("Unknown role: access denied");
                }

            } else {
                System.out.println("Login failed");
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void loadDashboard(String fxmlPath,String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Create a NEW stage for the dashboard
            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("FOT PORTAL"); // OS title bar
            dashboardStage.setScene(new Scene(root));
            dashboardStage.show();

            // Close the login stage
            Stage loginStage = (Stage) usernameField.getScene().getWindow();
            loginStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}