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
    private void updateAttendance() {
        try {
            Integer attendanceId = Integer.parseInt(attendanceIdField.getText().trim());
            String status = updateStatusField.getText();
            Double hours = Double.parseDouble(updateHoursField.getText().trim());

            boolean ok = attendanceService.updateAttendance(attendanceId, status, hours);
            resultLabel.setText(ok ? "Attendance updated." : "Update failed.");
        } catch (NumberFormatException ex) {
            showError("Attendance ID must be integer and Hours must be numeric.");
        } catch (Exception ex) {
            showError("Unexpected error: " + ex.getMessage());
        }
    }

    @FXML
    private void deleteAttendance() {
        try {
            Integer attendanceId = Integer.parseInt(attendanceIdField.getText().trim());
            boolean ok = attendanceService.deleteAttendance(attendanceId);
            resultLabel.setText(ok ? "Attendance deleted." : "Delete failed.");
        } catch (NumberFormatException ex) {
            showError("Attendance ID must be an integer.");
        } catch (Exception ex) {
            showError("Unexpected error: " + ex.getMessage());
        }
    }

    @FXML
    private void openMarkAttendance() {
        loadView("/view/MarkAttendance.fxml");
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
