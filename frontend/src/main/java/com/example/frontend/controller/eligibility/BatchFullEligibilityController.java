package com.example.frontend.controller.eligibility;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.service.EligibilityService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class BatchFullEligibilityController {

    @FXML
    private TextField batchField;
    @FXML
    private TextArea outputArea;

    private final EligibilityService service =
            new EligibilityService(LoginController.client);

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private void loadBatchEligibility() {
        JsonNode response = service.getBatchEligibility(batchField.getText());
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
