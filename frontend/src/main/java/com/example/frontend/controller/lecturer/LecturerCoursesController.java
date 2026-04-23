package com.example.frontend.controller.lecturer;

import com.example.frontend.controller.admin.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.net.URL;
import java.util.ResourceBundle;

public class LecturerCoursesController implements Initializable {

    @FXML
    private Label lecturerNameLabel;
    @FXML
    private Label totalCoursesLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private VBox coursesContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lecturerNameLabel.setText(LoginController.username == null || LoginController.username.isBlank()
                ? "Lecturer"
                : LoginController.username);

        loadCourses();
    }

    private void loadCourses() {
        coursesContainer.getChildren().clear();

        String[][] courses = {
                {"ICT2112", "Object Oriented Programming", "3", "Semester 1", "20"},
                {"ICT2132", "OOP Practicum", "2", "Semester 1", "20"},
                {"ICT2142", "Data Structures", "3", "Semester 2", "20"},
                {"ICT2152", "Web Technologies", "3", "Semester 2", "20"}
        };

        totalCoursesLabel.setText(String.valueOf(courses.length));
        statusLabel.setText("Loaded " + courses.length + " assigned course(s).");

        for (String[] course : courses) {
            coursesContainer.getChildren().add(buildCourseCard(course));
        }
    }

    private VBox buildCourseCard(String[] course) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: #162040; -fx-background-radius: 12; -fx-border-color: #1e3c72; -fx-border-radius: 12;");
        card.setPadding(new Insets(18));

        Label codeLabel = new Label(course[0]);
        codeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label nameLabel = new Label(course[1]);
        nameLabel.setStyle("-fx-text-fill: #a0c4ff; -fx-font-size: 14px;");

        HBox metaRow = new HBox(24);
        metaRow.getChildren().addAll(
                createMetaLabel("Credits: " + course[2]),
                createMetaLabel(course[3]),
                createMetaLabel("Students: " + course[4])
        );

        HBox actions = new HBox(10);
        Button marksButton = new Button("Upload Marks");
        marksButton.setStyle("-fx-background-color: #2a5298; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
        marksButton.setOnAction(event -> openMarks());

        Button caButton = new Button("CA Management");
        caButton.setStyle("-fx-background-color: #1e3c72; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
        caButton.setOnAction(event -> openCA());

        Button materialsButton = new Button("Lecture Materials");
        materialsButton.setStyle("-fx-background-color: #155724; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
        materialsButton.setOnAction(event -> openLectureMaterials(course[0]));

        actions.getChildren().addAll(marksButton, caButton, materialsButton);

        card.getChildren().addAll(codeLabel, nameLabel, metaRow, actions);
        return card;
    }

    private Label createMetaLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #d0e4ff; -fx-font-size: 12px;");
        return label;
    }

    @FXML
    private void backToDashboard() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/lecturerDashboard.fxml"));
        Stage stage = (Stage) coursesContainer.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void openMarks() {
        loadView("UploadFinalMarks.fxml");
    }

    @FXML
    private void openCA() {
        loadView("CAManagement.fxml");
    }

    private void openLectureMaterials(String courseCode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LectureMaterials.fxml"));
            Parent root = loader.load();

            LectureMaterialsController controller = loader.getController();
            controller.setCourseCode(courseCode);

            Stage stage = (Stage) coursesContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Failed to open lecture materials for " + courseCode + ".");
        }
    }

    private void loadView(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/" + fxmlFile));
            Stage stage = (Stage) coursesContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            statusLabel.setText("Failed to open " + fxmlFile);
        }
    }
}
