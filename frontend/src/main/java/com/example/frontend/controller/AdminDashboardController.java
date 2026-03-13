package com.example.frontend.controller;

import com.example.frontend.service.AuthService;
import com.example.frontend.session.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AdminDashboardController {

    @FXML
    private Label adminNameLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Button navCourses;

    @FXML
    private Button navDashboard;

    @FXML
    private Button navNotices;

    @FXML
    private Button navTimetables;

    @FXML
    private Button navUsers;

    @FXML
    private VBox noticesContainer;

    @FXML
    private Label statusBarTime;

    @FXML
    private Label totalCoursesLabel;

    @FXML
    private Label totalLecturersLabel;

    @FXML
    private Label totalStudentsLabel;

    @FXML
    private Label totalTechLabel;

    @FXML
    private Label totalUsersLabel;

    @FXML
    private Label welcomeLabel;

    @FXML
    void logout(ActionEvent event) {
        try {
            // Use the same client instance from LoginController
            AuthService authService = new AuthService(LoginController.client); // make client static in LoginController

            boolean success = authService.logout(SessionManager.getToken());

            if (success) {
                System.out.println("Logout successful!");

                // Clear session locally
                SessionManager.clear();

                // Close dashboard window
                Stage dashboardStage = (Stage) adminNameLabel.getScene().getWindow();
                dashboardStage.close();

                // Re-open login window
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
                Parent root = loader.load();

                Stage loginStage = new Stage();
                loginStage.initStyle(StageStyle.UNDECORATED);
                loginStage.setScene(new Scene(root));
                loginStage.show();

            } else {
                System.out.println("Logout failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openAddUser(ActionEvent event) {

    }

    @FXML
    void openCourses(ActionEvent event) {

    }

    @FXML
    void openNotices(ActionEvent event) {

    }

    @FXML
    void openReports(ActionEvent event) {

    }

    @FXML
    void openTimetables(ActionEvent event) {

    }

    @FXML
    void openUsers(ActionEvent event) {

    }

    // In AdminDashboardController.java
    public void setAdminName(String name) {
        adminNameLabel.setText(name);
        welcomeLabel.setText("Welcome back, " + name + " 👋");
    }


}
