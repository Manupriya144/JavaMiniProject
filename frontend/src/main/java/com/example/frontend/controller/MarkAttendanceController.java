package com.example.frontend.controller;

import com.example.frontend.model.Attendance;
import com.example.frontend.model.AttendanceSessionOption;
import com.example.frontend.model.AttendanceStudentOption;
import com.example.frontend.service.AttendanceService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MarkAttendanceController {

    @FXML
    private ComboBox<AttendanceStudentOption> studentComboBox;
    @FXML
    private ComboBox<AttendanceSessionOption> sessionComboBox;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TextField hoursField;
    @FXML
    private Label resultLabel;

    private final AttendanceService attendanceService = new AttendanceService(LoginController.client);

    @FXML
    public void initialize() {
        statusComboBox.getItems().addAll("Present", "Absent");
        statusComboBox.setValue("Present");

        var students = attendanceService.getStudentOptions();
        studentComboBox.getItems().setAll(students);
        String studentLoadMessage = attendanceService.getLastMessage();
        var sessions = attendanceService.getSessionOptions();
        sessionComboBox.getItems().setAll(sessions);
        String sessionLoadMessage = attendanceService.getLastMessage();

        if (studentComboBox.getItems().isEmpty() || sessionComboBox.getItems().isEmpty()) {
            String details = (studentLoadMessage == null ? "" : studentLoadMessage);
            if (sessionLoadMessage != null && !sessionLoadMessage.isBlank()) {
                details = details.isBlank() ? sessionLoadMessage : details + " | " + sessionLoadMessage;
            }
            if (details.isBlank()) {
                if (students.isEmpty() && sessions.isEmpty()) {
                    details = "No students and no sessions found in database";
                } else if (students.isEmpty()) {
                    details = "No students found in database";
                } else {
                    details = "No sessions found in database";
                }
            }
            resultLabel.setText("Cannot load students/sessions: " + details);
        }

        hoursField.setPromptText("Enter hours, e.g. 2 or 2.5");
    }

    @FXML
    private void addAttendance() {
        try {
            AttendanceStudentOption student = studentComboBox.getValue();
            AttendanceSessionOption session = sessionComboBox.getValue();
            String status = statusComboBox.getValue();
            if (student == null || session == null || status == null) {
                resultLabel.setText("Please select student, session and status.");
                return;
            }

            String studentId = student.getUserId();
            Integer sessionId = session.getSessionId();
            String rawHours = hoursField.getText();
            if (rawHours == null || rawHours.trim().isEmpty()) {
                showError("Hours attended is required (example: 2 or 2.5).");
                return;
            }
            String normalizedHours = rawHours.trim().replace(',', '.');
            Double hours = Double.parseDouble(normalizedHours);
            if (hours < 0) {
                showError("Hours attended cannot be negative.");
                return;
            }

            Attendance created = attendanceService.addAttendance(studentId, sessionId, status, hours);
            if (created != null) {
                resultLabel.setText("Added. Attendance ID: " + created.getAttendanceId());
            } else {
                resultLabel.setText("Add failed: " + attendanceService.getLastMessage());
            }
        } catch (NumberFormatException ex) {
            showError("Hours attended must be a valid number (examples: 2 or 2.5).");
        } catch (Exception ex) {
            showError("Unexpected error: " + ex.getMessage());
        }
    }

    @FXML
    private void openAttendanceManagement() {
        loadView("/view/AttendanceManagement.fxml");
    }

    @FXML
    private void backToDashboard() {
        loadView("/view/techOfficerDashboard.fxml");
    }

    private void loadView(String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) resultLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showError("Cannot load view: " + path);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation failed");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
