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

public class FullEligibilityController {

    @FXML
    private TextField studentIdField;
    @FXML
    private TextField courseIdField;
    @FXML
    private TextArea outputArea;

    private final EligibilityService service =
            new EligibilityService(LoginController.client);

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private void checkEligibility() {
        String studentId = studentIdField.getText() == null ? "" : studentIdField.getText().trim();
        String courseId = courseIdField.getText() == null ? "" : courseIdField.getText().trim();

        if (studentId.isBlank() || courseId.isBlank()) {
            outputArea.setText("Enter both Student ID and Course ID.");
            return;
        }

        JsonNode response = service.checkEligibility(studentId, courseId);
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
}
