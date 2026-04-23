package com.example.frontend.controller.finalMarks;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.service.FinalMarksService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UploadFinalMarksController {

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
    private void uploadMarks() {
        try {
            String studentId = studentIdField.getText();
            String courseId = courseIdField.getText();
            double marks = Double.parseDouble(marksField.getText());

            JsonNode response = service.uploadMarks(studentId, courseId, marks);
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

    @FXML
    private void backToDashboard() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/lecturerDashboard.fxml"));
        Stage stage = (Stage) outputArea.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
