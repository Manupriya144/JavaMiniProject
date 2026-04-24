package com.example.frontend.controller.student;

import com.example.frontend.network.ServerClient;
import com.example.frontend.service.AcademicEndpointService;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.util.Callback;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StudentCoursesController implements Initializable {

    @FXML private TableView<CourseProperty> coursesTable;
    @FXML private Label totalCoursesLabel;
    @FXML private Label statusLabel;
    @FXML private TableColumn<CourseProperty, Void> colAction;

    public static class CourseProperty {
        private String courseId;
        private String courseCode;
        private String courseName;
        private String courseCredit;
        private String academicYear;
        private String semester;
        private String registrationType;

        public CourseProperty(String courseId, String courseCode, String courseName, String courseCredit, String academicYear, String semester, String registrationType) {
            this.courseId = courseId;
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.courseCredit = courseCredit;
            this.academicYear = academicYear;
            this.semester = semester;
            this.registrationType = registrationType;
        }

        public String getCourseId() { return courseId; }
        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }
        public String getCourseCredit() { return courseCredit; }
        public String getAcademicYear() { return academicYear; }
        public String getSemester() { return semester; }
        public String getRegistrationType() { return registrationType; }
    }

    private final AcademicEndpointService academicService = new AcademicEndpointService(ServerClient.getInstance());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupActionColumn();
        loadCourses();
    }

    private void setupActionColumn() {
        Callback<TableColumn<CourseProperty, Void>, TableCell<CourseProperty, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<CourseProperty, Void> call(final TableColumn<CourseProperty, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("View");

                    {
                        btn.setStyle("-fx-background-color: #5b9fd9; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 4;");
                        btn.setOnAction((ActionEvent event) -> {
                            CourseProperty course = getTableView().getItems().get(getIndex());
                            openSingleCourseView(course);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        colAction.setCellFactory(cellFactory);
    }

    private void openSingleCourseView(CourseProperty course) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/student/SingleCourseView.fxml"));
            Parent root = loader.load();

            SingleCourseViewController controller = loader.getController();
            controller.initData(course);

            Stage stage = (Stage) coursesTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCourses() {
        statusLabel.setText("Fetching courses...");
        
        JsonNode response = academicService.getMyCourses();
        ObservableList<CourseProperty> courseList = FXCollections.observableArrayList();

        if (response != null && response.has("success") && response.get("success").asBoolean()) {
            JsonNode data = response.get("data");
            if (data != null && data.isArray()) {
                for (JsonNode node : data) {
                    String id = node.has("courseId") ? node.get("courseId").asText() : "";
                    String code = node.has("courseCode") ? node.get("courseCode").asText() : "N/A";
                    String name = node.has("courseName") ? node.get("courseName").asText() : "N/A";
                    String credits = node.has("courseCredit") ? node.get("courseCredit").asText() : "N/A";
                    String year = node.has("academicYear") ? node.get("academicYear").asText() : "N/A";
                    String sem = node.has("semester") ? node.get("semester").asText() : "N/A";
                    String type = node.has("registrationType") ? node.get("registrationType").asText() : "N/A";

                    courseList.add(new CourseProperty(id, code, name, credits, year, sem, type));
                }
                statusLabel.setText("Courses loaded successfully.");
                statusLabel.setStyle("-fx-text-fill: #4cba52;");
                totalCoursesLabel.setText(String.valueOf(courseList.size()));
            } else {
                statusLabel.setText("No courses found.");
                statusLabel.setStyle("-fx-text-fill: #8fa3b8;");
            }
        } else {
            statusLabel.setText("Failed to load courses.");
            statusLabel.setStyle("-fx-text-fill: #e85d5d;");
        }

        coursesTable.setItems(courseList);
    }

    @FXML
    private void backToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/student/studentDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) coursesTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
