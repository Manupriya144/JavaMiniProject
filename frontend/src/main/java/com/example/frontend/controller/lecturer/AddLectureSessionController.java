package com.example.frontend.controller.lecturer;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.dto.AddLectureSessionRequestDTO;
import com.example.frontend.dto.AddLectureSessionResponseDTO;
import com.example.frontend.model.LecturerCourseItem;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.LecturerCourseService;
import com.example.frontend.service.LecturerService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AddLectureSessionController {

    @FXML
    private Label lecturerNameLabel;

    @FXML
    private ComboBox<LecturerCourseItem> courseComboBox;

    @FXML
    private DatePicker sessionDatePicker;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField hoursField;

    @FXML
    private Label statusLabel;

    @FXML
    private Label statusBarTime;

    private LecturerService lectureSessionService;
    private LecturerCourseService lecturerCourseService;

    @FXML
    public void initialize() {
        lectureSessionService = new LecturerService(ServerClient.getInstance());
        lecturerCourseService = new LecturerCourseService(ServerClient.getInstance());

        loadLecturerInfo();
        loadSessionTypes();
        loadStatusBarTime();
        loadLecturerCourses();
    }

    private void loadLecturerInfo() {
        try {
            String username = LoginController.username;

            if (username != null && !username.isBlank()) {
                lecturerNameLabel.setText(username);
            } else {
                lecturerNameLabel.setText("Lecturer");
            }

        } catch (Exception e) {
            lecturerNameLabel.setText("Lecturer");
        }
    }

    private void loadSessionTypes() {
        typeComboBox.setItems(FXCollections.observableArrayList("Theory", "Practical"));
    }

    private void loadStatusBarTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        statusBarTime.setText(LocalDateTime.now().format(formatter));
    }

    private void loadLecturerCourses() {
        try {
            List<LecturerCourseItem> courseList = lecturerCourseService.getLecturerCourses();
            courseComboBox.setItems(FXCollections.observableArrayList(courseList));

            if (courseList == null || courseList.isEmpty()) {
                showError("No assigned courses found.");
                return;
            }

            courseComboBox.getSelectionModel().selectFirst();
            hideStatus();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load lecturer courses.");
        }
    }

    @FXML
    private void handleSaveSession() {
        try {
            LecturerCourseItem selectedCourse = courseComboBox.getValue();
            LocalDate selectedDate = sessionDatePicker.getValue();
            String type = typeComboBox.getValue();
            String hoursText = hoursField.getText();

            if (selectedCourse == null) {
                showError("Please select a course.");
                return;
            }

            if (selectedDate == null) {
                showError("Please select a session date.");
                return;
            }

            if (type == null || type.isBlank()) {
                showError("Please select a session type.");
                return;
            }

            if (hoursText == null || hoursText.trim().isEmpty()) {
                showError("Please enter session hours.");
                return;
            }

            double hours;
            try {
                hours = Double.parseDouble(hoursText.trim());
            } catch (NumberFormatException e) {
                showError("Session hours must be a valid number.");
                return;
            }

            if (hours <= 0) {
                showError("Session hours must be greater than 0.");
                return;
            }

            AddLectureSessionRequestDTO requestDTO = new AddLectureSessionRequestDTO(
                    selectedCourse.getCourseId(),
                    selectedDate.toString(),
                    hours,
                    type
            );

            AddLectureSessionResponseDTO responseDTO = lectureSessionService.addLectureSession(requestDTO);

            if (responseDTO != null && responseDTO.isSuccess()) {
                showSuccess("Lecture session added successfully. Session ID: " + responseDTO.getSessionId());
                clearFormFieldsAfterSave();
            } else {
                String message = "Failed to add lecture session.";
                if (responseDTO != null && responseDTO.getMessage() != null && !responseDTO.getMessage().isBlank()) {
                    message = responseDTO.getMessage();
                }
                showError(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error while saving lecture session.");
        }
    }

    @FXML
    private void handleClear() {
        courseComboBox.getSelectionModel().clearSelection();
        sessionDatePicker.setValue(null);
        typeComboBox.getSelectionModel().clearSelection();
        hoursField.clear();
        hideStatus();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/lecturer/lecturerDashboard.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) lecturerNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Lecturer Dashboard");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to open lecturer dashboard.");
        }
    }

    private void clearFormFieldsAfterSave() {
        sessionDatePicker.setValue(null);
        typeComboBox.getSelectionModel().clearSelection();
        hoursField.clear();
    }

    private void showSuccess(String message) {
        statusLabel.setManaged(true);
        statusLabel.setVisible(true);
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #4cba52; -fx-font-size: 12px; -fx-font-weight: bold;");
    }

    private void showError(String message) {
        statusLabel.setManaged(true);
        statusLabel.setVisible(true);
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px; -fx-font-weight: bold;");
    }

    private void hideStatus() {
        statusLabel.setText("");
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
    }
}