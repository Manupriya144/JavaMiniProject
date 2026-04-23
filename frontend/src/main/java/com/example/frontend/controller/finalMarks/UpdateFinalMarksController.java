package com.example.frontend.controller.finalMarks;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.service.FinalMarksService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class UpdateFinalMarksController {

    @FXML
    private TextField studentIdField;
    @FXML
    private TextField courseIdField;
    @FXML
    private TextField marksField;
    @FXML
    private TextArea outputArea;

    private final FinalMarksService service =
            new FinalMarksService(LoginController.client);

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private void updateMarks() {
        try {
            JsonNode response = service.updateMarks(
                    studentIdField.getText(),
                    courseIdField.getText(),
                    Double.parseDouble(marksField.getText())
            );

            outputArea.setText(pretty(response));

        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    private String pretty(JsonNode node) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (Exception e) {
            return "";
        }
    }
}
