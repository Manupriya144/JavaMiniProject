package com.example.frontend.controller.admin;

import com.example.frontend.dto.NoticeResponseDTO;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.AuthService;
import com.example.frontend.service.NoticeService;
import com.example.frontend.service.TimeTableService;
import com.example.frontend.session.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
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

    private final NoticeService noticeService = new NoticeService(ServerClient.getInstance());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setAdminInfo();
        setTodayDate();
        startClock();
        loadStats();
        loadRecentNotices();
    }

    private void setAdminInfo() {
        String username = LoginController.username;

        if (username == null || username.isBlank()) {
            username = "Administrator";
        }

        welcomeLabel.setText("Welcome back, " + username + " 👋");
        adminNameLabel.setText(username);
    }

    private void setTodayDate() {
        String today = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        dateLabel.setText(today + "  •  Admin Control Panel");
    }

    private void startClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e ->
                        statusBarTime.setText(LocalDateTime.now().format(formatter))),
                new KeyFrame(Duration.seconds(1))
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void loadStats() {
        /*
         * TODO:
         * Replace these values with real backend/service calls.
         *
         * Example later:
         * totalUsersLabel.setText(String.valueOf(userService.getTotalUsers()));
         * totalStudentsLabel.setText(String.valueOf(userService.getTotalStudents()));
         * totalLecturersLabel.setText(String.valueOf(userService.getTotalLecturers()));
         * totalTechLabel.setText(String.valueOf(userService.getTotalTechOfficers()));
         * totalCoursesLabel.setText(String.valueOf(courseService.getTotalCourses()));
         */

        totalUsersLabel.setText("30");
        totalStudentsLabel.setText("20");
        totalLecturersLabel.setText("5");
        totalTechLabel.setText("4");
        totalCoursesLabel.setText("8");
    }

    private void loadRecentNotices() {
        noticesContainer.getChildren().clear();

        try {
            List<NoticeResponseDTO> notices = noticeService.getAllNotices();

            if (notices == null || notices.isEmpty()) {
                noticesContainer.getChildren().add(buildEmptyNoticeRow("No notices available"));
                return;
            }

            notices.stream()
                    .sorted(Comparator.comparing(
                            n -> safe(n.getCreated_at()),
                            Comparator.reverseOrder()
                    ))
                    .limit(3)
                    .forEach(notice -> noticesContainer.getChildren().add(
                            buildNoticeRow(
                                    safe(notice.getTitle()),
                                    formatDate(safe(notice.getCreated_at()))
                            )
                    ));

        } catch (Exception e) {
            e.printStackTrace();
            noticesContainer.getChildren().add(buildEmptyNoticeRow("Failed to load notices"));
        }
    }

    private HBox buildNoticeRow(String title, String date) {
        HBox row = new HBox(12);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle(
                "-fx-background-color: #1e3c72;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #2a5298;" +
                        "-fx-border-radius: 8;"
        );

        Label icon = new Label("📢");
        icon.setStyle("-fx-font-size: 14px;");

        VBox textBox = new VBox(3);

        Label titleLbl = new Label(title);
        titleLbl.setWrapText(true);
        titleLbl.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");

        Label dateLbl = new Label(date);
        dateLbl.setStyle("-fx-text-fill: #a0b8e0; -fx-font-size: 11px;");

        textBox.getChildren().addAll(titleLbl, dateLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(icon, textBox, spacer);
        return row;
    }

    private HBox buildEmptyNoticeRow(String message) {
        HBox row = new HBox();
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle(
                "-fx-background-color: #1e3c72;" +
                        "-fx-background-radius: 8;"
        );

        Label msg = new Label(message);
        msg.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        row.getChildren().add(msg);
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
        loadView("CourseDisplay.fxml");
    }

    @FXML
    private void openAddCourses() {
        loadView("AddCourse.fxml");
    }

    @FXML
    private void openAssignCourse() {
        loadView("AssignCourse.fxml");
    }

    @FXML
    private void openNotices() {
        loadView("NoticeDisplay.fxml");
    }

    @FXML
    private void openAddNotices() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddNotice.fxml"));
            Parent root = loader.load();

            AddNoticeController controller = loader.getController();
            controller.setNoticeService(new NoticeService(ServerClient.getInstance()));

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openTimetables() {
        loadView("DisplayTimeTable.fxml");
    }

    @FXML
    private void openTimeAddTimeTables() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddTimeTable.fxml"));
            Parent root = loader.load();

            AddTimeTableController controller = loader.getController();
            controller.setTimeTableService(new TimeTableService(ServerClient.getInstance()));

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openReports() {
        loadView("Reports.fxml");
    }

    @FXML
    void logout(ActionEvent event) {
        try {
            AuthService authService = new AuthService(LoginController.client);
            boolean success = authService.logout(SessionManager.getToken());

            if (success) {
                SessionManager.clear();

                Stage dashboardStage = (Stage) adminNameLabel.getScene().getWindow();
                dashboardStage.close();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
                Parent root = loader.load();

                Stage loginStage = new Stage();
                loginStage.initStyle(StageStyle.UNDECORATED);
                loginStage.setScene(new Scene(root));
                loginStage.centerOnScreen();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String formatDate(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.replace("T", " ");
    }
}