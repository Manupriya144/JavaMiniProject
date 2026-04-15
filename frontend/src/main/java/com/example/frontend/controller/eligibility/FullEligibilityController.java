package com.example.frontend.controller.eligibility;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.service.EligibilityService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FullEligibilityController {

    @FXML
    private TextField studentIdField;
    @FXML
    private TextArea outputArea;

    private final EligibilityService service =
            new EligibilityService(LoginController.client);

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private void checkEligibility() {
        JsonNode response = service.checkEligibility(studentIdField.getText());
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