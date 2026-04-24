package com.example.frontend.controller.student;

import com.example.frontend.network.ServerClient;
import com.example.frontend.service.AcademicEndpointService;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class SingleCourseViewController {

    @FXML private Label courseTitleLabel;
    @FXML private Label courseCodeLabel;
    @FXML private Label statusLabel;
    @FXML private ListView<MaterialItem> materialsListView;

    private StudentCoursesController.CourseProperty currentCourse;
    private final AcademicEndpointService academicService = new AcademicEndpointService(ServerClient.getInstance());

    public static class MaterialItem {
        private String title;
        private String filePath;

        public MaterialItem(String title, String filePath) {
            this.title = title;
            this.filePath = filePath;
        }

        public String getTitle() { return title; }
        public String getFilePath() { return filePath; }

        @Override
        public String toString() {
            return "📄 " + title;
        }
    }

    public void initData(StudentCoursesController.CourseProperty course) {
        this.currentCourse = course;
        courseTitleLabel.setText(course.getCourseName());
        courseCodeLabel.setText(course.getCourseCode());
        loadMaterials();
    }

    private void loadMaterials() {
        statusLabel.setText("Loading materials...");
        
        JsonNode response = academicService.getCourseMaterials(currentCourse.getCourseId());
        ObservableList<MaterialItem> items = FXCollections.observableArrayList();

        if (response != null && response.has("success") && response.get("success").asBoolean()) {
            JsonNode data = response.get("data");
            if (data != null && data.isArray() && data.size() > 0) {
                for (JsonNode node : data) {
                    String title = node.has("title") ? node.get("title").asText() : "Unknown Document";
                    String filePath = node.has("filePath") ? node.get("filePath").asText() : "";
                    items.add(new MaterialItem(title, filePath));
                }
                statusLabel.setText(items.size() + " material(s) loaded.");
                statusLabel.setStyle("-fx-text-fill: #4cba52;");
            } else {
                statusLabel.setText("No materials found for this course.");
                statusLabel.setStyle("-fx-text-fill: #8fa3b8;");
            }
        } else {
            statusLabel.setText("Failed to load materials.");
            statusLabel.setStyle("-fx-text-fill: #e85d5d;");
        }

        materialsListView.setItems(items);

        // Add double click listener to open PDF
        materialsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                MaterialItem selected = materialsListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openPdf(selected.getFilePath());
                }
            }
        });
        
        // Setup cell factory for a cleaner look
        materialsListView.setCellFactory(param -> new ListCell<MaterialItem>() {
            @Override
            protected void updateItem(MaterialItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.toString() + " (Double-click to open)");
                    setStyle("-fx-padding: 10px; -fx-cursor: hand;");
                }
            }
        });
    }

    private void openPdf(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            statusLabel.setText("Error: File path is empty.");
            statusLabel.setStyle("-fx-text-fill: #e85d5d;");
            return;
        }

        try {
            File file = new File(filePath);
            if (file.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                    statusLabel.setText("Opened " + file.getName());
                    statusLabel.setStyle("-fx-text-fill: #4cba52;");
                } else {
                    statusLabel.setText("Error: Desktop operations not supported.");
                    statusLabel.setStyle("-fx-text-fill: #e85d5d;");
                }
            } else {
                statusLabel.setText("Error: File not found at " + filePath);
                statusLabel.setStyle("-fx-text-fill: #e85d5d;");
            }
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error opening file: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: #e85d5d;");
        }
    }

    @FXML
    private void backToCourses(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/student/StudentCourses.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) courseTitleLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
