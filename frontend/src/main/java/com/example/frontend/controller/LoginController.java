package com.example.frontend.controller;

import com.example.frontend.model.User;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.AuthService;
import com.example.frontend.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    static final ServerClient client = new ServerClient();

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

            User user = authService.login(
                    usernameField.getText(),
                    passwordField.getText()
            );

            if(user != null){
                // Store token & role in session
                SessionManager.setToken(user.getToken());
                SessionManager.setRole(user.getRole());

                System.out.println("Login success!");
                System.out.println("Token: " + user.getToken());
                System.out.println("Role: " + user.getRole());

                // Load different pages based on role
                switch(user.getRole()) {
                    case "Student":
                        loadDashboard("/view/studentDashboard.fxml",user.getUsername());
                        break;
                    case "Lecturer":
                        loadDashboard("/view/lecturerDashboard.fxml",user.getUsername());
                        break;
                    case "Tech_Officer":
                        loadDashboard("/view/techOfficerDashboard.fxml",user.getUsername());
                        break;
                    case "Admin":
                    case "Dean":
                        loadDashboard("/view/adminDashboard.fxml",user.getUsername());
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
            dashboardStage.setTitle("Dashboard"); // OS title bar
            dashboardStage.setScene(new Scene(root));
            dashboardStage.show();

            // Close the login stage
            AdminDashboardController controller = loader.getController();
            controller.setAdminName(username);
            Stage loginStage = (Stage) usernameField.getScene().getWindow();
            loginStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}