package com.example.frontend.controller.grade;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.service.GradeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class BatchGradesController {

    @FXML
    private ComboBox<String> batchCombo;
    @FXML
    private ComboBox<Integer> academicYearCombo;
    @FXML
    private ComboBox<Integer> semesterCombo;
    @FXML
    private ComboBox<String> gradeFilterCombo;
    @FXML
    private TextArea outputArea;

    private final GradeService service =
            new GradeService(LoginController.client);

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private void initialize() {
        batchCombo.setItems(FXCollections.observableArrayList(service.fetchBatches()));
        academicYearCombo.setItems(FXCollections.observableArrayList(service.fetchResultAcademicYears(null, null)));
        semesterCombo.setItems(FXCollections.observableArrayList(service.fetchResultSemesters(null, null, null)));
        gradeFilterCombo.setItems(FXCollections.observableArrayList(service.fetchResultGrades(null, null, null, null)));

        if (!academicYearCombo.getItems().isEmpty()) {
            academicYearCombo.setValue(academicYearCombo.getItems().get(academicYearCombo.getItems().size() - 1));
        }
        if (!semesterCombo.getItems().isEmpty()) {
            semesterCombo.setValue(semesterCombo.getItems().get(0));
        }
    }

    @FXML
    private void loadBatchGrades() {
        String batch = batchCombo.getValue() == null ? "" : batchCombo.getValue().trim();
        int academicYear = academicYearCombo.getValue() == null ? 2026 : academicYearCombo.getValue();
        int semester = semesterCombo.getValue() == null ? 1 : semesterCombo.getValue();

        if (batch.isBlank()) {
            outputArea.setText("Enter batch to search grades.");
            return;
        }

        JsonNode response = service.getBatchGrades(batch, academicYear, semester);
        if (response == null) {
            outputArea.setText("No response from server.");
            return;
        }

        String filter = gradeFilterCombo.getValue() == null ? "" : gradeFilterCombo.getValue().trim();
        if (!filter.isBlank() && response.path("data").isArray()) {
            ArrayNode filtered = mapper.createArrayNode();
            for (JsonNode item : response.path("data")) {
                String grade = item.path("grade").asText("");
                if (grade.toLowerCase().contains(filter.toLowerCase())) {
                    filtered.add(item);
                }
            }

            if (filtered.isEmpty()) {
                outputArea.setText("No batch grades match filter '" + filter + "'.");
                return;
            }

            ((com.fasterxml.jackson.databind.node.ObjectNode) response).set("data", filtered);
        }

        outputArea.setText(pretty(response));
    }

    private String pretty(JsonNode node) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (Exception e) {
            return "";
        }
    }

    @FXML
    private void backToDashboard() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/lecturerDashboard.fxml"));
        Stage stage = (Stage) outputArea.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

}
