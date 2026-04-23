package com.example.frontend.controller.attendance;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.service.AttendanceService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AttendanceManagementController {

    @FXML
    private TextField attendanceIdField;
    @FXML
    private TextField updateStatusField;
    @FXML
    private TextField updateHoursField;
    @FXML
    private Label resultLabel;

    private final AttendanceService attendanceService = new AttendanceService(LoginController.client);

    @FXML
    public void initialize() {
        showResult("", ResultType.INFO);
    }

    @FXML
    private void updateAttendance() {
        try {
            Integer attendanceId = Integer.parseInt(attendanceIdField.getText().trim());
            String status = updateStatusField.getText() == null ? "" : updateStatusField.getText().trim();
            Double hours = Double.parseDouble(updateHoursField.getText().trim());

            if (status.isEmpty()) {
                showResult("Status is required.", ResultType.ERROR);
                return;
            }

            boolean ok = attendanceService.updateAttendance(attendanceId, status, hours);
            showResult(ok ? "Attendance updated successfully." : "Update failed.", ok ? ResultType.SUCCESS : ResultType.ERROR);

        } catch (NumberFormatException ex) {
            showResult("Attendance ID must be integer and Hours must be numeric.", ResultType.ERROR);
        } catch (Exception ex) {
            showResult("Unexpected error: " + ex.getMessage(), ResultType.ERROR);
        }
    }

    @FXML
    private void deleteAttendance() {
        try {
            Integer attendanceId = Integer.parseInt(attendanceIdField.getText().trim());
            boolean ok = attendanceService.deleteAttendance(attendanceId);
            showResult(ok ? "Attendance deleted successfully." : "Delete failed.", ok ? ResultType.SUCCESS : ResultType.ERROR);

        } catch (NumberFormatException ex) {
            showResult("Attendance ID must be an integer.", ResultType.ERROR);
        } catch (Exception ex) {
            showResult("Unexpected error: " + ex.getMessage(), ResultType.ERROR);
        }
    }

    @FXML
    private void openMarkAttendance() {
        loadView("/view/techofficer/MarkAttendance.fxml");
    }

    @FXML
    private void backToDashboard() {
        loadView("/view/techofficer/techOfficerDashboard.fxml");
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