package com.example.frontend.controller.lecturer;

import com.example.frontend.model.LecturerCourseItem;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.CourseMaterialService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;

public class AddCourseMaterialController {

    @FXML private Label courseNameLabel;
    @FXML private TextField titleField;
    @FXML private TextField filePathField;
    @FXML private Label statusLabel;

    private LecturerCourseItem selectedCourse;
    private CourseMaterialService courseMaterialService;

    @FXML
    public void initialize() {
        courseMaterialService = new CourseMaterialService(ServerClient.getInstance());
        hideStatus();
    }

    public void setCourse(LecturerCourseItem course) {
        this.selectedCourse = course;

        if (course != null) {
            courseNameLabel.setText(course.getCourseName() + " - Add Material");
        } else {
            courseNameLabel.setText("Add Material");
        }
    }

    @FXML
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Course Material");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("PowerPoint Files", "*.ppt", "*.pptx"),
                new FileChooser.ExtensionFilter("Word Files", "*.doc", "*.docx"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = fileChooser.showOpenDialog(filePathField.getScene().getWindow());

        if (file != null) {
            filePathField.setText(file.getAbsolutePath());
            hideStatus();
        }
    }

    @FXML
    private void saveMaterial() {
        try {
            String title = titleField.getText();
            String filePath = filePathField.getText();

            if (selectedCourse == null) {
                showError("Course is not selected.");
                return;
            }

            if (title == null || title.trim().isEmpty()) {
                showError("Please enter material title.");
                return;
            }

            if (filePath == null || filePath.trim().isEmpty()) {
                showError("Please select material file.");
                return;
            }

            boolean success = courseMaterialService.addMaterial(
                    selectedCourse.getCourseId(),
                    title.trim(),
                    filePath.trim()
            );

            if (success) {
                showSuccess("Material added successfully.");
                clearForm();
            } else {
                showError("Failed to add material.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error while adding material.");
        }
    }

    @FXML
    private void clearForm() {
        titleField.clear();
        filePathField.clear();
        hideStatus();
    }

    @FXML
    private void goBack() {
        openCourseMaterialsPage();
    }

    private void openCourseMaterialsPage() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/lecturer/CourseMaterials.fxml")
            );

            Parent root = loader.load();

            CourseMaterialsController controller = loader.getController();
            controller.setCourse(selectedCourse);

            titleField.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to go back.");
        }
    }

    private void showSuccess(String message) {
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
    }

    private void showError(String message) {
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
    }

    private void hideStatus() {
        statusLabel.setText("");
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
    }
}