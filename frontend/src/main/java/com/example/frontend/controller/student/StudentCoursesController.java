package com.example.frontend.controller.student;

import com.example.frontend.model.StudentRegisteredCourse;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.StudentCourseService;
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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StudentCoursesController implements Initializable {

    @FXML private VBox coursesContainer;

    private final StudentCourseService studentCourseService =
            new StudentCourseService(ServerClient.getInstance());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadRegisteredCourses();
    }

    private void loadRegisteredCourses() {
        coursesContainer.getChildren().clear();

        List<StudentRegisteredCourse> courses = studentCourseService.getRegisteredCourses();

        if (courses == null || courses.isEmpty()) {
            Label empty = new Label("No registered courses found.");
            empty.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 13px;");
            coursesContainer.getChildren().add(empty);
            return;
        }

        for (StudentRegisteredCourse course : courses) {
            coursesContainer.getChildren().add(buildCourseRow(course));
        }
    }

    private HBox buildCourseRow(StudentRegisteredCourse course) {
        HBox row = new HBox(0);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #e8eef5;" +
                "-fx-border-radius: 10;" +
                "-fx-border-width: 1;"
        );

        Label codeLbl = createLabel(course.getCourseCode(), 150);
        Label nameLbl = createLabel(course.getCourseName(), 420);
        Label creditLbl = createLabel(String.valueOf(course.getCourseCredit()), 100);

        Button viewBtn = new Button("View Materials");
        viewBtn.setPrefWidth(150);
        viewBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #5b9fd9, #4c8fce);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );

        viewBtn.setOnAction(e -> openMaterials(course));

        row.getChildren().addAll(codeLbl, nameLbl, creditLbl, viewBtn);
        return row;
    }

    private Label createLabel(String text, double width) {
        Label label = new Label(text == null ? "" : text);
        label.setPrefWidth(width);
        label.setStyle("-fx-text-fill: #1a3a52; -fx-font-size: 13px;");
        return label;
    }

    private void openMaterials(StudentRegisteredCourse course) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/student/StudentCourseMaterials.fxml")
            );

            Parent root = loader.load();

            StudentCourseMaterialsController controller = loader.getController();
            controller.setCourse(course);

            Stage stage = (Stage) coursesContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/view/student/studentDashboard.fxml")
            );

            Stage stage = (Stage) coursesContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}