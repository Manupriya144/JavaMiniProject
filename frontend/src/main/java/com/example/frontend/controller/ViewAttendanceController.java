package com.example.frontend.controller;

import com.example.frontend.model.Attendance;
import com.example.frontend.service.AttendanceService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ViewAttendanceController {

    @FXML
    private TextField attendanceIdField;
    @FXML
    private Label attendanceIdValue;
    @FXML
    private Label studentIdValue;
    @FXML
    private Label sessionIdValue;
    @FXML
    private Label statusValue;
    @FXML
    private Label hoursValue;
    @FXML
    private Label resultLabel;

    private final AttendanceService attendanceService = new AttendanceService(LoginController.client);

    @FXML
    private void findAttendance() {
        try {
            Integer attendanceId = Integer.parseInt(attendanceIdField.getText().trim());
            Attendance attendance = attendanceService.getAttendanceById(attendanceId);
            if (attendance == null) {
                resultLabel.setText("No record found.");
                clearValues();
                return;
            }

            attendanceIdValue.setText(String.valueOf(attendance.getAttendanceId()));
            studentIdValue.setText(attendance.getStudentId());
            sessionIdValue.setText(String.valueOf(attendance.getSessionId()));
            statusValue.setText(attendance.getStatus());
            hoursValue.setText(String.valueOf(attendance.getHoursAttended()));
            resultLabel.setText("Attendance loaded.");
        } catch (NumberFormatException ex) {
            showError("Attendance ID must be an integer.");
        } catch (Exception ex) {
            showError("Unexpected error: " + ex.getMessage());
        }
    }

    @FXML
    private void goUpdateDelete() {
        loadView("/view/AttendanceManagement.fxml");
    }

    @FXML
    private void backToDashboard() {
        loadView("/view/techOfficerDashboard.fxml");
    }

    private void clearValues() {
        attendanceIdValue.setText("-");
        studentIdValue.setText("-");
        sessionIdValue.setText("-");
        statusValue.setText("-");
        hoursValue.setText("-");
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
