package com.example.frontend.controller.lecturer;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.model.Student;
import com.example.frontend.service.StudentService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LecturerStudentsController {

    @FXML
    private TextField studentIdField;
    @FXML
    private Label nameValue;
    @FXML
    private Label userIdValue;
    @FXML
    private Label regNoValue;
    @FXML
    private Label batchValue;
    @FXML
    private Label levelValue;
    @FXML
    private Label departmentValue;
    @FXML
    private Label emailValue;
    @FXML
    private Label contactValue;
    @FXML
    private Label statusLabel;

    private final StudentService studentService = new StudentService(LoginController.client);

    @FXML
    private void findStudent() {
        String studentId = studentIdField.getText() == null ? "" : studentIdField.getText().trim();
        if (studentId.isBlank()) {
            statusLabel.setText("Enter a student user ID.");
            clearStudent();
            return;
        }

        Student student = studentService.getStudentByIdAll(studentId);
        if (student == null) {
            statusLabel.setText("No student found for that user ID.");
            clearStudent();
            return;
        }

        nameValue.setText(value(student.getUsername()));
        userIdValue.setText(value(student.getUserId()));
        regNoValue.setText(value(student.getRegNo()));
        batchValue.setText(value(student.getBatch()));
        levelValue.setText(String.valueOf(student.getAcademicLevel()));
        departmentValue.setText(value(student.getDepartmentId()));
        emailValue.setText(value(student.getEmail()));
        contactValue.setText(value(student.getContactNumber()));
        statusLabel.setText("Student loaded.");
    }

    @FXML
    private void backToDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/lecturerDashboard.fxml"));
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Cannot open dashboard");
            alert.setContentText("Failed to load /view/lecturerDashboard.fxml");
            alert.showAndWait();
        }
    }

    private void clearStudent() {
        nameValue.setText("-");
        userIdValue.setText("-");
        regNoValue.setText("-");
        batchValue.setText("-");
        levelValue.setText("-");
        departmentValue.setText("-");
        emailValue.setText("-");
        contactValue.setText("-");
    }

    private String value(String text) {
        return text == null || text.isBlank() ? "-" : text;
    }
}
