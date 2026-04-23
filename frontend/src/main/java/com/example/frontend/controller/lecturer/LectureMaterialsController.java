package com.example.frontend.controller.lecturer;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.dto.CourseMaterialRequestDTO;
import com.example.frontend.service.CourseMaterialService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.frontend.model.CourseMaterialRow;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DatePicker;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LectureMaterialsController {

    @FXML private Label courseCodeLabel;
    @FXML private TextField titleField;
    @FXML private TextField filePathField;
    @FXML private DatePicker deadlinePicker;
    @FXML private TableView<CourseMaterialRow> materialsTable;
    @FXML private TableColumn<CourseMaterialRow, Number> materialIdColumn;
    @FXML private TableColumn<CourseMaterialRow, String> titleColumn;
    @FXML private TableColumn<CourseMaterialRow, String> filePathColumn;
    @FXML private TableColumn<CourseMaterialRow, String> deadlineColumn;
    @FXML private TableColumn<CourseMaterialRow, String> uploadedAtColumn;
    @FXML private Label statusLabel;

    private final CourseMaterialService courseMaterialService = new CourseMaterialService(LoginController.client);
    private final ObjectMapper mapper = new ObjectMapper();
    private String courseCode;

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
        if (courseCodeLabel != null) {
            courseCodeLabel.setText(courseCode == null ? "Unknown course" : courseCode);
        }
        refreshMaterials();
    }

    @FXML
    public void initialize() {
        materialIdColumn.setCellValueFactory(new PropertyValueFactory<>("materialId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        filePathColumn.setCellValueFactory(new PropertyValueFactory<>("filePath"));
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        uploadedAtColumn.setCellValueFactory(new PropertyValueFactory<>("uploadedAt"));

        materialsTable.getSelectionModel().selectedItemProperty().addListener((ChangeListener<CourseMaterialRow>) (obs, oldValue, newValue) -> {
            if (newValue != null && newValue.getDeadline() != null && !newValue.getDeadline().isBlank()) {
                try {
                    deadlinePicker.setValue(LocalDate.parse(newValue.getDeadline()));
                } catch (Exception ignored) {
                    deadlinePicker.setValue(null);
                }
            }
        });

        if (courseCodeLabel != null) {
            courseCodeLabel.setText(courseCode == null ? "Course materials" : courseCode);
        }
    }

    @FXML
    private void browseFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Lecture Material");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Documents", "*.doc", "*.docx", "*.ppt", "*.pptx", "*.pdf"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = chooser.showOpenDialog(filePathField.getScene().getWindow());
        if (file != null) {
            filePathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void uploadMaterial() {
        String title = titleField.getText() == null ? "" : titleField.getText().trim();
        String filePath = filePathField.getText() == null ? "" : filePathField.getText().trim();
        String deadline = deadlinePicker.getValue() == null ? null : deadlinePicker.getValue().toString();

        if (courseCode == null || courseCode.isBlank()) {
            showStatus("Course is missing.", false);
            return;
        }
        if (title.isBlank()) {
            showStatus("Enter a material title.", false);
            return;
        }
        if (filePath.isBlank()) {
            showStatus("Choose a file to upload.", false);
            return;
        }

        CourseMaterialRequestDTO dto = new CourseMaterialRequestDTO(courseCode, title, filePath);
        dto.setDeadline(deadline);
        JsonNode response = courseMaterialService.uploadCourseMaterial(dto);

        if (response != null && response.path("success").asBoolean(false)) {
            showStatus(response.path("message").asText("Course material uploaded."), true);
            titleField.clear();
            filePathField.clear();
            deadlinePicker.setValue(null);
            refreshMaterials();
            showInfo("Success", response.path("message").asText("Course material uploaded."));
        } else {
            showStatus(response == null ? "No response from server." : response.path("message").asText("Upload failed."), false);
        }
    }

    @FXML
    private void refreshMaterials() {
        if (courseCode == null || courseCode.isBlank()) {
            return;
        }

        JsonNode response = courseMaterialService.getCourseMaterials(courseCode);
        if (response == null || !response.path("success").asBoolean(false)) {
            showStatus(response == null ? "No response from server." : response.path("message").asText("Failed to load materials."), false);
            return;
        }

        List<CourseMaterialRow> rows = new ArrayList<>();
        JsonNode data = response.path("data");
        if (data.isArray()) {
            for (JsonNode node : data) {
                rows.add(new CourseMaterialRow(
                        node.path("materialId").asInt(),
                        node.path("title").asText(""),
                        node.path("filePath").asText(""),
                        node.path("deadline").isMissingNode() || node.path("deadline").isNull() ? "" : node.path("deadline").asText(""),
                        node.path("uploadedAt").asText("")
                ));
            }
        }

        materialsTable.getItems().setAll(rows);
        showStatus("Loaded " + rows.size() + " material(s).", true);
    }

    @FXML
    private void deleteSelectedMaterial() {
        CourseMaterialRow selected = materialsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus("Select a material to remove.", false);
            return;
        }

        JsonNode response = courseMaterialService.deleteCourseMaterial(selected.getMaterialId());
        if (response != null && response.path("success").asBoolean(false)) {
            showStatus(response.path("message").asText("Course material removed."), true);
            refreshMaterials();
        } else {
            showStatus(response == null ? "No response from server." : response.path("message").asText("Failed to remove material."), false);
        }
    }

    @FXML
    private void extendSelectedDeadline() {
        CourseMaterialRow selected = materialsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus("Select a material to extend.", false);
            return;
        }

        if (deadlinePicker.getValue() == null) {
            showStatus("Choose a new deadline date.", false);
            return;
        }

        JsonNode response = courseMaterialService.updateDeadline(selected.getMaterialId(), deadlinePicker.getValue().toString());
        if (response != null && response.path("success").asBoolean(false)) {
            showStatus(response.path("message").asText("Deadline updated."), true);
            refreshMaterials();
        } else {
            showStatus(response == null ? "No response from server." : response.path("message").asText("Failed to update deadline."), false);
        }
    }

    @FXML
    private void backToCourses() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LecturerCourses.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showStatus("Failed to return to courses.", false);
        }
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle(success
                ? "-fx-text-fill: #74c69d; -fx-font-size: 12px; -fx-font-weight: bold;"
                : "-fx-text-fill: #ff7b7b; -fx-font-size: 12px; -fx-font-weight: bold;");
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}