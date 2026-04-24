package com.example.frontend.controller.lecturer;

import com.example.frontend.model.LecturerCourseItem;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.LecturerCourseService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class LecturerCoursesController {

    @FXML private ListView<LecturerCourseItem> courseListView;
    @FXML private Label statusLabel;

    private LecturerCourseService lecturerCourseService;

    @FXML
    public void initialize() {
        lecturerCourseService = new LecturerCourseService(ServerClient.getInstance());
        setupCourseListView();
        loadLecturerCourses();
        hideStatus();
    }

    private void setupCourseListView() {
        courseListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(LecturerCourseItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("📚  " + item.getCourseName()
                            + "\n     Course ID: " + item.getCourseId());
                    setStyle("""
                            -fx-font-size: 13px;
                            -fx-padding: 15;
                            -fx-text-fill: #1a3a52;
                            -fx-background-color: white;
                            -fx-border-color: #e8eef5;
                            -fx-border-width: 0 0 1 0;
                            """);
                }
            }
        });
    }

    private void loadLecturerCourses() {
        try {
            List<LecturerCourseItem> courses = lecturerCourseService.getLecturerCourses();

            if (courses == null || courses.isEmpty()) {
                showInfo("No assigned courses found.");
                return;
            }

            courseListView.setItems(FXCollections.observableArrayList(courses));
            hideStatus();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load lecturer courses.");
        }
    }

    @FXML
    private void openSelectedCourse(MouseEvent event) {
        LecturerCourseItem selectedCourse =
                courseListView.getSelectionModel().getSelectedItem();

        if (selectedCourse == null) {
            showError("Please select a course.");
            return;
        }

        openCourseMaterials(selectedCourse);
    }

    private void openCourseMaterials(LecturerCourseItem course) {
        try {
            System.out.println("Opening course: " + course.getCourseId());
            System.out.println("FXML path: " + getClass().getResource("/view/lecturer/CourseMaterials.fxml"));

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/lecturer/CourseMaterials.fxml")
            );

            Parent root = loader.load();

            CourseMaterialsController controller = loader.getController();
            controller.setCourse(course);

            courseListView.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to open course materials.");
        }
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

    private void hideStatus() {
        statusLabel.setText("");
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/lecturer/lecturerDashboard.fxml")
            );

            Parent root = loader.load();

            courseListView.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to go back.");
        }
    }
}