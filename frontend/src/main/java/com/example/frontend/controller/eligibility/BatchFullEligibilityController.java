package com.example.frontend.controller.eligibility;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.service.EligibilityService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BatchFullEligibilityController {

    @FXML
    private TextField academicYearField;
    @FXML
    private TextField semesterField;
    @FXML
    private TextArea outputArea;

    private final EligibilityService service =
            new EligibilityService(LoginController.client);

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private void loadBatchEligibility() {
        String academicYear = academicYearField.getText() == null ? "" : academicYearField.getText().trim();
        String semester = semesterField.getText() == null ? "" : semesterField.getText().trim();

        if (!academicYear.matches("\\d{4}")) {
            outputArea.setText("Enter a valid Academic Year (e.g. 2024).");
            return;
        }
        if (!semester.matches("[12]")) {
            outputArea.setText("Enter Semester as 1 or 2.");
            return;
        }

        JsonNode response = service.getBatchEligibility(academicYear, semester);
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
}
