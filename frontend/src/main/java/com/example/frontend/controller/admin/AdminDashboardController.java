package com.example.frontend.controller.admin;


import com.example.frontend.network.ServerClient;
import com.example.frontend.service.AuthService;
import com.example.frontend.service.NoticeService;
import com.example.frontend.service.TimeTableService;
import com.example.frontend.session.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML private Label welcomeLabel;
    @FXML private Label dateLabel;
    @FXML private Label adminNameLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalStudentsLabel;
    @FXML private Label totalLecturersLabel;
    @FXML private Label totalTechLabel;
    @FXML private Label totalCoursesLabel;
    @FXML private Label statusBarTime;
    @FXML private VBox noticesContainer;


    private final String adminName = LoginController.username;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set date
        String today = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        dateLabel.setText(today + "  •  Admin Control Panel");
        statusBarTime.setText(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("d MMMM yyyy")));

        // Welcome
        welcomeLabel.setText("Welcome back, " + adminName + " 👋");
        adminNameLabel.setText(adminName);

        // Load stats from DB
        loadStats();

        // Load recent notices
        loadRecentNotices();

    }


    private void loadStats() {
        // TODO: replace with actual DB queries
        totalUsersLabel.setText("30");
        totalStudentsLabel.setText("20");
        totalLecturersLabel.setText("5");
        totalTechLabel.setText("4");
        totalCoursesLabel.setText("8");
    }

    private void loadRecentNotices() {
        // TODO: replace with actual DB query — show last 3 notices
        String[][] sampleNotices = {
                {"Mid-Semester Exam Schedule Released", "2026-03-10"},
                {"Lab Maintenance – Labs closed on 15 March", "2026-03-08"},
                {"Semester Registration Deadline Reminder", "2026-03-05"}
        };

        for (String[] notice : sampleNotices) {
            noticesContainer.getChildren().add(buildNoticeRow(notice[0], notice[1]));
        }
    }

    private HBox buildNoticeRow(String title, String date) {
        HBox row = new HBox(12);
        row.setStyle("-fx-background-color: #1e3c72; -fx-background-radius: 8;");
        row.setPadding(new Insets(12, 16, 12, 16));

        Label icon = new Label("📢");
        icon.setStyle("-fx-font-size: 14px;");

        VBox text = new VBox(3);
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        Label dateLbl = new Label(date);
        dateLbl.setStyle("-fx-text-fill: #6a90c8; -fx-font-size: 11px;");
        text.getChildren().addAll(titleLbl, dateLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        row.getChildren().addAll(icon, text, spacer);
        return row;
    }



    @FXML
    private void openUsers() {
        loadView("UserManagement.fxml");
    }

    @FXML
    private void openAddUser() {
        loadView("createUser.fxml");
    }

    @FXML
    private void openCourses() {
        loadView("CourseManagement.fxml");
    }

    @FXML
    private void openNotices() {
        loadView("NoticeManagement.fxml");
    }

    @FXML
    private void openTimetables() {
        loadView("TimetableManagement.fxml");
    }

    @FXML
    private void openReports() {
        loadView("Reports.fxml");
    }

    @FXML
    public void openAddCourses() {
        loadView("AddCourse.fxml");
    }

    @FXML
    public void openAddNotices() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddNotice.fxml"));
            Parent root = loader.load();

            AddNoticeController controller = loader.getController();
            controller.setNoticeService(new NoticeService(ServerClient.getInstance()));

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void openTimeAddTimeTables() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddTimeTable.fxml"));
            Parent root = loader.load();

            AddTimeTableController controller = loader.getController();
            controller.setTimeTableService(new TimeTableService(ServerClient.getInstance()));

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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


    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}