package com.example.frontend.controller.ca;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.model.CAAssessmentTypeOption;
import com.example.frontend.model.CAMark;
import com.example.frontend.model.LecturerCourseItem;
import com.example.frontend.service.CAMarkService;
import com.example.frontend.service.LecturerCourseService;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class CAManagementController {

    @FXML private ComboBox<LecturerCourseItem> uploadCourseBox;
    @FXML private ComboBox<CAAssessmentTypeOption> uploadAssessmentTypeBox;
    @FXML private TextField uploadStudentIdField;
    @FXML private TextField uploadMarksField;

    @FXML private TextField updateMarkIdField;
    @FXML private TextField updateMarksField;
    @FXML private Label statusLabel;

    private final CAMarkService caService = new CAMarkService(LoginController.client);
    private final LecturerCourseService lecturerCourseService = new LecturerCourseService(LoginController.client);
    private final ObservableList<LecturerCourseItem> lecturerCourses = FXCollections.observableArrayList();
    private final ObservableList<CAAssessmentTypeOption> assessmentTypeOptions = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        showStatus("", StatusType.INFO);
        setupCourseBox(uploadCourseBox, "Select Course");
        setupAssessmentTypeBox();
        uploadCourseBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                assessmentTypeOptions.clear();
                uploadAssessmentTypeBox.getSelectionModel().clearSelection();
                return;
            }
            loadCourseReference(newValue.getCourseId(), true);
        });
        loadLecturerCourses();
    }

    private void setupCourseBox(ComboBox<LecturerCourseItem> comboBox, String emptyText) {
        comboBox.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(LecturerCourseItem item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getCourseName() + " | " + item.getCourseId());
            }
        });

        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(LecturerCourseItem item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? emptyText : item.getCourseName() + " | " + item.getCourseId());
            }
        });
    }

    private void setupAssessmentTypeBox() {
        uploadAssessmentTypeBox.setItems(assessmentTypeOptions);
        uploadAssessmentTypeBox.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(CAAssessmentTypeOption item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });

        uploadAssessmentTypeBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(CAAssessmentTypeOption item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Assignment ID" : item.toString());
            }
        });
    }

    private void loadLecturerCourses() {
        try {
            List<LecturerCourseItem> courses = lecturerCourseService.getLecturerCourses();

            if (courses == null || courses.isEmpty()) {
                showStatus("No assigned courses found for this lecturer.", StatusType.ERROR);
                return;
            }

            lecturerCourses.setAll(courses);
            uploadCourseBox.setItems(lecturerCourses);

            uploadCourseBox.getSelectionModel().selectFirst();
        } catch (Exception e) {
            e.printStackTrace();
            showStatus("Failed to load lecturer course list.", StatusType.ERROR);
        }
    }

    @FXML
    private void uploadCAMarks() {
        try {
            LecturerCourseItem selectedCourse = uploadCourseBox.getValue();
            CAAssessmentTypeOption selectedAssessment = uploadAssessmentTypeBox.getValue();
            String studentRegNo = uploadStudentIdField.getText() == null ? "" : uploadStudentIdField.getText().trim();
            Double marks = Double.parseDouble(uploadMarksField.getText().trim());

            if (selectedCourse == null) {
                showStatus("Please select a course.", StatusType.ERROR);
                return;
            }

            if (selectedAssessment == null || selectedAssessment.getAssessmentTypeId() == null) {
                showStatus("Please select an assignment type.", StatusType.ERROR);
                return;
            }

            if (studentRegNo.isBlank()) {
                showStatus("Student registration number is required.", StatusType.ERROR);
                return;
            }

            CAMark saved = caService.uploadCAMarks(studentRegNo, selectedAssessment.getAssessmentTypeId(), marks);
            if (saved == null) {
                showStatus(caService.getLastMessage(), StatusType.ERROR);
                return;
            }

            showStatus("CA mark uploaded successfully.", StatusType.SUCCESS);
            showInfoMessage(
                    "Upload Successful",
                    "CA mark uploaded successfully.",
                    "Student Reg No: " + studentRegNo
                            + "\nAssignment: " + selectedAssessment
                            + "\nMarks: " + marks
            );
            clearUploadFields();
            loadCourseReference(selectedCourse.getCourseId(), false);
        } catch (NumberFormatException e) {
            showStatus("Marks must be a valid number.", StatusType.ERROR);
        } catch (Exception e) {
            showStatus("Invalid upload input.", StatusType.ERROR);
        }
    }

    @FXML
    private void updateCAMarks() {
        try {
            Integer markId = Integer.parseInt(updateMarkIdField.getText().trim());
            Double marks = Double.parseDouble(updateMarksField.getText().trim());

            boolean ok = caService.updateCAMarks(markId, marks);
            showStatus(ok ? "CA mark updated successfully." : caService.getLastMessage(),
                    ok ? StatusType.SUCCESS : StatusType.ERROR);

            if (ok) {
                showInfoMessage(
                        "Update Successful",
                        "CA mark updated successfully.",
                        "Mark ID: " + markId + "\nNew Marks: " + marks
                );
                LecturerCourseItem selectedCourse = uploadCourseBox.getValue();
                if (selectedCourse != null) {
                    loadCourseReference(selectedCourse.getCourseId(), false);
                }
            }
        } catch (Exception e) {
            showStatus("Invalid update input.", StatusType.ERROR);
        }
    }

    private void loadCourseReference(String courseId, boolean showLoadStatus) {
        JsonNode node = caService.getCourseCAReference(courseId);
        if (node == null || !node.path("success").asBoolean(false)) {
            assessmentTypeOptions.clear();
            uploadAssessmentTypeBox.getSelectionModel().clearSelection();
            if (showLoadStatus) {
                showStatus(caService.getLastMessage(), StatusType.ERROR);
            }
            return;
        }

        JsonNode data = node.path("data");
        assessmentTypeOptions.setAll(readAssessmentTypes(data.path("assessmentTypes")));

        if (assessmentTypeOptions.isEmpty()) {
            uploadAssessmentTypeBox.getSelectionModel().clearSelection();
            if (showLoadStatus) {
                showStatus("No assignment IDs found for selected course.", StatusType.ERROR);
            }
        } else if (showLoadStatus) {
            uploadAssessmentTypeBox.getSelectionModel().selectFirst();
            showStatus("Assignment IDs loaded for selected course.", StatusType.SUCCESS);
        } else {
            uploadAssessmentTypeBox.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void backToDashboard() {
        loadView("/view/lecturer/lecturerDashboard.fxml");
    }

    private List<CAAssessmentTypeOption> readAssessmentTypes(JsonNode arrayNode) {
        ObservableList<CAAssessmentTypeOption> items = FXCollections.observableArrayList();
        if (arrayNode == null || !arrayNode.isArray()) {
            return items;
        }

        for (JsonNode row : arrayNode) {
            CAAssessmentTypeOption option = new CAAssessmentTypeOption();
            if (row.hasNonNull("assessmentTypeId")) {
                option.setAssessmentTypeId(row.path("assessmentTypeId").asInt());
            }
            option.setAssessmentName(row.path("assessmentName").asText(""));
            if (row.has("weight") && !row.path("weight").isNull()) {
                option.setWeight(row.path("weight").asDouble());
            }
            items.add(option);
        }
        return items;
    }

    private void clearUploadFields() {
        uploadStudentIdField.clear();
        uploadMarksField.clear();
    }

    private void showInfoMessage(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadView(String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot open view");
            alert.setContentText(path);
            alert.showAndWait();
        }
    }

    private enum StatusType {
        SUCCESS, ERROR, INFO
    }

    private void showStatus(String message, StatusType type) {
        statusLabel.setText(message);

        String color = switch (type) {
            case SUCCESS -> "#4cba52";
            case ERROR -> "#e85d5d";
            case INFO -> "#8fa3b8";
        };

        statusLabel.setStyle(
                "-fx-text-fill: " + color + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;"
        );
    }
}
