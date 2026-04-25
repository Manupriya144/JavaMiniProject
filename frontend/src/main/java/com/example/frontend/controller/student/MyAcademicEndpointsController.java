package com.example.frontend.controller.student;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.service.AcademicEndpointService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MyAcademicEndpointsController {

    @FXML
    private TextField studentIdField;
    @FXML
    private TextField batchField;
    @FXML
    private TextField academicYearField;
    @FXML
    private TextField semesterField;
    @FXML
    private TextArea outputArea;

    private final AcademicEndpointService service = new AcademicEndpointService(LoginController.client);
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private void initialize() {
        studentIdField.setText(LoginController.userId);
        if (academicYearField.getText() == null || academicYearField.getText().isBlank()) {
            academicYearField.setText("2026");
        }
        if (semesterField.getText() == null || semesterField.getText().isBlank()) {
            semesterField.setText("1");
        }
    }

    @FXML
    private void onCalculateSGPA() {
        print(service.calculateSGPA(studentIdField.getText(), readInt(academicYearField, 2026), readInt(semesterField, 1)));
    }

    @FXML
    private void onCalculateCGPA() {
        print(service.calculateCGPA(studentIdField.getText()));
    }

    @FXML
    private void onGetStudentGPAReport() {
        print(service.getStudentGPAReport(studentIdField.getText()));
    }

    @FXML
    private void onGetBatchGPAReport() {
        print(service.getBatchGPAReport(batchField.getText(), readInt(academicYearField, 2026), readInt(semesterField, 1)));
    }

    @FXML
    private void onGetMyAttendance() { print(service.getMyAttendance()); }

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
    private void onGetStudentFullAcademicReport() { print(service.getStudentFullAcademicReport(studentIdField.getText())); }

    @FXML
    private void onGetBatchFullAcademicReport() { print(service.getBatchFullAcademicReport(batchField.getText())); }

    @FXML
    private void onBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/student/studentDashboard.fxml"));
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

    private int readInt(TextField field, int fallback) {
        try {
            return Integer.parseInt(field.getText());
        } catch (Exception e) {
            return fallback;
        }
    }
}
