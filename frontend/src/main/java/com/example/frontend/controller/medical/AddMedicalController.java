package com.example.frontend.controller.medical;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.model.Medical;
import com.example.frontend.service.MedicalService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddMedicalController {

    @FXML
    private TextField studentIdField;
    @FXML
    private TextField courseIdField;
    @FXML
    private ComboBox<String> examTypeCombo;
    @FXML
    private DatePicker submittedDatePicker;
    @FXML
    private TextField medicalCopyField;
    @FXML
    private Label statusLabel;

    private final MedicalService medicalService = new MedicalService(LoginController.client);

    @FXML
    public void initialize() {
        examTypeCombo.getItems().addAll("Mid", "Final", "Attendance");
        examTypeCombo.getSelectionModel().select("Attendance");
    }

    @FXML
    private void submitMedical() {
        String studentId = studentIdField.getText();
        String courseId = courseIdField.getText();
        String examType = examTypeCombo.getValue();
        String date = submittedDatePicker.getValue() == null ? null : submittedDatePicker.getValue().toString();
        String copy = medicalCopyField.getText();

        if (studentId == null || studentId.isBlank()) {
            statusLabel.setText("Student ID is required.");
            return;
        }
        if (courseId == null || courseId.isBlank()) {
            statusLabel.setText("Course ID is required.");
            return;
        }
        if (date == null || date.isBlank()) {
            statusLabel.setText("Date submitted is required and must match session date.");
            return;
        }

        Medical added = medicalService.addMedical(studentId, courseId, examType, date, copy);
        if (added != null) {
            statusLabel.setText("Added medical record #" + added.getMedicalId() + " with Pending status.");
            clearForm();
        } else {
            statusLabel.setText(medicalService.getLastMessage());
        }
    }

    @FXML
    private void backToDashboard() {
        loadView("/view/techOfficerDashboard.fxml");
    }

    private void clearForm() {
        studentIdField.clear();
        courseIdField.clear();
        medicalCopyField.clear();
        submittedDatePicker.setValue(null);
        examTypeCombo.getSelectionModel().select("Attendance");
    }

    private void loadView(String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot open view");
            alert.setContentText(path);
            alert.showAndWait();
        }
    }
}
