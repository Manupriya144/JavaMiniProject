package com.example.frontend.controller.lecturer;

import com.example.frontend.model.LecturerCourseItem;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.FinalMarksService;
import com.example.frontend.service.LecturerCourseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.util.List;

public class UploadFinalMarksController {

    @FXML private ComboBox<LecturerCourseItem> courseBox;
    @FXML private TextField regNoField;
    @FXML private TextField marksField;
    @FXML private Label statusLabel;

    @FXML private TableView<FinalMarkRow> marksTable;
    @FXML private TableColumn<FinalMarkRow, String> regNoColumn;
    @FXML private TableColumn<FinalMarkRow, String> courseColumn;
    @FXML private TableColumn<FinalMarkRow, String> marksColumn;

    private FinalMarksService finalMarksService;
    private LecturerCourseService lecturerCourseService;

    private final ObservableList<FinalMarkRow> uploadedMarks =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        finalMarksService = new FinalMarksService(ServerClient.getInstance());
        lecturerCourseService = new LecturerCourseService(ServerClient.getInstance());

        setupTable();
        setupCourseBox();
        loadCourses();
        hideStatus();
    }

    private void setupTable() {
        regNoColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getRegNo()));

        courseColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCourseName()));

        marksColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getMarks()));

        marksTable.setItems(uploadedMarks);
    }

    private void setupCourseBox() {
        courseBox.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(LecturerCourseItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCourseName() + "  |  " + item.getCourseId());
                }
            }
        });

        courseBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(LecturerCourseItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText("Select Course");
                } else {
                    setText(item.getCourseName() + "  |  " + item.getCourseId());
                }
            }
        });
    }

    private void loadCourses() {
        try {
            List<LecturerCourseItem> courses =
                    lecturerCourseService.getLecturerCourses();

            if (courses == null || courses.isEmpty()) {
                showError("No assigned courses found.");
                return;
            }

            courseBox.setItems(FXCollections.observableArrayList(courses));
            courseBox.getSelectionModel().selectFirst();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load courses.");
        }
    }

    @FXML
    private void uploadMarks() {
        uploadOrUpdateMarks("Final marks uploaded successfully.");
    }

    @FXML
    private void clearForm() {
        regNoField.clear();
        marksField.clear();
        hideStatus();
    }

    @FXML
    private void updateMarks(){
        uploadOrUpdateMarks("Final marks updated successfully.");
    }

    private void uploadOrUpdateMarks(String successMessage) {
        try {
            LecturerCourseItem selectedCourse = courseBox.getValue();
            String regNo = regNoField.getText();
            String marksText = marksField.getText();

            if (selectedCourse == null) {
                showError("Please select course.");
                return;
            }

            if (regNo == null || regNo.trim().isEmpty()) {
                showError("Please enter student registration number.");
                return;
            }

            if (marksText == null || marksText.trim().isEmpty()) {
                showError("Please enter marks.");
                return;
            }

            double marks;

            try {
                marks = Double.parseDouble(marksText.trim());
            } catch (NumberFormatException e) {
                showError("Marks must be a valid number.");
                return;
            }

            if (marks < 0 || marks > 100) {
                showError("Marks must be between 0 and 100.");
                return;
            }

            boolean success = finalMarksService.uploadFinalMarks(
                    regNo.trim(),
                    selectedCourse.getCourseId(),
                    marks
            );

            if (success) {
                uploadedMarks.add(new FinalMarkRow(
                        regNo.trim(),
                        selectedCourse.getCourseName(),
                        String.valueOf(marks)
                ));

                showSuccess(successMessage);
                clearFieldsOnly();
            } else {
                showError("Failed to save final marks.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error while saving final marks.");
        }
    }

    private void clearFieldsOnly() {
        regNoField.clear();
        marksField.clear();
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/lecturer/lecturerDashboard.fxml")
            );

            Parent root = loader.load();
            statusLabel.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to go back.");
        }
    }

    private void showSuccess(String message) {
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
    }

    private void showError(String message) {
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
    }

    private void hideStatus() {
        statusLabel.setText("");
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
    }

    public static class FinalMarkRow {
        private final String regNo;
        private final String courseName;
        private final String marks;

        public FinalMarkRow(String regNo, String courseName, String marks) {
            this.regNo = regNo;
            this.courseName = courseName;
            this.marks = marks;
        }

        public String getRegNo() {
            return regNo;
        }

        public String getCourseName() {
            return courseName;
        }

        public String getMarks() {
            return marks;
        }
    }
}