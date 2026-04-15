package com.example.frontend.controller.admin;

import com.example.frontend.dto.NoticeRequestDTO;
import com.example.frontend.service.NoticeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddNoticeController {

    @FXML
    private Label adminNameLabel;

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField pdfPathField;

    @FXML
    private Label statusLabel;

    @FXML
    private Label statusBarTime;

    @FXML
    private Button addBtn;

    private NoticeService noticeService;

    public void setNoticeService(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @FXML
    public void initialize() {
        statusBarTime.setText(
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
        );
    }

    @FXML
    public void browsePdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select PDF File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File file = fileChooser.showOpenDialog(pdfPathField.getScene().getWindow());
        if (file != null) {
            pdfPathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void addNotice() {
        String title = titleField.getText() != null ? titleField.getText().trim() : "";
        String description = descriptionArea.getText() != null ? descriptionArea.getText().trim() : "";
        String pdfPath = pdfPathField.getText() != null ? pdfPathField.getText().trim() : "";

        if (title.isEmpty()) {
            showStatus("Title is required.", false);
            return;
        }

        if (description.isEmpty() && pdfPath.isEmpty()) {
            showStatus("Provide a description or a PDF file path.", false);
            return;
        }

        NoticeRequestDTO dto = new NoticeRequestDTO();
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setPdf_file_path(pdfPath);

        if (noticeService == null) {
            showStatus("Notice service is not initialized.", false);
            return;
        }

        addBtn.setDisable(true);

        try {
            boolean success = noticeService.createNotice(dto);

            if (success) {
                showStatus("Notice published successfully.", true);
                clearForm();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Notice published successfully.");
            } else {
                showStatus("Failed to publish notice.", false);
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to publish notice.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showStatus("Unexpected error occurred.", false);
            showAlert(Alert.AlertType.ERROR, "Error", "Unexpected error occurred.");
        } finally {
            addBtn.setDisable(false);
        }
    }

    @FXML
    public void clearForm() {
        titleField.clear();
        descriptionArea.clear();
        pdfPathField.clear();
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
        statusLabel.setText("");
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/adminDashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) adminNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showStatus("Failed to load dashboard", false);
        }
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle(success
                ? "-fx-text-fill: #28a745; -fx-font-size: 12px; -fx-font-weight: bold;"
                : "-fx-text-fill: #ff6b6b; -fx-font-size: 12px; -fx-font-weight: bold;");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}