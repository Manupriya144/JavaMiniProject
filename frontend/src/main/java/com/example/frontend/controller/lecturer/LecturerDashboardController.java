package com.example.frontend.controller.lecturer;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.service.AuthService;
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
    private int lecturerId = -1;

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

    public void setLecturerInfo(String name, int id) {
        this.lecturerName = name;
        this.lecturerId = id;
    }

    private void loadStats() {
        myCoursesLabel.setText("4");
        myStudentsLabel.setText("20");
        eligibleLabel.setText("16");
        pendingMarksLabel.setText("2");
    }

    private void loadCourses() {
        coursesContainer.getChildren().clear();

        String[][] courses = {
                {"ICT2112", "Object Oriented Programming", "3", "20"},
                {"ICT2132", "OOP Practicum", "2", "20"},
                {"ICT2142", "Data Structures", "3", "20"},
                {"ICT2152", "Web Technologies", "3", "20"}
        };

        for (String[] c : courses) {
            coursesContainer.getChildren().add(buildCourseRow(c[0], c[1], c[2], c[3]));
        }
    }

    private HBox buildCourseRow(String code, String name, String credits, String students) {
        HBox row = new HBox(0);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setSpacing(0);
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

        Label studLbl = new Label(students);
        studLbl.setPrefWidth(100);

        String textStyle = "-fx-text-fill: #1a3a52; -fx-font-size: 12px;";
        codeLbl.setStyle(textStyle);
        nameLbl.setStyle(textStyle);
        credLbl.setStyle(textStyle);
        studLbl.setStyle(textStyle);

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
        actionBtn.setOnAction(e -> openMarks());

        row.getChildren().addAll(codeLbl, nameLbl, credLbl, studLbl, actionBtn);
        return row;
    }

    @FXML
    private void openCourses() {
        loadView("LecturerCourses.fxml");
    }

    @FXML
    private void openCA() {
        loadView("techofficer/CAManagement.fxml");
    }

    @FXML
    private void openMarks() {
        loadView("MarksUpload.fxml");
    }

    @FXML
    private void openStudents() {
        loadView("StudentDetails.fxml");
    }

    @FXML
    private void openEligibility() {
        loadView("Eligibility.fxml");
    }

    @FXML
    private void openAttendance() {
        loadView("AttendanceView.fxml");
    }

    @FXML
    private void openAddSession() {
        loadView("lecturer/AddLectureSession.fxml");
    }

    @FXML
    private void openGrades() {
        loadView("GradesGPA.fxml");
    }

    @FXML
    private void openNotices() {
        loadView("NoticesView.fxml");
    }

    @FXML
    private void openProfile() {
        loadView("LecturerProfile.fxml");
    }

    @FXML
    private void openUploadFinalMarks() {
        loadView("UploadFinalMarks.fxml");
    }

    @FXML
    private void openUpdateFinalMarks() {
        loadView("UpdateFinalMarks.fxml");
    }

    @FXML
    private void openBatchFinalMarks() {
        loadView("BatchFinalMarks.fxml");
    }

    @FXML
    private void openGenerateGrades() {
        loadView("GenerateGrade.fxml");
    }

    @FXML
    private void openBatchGrades() {
        loadView("BatchGrades.fxml");
    }

    @FXML
    private void openBatchEligibility() {
        loadView("BatchFullEligibility.fxml");
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