package com.example.frontend.controller.grade;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.service.GradeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class GenerateGradeController {

    @FXML
    private TextField studentIdField;
    @FXML
    private TextField courseIdField;
    @FXML
    private TextArea outputArea;

    private final GradeService service =
            new GradeService(LoginController.client);

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private void generateGrade() {
        JsonNode response = service.generateGrade(
                studentIdField.getText(),
                courseIdField.getText()
        );
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
