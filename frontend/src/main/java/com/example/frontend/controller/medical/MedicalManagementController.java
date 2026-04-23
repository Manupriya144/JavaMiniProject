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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class MedicalManagementController {

    @FXML
    private TextField batchField;
    @FXML
    private ComboBox<String> statusFilterCombo;
    @FXML
    private TextArea recordsTextArea;
    @FXML
    private TextField medicalIdField;
    @FXML
    private Label statusLabel;

    private final MedicalService medicalService = new MedicalService(LoginController.client);

    @FXML
    public void initialize() {
        statusFilterCombo.getItems().addAll("All", "Pending", "Approved", "Rejected");
        statusFilterCombo.getSelectionModel().select("All");
        showStatus("", StatusType.INFO);
    }

    @FXML
    private void loadBatchRecords() {
        String batch = batchField.getText() == null ? "" : batchField.getText().trim();

        if (batch.isEmpty()) {
            showStatus("Enter batch first.", StatusType.ERROR);
            return;
        }

        List<Medical> records = medicalService.getBatchMedicalRecords(batch);
        String statusFilter = statusFilterCombo.getValue();
        String text = formatRecords(records, statusFilter);

        recordsTextArea.setText(text);

        boolean hasVisibleRecords = text != null && !text.equals("No medical records found.");
        showStatus(hasVisibleRecords ? "Records loaded successfully." : medicalService.getLastMessage(),
                hasVisibleRecords ? StatusType.SUCCESS : StatusType.ERROR);
    }

    @FXML
    private void approveSelected() {
        Integer id = readMedicalId();
        if (id == null) {
            return;
        }

        boolean ok = medicalService.approveMedical(id);
        showStatus(ok ? "Medical #" + id + " approved." : medicalService.getLastMessage(),
                ok ? StatusType.SUCCESS : StatusType.ERROR);

        if (ok) {
            loadBatchRecords();
        }
    }

    @FXML
    private void rejectSelected() {
        Integer id = readMedicalId();
        if (id == null) {
            return;
        }

        boolean ok = medicalService.rejectMedical(id);
        showStatus(ok ? "Medical #" + id + " rejected." : medicalService.getLastMessage(),
                ok ? StatusType.SUCCESS : StatusType.ERROR);

        if (ok) {
            loadBatchRecords();
        }
    }

    @FXML
    private void backToDashboard() {
        loadView("/view/techofficer/techOfficerDashboard.fxml");
    }

    private Integer readMedicalId() {
        try {
            return Integer.parseInt(medicalIdField.getText().trim());
        } catch (Exception e) {
            showStatus("Enter valid medical id.", StatusType.ERROR);
            return null;
        }
    }

    private String formatRecords(List<Medical> records, String statusFilter) {
        if (records == null || records.isEmpty()) {
            return "No medical records found.";
        }

        StringBuilder sb = new StringBuilder();
        for (Medical m : records) {
            if (!"All".equalsIgnoreCase(statusFilter) && !statusFilter.equalsIgnoreCase(m.getStatus())) {
                continue;
            }

            sb.append("ID: ").append(m.getMedicalId())
                    .append(" | Student: ").append(value(m.getStudentId()))
                    .append(" | Course: ").append(value(m.getCourseId()))
                    .append(" | Exam: ").append(value(m.getExamType()))
                    .append(" | Date: ").append(value(m.getDateSubmitted()))
                    .append(" | Status: ").append(value(m.getStatus()))
                    .append("\nCopy: ").append(value(m.getMedicalCopy()))
                    .append("\n--------------------------------------------------\n");
        }

        return sb.length() == 0 ? "No medical records found." : sb.toString();
    }

    private String value(String v) {
        return v == null ? "-" : v;
    }

    private void loadView(String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot open view");
            alert.setContentText(path);
            alert.showAndWait();
        }
    }

    private enum StatusType {
        SUCCESS, ERROR, INFO
    }

    private void showStatus(String message, StatusType type) {
        statusLabel.setText(message);

        String color = switch (type) {
            case SUCCESS -> "#4cba52";
            case ERROR -> "#e85d5d";
            case INFO -> "#8fa3b8";
        };

        statusLabel.setStyle(
                "-fx-text-fill: " + color + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;"
        );
    }
}