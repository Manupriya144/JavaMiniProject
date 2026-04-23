package com.example.frontend.controller.attendance;

import com.example.frontend.controller.admin.LoginController;
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
    public void initialize() {
        clearValues();
        showResult("", ResultType.INFO);
    }

    @FXML
    private void findAttendance() {
        try {
            Integer attendanceId = Integer.parseInt(attendanceIdField.getText().trim());
            Attendance attendance = attendanceService.getAttendanceById(attendanceId);

            if (attendance == null) {
                showResult("No record found.", ResultType.ERROR);
                clearValues();
                return;
            }

            attendanceIdValue.setText(String.valueOf(attendance.getAttendanceId()));
            studentIdValue.setText(attendance.getStudentId() == null ? "-" : attendance.getStudentId());
            sessionIdValue.setText(String.valueOf(attendance.getSessionId()));
            statusValue.setText(attendance.getStatus() == null ? "-" : attendance.getStatus());
            hoursValue.setText(String.valueOf(attendance.getHoursAttended()));

            showResult("Attendance loaded successfully.", ResultType.SUCCESS);

        } catch (NumberFormatException ex) {
            showError("Attendance ID must be an integer.");
        } catch (Exception ex) {
            showError("Unexpected error: " + ex.getMessage());
        }
    }

    @FXML
    private void goUpdateDelete() {
        loadView("/view/techofficer/AttendanceManagement.fxml");
    }

    @FXML
    private void backToDashboard() {
        loadView("/view/techofficer/techOfficerDashboard.fxml");
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
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            showError("Cannot load view: " + path);
        }
    }

    private void showError(String message) {
        showResult(message, ResultType.ERROR);

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private enum ResultType {
        SUCCESS, ERROR, INFO
    }

    private void showResult(String message, ResultType type) {
        resultLabel.setText(message);

        String color = switch (type) {
            case SUCCESS -> "#4cba52";
            case ERROR -> "#e85d5d";
            case INFO -> "#8fa3b8";
        };

        resultLabel.setStyle(
                "-fx-text-fill: " + color + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;"
        );
    }
}