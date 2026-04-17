package com.example.frontend.controller.student;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.model.Student;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.AuthService;
import com.example.frontend.service.StudentService;
import com.example.frontend.session.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class StudentDashboardController implements Initializable {

    @FXML private Label welcomeLabel;
    @FXML private Label dateLabel;
    @FXML private Label studentNameLabel;
    @FXML private Label studentIdLabel;
    @FXML private Label overallAttendanceLabel;
    @FXML private Label sgpaLabel;
    @FXML private Label cgpaLabel;
    @FXML private Label enrolledCoursesLabel;
    @FXML private Label eligibilityStatusLabel;
    @FXML private Label statusBarTime;
    @FXML private VBox coursesContainer;
    @FXML private VBox noticesContainer;
    @FXML private HBox eligibilityAlertBox;
    @FXML private StackPane profileContainer;
    @FXML private Circle profileCircle;
    @FXML private ImageView profileImage;
    @FXML private Label profileInitial;

    private String studentName = LoginController.username;
    private String studentRegNo = LoginController.reNo;
    public static final ServerClient client = ServerClient.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String today = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        dateLabel.setText(today + "  •  Undergraduate Portal");
        statusBarTime.setText(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("d MMMM yyyy")));

        welcomeLabel.setText("Welcome back, " + studentName + "! 👋");
        studentNameLabel.setText(studentName);
        studentIdLabel.setText("Reg No: " + studentRegNo);

        StudentService studentService = new StudentService(client);
        Student student = studentService.getStudentByIdAll(LoginController.userId);
        setupProfile(student);

        loadStats();
        loadCourses();
        loadNotices();
        checkEligibility();
    }

    public void setStudentInfo(String name, String regNo) {
        this.studentName = name;
        this.studentRegNo = regNo;

    }

    private void setupProfile(Student student) {

        String profilePicture = student.getProfilePicture();
        String username = student.getUsername();

        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                File file = new File(profilePicture);

                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    profileImage.setImage(image);

                    // Make image circular
                    Circle clip = new Circle(18, 18, 18);
                    profileImage.setClip(clip);

                    profileImage.setVisible(true);
                    profileInitial.setVisible(false);
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // fallback → first letter
        if (username != null && !username.isEmpty()) {
            profileInitial.setText(username.substring(0, 1).toUpperCase());
        } else {
            profileInitial.setText("U");
        }

        profileImage.setVisible(false);
        profileInitial.setVisible(true);
    }

    // ─── DB Methods ──────────────────────────────────────────────────────────

    private void loadStats() {
        // TODO: replace with DB queries for this student
        overallAttendanceLabel.setText("87%");
        sgpaLabel.setText("3.45");
        cgpaLabel.setText("3.28");
        enrolledCoursesLabel.setText("4");
    }

    private void checkEligibility() {
        // TODO: real eligibility check (attendance ≥ 80% AND CA ≥ 40%)
        // Example: all eligible
        boolean eligible = true;
        if (eligible) {
            eligibilityAlertBox.setStyle(
                    "-fx-background-color: #162040; -fx-background-radius: 10; " +
                            "-fx-border-color: #28a745; -fx-border-radius: 10; -fx-border-width: 1;");
            eligibilityStatusLabel.setText(
                    "✅  You are eligible for all final examinations. (CA ≥ 40% & Attendance ≥ 80%)");
        } else {
            eligibilityAlertBox.setStyle(
                    "-fx-background-color: #162040; -fx-background-radius: 10; " +
                            "-fx-border-color: #c0392b; -fx-border-radius: 10; -fx-border-width: 1;");
            eligibilityStatusLabel.setText(
                    "❌  You are NOT eligible for one or more exams. Check attendance or CA marks.");
        }
    }

    private void loadCourses() {
        // TODO: replace with DB query
        String[][] courses = {
                {"ICT2112", "Object Oriented Programming", "3", "B+"},
                {"ICT2132", "OOP Practicum",               "2", "A"},
                {"ICT2142", "Data Structures",              "3", "B"},
                {"ICT2152", "Web Technologies",             "3", "A-"},
        };

        for (String[] c : courses) {
            coursesContainer.getChildren().add(buildCourseRow(c[0], c[1], c[2], c[3]));
        }
    }

    private HBox buildCourseRow(String code, String name, String credits, String grade) {
        HBox row = new HBox(0);
        row.setStyle("-fx-background-color: #1a2d50; -fx-background-radius: 6;");
        row.setPadding(new Insets(10, 12, 10, 12));

        Label codeLbl    = new Label(code);    codeLbl.setPrefWidth(110);
        Label nameLbl    = new Label(name);    nameLbl.setPrefWidth(200);
        Label credLbl    = new Label(credits); credLbl.setPrefWidth(70);
        Label gradeLbl   = new Label(grade);   gradeLbl.setPrefWidth(70);

        String base = "-fx-text-fill: #d0e4ff; -fx-font-size: 12px;";
        codeLbl.setStyle(base); nameLbl.setStyle(base); credLbl.setStyle(base);
        gradeLbl.setStyle("-fx-text-fill: #a8e6cf; -fx-font-size: 12px; -fx-font-weight: bold;");

        row.getChildren().addAll(codeLbl, nameLbl, credLbl, gradeLbl);
        return row;
    }

    private void loadNotices() {
        // TODO: replace with DB query
        String[][] notices = {
                {"📢", "Mid-Semester Exam Schedule Released", "2026-03-10"},
                {"📢", "Lab Maintenance – 15 March",         "2026-03-08"},
                {"📢", "Semester Registration Deadline",      "2026-03-05"},
        };

        for (String[] n : notices) {
            noticesContainer.getChildren().add(buildNoticeItem(n[0], n[1], n[2]));
        }
    }

    private VBox buildNoticeItem(String icon, String title, String date) {
        VBox item = new VBox(4);
        item.setStyle("-fx-background-color: #1e3c72; -fx-background-radius: 8;");
        item.setPadding(new Insets(12, 16, 12, 16));

        Label titleLbl = new Label(icon + "  " + title);
        titleLbl.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        titleLbl.setWrapText(true);

        Label dateLbl = new Label(date);
        dateLbl.setStyle("-fx-text-fill: #6a90c8; -fx-font-size: 10px;");

        item.getChildren().addAll(titleLbl, dateLbl);
        return item;
    }

    // ─── Navigation ──────────────────────────────────────────────────────────

    @FXML private void openCourses()    { loadView("StudentCourses.fxml"); }
    @FXML private void openAttendance() { loadView("StudentAttendance.fxml"); }
    @FXML private void openMedical()    { loadView("StudentMedical.fxml"); }
    @FXML private void openGrades()     { loadView("StudentGrades.fxml"); }
    @FXML private void openTimetable()  { loadView("StudentTimetable.fxml"); }
    @FXML private void openNotices()    { loadView("NoticesView.fxml"); }
    @FXML private void openEligibility(){ loadView("StudentEligibility.fxml"); }
//    @FXML private void openMyAcademicEndpoints() { loadView("MyAcademicEndpoints.fxml"); }
    @FXML private void openStudentFinalMarks() { loadView("ViewStudentFinalMarks.fxml"); }
    @FXML private void openStudentGrades() { loadView("StudentGrades.fxml");}

        @FXML
    private void openProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/StudentProfile.fxml")
            );
            Parent root = loader.load();

            StudentProfileController controller = loader.getController();

            StudentService studentService = new StudentService(client);
            Student student = studentService.getStudentByIdAll(LoginController.userId);

            controller.setStudent(student);

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
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
                Stage dashboardStage = (Stage) studentNameLabel.getScene().getWindow();
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
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}