package com.example.frontend.controller.admin;

import com.example.frontend.dto.CourseAllResponseDTO;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.CourseService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CourseDisplayController implements Initializable {

    @FXML private TableView<CourseAllResponseDTO> courseTable;
    @FXML private TableColumn<CourseAllResponseDTO, String> colCourseId;
    @FXML private TableColumn<CourseAllResponseDTO, String> colCourseCode;
    @FXML private TableColumn<CourseAllResponseDTO, String> colName;
    @FXML private TableColumn<CourseAllResponseDTO, Integer> colCredit;
    @FXML private TableColumn<CourseAllResponseDTO, Integer> colLevel;
    @FXML private TableColumn<CourseAllResponseDTO, String> colSemester;
    @FXML private TableColumn<CourseAllResponseDTO, String> colDepartmentId;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> levelFilter;
    @FXML private ComboBox<String> semesterFilter;
    @FXML private Label statusLabel;
    @FXML private Label adminNameLabel;
    @FXML private Label statusBarTime;

    @FXML private Button updateCourseButton;
    @FXML private Button deleteCourseButton;

    private CourseService courseService;
    private List<CourseAllResponseDTO> allCourses = new ArrayList<>();
    private CourseAllResponseDTO selectedCourse;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseService = new CourseService(ServerClient.getInstance());

        setAdminDetails();
        setupTable();
        setupFilters();
        setupListeners();
        startClock();
        loadCourses();

        updateCourseButton.setDisable(true);
        deleteCourseButton.setDisable(true);

        Platform.runLater(() -> {
            styleTableHeader();
            styleScrollBar();
        });
    }

    private void setAdminDetails() {
        String username = LoginController.username;
        adminNameLabel.setText(username == null || username.isBlank() ? "Administrator" : username);
    }

    private void setupTable() {
        colCourseId.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        colCourseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCredit.setCellValueFactory(new PropertyValueFactory<>("courseCredit"));
        colLevel.setCellValueFactory(new PropertyValueFactory<>("academicLevel"));
        colSemester.setCellValueFactory(new PropertyValueFactory<>("semester"));
        colDepartmentId.setCellValueFactory(new PropertyValueFactory<>("departmentId"));

        courseTable.setPlaceholder(new Label("No courses available"));

        courseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            selectedCourse = selected;

            boolean hasSelection = selectedCourse != null;
            updateCourseButton.setDisable(!hasSelection);
            deleteCourseButton.setDisable(!hasSelection);

            if (hasSelection) {
                statusLabel.setStyle("-fx-text-fill: #5b9fd9; -fx-font-size: 13px; -fx-font-weight: bold;");
                statusLabel.setText("Selected course: " + selectedCourse.getCourseCode());
            }
        });
    }

    private void setupFilters() {
        levelFilter.setItems(FXCollections.observableArrayList("All", "1", "2", "3", "4"));
        semesterFilter.setItems(FXCollections.observableArrayList("All", "1", "2"));

        levelFilter.setValue("All");
        semesterFilter.setValue("All");
    }

    private void setupListeners() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        levelFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        semesterFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void loadCourses() {
        try {
            List<CourseAllResponseDTO> courses = courseService.getAllCoursesFull();

            if (courses == null) {
                courses = new ArrayList<>();
            }

            allCourses = courses;
            courseTable.setItems(FXCollections.observableArrayList(allCourses));

            selectedCourse = null;
            updateCourseButton.setDisable(true);
            deleteCourseButton.setDisable(true);

            if (allCourses.isEmpty()) {
                statusLabel.setStyle("-fx-text-fill: #f5a623; -fx-font-size: 13px; -fx-font-weight: bold;");
                statusLabel.setText("No courses found.");
            } else {
                statusLabel.setStyle("-fx-text-fill: #4cba52; -fx-font-size: 13px; -fx-font-weight: bold;");
                statusLabel.setText("Loaded " + allCourses.size() + " course(s) successfully.");
            }

            Platform.runLater(() -> {
                styleTableHeader();
                styleScrollBar();
            });

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setStyle("-fx-text-fill: #e85d5d; -fx-font-size: 13px; -fx-font-weight: bold;");
            statusLabel.setText("Failed to load courses.");
        }
    }

    private void applyFilters() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().toLowerCase().trim();
        String selectedLevel = levelFilter.getValue();
        String selectedSemester = semesterFilter.getValue();

        List<CourseAllResponseDTO> filtered = allCourses.stream()
                .filter(course -> {
                    boolean matchesSearch = keyword.isEmpty()
                            || safe(course.getName()).toLowerCase().contains(keyword)
                            || safe(course.getCourseCode()).toLowerCase().contains(keyword)
                            || safe(course.getCourseId()).toLowerCase().contains(keyword);

                    boolean matchesLevel = selectedLevel == null
                            || selectedLevel.equals("All")
                            || String.valueOf(course.getAcademicLevel()).equals(selectedLevel);

                    boolean matchesSemester = selectedSemester == null
                            || selectedSemester.equals("All")
                            || safe(course.getSemester()).equals(selectedSemester);

                    return matchesSearch && matchesLevel && matchesSemester;
                })
                .collect(Collectors.toList());

        courseTable.setItems(FXCollections.observableArrayList(filtered));

        selectedCourse = null;
        updateCourseButton.setDisable(true);
        deleteCourseButton.setDisable(true);

        statusLabel.setStyle("-fx-text-fill: #5b9fd9; -fx-font-size: 13px; -fx-font-weight: bold;");
        statusLabel.setText("Showing " + filtered.size() + " course(s).");

        Platform.runLater(() -> {
            styleTableHeader();
            styleScrollBar();
        });
    }

    @FXML
    private void refreshCourses() {
        searchField.clear();
        levelFilter.setValue("All");
        semesterFilter.setValue("All");
        loadCourses();
    }

    @FXML
    private void updateSelectedCourse() {
        if (selectedCourse == null) {
            statusLabel.setText("Please select a course first.");
            return;
        }

        Dialog<CourseAllResponseDTO> dialog = new Dialog<>();
        dialog.setTitle("Update Course");
        dialog.setHeaderText("Update course: " + selectedCourse.getCourseCode());

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        TextField courseCodeField = new TextField(safe(selectedCourse.getCourseCode()));
        TextField nameField = new TextField(safe(selectedCourse.getName()));
        TextField creditField = new TextField(String.valueOf(selectedCourse.getCourseCredit()));
        TextField levelField = new TextField(String.valueOf(selectedCourse.getAcademicLevel()));
        ComboBox<String> semesterBox = new ComboBox<>(FXCollections.observableArrayList("1", "2"));
        semesterBox.setValue(safe(selectedCourse.getSemester()));
        TextField departmentField = new TextField(safe(selectedCourse.getDepartmentId()));

        VBox form = new VBox(10);
        form.setStyle("-fx-padding: 12;");
        form.getChildren().addAll(
                new Label("Course Code"), courseCodeField,
                new Label("Course Name"), nameField,
                new Label("Course Credit"), creditField,
                new Label("Academic Level"), levelField,
                new Label("Semester"), semesterBox,
                new Label("Department ID"), departmentField
        );

        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(button -> {
            if (button == updateButtonType) {
                try {
                    CourseAllResponseDTO updated = new CourseAllResponseDTO();

                    updated.setCourseId(selectedCourse.getCourseId());
                    updated.setCourseCode(courseCodeField.getText().trim());
                    updated.setName(nameField.getText().trim());
                    updated.setCourseCredit(Integer.parseInt(creditField.getText().trim()));
                    updated.setAcademicLevel(Integer.parseInt(levelField.getText().trim()));
                    updated.setSemester(semesterBox.getValue());
                    updated.setDepartmentId(departmentField.getText().trim());

                    return updated;

                } catch (Exception e) {
                    statusLabel.setStyle("-fx-text-fill: #e85d5d; -fx-font-size: 13px; -fx-font-weight: bold;");
                    statusLabel.setText("Invalid input. Credit and level must be numbers.");
                    return null;
                }
            }
            return null;
        });

        Optional<CourseAllResponseDTO> result = dialog.showAndWait();

        result.ifPresent(updatedCourse -> {
            boolean updated = courseService.updateCourse(updatedCourse);

            if (updated) {
                statusLabel.setStyle("-fx-text-fill: #4cba52; -fx-font-size: 13px; -fx-font-weight: bold;");
                statusLabel.setText("Course updated successfully.");
                refreshCourses();
            } else {
                statusLabel.setStyle("-fx-text-fill: #e85d5d; -fx-font-size: 13px; -fx-font-weight: bold;");
                statusLabel.setText("Failed to update course.");
            }
        });
    }

    @FXML
    private void deleteSelectedCourse() {
        if (selectedCourse == null) {
            statusLabel.setText("Please select a course first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Course");
        confirm.setHeaderText("Delete selected course?");
        confirm.setContentText(
                "Are you sure you want to delete:\n\n" +
                        selectedCourse.getCourseCode() + " - " + selectedCourse.getName()
        );

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = courseService.deleteCourse(selectedCourse.getCourseId());

            if (deleted) {
                statusLabel.setStyle("-fx-text-fill: #4cba52; -fx-font-size: 13px; -fx-font-weight: bold;");
                statusLabel.setText("Course deleted successfully.");
                refreshCourses();
            } else {
                statusLabel.setStyle("-fx-text-fill: #e85d5d; -fx-font-size: 13px; -fx-font-weight: bold;");
                statusLabel.setText("Failed to delete course.");
            }
        }
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/AdminDashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) courseTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 13px; -fx-font-weight: bold;");
            statusLabel.setText("Failed to go back.");
        }
    }

    private void startClock() {
        updateTime();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> updateTime())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy  •  hh:mm:ss a");
        statusBarTime.setText(LocalDateTime.now().format(formatter));
    }

    private void styleTableHeader() {
        courseTable.lookupAll(".column-header-background").forEach(node ->
                node.setStyle("-fx-background-color: #ffffff;"));

        courseTable.lookupAll(".column-header").forEach(node ->
                node.setStyle("-fx-background-color: #f5f9ff; -fx-border-color: #e8eef5;"));

        courseTable.lookupAll(".column-header .label").forEach(node ->
                node.setStyle("-fx-text-fill: #1a3a52; -fx-font-weight: bold; -fx-font-size: 12px;"));

        courseTable.lookupAll(".filler").forEach(node ->
                node.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e8eef5;"));
    }

    private void styleScrollBar() {
        courseTable.lookupAll(".scroll-bar").forEach(node ->
                node.setStyle("-fx-background-color: #ffffff;"));

        courseTable.lookupAll(".scroll-bar .thumb").forEach(node ->
                node.setStyle("-fx-background-color: #d4e4f7; -fx-background-radius: 8;"));

        courseTable.lookupAll(".scroll-bar .track").forEach(node ->
                node.setStyle("-fx-background-color: #f5f9ff;"));
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}