package com.example.frontend.controller.finalMarks;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.service.FinalMarksService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ViewStudentFinalMarksController {

    @FXML
    private TextField studentIdField;
    @FXML
    private TextField courseIdField;
    @FXML
    private TextArea outputArea;

    private final FinalMarksService service =
            new FinalMarksService(LoginController.client);

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private void loadMarks() {
        String studentId = studentIdField.getText() == null ? "" : studentIdField.getText().trim();
        String courseId = courseIdField.getText() == null ? "" : courseIdField.getText().trim();
        JsonNode response = service.getStudentMarks(studentId, courseId);
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
