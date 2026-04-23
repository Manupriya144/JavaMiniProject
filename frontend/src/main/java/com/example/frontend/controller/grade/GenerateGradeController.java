package com.example.frontend.controller.grade;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.service.GradeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class GenerateGradeController {

    @FXML
    private TextField studentIdField;
    @FXML
    private TextField courseIdField;
    @FXML
    private TextField academicYearField;
    @FXML
    private TextField semesterField;
    @FXML
    private TextField gradeField;
    @FXML
    private TextArea outputArea;

    private final GradeService service =
            new GradeService(LoginController.client);

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private void generateGrade() {
        String studentId = studentIdField.getText() == null ? "" : studentIdField.getText().trim();
        String courseId = courseIdField.getText() == null ? "" : courseIdField.getText().trim();
        int academicYear = readInt(academicYearField, 2026);
        int semester = readInt(semesterField, 1);
        String grade = gradeField.getText() == null ? "" : gradeField.getText().trim();

        if (studentId.isBlank() || courseId.isBlank() || grade.isBlank()) {
            outputArea.setText("Student ID, Course ID, and Grade are required.");
            return;
        }

        JsonNode response = service.generateGrade(studentId, courseId, academicYear, semester, grade);
        if (response == null) {
            outputArea.setText("No response from server.");
            return;
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

    private int readInt(TextField field, int fallback) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (Exception e) {
            return fallback;
        }
    }
}
