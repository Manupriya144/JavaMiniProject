package com.example.frontend.controller.admin;

import com.example.frontend.dto.CourseRequestDTO;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.CourseService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AddCourseController implements Initializable {

    @FXML private Label adminNameLabel;
    @FXML private Label statusBarTime;
    @FXML private TextField courseIdField;
    @FXML private TextField courseCodeField;
    @FXML private TextField courseNameField;
    @FXML private TextField courseCreditField;
    @FXML private ComboBox<String> academicLevelBox;
    @FXML private ComboBox<String> semesterBox;
    @FXML private ComboBox<String> departmentBox;
    @FXML private Label statusLabel;
    @FXML private Button addBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        adminNameLabel.setText(LoginController.username);

        statusBarTime.setText(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("d MMMM yyyy")));

        academicLevelBox.setItems(FXCollections.observableArrayList("1", "2", "3", "4"));
        semesterBox.setItems(FXCollections.observableArrayList("1", "2"));
        departmentBox.setItems(FXCollections.observableArrayList("BST", "ET", "ICT"));
    }

    @FXML
    private void addCourse(ActionEvent event) {
        try {
            CourseRequestDTO dto = new CourseRequestDTO();
            dto.setCourseId(courseIdField.getText().trim());
            dto.setCourseCode(courseCodeField.getText().trim());
            dto.setName(courseNameField.getText().trim());
            dto.setCourseCredit(Integer.parseInt(courseCreditField.getText().trim()));
            dto.setAcademicLevel(Integer.parseInt(academicLevelBox.getValue()));
            dto.setSemester(semesterBox.getValue());
            dto.setDepartmentId(departmentBox.getValue());

            CourseService courseService = new CourseService(ServerClient.getInstance());
            boolean success = courseService.addCourse(dto);

            if (success) {
                showStatus("Course added successfully", "#74c69d");
                clearForm(null);
            } else {
                showStatus("Course creation failed", "#ff6b6b");
            }

        } catch (NumberFormatException e) {
            showStatus("Course credit must be a number", "#ff6b6b");
        } catch (Exception e) {
            e.printStackTrace();
            showStatus("Please fill all fields correctly", "#ff6b6b");
        }
    }

    @FXML
    private void clearForm(ActionEvent event) {
        courseIdField.clear();
        courseCodeField.clear();
        courseNameField.clear();
        courseCreditField.clear();
        academicLevelBox.setValue(null);
        semesterBox.setValue(null);
        departmentBox.setValue(null);
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/adminDashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) adminNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showStatus("Failed to load dashboard", "#ff6b6b");
        }
    }

    private void showStatus(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: bold;");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
}