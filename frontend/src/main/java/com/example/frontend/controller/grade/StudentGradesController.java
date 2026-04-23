package com.example.frontend.controller.grade;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.service.GradeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class StudentGradesController {

    @FXML
    private ComboBox<String> studentIdCombo;
    @FXML
    private ComboBox<String> courseIdCombo;
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
        studentIdCombo.setItems(FXCollections.observableArrayList(service.fetchResultStudentIds()));
        if (!studentIdCombo.getItems().isEmpty()) {
            studentIdCombo.setValue(studentIdCombo.getItems().get(0));
        }

        studentIdCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            courseIdCombo.setItems(FXCollections.observableArrayList(service.fetchResultCourseIds(newVal)));
            if (!courseIdCombo.getItems().isEmpty()) {
                courseIdCombo.setValue(courseIdCombo.getItems().get(0));
            } else {
                courseIdCombo.setValue(null);
            }
            academicYearCombo.getItems().clear();
            semesterCombo.getItems().clear();
            gradeFilterCombo.getItems().clear();
        });

        courseIdCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            String studentId = studentIdCombo.getValue();
            academicYearCombo.setItems(FXCollections.observableArrayList(service.fetchResultAcademicYears(studentId, newVal)));
            if (!academicYearCombo.getItems().isEmpty()) {
                academicYearCombo.setValue(academicYearCombo.getItems().get(academicYearCombo.getItems().size() - 1));
            } else {
                academicYearCombo.setValue(null);
            }
            semesterCombo.getItems().clear();
            gradeFilterCombo.getItems().clear();
        });

        academicYearCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            String studentId = studentIdCombo.getValue();
            String courseId = courseIdCombo.getValue();
            semesterCombo.setItems(FXCollections.observableArrayList(service.fetchResultSemesters(studentId, courseId, newVal)));
            if (!semesterCombo.getItems().isEmpty()) {
                semesterCombo.setValue(semesterCombo.getItems().get(0));
            } else {
                semesterCombo.setValue(null);
            }
            gradeFilterCombo.getItems().clear();
        });

        semesterCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            String studentId = studentIdCombo.getValue();
            String courseId = courseIdCombo.getValue();
            Integer year = academicYearCombo.getValue();
            gradeFilterCombo.setItems(FXCollections.observableArrayList(service.fetchResultGrades(studentId, courseId, year, newVal)));
            gradeFilterCombo.setValue(null);
        });

        if (studentIdCombo.getValue() != null) {
            courseIdCombo.setItems(FXCollections.observableArrayList(service.fetchResultCourseIds(studentIdCombo.getValue())));
            if (!courseIdCombo.getItems().isEmpty()) {
                courseIdCombo.setValue(courseIdCombo.getItems().get(0));
                academicYearCombo.setItems(FXCollections.observableArrayList(service.fetchResultAcademicYears(studentIdCombo.getValue(), courseIdCombo.getValue())));
                if (!academicYearCombo.getItems().isEmpty()) {
                    academicYearCombo.setValue(academicYearCombo.getItems().get(academicYearCombo.getItems().size() - 1));
                    semesterCombo.setItems(FXCollections.observableArrayList(service.fetchResultSemesters(studentIdCombo.getValue(), courseIdCombo.getValue(), academicYearCombo.getValue())));
                    if (!semesterCombo.getItems().isEmpty()) {
                        semesterCombo.setValue(semesterCombo.getItems().get(0));
                    }
                }
            }
        }
    }

    @FXML
    private void loadGrades() {
        String studentId = studentIdCombo.getValue() == null ? "" : studentIdCombo.getValue().trim();
        String courseId = courseIdCombo.getValue() == null ? "" : courseIdCombo.getValue().trim();

        if (studentId.isBlank() || courseId.isBlank()) {
            outputArea.setText("Enter both Student ID and Course ID to search grades.");
            return;
        }

        JsonNode response = service.getStudentGrades(studentId, courseId, academicYearCombo.getValue(), semesterCombo.getValue());
        if (response == null) {
            outputArea.setText("No response from server.");
            return;
        }

        if (!response.path("success").asBoolean(true)) {
            outputArea.setText(response.path("message").asText("No grade data found for the selected student/course/year/semester."));
            return;
        }

        String filter = gradeFilterCombo.getValue() == null ? "" : gradeFilterCombo.getValue().trim();
        if (!filter.isBlank() && response.path("data").path("grade").isTextual()) {
            String grade = response.path("data").path("grade").asText("");
            if (!grade.toLowerCase().contains(filter.toLowerCase())) {
                outputArea.setText("No grade matches filter '" + filter + "'.");
                return;
            }
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
}
