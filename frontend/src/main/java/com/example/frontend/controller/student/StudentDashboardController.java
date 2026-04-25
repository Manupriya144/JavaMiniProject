package com.example.frontend.controller.student;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.dto.NoticeResponseDTO;
import com.example.frontend.model.Student;
import com.example.frontend.model.StudentCourseDashboard;
import com.example.frontend.model.StudentDashboardData;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.AuthService;
import com.example.frontend.service.NoticeService;
import com.example.frontend.service.StudentDashboardService;
import com.example.frontend.service.StudentService;
import com.example.frontend.session.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
    @FXML private MenuButton medicalsMenuButton;

    @FXML private ImageView profileImage;
    @FXML private Label profileInitial;
    @FXML private ImageView welcomeAvatarImage;
    @FXML private Label welcomeAvatarInitial;

    private String studentName = LoginController.username;
    private String studentRegNo = LoginController.reNo;

    public static final ServerClient client = ServerClient.getInstance();

    private final NoticeService noticeService =
            new NoticeService(ServerClient.getInstance());

    private final StudentDashboardService dashboardService =
            new StudentDashboardService(ServerClient.getInstance());

    private StudentDashboardData dashboardData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String today = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));

        dateLabel.setText(today + "  •  Undergraduate Portal");
        statusBarTime.setText(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("d MMMM yyyy")));

        if (studentName == null || studentName.isBlank()) {
            studentName = "Student";
        }

        if (studentRegNo == null || studentRegNo.isBlank()) {
            studentRegNo = "—";
        }

        welcomeLabel.setText("Welcome back, " + studentName + "! 👋");
        studentNameLabel.setText(studentName);
        studentIdLabel.setText("Reg No: " + studentRegNo);

        StudentService studentService = new StudentService(client);
        Student student = studentService.getStudentByIdAll(LoginController.userId);

        if (student != null) {
            setupProfile(student);
        } else {
            setupDefaultProfile();
        }

        styleMedicalsMenuButton();

        loadDashboardData();
        loadNotices();
        checkEligibility();
    }

    private void loadDashboardData() {
        coursesContainer.getChildren().clear();

        dashboardData = dashboardService.getDashboardData();

        if (dashboardData == null) {
            overallAttendanceLabel.setText("0%");
            sgpaLabel.setText("0.00");
            cgpaLabel.setText("0.00");
            enrolledCoursesLabel.setText("0");

            Label error = new Label("Failed to load dashboard data.");
            error.setStyle("-fx-text-fill: #e85d5d; -fx-font-size: 13px;");
            coursesContainer.getChildren().add(error);
            return;
        }

        overallAttendanceLabel.setText(String.format("%.1f%%", dashboardData.getOverallAttendance()));
        sgpaLabel.setText(String.format("%.2f", dashboardData.getSgpa()));
        cgpaLabel.setText(String.format("%.2f", dashboardData.getCgpa()));
        enrolledCoursesLabel.setText(String.valueOf(dashboardData.getEnrolledCourses()));

        loadCoursesFromData();
    }

    private void loadCoursesFromData() {
        coursesContainer.getChildren().clear();

        List<StudentCourseDashboard> courses = dashboardData.getCourses();

        if (courses == null || courses.isEmpty()) {
            Label empty = new Label("No registered courses found.");
            empty.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 13px;");
            coursesContainer.getChildren().add(empty);
            return;
        }

        for (StudentCourseDashboard c : courses) {
            coursesContainer.getChildren().add(
                    buildCourseRow(
                            c.getCourseCode(),
                            c.getCourseName(),
                            String.valueOf(c.getCourseCredit())
                    )
            );
        }
    }

    private HBox buildCourseRow(String code, String name, String credits) {
        HBox row = new HBox(0);
        row.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-background-radius: 6; " +
                        "-fx-border-color: #e8eef5; " +
                        "-fx-border-radius: 6; " +
                        "-fx-border-width: 1;"
        );
        row.setPadding(new Insets(10, 12, 10, 12));

        Label codeLbl = new Label(safe(code));
        codeLbl.setPrefWidth(110);

        Label nameLbl = new Label(safe(name));
        nameLbl.setPrefWidth(250);

        Label credLbl = new Label(safe(credits));
        credLbl.setPrefWidth(80);

        String base = "-fx-text-fill: #1a3a52; -fx-font-size: 12px;";
        codeLbl.setStyle(base);
        nameLbl.setStyle(base);
        credLbl.setStyle(base);

        row.getChildren().addAll(codeLbl, nameLbl, credLbl);
        return row;
    }

    private void checkEligibility() {
        double attendance = dashboardData == null ? 0 : dashboardData.getOverallAttendance();
        boolean eligible = attendance >= 80;

        if (eligible) {
            eligibilityAlertBox.setStyle(
                    "-fx-background-color: #f6fff7; -fx-background-radius: 10; " +
                            "-fx-border-color: #4cba52; -fx-border-radius: 10; -fx-border-width: 1;"
            );
            eligibilityStatusLabel.setText(
                    "✅  You are eligible based on attendance. Current attendance: " +
                            String.format("%.1f%%", attendance)
            );
        } else {
            eligibilityAlertBox.setStyle(
                    "-fx-background-color: #fff8f8; -fx-background-radius: 10; " +
                            "-fx-border-color: #e85d5d; -fx-border-radius: 10; -fx-border-width: 1;"
            );
            eligibilityStatusLabel.setText(
                    "❌  Attendance is below 80%. Current attendance: " +
                            String.format("%.1f%%", attendance)
            );
        }

        eligibilityStatusLabel.setStyle("-fx-text-fill: #5f748a; -fx-font-size: 12px;");
    }

    private void setupProfile(Student student) {
        String profilePicture = student.getProfilePicture();
        String username = student.getUsername();

        String initial = (username != null && !username.isBlank())
                ? username.substring(0, 1).toUpperCase()
                : LoginController.username.toUpperCase().substring(0,1);

        if (profilePicture != null && !profilePicture.isBlank()) {
            try {
                File file = new File(profilePicture);

                if (file.exists()) {
                    Image image = new Image(file.toURI().toString(), false);

                    profileImage.setImage(image);
                    profileImage.setFitWidth(36);
                    profileImage.setFitHeight(36);
                    profileImage.setClip(new Circle(18, 18, 18));
                    profileImage.setVisible(true);
                    profileInitial.setVisible(false);


                    welcomeAvatarImage.setImage(image);
                    welcomeAvatarImage.setFitWidth(58);
                    welcomeAvatarImage.setFitHeight(58);
                    welcomeAvatarImage.setClip(new Circle(29, 29, 29));
                    welcomeAvatarImage.setVisible(true);
                    welcomeAvatarInitial.setVisible(false);

                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        profileImage.setVisible(false);
        profileInitial.setText(initial);
        profileInitial.setVisible(true);

        welcomeAvatarImage.setVisible(false);
        welcomeAvatarInitial.setText(initial);
        welcomeAvatarInitial.setVisible(true);
    }

    private void setupDefaultProfile() {
        profileInitial.setText("U");
        profileImage.setVisible(false);
        profileInitial.setVisible(true);
    }

    private void styleMedicalsMenuButton() {
        Platform.runLater(() -> {
            if (medicalsMenuButton == null) {
                return;
            }

            var labelNode = medicalsMenuButton.lookup(".label");
            if (labelNode != null) {
                labelNode.setStyle("-fx-text-fill: white;");
            }

            var arrowNode = medicalsMenuButton.lookup(".arrow");
            if (arrowNode != null) {
                arrowNode.setStyle("-fx-background-color: white;");
            }
        });
    }

    private void loadNotices() {
        noticesContainer.getChildren().clear();

        try {
            List<NoticeResponseDTO> notices = noticeService.getAllNotices();

            if (notices == null || notices.isEmpty()) {
                noticesContainer.getChildren().add(buildEmptyNoticeItem());
                return;
            }

            int limit = Math.min(notices.size(), 3);

            for (int i = 0; i < limit; i++) {
                NoticeResponseDTO notice = notices.get(i);

                noticesContainer.getChildren().add(
                        buildNoticeItem(
                                "📢",
                                safe(notice.getTitle()),
                                formatNoticeDate(safe(notice.getCreated_at()))
                        )
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

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String formatNoticeDate(String value) {
        if (value == null || value.isBlank()) {
            return "—";
        }
        return value.replace("T", " ");
    }

    @FXML private void openCourses() { loadView("student/StudentCourses.fxml"); }
    @FXML private void openAttendance() { loadView("student/StudentAttendance.fxml"); }
    @FXML private void openTimetable() { loadView("admin/DisplayTimeTable.fxml"); }
    @FXML private void openNotices() { loadView("admin/NoticeDisplay.fxml"); }
    @FXML private void openEligibility() { loadView("student/StudentEligibility.fxml"); }
    @FXML private void openAddMedical() { loadView("student/StudentAddMedical.fxml"); }
    @FXML private void openViewMedical() { loadView("student/StudentViewMedical.fxml"); }

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

    @FXML
    private void openCourseRegistration() {
        loadView("student/StudentCourseRegistration.fxml");
    }
}