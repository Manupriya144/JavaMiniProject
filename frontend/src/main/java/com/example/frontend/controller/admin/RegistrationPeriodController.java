package com.example.frontend.controller.admin;

import com.example.frontend.dto.RegistrationPeriodRequestDTO;
import com.example.frontend.service.RegistrationPeriodService;
import com.example.frontend.network.ServerClient;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistrationPeriodController {

    @FXML private TextField departmentIdField;
    @FXML private ComboBox<String> levelBox;
    @FXML private ComboBox<String> semesterBox;
    @FXML private TextField academicYearField;
    @FXML private TextField startAtField;
    @FXML private TextField endAtField;
    @FXML private ComboBox<String> statusBox;
    @FXML private Label statusLabel;

    private final RegistrationPeriodService service =
            new RegistrationPeriodService(ServerClient.getInstance());

    @FXML
    public void initialize() {
        levelBox.setItems(FXCollections.observableArrayList("1", "2", "3", "4"));
        semesterBox.setItems(FXCollections.observableArrayList("1", "2"));
        statusBox.setItems(FXCollections.observableArrayList("Open", "Closed"));

        statusBox.setValue("Open");
    }

    @FXML
    private void saveRegistrationPeriod() {
        try {
            String departmentId = departmentIdField.getText().trim();
            String levelText = levelBox.getValue();
            String semester = semesterBox.getValue();
            String yearText = academicYearField.getText().trim();
            String startAt = startAtField.getText().trim();
            String endAt = endAtField.getText().trim();
            String status = statusBox.getValue();

            if (departmentId.isBlank() || levelText == null || semester == null ||
                    yearText.isBlank() || startAt.isBlank() || endAt.isBlank() || status == null) {
                showError("Please fill all fields.");
                return;
            }

            RegistrationPeriodRequestDTO dto = new RegistrationPeriodRequestDTO(
                    departmentId,
                    Integer.parseInt(levelText),
                    semester,
                    Integer.parseInt(yearText),
                    startAt,
                    endAt,
                    status
            );

            boolean saved = service.saveRegistrationPeriod(dto);

            if (saved) {
                statusLabel.setStyle("-fx-text-fill: #4cba52; -fx-font-weight: bold;");
                statusLabel.setText("Registration period saved successfully.");
            } else {
                showError("Failed to save registration period.");
            }

        } catch (NumberFormatException e) {
            showError("Academic year must be a number.");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Something went wrong.");
        }
    }

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: #e85d5d; -fx-font-weight: bold;");
        statusLabel.setText(message);
    }

    @FXML
    private void goBack() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/view/admin/AdminDashboard.fxml")
            );

            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}