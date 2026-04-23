package com.example.frontend.controller.student;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.service.AcademicEndpointService;
import com.example.frontend.service.GradeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MyAcademicEndpointsController {

    @FXML
    private ComboBox<String> studentIdCombo;
    @FXML
    private ComboBox<String> batchCombo;
    @FXML
    private ComboBox<Integer> academicYearCombo;
    @FXML
    private ComboBox<Integer> semesterCombo;
    @FXML
    private TextField gpaFilterField;
    @FXML
    private TextArea outputArea;

    private final AcademicEndpointService service = new AcademicEndpointService(LoginController.client);
    private final GradeService lookupService = new GradeService(LoginController.client);
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private void initialize() {
        studentIdCombo.setItems(FXCollections.observableArrayList(lookupService.fetchStudentIds()));
        batchCombo.setItems(FXCollections.observableArrayList(lookupService.fetchBatches()));
        academicYearCombo.setItems(FXCollections.observableArrayList(lookupService.fetchResultAcademicYears(null, null)));
        semesterCombo.setItems(FXCollections.observableArrayList(lookupService.fetchResultSemesters(null, null, null)));
        if (LoginController.userId != null && !LoginController.userId.isBlank()) {
            studentIdCombo.setValue(LoginController.userId);
        }
        if (!academicYearCombo.getItems().isEmpty()) {
            academicYearCombo.setValue(academicYearCombo.getItems().get(academicYearCombo.getItems().size() - 1));
        }
        if (!semesterCombo.getItems().isEmpty()) {
            semesterCombo.setValue(semesterCombo.getItems().get(0));
        }
    }

    @FXML
    private void onSearchGPA() {
        String studentId = studentIdCombo.getValue() == null ? "" : studentIdCombo.getValue().trim();
        String batch = batchCombo.getValue() == null ? "" : batchCombo.getValue().trim();
        String filter = gpaFilterField.getText() == null ? "" : gpaFilterField.getText().trim();

        JsonNode response;
        if (!batch.isBlank()) {
            response = service.getBatchGPAReport(batch, selectedYear(), selectedSemester());
        } else if (!studentId.isBlank()) {
            response = service.getStudentGPAReport(studentId);
        } else {
            outputArea.setText("Enter Student ID or Batch to search GPA.");
            return;
        }

        if (response == null) {
            outputArea.setText("No response from server.");
            return;
        }

        if (!filter.isBlank() && response.path("data").isArray()) {
            ArrayNode filtered = mapper.createArrayNode();
            for (JsonNode item : response.path("data")) {
                if (item.toString().toLowerCase().contains(filter.toLowerCase())) {
                    filtered.add(item);
                }
            }
            if (filtered.isEmpty()) {
                outputArea.setText("No GPA records match filter '" + filter + "'.");
                return;
            }
            ((com.fasterxml.jackson.databind.node.ObjectNode) response).set("data", filtered);
        }

        print(response);
    }

    @FXML
    private void onCalculateSGPA() {
        print(service.calculateSGPA(studentIdCombo.getValue(), selectedYear(), selectedSemester()));
    }

    @FXML
    private void onCalculateCGPA() {
        print(service.calculateCGPA(studentIdCombo.getValue()));
    }

    @FXML
    private void onGetStudentGPAReport() {
        print(service.getStudentGPAReport(studentIdCombo.getValue()));
    }

    @FXML
    private void onGetBatchGPAReport() {
        print(service.getBatchGPAReport(batchCombo.getValue(), selectedYear(), selectedSemester()));
    }

    @FXML
    private void onGetMyAttendance() { print(service.getMyAttendance()); }

    @FXML
    private void onGetMyMedicalRecords() { print(service.getMyMedicalRecords()); }

    @FXML
    private void onGetMyCourses() { print(service.getMyCourses()); }

    @FXML
    private void onGetMyMarks() { print(service.getMyMarks()); }

    @FXML
    private void onGetMyGrades() { print(service.getMyGrades()); }

    @FXML
    private void onGetMyGPA() { print(service.getMyGPA()); }

    @FXML
    private void onGetMyTimetable() { print(service.getMyTimetable()); }

    @FXML
    private void onGetAllNotices() { print(service.getAllNotices()); }

    @FXML
    private void onGetStudentFullAcademicReport() { print(service.getStudentFullAcademicReport(studentIdCombo.getValue())); }

    @FXML
    private void onGetBatchFullAcademicReport() { print(service.getBatchFullAcademicReport(batchCombo.getValue())); }

    @FXML
    private void onBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/studentDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) outputArea.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void print(JsonNode node) {
        try {
            if (node == null) {
                outputArea.setText("No response from server.");
                return;
            }
            outputArea.setText(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node));
        } catch (Exception e) {
            outputArea.setText(String.valueOf(node));
        }
    }

    private int selectedYear() {
        Integer year = academicYearCombo.getValue();
        return year == null ? 2026 : year;
    }

    private int selectedSemester() {
        Integer semester = semesterCombo.getValue();
        return semester == null ? 1 : semester;
    }
}
