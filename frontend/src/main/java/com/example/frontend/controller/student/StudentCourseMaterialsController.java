package com.example.frontend.controller.student;

import com.example.frontend.model.CourseMaterial;
import com.example.frontend.model.StudentRegisteredCourse;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.CourseMaterialService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.List;

public class StudentCourseMaterialsController {

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private VBox materialsContainer;

    private StudentRegisteredCourse selectedCourse;

    private final CourseMaterialService materialService =
            new CourseMaterialService(ServerClient.getInstance());

    public void setCourse(StudentRegisteredCourse course) {
        this.selectedCourse = course;

        titleLabel.setText(course.getCourseCode() + " Materials");
        subtitleLabel.setText(course.getCourseName());

        loadMaterials();
    }

    private void loadMaterials() {
        materialsContainer.getChildren().clear();

        List<CourseMaterial> materials =
                materialService.getMaterialsByCourse(selectedCourse.getCourseId());

        if (materials == null || materials.isEmpty()) {
            Label empty = new Label("No materials available.");
            empty.setStyle("-fx-text-fill: #8fa3b8;");
            materialsContainer.getChildren().add(empty);
            return;
        }

        for (CourseMaterial material : materials) {
            materialsContainer.getChildren().add(buildRow(material));
        }
    }

    private HBox buildRow(CourseMaterial material) {
        HBox row = new HBox(12);
        row.setPadding(new Insets(12));
        row.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");

        String titleText = material.getTitle();

        Label title = new Label("📄 " + titleText);
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #1a3a52; -fx-font-size: 14px;");
        title.setWrapText(true);

        Label fileName = new Label(getFileNameOnly(material.getFilePath()));
        fileName.setStyle("-fx-text-fill: gray;");
        fileName.setWrapText(true);

        VBox textBox = new VBox(4, title, fileName);

        // 🔥 IMPORTANT FIX
        textBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(textBox, javafx.scene.layout.Priority.ALWAYS);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button openBtn = new Button("Open PDF");
        openBtn.setPrefWidth(120);

        openBtn.setOnAction(e -> openPdf(material.getFilePath()));

        row.getChildren().addAll(textBox, spacer, openBtn);
        return row;
    }

    // 🔥 IMPORTANT: show only last name
    private String getFileNameOnly(String path) {
        if (path == null || path.isBlank()) return "No file";
        return new File(path).getName();
    }

    // 🔥 FINAL OPEN METHOD (WORKS ON ALL OS)
    private void openPdf(String path) {
        new Thread(() -> {
            try {
                System.out.println("PDF PATH FROM DB: " + path);

                if (path == null || path.isBlank()) {
                    showAlertUI("No file available.");
                    return;
                }

                File file = new File(path);

                if (!file.isAbsolute()) {
                    file = new File(System.getProperty("user.dir"), path);
                }

                System.out.println("Resolved path: " + file.getAbsolutePath());

                if (!file.exists()) {
                    showAlertUI("File not found: " + file.getAbsolutePath());
                    return;
                }

                String os = System.getProperty("os.name").toLowerCase();

                // ✅ Linux (YOU ARE USING THIS)
                if (os.contains("linux")) {
                    new ProcessBuilder("xdg-open", file.getAbsolutePath()).start();
                    return;
                }

                // ✅ Windows / Mac
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                    return;
                }

                showAlertUI("Cannot open file.");

            } catch (Exception e) {
                e.printStackTrace();
                showAlertUI("Error: " + e.getMessage());
            }
        }).start();
    }

    private void showAlertUI(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(msg);
            alert.show();
        });
    }

    @FXML
    private void goBack() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/view/student/StudentCourses.fxml")
            );

            Stage stage = (Stage) materialsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}