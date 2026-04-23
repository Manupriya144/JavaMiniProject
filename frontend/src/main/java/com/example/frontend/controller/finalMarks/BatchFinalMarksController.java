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

public class BatchFinalMarksController {

    @FXML
    private TextField academicYearField;
    @FXML
    private TextField semesterField;
    @FXML
    private TextArea outputArea;

    private final FinalMarksService service =
            new FinalMarksService(LoginController.client);

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private void loadBatchMarks() {
        int academicYear = readInt(academicYearField, 2026);
        int semester = readInt(semesterField, 1);
        JsonNode response = service.getBatchMarks(academicYear, semester);
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

    private int readInt(TextField field, int fallback) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (Exception e) {
            return fallback;
        }
    }
}
