package com.example.frontend.controller.ca;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.model.CAMark;
import com.example.frontend.service.CAMarkService;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class CAManagementController {

    @FXML
    private TextField uploadStudentIdField;
    @FXML
    private TextField uploadAssessmentTypeIdField;
    @FXML
    private TextField uploadMarksField;

    @FXML
    private TextField updateMarkIdField;
    @FXML
    private TextField updateMarksField;

    @FXML
    private TextField studentMarksStudentIdField;
    @FXML
    private TextField studentMarksCourseIdField;

    @FXML
    private TextField batchMarksBatchField;
    @FXML
    private TextField batchMarksCourseIdField;

    @FXML
    private TextField studentEligibilityStudentIdField;
    @FXML
    private TextField studentEligibilityCourseIdField;

    @FXML
    private TextField batchEligibilityBatchField;
    @FXML
    private TextField batchEligibilityCourseIdField;

    @FXML
    private TextArea outputArea;
    @FXML
    private Label statusLabel;

    private final CAMarkService caService = new CAMarkService(LoginController.client);

    @FXML
    private void uploadCAMarks() {
        try {
            String studentId = uploadStudentIdField.getText().trim();
            Integer assessmentTypeId = Integer.parseInt(uploadAssessmentTypeIdField.getText().trim());
            Double marks = Double.parseDouble(uploadMarksField.getText().trim());
            CAMark saved = caService.uploadCAMarks(studentId, assessmentTypeId, marks);
            if (saved == null) {
                statusLabel.setText(caService.getLastMessage());
                return;
            }
            statusLabel.setText("CA mark uploaded.");
            outputArea.setText("Mark ID: " + saved.getMarkId() + "\nStudent: " + saved.getStudentId()
                    + "\nCourse: " + saved.getCourseId() + "\nAssessment: " + saved.getAssessmentName()
                    + "\nMarks: " + saved.getMarks());
        } catch (Exception e) {
            statusLabel.setText("Invalid upload input.");
        }
    }

    @FXML
    private void updateCAMarks() {
        try {
            Integer markId = Integer.parseInt(updateMarkIdField.getText().trim());
            Double marks = Double.parseDouble(updateMarksField.getText().trim());
            boolean ok = caService.updateCAMarks(markId, marks);
            statusLabel.setText(ok ? "CA mark updated." : caService.getLastMessage());
        } catch (Exception e) {
            statusLabel.setText("Invalid update input.");
        }
    }

    @FXML
    private void getStudentCAMarks() {
        String studentId = studentMarksStudentIdField.getText() == null ? "" : studentMarksStudentIdField.getText().trim();
        String courseId = studentMarksCourseIdField.getText() == null ? "" : studentMarksCourseIdField.getText().trim();
        List<CAMark> list = caService.getStudentCAMarks(studentId, courseId.isBlank() ? null : courseId);
        outputArea.setText(formatCAMarks(list));
        statusLabel.setText(list.isEmpty() ? caService.getLastMessage() : "Loaded student CA marks.");
    }

    @FXML
    private void getBatchCAMarks() {
        String batch = batchMarksBatchField.getText() == null ? "" : batchMarksBatchField.getText().trim();
        String courseId = batchMarksCourseIdField.getText() == null ? "" : batchMarksCourseIdField.getText().trim();
        List<CAMark> list = caService.getBatchCAMarks(batch, courseId.isBlank() ? null : courseId);
        outputArea.setText(formatCAMarks(list));
        statusLabel.setText(list.isEmpty() ? caService.getLastMessage() : "Loaded batch CA marks.");
    }

    @FXML
    private void checkCAEligibility() {
        String studentId = studentEligibilityStudentIdField.getText() == null ? "" : studentEligibilityStudentIdField.getText().trim();
        String courseId = studentEligibilityCourseIdField.getText() == null ? "" : studentEligibilityCourseIdField.getText().trim();
        JsonNode node = caService.checkCAEligibility(studentId, courseId);
        if (node == null || !node.path("success").asBoolean(false)) {
            statusLabel.setText(caService.getLastMessage());
            return;
        }
        JsonNode data = node.path("data");
        outputArea.setText("Student: " + data.path("studentId").asText("-")
                + "\nCourse: " + data.path("courseId").asText("-")
                + "\nCA %: " + data.path("caPercentage").asDouble(0.0)
                + "\nThreshold: " + data.path("thresholdPercent").asDouble(40.0)
                + "\nEligible: " + data.path("eligible").asBoolean(false));
        statusLabel.setText("Student CA eligibility checked.");
    }

    @FXML
    private void getBatchCAEligibilityReport() {
        String batch = batchEligibilityBatchField.getText() == null ? "" : batchEligibilityBatchField.getText().trim();
        String courseId = batchEligibilityCourseIdField.getText() == null ? "" : batchEligibilityCourseIdField.getText().trim();
        JsonNode node = caService.getBatchCAEligibilityReport(batch, courseId);
        if (node == null || !node.path("success").asBoolean(false) || !node.path("data").isArray()) {
            statusLabel.setText(caService.getLastMessage());
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (JsonNode row : node.path("data")) {
            sb.append(row.path("regNo").asText("-"))
                    .append(" | ").append(row.path("studentName").asText("-"))
                    .append(" | CA% ").append(row.path("caPercentage").asDouble(0.0))
                    .append(" | Eligible: ").append(row.path("eligible").asBoolean(false))
                    .append('\n');
        }
        outputArea.setText(sb.length() == 0 ? "No eligibility records found." : sb.toString());
        statusLabel.setText("Batch CA eligibility report loaded.");
    }

    @FXML
    private void backToDashboard() {
        loadView("/view/lecturerDashboard.fxml");
    }

    private String formatCAMarks(List<CAMark> list) {
        if (list == null || list.isEmpty()) {
            return "No CA marks found.";
        }
        StringBuilder sb = new StringBuilder();
        for (CAMark m : list) {
            sb.append("Mark ID: ").append(m.getMarkId())
                    .append(" | Student: ").append(value(m.getStudentId()))
                    .append(" | Course: ").append(value(m.getCourseId()))
                    .append(" | Assessment: ").append(value(m.getAssessmentName()))
                    .append(" (").append(m.getAssessmentWeight() == null ? "-" : m.getAssessmentWeight()).append("%)")
                    .append(" | Marks: ").append(m.getMarks() == null ? "-" : m.getMarks())
                    .append('\n');
        }
        return sb.toString();
    }

    private String value(String text) {
        return text == null ? "-" : text;
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
