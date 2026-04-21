package com.example.frontend.controller.admin;

import com.example.frontend.dto.TimeTableRequestDTO;
import com.example.frontend.service.TimeTableService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddTimeTableController {

    @FXML
    private Label adminNameLabel;

    @FXML
    private ComboBox<String> departmentComboBox;

    @FXML
    private ComboBox<Integer> academicLevelComboBox;

    @FXML
    private ComboBox<String> semesterComboBox;

    @FXML
    private TextField titleField;

    @FXML
    private TextField pdfPathField;

    @FXML
    private Label statusLabel;

    @FXML
    private Label statusBarTime;

    @FXML
    private Button addBtn;

    private TimeTableService timeTableService;

    public void setTimeTableService(TimeTableService timeTableService) {
        this.timeTableService = timeTableService;
    }

    @FXML
    public void initialize() {
        statusBarTime.setText(
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
        );

        academicLevelComboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4));
        semesterComboBox.setItems(FXCollections.observableArrayList("1", "2"));

        // Example department ids. Replace these with real values from DB/service if needed.
        departmentComboBox.setItems(FXCollections.observableArrayList(
                "", "ET", "BST", "ICT"
        ));
    }

    @FXML
    public void browsePdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select TimeTable PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File file = fileChooser.showOpenDialog(pdfPathField.getScene().getWindow());
        if (file != null) {
            pdfPathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void addTimeTable() {
        String departmentId = departmentComboBox.getValue();
        Integer academicLevel = academicLevelComboBox.getValue();
        String semester = semesterComboBox.getValue();
        String title = titleField.getText() != null ? titleField.getText().trim() : "";
        String pdfPath = pdfPathField.getText() != null ? pdfPathField.getText().trim() : "";

        if (departmentId == null || departmentId.isEmpty()) {
            showStatus("Department is required.", false);
            return;
        }

        if (academicLevel == null) {
            showStatus("Academic level is required.", false);
            return;
        }

        if (semester == null || semester.isEmpty()) {
            showStatus("Semester is required.", false);
            return;
        }

        if (pdfPath.isEmpty()) {
            showStatus("PDF file path is required.", false);
            return;
        }

        TimeTableRequestDTO dto = new TimeTableRequestDTO();
        dto.setDepartmentId(departmentId);
        dto.setAcademicLevel(academicLevel);
        dto.setSemester(semester);
        dto.setTitle(title);
        dto.setPdfFilePath(pdfPath);

        if (timeTableService == null) {
            showStatus("TimeTable service is not initialized.", false);
            return;
        }

        addBtn.setDisable(true);

        try {
            boolean success = timeTableService.createTimeTable(dto);

            if (success) {
                showStatus("TimeTable added successfully.", true);
                clearForm();
                showAlert(Alert.AlertType.INFORMATION, "Success", "TimeTable added successfully.");
            } else {
                showStatus("Failed to add TimeTable.", false);
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add TimeTable.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showStatus("Unexpected error occurred.", false);
            showAlert(Alert.AlertType.ERROR, "Error", "Unexpected error occurred.");
        } finally {
            addBtn.setDisable(false);
        }
    }

    @FXML
    public void clearForm() {
        departmentComboBox.setValue(null);
        academicLevelComboBox.setValue(null);
        semesterComboBox.setValue(null);
        titleField.clear();
        pdfPathField.clear();
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
        statusLabel.setText("");
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminDashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) adminNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showStatus("Failed to load dashboard", false);
        }
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle(success
                ? "-fx-text-fill: #28a745; -fx-font-size: 12px; -fx-font-weight: bold;"
                : "-fx-text-fill: #ff6b6b; -fx-font-size: 12px; -fx-font-weight: bold;");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}