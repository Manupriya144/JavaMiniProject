package com.example.frontend.controller.lecturer;

import com.example.frontend.model.CourseMaterial;
import com.example.frontend.model.LecturerCourseItem;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.CourseMaterialService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.List;

public class CourseMaterialsController {

    @FXML private Label courseTitleLabel;
    @FXML private ListView<CourseMaterial> materialListView;
    @FXML private Label statusLabel;

    private LecturerCourseItem selectedCourse;
    private CourseMaterialService courseMaterialService;

    @FXML
    public void initialize() {
        courseMaterialService = new CourseMaterialService(ServerClient.getInstance());
        setupMaterialListView();
        hideStatus();
    }

    public void setCourse(LecturerCourseItem course) {
        this.selectedCourse = course;

        if (course != null) {
            courseTitleLabel.setText(course.getCourseName() + " Materials");
            loadMaterials();
        } else {
            courseTitleLabel.setText("Course Materials");
            showError("Course not selected.");
        }
    }

    private void setupMaterialListView() {
        materialListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(CourseMaterial item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                    return;
                }

                Label fileLabel = new Label(
                        "📄  " + safe(item.getTitle()) + "\n     " + getFileNameOnly(item.getFilePath())
                );
                fileLabel.setStyle("-fx-text-fill: #1a3a52; -fx-font-size: 13px;");

                Button openBtn = new Button("Open");
                openBtn.setStyle("""
                    -fx-background-color: #5b9fd9;
                    -fx-text-fill: white;
                    -fx-font-weight: bold;
                    -fx-background-radius: 8;
                    -fx-cursor: hand;
                    """);
                openBtn.setOnAction(e -> openMaterialFile(item));

                Button deleteBtn = new Button("Delete");
                deleteBtn.setStyle("""
                    -fx-background-color: #ef4444;
                    -fx-text-fill: white;
                    -fx-font-weight: bold;
                    -fx-background-radius: 8;
                    -fx-cursor: hand;
                    """);
                deleteBtn.setOnAction(e -> deleteMaterial(item));

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                HBox row = new HBox(12, fileLabel, spacer, openBtn, deleteBtn);
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                row.setStyle("""
                    -fx-padding: 14;
                    -fx-background-color: white;
                    -fx-border-color: #e8eef5;
                    -fx-border-width: 0 0 1 0;
                    """);

                setText(null);
                setGraphic(row);
            }
        });
    }

    private void deleteMaterial(CourseMaterial material) {

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Confirmation");
        confirmAlert.setHeaderText("Delete Course Material");
        confirmAlert.setContentText(
                "Are you sure you want to delete:\n\n" +
                        material.getTitle() + "\n(" + getFileNameOnly(material.getFilePath()) + ")"
        );

        ButtonType yesBtn = new ButtonType("Yes, Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmAlert.getButtonTypes().setAll(yesBtn, cancelBtn);

        confirmAlert.showAndWait().ifPresent(response -> {

            if (response == yesBtn) {

                try {
                    boolean deleted = courseMaterialService.deleteMaterial(material.getMaterialId());

                    if (deleted) {
                        showInfo("Material deleted successfully.");
                        loadMaterials();
                    } else {
                        showError("Failed to delete material.");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Error while deleting material.");
                }

            }
        });
    }

    private void loadMaterials() {
        try {
            List<CourseMaterial> materials =
                    courseMaterialService.getMaterialsByCourseId(selectedCourse.getCourseId());

            materialListView.setItems(FXCollections.observableArrayList(materials));

            if (materials == null || materials.isEmpty()) {
                showInfo("No materials added yet.");
            } else {
                hideStatus();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load materials.");
        }
    }

    @FXML
    private void openAddMaterial() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/lecturer/AddCourseMaterial.fxml")
            );

            Parent root = loader.load();

            AddCourseMaterialController controller = loader.getController();
            controller.setCourse(selectedCourse);

            materialListView.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to open add material page.");
        }
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/lecturer/LecturerCourses.fxml")
            );

            Parent root = loader.load();
            materialListView.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to go back.");
        }
    }

    private void openMaterialFile(CourseMaterial material) {
        new Thread(() -> {
            try {
                String path = material.getFilePath();

                System.out.println("FILE PATH FROM DB: " + path);

                if (path == null || path.trim().isEmpty()) {
                    showErrorUI("No file available.");
                    return;
                }

                path = path.trim();

                if (path.startsWith("http://") || path.startsWith("https://")) {
                    if (Desktop.isDesktopSupported()
                            && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {

                        Desktop.getDesktop().browse(new URI(path));
                        showInfoUI("Opening link...");
                    } else {
                        showErrorUI("Browser opening is not supported.");
                    }
                    return;
                }

                File file = new File(path);

                if (!file.isAbsolute()) {
                    file = new File(System.getProperty("user.dir"), path);
                }

                System.out.println("Resolved path: " + file.getAbsolutePath());

                if (!file.exists()) {
                    showErrorUI("File not found: " + getFileNameOnly(path));
                    return;
                }

                String os = System.getProperty("os.name").toLowerCase();

                if (os.contains("linux")) {
                    new ProcessBuilder("xdg-open", file.getAbsolutePath()).start();
                    showInfoUI("Opening file...");
                    return;
                }

                if (Desktop.isDesktopSupported()
                        && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {

                    Desktop.getDesktop().open(file);
                    showInfoUI("Opening file...");
                    return;
                }

                showErrorUI("Open action is not supported.");

            } catch (Exception e) {
                e.printStackTrace();
                showErrorUI("Error opening file: " + e.getMessage());
            }
        }).start();
    }

    private String getFileNameOnly(String path) {
        if (path == null || path.trim().isEmpty()) {
            return "No file";
        }

        try {
            return new File(path).getName();
        } catch (Exception e) {
            return path;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void showInfo(String message) {
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #8fa3b8; -fx-font-weight: bold;");
    }

    private void showError(String message) {
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
    }

    private void showInfoUI(String message) {
        Platform.runLater(() -> showInfo(message));
    }

    private void showErrorUI(String message) {
        Platform.runLater(() -> showError(message));
    }

    private void hideStatus() {
        statusLabel.setText("");
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
    }
}