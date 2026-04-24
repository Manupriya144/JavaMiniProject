package com.example.frontend.controller.student;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.dto.NoticeResponseDTO;
import com.example.frontend.model.Student;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.AuthService;
import com.example.frontend.service.NoticeService;
import com.example.frontend.service.StudentService;
import com.example.frontend.session.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
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
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

    @FXML private ImageView profileImage;
    @FXML private Label profileInitial;

    private String studentName = LoginController.username;
    private String studentRegNo = LoginController.reNo;
    public static final ServerClient client = ServerClient.getInstance();
    private final NoticeService noticeService = new NoticeService(ServerClient.getInstance());

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

        if (username != null && !username.isEmpty()) {
            profileInitial.setText(username.substring(0, 1).toUpperCase());
        } else {
            profileInitial.setText("U");
        }

        profileImage.setVisible(false);
        profileInitial.setVisible(true);
    }

    private void loadStats() {
        overallAttendanceLabel.setText("87%");
        sgpaLabel.setText("3.45");
        cgpaLabel.setText("3.28");
        enrolledCoursesLabel.setText("4");
    }

    private void checkEligibility() {
        boolean eligible = true;

        if (eligible) {
            eligibilityAlertBox.setStyle(
                    "-fx-background-color: #f6fff7; -fx-background-radius: 10; " +
                            "-fx-border-color: #4cba52; -fx-border-radius: 10; -fx-border-width: 1;"
            );
            eligibilityStatusLabel.setText(
                    "✅  You are eligible for all final examinations. (CA ≥ 40% & Attendance ≥ 80%)"
            );
            eligibilityStatusLabel.setStyle("-fx-text-fill: #5f748a; -fx-font-size: 12px;");
        } else {
            eligibilityAlertBox.setStyle(
                    "-fx-background-color: #fff8f8; -fx-background-radius: 10; " +
                            "-fx-border-color: #e85d5d; -fx-border-radius: 10; -fx-border-width: 1;"
            );
            eligibilityStatusLabel.setText(
                    "❌  You are NOT eligible for one or more exams. Check attendance or CA marks."
            );
            eligibilityStatusLabel.setStyle("-fx-text-fill: #5f748a; -fx-font-size: 12px;");
        }
    }

    private void loadCourses() {
        coursesContainer.getChildren().clear();

        com.example.frontend.service.AcademicEndpointService academicService = new com.example.frontend.service.AcademicEndpointService(client);
        com.fasterxml.jackson.databind.JsonNode response = academicService.getMyCourses();

        if (response != null && response.has("success") && response.get("success").asBoolean()) {
            com.fasterxml.jackson.databind.JsonNode data = response.get("data");
            if (data != null && data.isArray()) {
                int count = 0;
                for (com.fasterxml.jackson.databind.JsonNode node : data) {
                    if (count >= 4) break; // limit to 4 on dashboard
                    String code = node.has("courseCode") ? node.get("courseCode").asText() : "";
                    String name = node.has("courseName") ? node.get("courseName").asText() : "";
                    String credits = node.has("courseCredit") ? node.get("courseCredit").asText() : "";
                    String type = node.has("registrationType") ? node.get("registrationType").asText() : "";
                    coursesContainer.getChildren().add(buildCourseRow(code, name, credits, type));
                    count++;
                }
                
                enrolledCoursesLabel.setText(String.valueOf(data.size()));
            } else {
                enrolledCoursesLabel.setText("0");
            }
        } else {
            enrolledCoursesLabel.setText("0");
        }
    }

    private HBox buildCourseRow(String code, String name, String credits, String grade) {
        HBox row = new HBox(0);
        row.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-background-radius: 6; " +
                        "-fx-border-color: #e8eef5; " +
                        "-fx-border-radius: 6; " +
                        "-fx-border-width: 1;"
        );
        row.setPadding(new Insets(10, 12, 10, 12));

        Label codeLbl = new Label(code);
        codeLbl.setPrefWidth(110);

        Label nameLbl = new Label(name);
        nameLbl.setPrefWidth(200);

        Label credLbl = new Label(credits);
        credLbl.setPrefWidth(70);

        Label gradeLbl = new Label(grade);
        gradeLbl.setPrefWidth(70);

        String base = "-fx-text-fill: #1a3a52; -fx-font-size: 12px;";
        codeLbl.setStyle(base);
        nameLbl.setStyle(base);
        credLbl.setStyle(base);
        gradeLbl.setStyle("-fx-text-fill: #4cba52; -fx-font-size: 12px; -fx-font-weight: bold;");

        row.getChildren().addAll(codeLbl, nameLbl, credLbl, gradeLbl);
        return row;
    }

    private void loadNotices() {
        noticesContainer.getChildren().clear();

        try {
            List<NoticeResponseDTO> notices = noticeService.getAllNotices();

            if (notices == null || notices.isEmpty()) {
                noticesContainer.getChildren().add(buildEmptyNoticeItem());
                return;
            }

            int limit = Math.min(notices.size(), 3); // show only top 3 in dashboard

            for (int i = 0; i < limit; i++) {
                NoticeResponseDTO notice = notices.get(i);

                String title = safe(notice.getTitle());
                String date = formatNoticeDate(safe(notice.getCreated_at()));

                noticesContainer.getChildren().add(
                        buildNoticeItem("📢", title, date)
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            noticesContainer.getChildren().add(buildErrorNoticeItem());
        }
    }

    private VBox buildEmptyNoticeItem() {
        VBox item = new VBox(4);
        item.setStyle(
                "-fx-background-color: #f5f9ff; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: #d4e4f7; " +
                        "-fx-border-radius: 8; " +
                        "-fx-border-width: 1;"
        );
        item.setPadding(new Insets(12, 16, 12, 16));

        Label titleLbl = new Label("No notices available");
        titleLbl.setStyle("-fx-text-fill: #1a3a52; -fx-font-size: 12px; -fx-font-weight: bold;");

        Label dateLbl = new Label("Please check again later");
        dateLbl.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 10px;");

        item.getChildren().addAll(titleLbl, dateLbl);
        return item;
    }

    private VBox buildErrorNoticeItem() {
        VBox item = new VBox(4);
        item.setStyle(
                "-fx-background-color: #fff8f8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: #f1c0c0; " +
                        "-fx-border-radius: 8; " +
                        "-fx-border-width: 1;"
        );
        item.setPadding(new Insets(12, 16, 12, 16));

        Label titleLbl = new Label("Failed to load notices");
        titleLbl.setStyle("-fx-text-fill: #e85d5d; -fx-font-size: 12px; -fx-font-weight: bold;");

        Label dateLbl = new Label("Please try again");
        dateLbl.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 10px;");

        item.getChildren().addAll(titleLbl, dateLbl);
        return item;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String formatNoticeDate(String value) {
        if (value == null || value.isBlank()) {
            return "—";
        }
        return value.replace("T", " ");
    }

    private VBox buildNoticeItem(String icon, String title, String date) {
        VBox item = new VBox(4);
        item.setStyle(
                "-fx-background-color: #f5f9ff; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: #d4e4f7; " +
                        "-fx-border-radius: 8; " +
                        "-fx-border-width: 1;"
        );
        item.setPadding(new Insets(12, 16, 12, 16));

        Label titleLbl = new Label(icon + "  " + title);
        titleLbl.setStyle("-fx-text-fill: #1a3a52; -fx-font-size: 12px; -fx-font-weight: bold;");
        titleLbl.setWrapText(true);

        Label dateLbl = new Label(date);
        dateLbl.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 10px;");

        item.getChildren().addAll(titleLbl, dateLbl);
        return item;
    }

    @FXML private void openCourses() { loadView("student/StudentCourses.fxml"); }
    @FXML private void openAttendance() { loadView("StudentAttendance.fxml"); }
    @FXML private void openMedical() { loadView("StudentMedical.fxml"); }
    @FXML private void openGrades() { loadView("StudentGrades.fxml"); }
    @FXML private void openTimetable() { loadView("admin/DisplayTimeTable.fxml"); }
    @FXML private void openNotices() { loadView("admin/NoticeDisplay.fxml"); }
    @FXML private void openEligibility() { loadView("StudentEligibility.fxml"); }
    @FXML private void openStudentFinalMarks() { loadView("ViewStudentFinalMarks.fxml"); }
    @FXML private void openStudentGrades() { loadView("StudentGrades.fxml"); }

    @FXML
    private void openProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/student/StudentProfile.fxml")
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
            AuthService authService = new AuthService(LoginController.client);
            boolean success = authService.logout(SessionManager.getToken());

            if (success) {
                SessionManager.clear();

                Stage dashboardStage = (Stage) studentNameLabel.getScene().getWindow();
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
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}