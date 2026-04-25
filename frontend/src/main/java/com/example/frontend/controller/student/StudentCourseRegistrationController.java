package com.example.frontend.controller.student;

import com.example.frontend.model.StudentCourseRegistration;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.StudentCourseRegistrationService;
import com.example.frontend.service.StudentCourseRegistrationService.RegistrationPageData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class StudentCourseRegistrationController {

    @FXML private Label periodStatusLabel;
    @FXML private VBox properCoursesContainer;
    @FXML private VBox repeatCoursesContainer;
    @FXML private Label statusLabel;

    private RegistrationPageData currentData;

    private final StudentCourseRegistrationService service =
            new StudentCourseRegistrationService(ServerClient.getInstance());

    @FXML
    public void initialize() {
        loadRegistrationCourses();
    }

    private void loadRegistrationCourses() {
        properCoursesContainer.getChildren().clear();
        repeatCoursesContainer.getChildren().clear();

        currentData = service.getRegistrationCourses();

        if (currentData == null) {
            periodStatusLabel.setText("❌ Registration status unavailable");
            showEmpty(properCoursesContainer, "Failed to load registration data.");
            showEmpty(repeatCoursesContainer, "Failed to load registration data.");
            statusLabel.setStyle("-fx-text-fill: #e85d5d;");
            statusLabel.setText("Failed to load course registration.");
            return;
        }

        if (currentData.isRegistrationOpen()) {
            periodStatusLabel.setText("🟢 Registration Open");
            statusLabel.setStyle("-fx-text-fill: #4cba52;");
            statusLabel.setText("Registration is open. You can register available courses.");
        } else {
            periodStatusLabel.setText("❌ Registration Closed");
            statusLabel.setStyle("-fx-text-fill: #e85d5d;");
            statusLabel.setText(currentData.getMessage());
        }

        renderCourses(
                properCoursesContainer,
                currentData.getProperCourses(),
                currentData.isRegistrationOpen()
        );

        renderCourses(
                repeatCoursesContainer,
                currentData.getRepeatCourses(),
                currentData.isRegistrationOpen()
        );
    }

    private void renderCourses(VBox container,
                               List<StudentCourseRegistration> courses,
                               boolean registrationOpen) {
        container.getChildren().clear();

        if (courses == null || courses.isEmpty()) {
            showEmpty(container, registrationOpen
                    ? "No courses available."
                    : "Registration is closed.");
            return;
        }

        for (StudentCourseRegistration course : courses) {
            container.getChildren().add(buildCourseRow(course, registrationOpen));
        }
    }

    private HBox buildCourseRow(StudentCourseRegistration course, boolean registrationOpen) {
        HBox row = new HBox(14);
        row.setPadding(new Insets(14, 16, 14, 16));
        row.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #e8eef5;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );

        VBox textBox = new VBox(4);

        Label title = new Label(
                safe(course.getCourseCode()) + " - " + safe(course.getCourseName())
        );
        title.setStyle("-fx-text-fill: #1a3a52; -fx-font-size: 14px; -fx-font-weight: bold;");
        title.setWrapText(true);

        Label meta = new Label(
                "Credits: " + course.getCourseCredit() +
                        "  •  Level: " + course.getAcademicLevel() +
                        "  •  Semester: " + safe(course.getSemester()) +
                        "  •  Type: " + safe(course.getRegistrationType())
        );
        meta.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 11px;");

        textBox.getChildren().addAll(title, meta);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button registerBtn = new Button();
        registerBtn.setPrefWidth(120);

        if (course.isAlreadyRegistered()) {
            registerBtn.setText("Registered");
            registerBtn.setDisable(true);
            registerBtn.setStyle(
                    "-fx-background-color: #e8eef5;" +
                            "-fx-text-fill: #8fa3b8;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 8;"
            );

        } else if (!registrationOpen) {
            registerBtn.setText("Closed");
            registerBtn.setDisable(true);
            registerBtn.setStyle(
                    "-fx-background-color: #f5f5f5;" +
                            "-fx-text-fill: #b0b0b0;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 8;"
            );

        } else {
            registerBtn.setText("Register");
            registerBtn.setStyle(
                    "-fx-background-color: #4cba52;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 8;" +
                            "-fx-cursor: hand;"
            );

            registerBtn.setOnAction(e -> registerCourse(course));
        }

        row.getChildren().addAll(textBox, spacer, registerBtn);
        return row;
    }

    private void registerCourse(StudentCourseRegistration course) {
        if (currentData == null || !currentData.isRegistrationOpen()) {
            statusLabel.setStyle("-fx-text-fill: #e85d5d;");
            statusLabel.setText("Registration is closed.");
            return;
        }

        if (course.isAlreadyRegistered()) {
            statusLabel.setStyle("-fx-text-fill: #8fa3b8;");
            statusLabel.setText("You already registered this course.");
            return;
        }

        boolean success = service.registerCourse(course);

        if (success) {
            statusLabel.setStyle("-fx-text-fill: #4cba52;");
            statusLabel.setText(course.getCourseCode() + " registered successfully.");
            loadRegistrationCourses();
        } else {
            statusLabel.setStyle("-fx-text-fill: #e85d5d;");
            statusLabel.setText("Failed to register " + course.getCourseCode() + ".");
        }
    }

    private void showEmpty(VBox container, String message) {
        Label empty = new Label(message);
        empty.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 13px;");
        container.getChildren().add(empty);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    @FXML
    private void goBack() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/view/student/studentDashboard.fxml")
            );

            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}