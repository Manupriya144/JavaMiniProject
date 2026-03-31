package com.example.frontend.controller;

import com.example.frontend.service.AttendanceService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class AdvancedAttendanceViewController {

    @FXML
    private ComboBox<String> modeComboBox;
    @FXML
    private ComboBox<String> viewTypeComboBox;
    @FXML
    private TextField targetField;
    @FXML
    private Label targetLabel;
    @FXML
    private TextArea detailsArea;
    @FXML
    private TextArea summaryArea;
    @FXML
    private Label statusLabel;

    private final AttendanceService attendanceService = new AttendanceService(LoginController.client);
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        modeComboBox.getItems().addAll("Individual", "Batch");
        modeComboBox.setValue("Individual");
        viewTypeComboBox.getItems().addAll("Theory", "Practical", "Combined");
        viewTypeComboBox.setValue("Combined");
        targetLabel.setText("Student ID");
        targetField.setPromptText("Enter student user_id");
        modeComboBox.setOnAction(event -> onModeChanged());
    }

    @FXML
    private void loadDetails() {
        String target = targetField.getText() == null ? "" : targetField.getText().trim();
        if (target.isEmpty()) {
            statusLabel.setText("Please enter target value.");
            return;
        }
        String viewType = viewTypeComboBox.getValue();
        JsonNode response = "Individual".equals(modeComboBox.getValue())
                ? attendanceService.getStudentAttendance(target, viewType)
                : attendanceService.getBatchAttendance(target, viewType);
        detailsArea.setText(pretty(response));
        statusLabel.setText("Details loaded.");
    }

    @FXML
    private void loadSummary() {
        String target = targetField.getText() == null ? "" : targetField.getText().trim();
        if (target.isEmpty()) {
            statusLabel.setText("Please enter target value.");
            return;
        }
        String viewType = viewTypeComboBox.getValue();
        JsonNode response = "Individual".equals(modeComboBox.getValue())
                ? attendanceService.getStudentAttendanceSummary(target, viewType)
                : attendanceService.getBatchAttendanceSummary(target, viewType);
        summaryArea.setText(pretty(response));
        statusLabel.setText("Summary loaded.");
    }

    @FXML
    private void backToDashboard() {
        loadView("/view/techOfficerDashboard.fxml");
    }

    private void onModeChanged() {
        if ("Batch".equals(modeComboBox.getValue())) {
            targetLabel.setText("Batch");
            targetField.setPromptText("Enter batch (e.g. 2023)");
        } else {
            targetLabel.setText("Student ID");
            targetField.setPromptText("Enter student user_id");
        }
    }

    private String pretty(JsonNode node) {
        try {
            if (node == null) {
                return "{ \"success\": false, \"message\": \"" + attendanceService.getLastMessage() + "\" }";
            }
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (Exception e) {
            return node == null ? "" : node.toString();
        }
    }

    private void loadView(String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) detailsArea.getScene().getWindow();
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
