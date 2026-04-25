package com.example.frontend.controller.lecturer;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.model.LecturerCourseItem;
import com.example.frontend.model.LecturerDashboardStats;
import com.example.frontend.service.AuthService;
import com.example.frontend.service.LecturerCourseService;
import com.example.frontend.session.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class LecturerDashboardController implements Initializable {

    @FXML private Label welcomeLabel;
    @FXML private Label dateLabel;
    @FXML private Label lecturerNameLabel;
    @FXML private Label myCoursesLabel;
    @FXML private Label myStudentsLabel;
    @FXML private Label eligibleLabel;
    @FXML private Label pendingMarksLabel;
    @FXML private Label statusBarTime;
    @FXML private VBox coursesContainer;

    private String lecturerName = LoginController.username;
    private String lecturerId = LoginController.userId;

    private final LecturerCourseService lecturerCourseService =
            new LecturerCourseService(LoginController.client);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String today = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));

        dateLabel.setText(today + "  •  Lecturer Panel");
        statusBarTime.setText(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("d MMMM yyyy")));

        if (lecturerName == null || lecturerName.isBlank()) {
            lecturerName = "Lecturer";
        }

        welcomeLabel.setText("Welcome, " + lecturerName + " 👋");
        lecturerNameLabel.setText(lecturerName);

        loadStats();
        loadCourses();
    }

    private void loadStats() {
        try {
            LecturerDashboardStats stats =
                    lecturerCourseService.getLecturerDashboardStats();

            if (stats == null) {
                myCoursesLabel.setText("0");
                myStudentsLabel.setText("0");
                eligibleLabel.setText("0");
                pendingMarksLabel.setText("0");
                return;
            }

            myCoursesLabel.setText(String.valueOf(stats.getMyCourses()));
            myStudentsLabel.setText(String.valueOf(stats.getMyStudents()));
            eligibleLabel.setText(String.valueOf(stats.getEligibleStudents()));
            pendingMarksLabel.setText(String.valueOf(stats.getPendingMarks()));

        } catch (Exception e) {
            e.printStackTrace();

            myCoursesLabel.setText("0");
            myStudentsLabel.setText("0");
            eligibleLabel.setText("0");
            pendingMarksLabel.setText("0");
        }
    }

    private void loadCourses() {
        coursesContainer.getChildren().clear();

        try {
            List<LecturerCourseItem> courses = lecturerCourseService.getLecturerCourses();

            if (courses == null || courses.isEmpty()) {
                Label emptyLabel = new Label("No assigned courses found.");
                emptyLabel.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 13px;");
                coursesContainer.getChildren().add(emptyLabel);
                return;
            }

            for (LecturerCourseItem course : courses) {
                coursesContainer.getChildren().add(
                        buildCourseRow(
                                course.getCourseCode(),
                                course.getCourseName(),
                                String.valueOf(course.getCourseCredit())
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();

            Label errorLabel = new Label("Failed to load assigned courses.");
            errorLabel.setStyle("-fx-text-fill: #e85d5d; -fx-font-size: 13px;");
            coursesContainer.getChildren().add(errorLabel);
        }
    }

    private HBox buildCourseRow(String code, String name, String credits) {
        HBox row = new HBox(0);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #d7e7f8;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );

        Label codeLbl = new Label(code);
        codeLbl.setPrefWidth(160);

        Label nameLbl = new Label(name);
        nameLbl.setPrefWidth(300);

        Label credLbl = new Label(credits);
        credLbl.setPrefWidth(100);

        String textStyle = "-fx-text-fill: #1a3a52; -fx-font-size: 12px;";
        codeLbl.setStyle(textStyle);
        nameLbl.setStyle(textStyle);
        credLbl.setStyle(textStyle);

        Button actionBtn = new Button("Upload Marks");
        actionBtn.setPrefWidth(120);
        actionBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #5b9fd9, #4c8fce);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );

        actionBtn.setOnAction(e -> openFinalMarks());

        row.getChildren().addAll(codeLbl, nameLbl, credLbl, actionBtn);
        return row;
    }

    @FXML
    private void openCourses() {
        loadView("lecturer/LecturerCourses.fxml");
    }

    @FXML
    private void openCA() {
        loadView("techofficer/CAManagement.fxml");
    }

    @FXML
    private void openMarks() {
        loadView("lecturer/StudentCourseMarks.fxml");
    }

    @FXML
    private void openStudents() {
        loadView("admin/UserManagement.fxml");
    }

    @FXML
    private void openFinalMarks() {
        loadView("lecturer/UploadFinalMarks.fxml");
    }

    @FXML
    private void openEligibility() {
        loadView("lecturer/FinalEligibility.fxml");
    }

    @FXML
    private void openAttendance() {
        loadView("lecturer/StudentEligibility.fxml");
    }

    @FXML
    private void openAddSession() {
        loadView("lecturer/AddLectureSession.fxml");
    }

    @FXML
    private void openGrades() {
        loadView("lecturer/GradesGPA.fxml");
    }

    @FXML
    private void openNotices() {
        loadView("admin/NoticeDisplay.fxml");
    }

    @FXML
    private void openProfile() {
        loadView("LecturerProfile.fxml");
    }


    @FXML
    private void openCaEligibility() {
        loadView("lecturer/CAEligibility.fxml");
    }


    @FXML
    void logout(ActionEvent event) {
        try {
            AuthService authService = new AuthService(LoginController.client);
            boolean success = authService.logout(SessionManager.getToken());

            if (success) {
                SessionManager.clear();

                Stage dashboardStage = (Stage) lecturerNameLabel.getScene().getWindow();
                dashboardStage.close();

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
}