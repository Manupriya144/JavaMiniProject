package com.example.frontend.controller.finalMarks;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.service.FinalMarksService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class BatchFinalMarksController {

    @FXML
    private TextField batchField;
    @FXML
    private TextArea outputArea;

    private final FinalMarksService service =
            new FinalMarksService(LoginController.client);

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private void loadBatchMarks() {
        JsonNode response = service.getBatchMarks(batchField.getText());
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
