package com.example.frontend.controller.admin;

import com.example.frontend.dto.CourseAllResponseDTO;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.CourseService;
import com.example.frontend.session.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CourseDisplayController implements Initializable {

    @FXML
    private TableView<CourseAllResponseDTO> courseTable;
    @FXML
    private TableColumn<CourseAllResponseDTO, String> colCourseId;
    @FXML
    private TableColumn<CourseAllResponseDTO, String> colCourseCode;
    @FXML
    private TableColumn<CourseAllResponseDTO, String> colName;
    @FXML
    private TableColumn<CourseAllResponseDTO, Integer> colCredit;
    @FXML
    private TableColumn<CourseAllResponseDTO, Integer> colLevel;
    @FXML
    private TableColumn<CourseAllResponseDTO, String> colSemester;
    @FXML
    private TableColumn<CourseAllResponseDTO, String> colDepartmentId;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> levelFilter;
    @FXML
    private ComboBox<String> semesterFilter;
    @FXML
    private Label statusLabel;
    @FXML
    private Label adminNameLabel;
    @FXML
    private Label statusBarTime;

    private CourseService courseService;
    private List<CourseAllResponseDTO> allCourses = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseService = new CourseService(ServerClient.getInstance());

        setAdminDetails();
        setupTable();
        setupFilters();
        setupListeners();
        startClock();
        loadCourses();

        Platform.runLater(() -> {
            styleTableHeader();
            styleScrollBar();
        });
    }

    private void setAdminDetails() {
        try {
            String username = LoginController.username;
            if (username != null && !username.trim().isEmpty()) {
                adminNameLabel.setText(username);
            } else {
                adminNameLabel.setText("Administrator");
            }
        } catch (Exception e) {
            adminNameLabel.setText("Administrator");
        }
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
    }

    private void styleTableHeader() {
        courseTable.lookupAll(".column-header-background").forEach(node ->
                node.setStyle("-fx-background-color: #0f1b35;"));

        courseTable.lookupAll(".column-header").forEach(node ->
                node.setStyle("-fx-background-color: #0f1b35; -fx-border-color: #1e3c72;"));

        courseTable.lookupAll(".column-header .label").forEach(node ->
                node.setStyle("-fx-text-fill: #a0c4ff; -fx-font-weight: bold; -fx-font-size: 12px;"));

        courseTable.lookupAll(".filler").forEach(node ->
                node.setStyle("-fx-background-color: #0f1b35; -fx-border-color: #1e3c72;"));
    }

    private void styleScrollBar() {
        courseTable.lookupAll(".scroll-bar").forEach(node ->
                node.setStyle("-fx-background-color: #0f1b35;"));

        courseTable.lookupAll(".scroll-bar .thumb").forEach(node ->
                node.setStyle("-fx-background-color: #2a4a7f; -fx-background-radius: 8;"));

        courseTable.lookupAll(".scroll-bar .track").forEach(node ->
                node.setStyle("-fx-background-color: #0f1b35;"));
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

    private void loadCourses() {
        try {
            allCourses = courseService.getAllCoursesFull();
            courseTable.setItems(FXCollections.observableArrayList(allCourses));

            if (allCourses == null || allCourses.isEmpty()) {
                statusLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 13px; -fx-font-weight: bold;");
                statusLabel.setText("No courses found.");
            } else {
                statusLabel.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 13px; -fx-font-weight: bold;");
                statusLabel.setText("Loaded " + allCourses.size() + " course(s) successfully.");
            }

            Platform.runLater(() -> {
                styleTableHeader();
                styleScrollBar();
            });

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 13px; -fx-font-weight: bold;");
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
                            || (course.getName() != null && course.getName().toLowerCase().contains(keyword))
                            || (course.getCourseCode() != null && course.getCourseCode().toLowerCase().contains(keyword));

                    boolean matchesLevel = selectedLevel == null
                            || selectedLevel.equals("All")
                            || String.valueOf(course.getAcademicLevel()).equals(selectedLevel);

                    boolean matchesSemester = selectedSemester == null
                            || selectedSemester.equals("All")
                            || (course.getSemester() != null && course.getSemester().equals(selectedSemester));

                    return matchesSearch && matchesLevel && matchesSemester;
                })
                .collect(Collectors.toList());

        courseTable.setItems(FXCollections.observableArrayList(filtered));
        statusLabel.setStyle("-fx-text-fill: #93c5fd; -fx-font-size: 13px; -fx-font-weight: bold;");
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
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resolveDashboardPath()));
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

    private String resolveDashboardPath() {
        String role = SessionManager.getRole();
        if ("Lecturer".equalsIgnoreCase(role)) {
            return "/view/lecturerDashboard.fxml";
        }
        if ("Technical Officer".equalsIgnoreCase(role)) {
            return "/view/techOfficerDashboard.fxml";
        }
        return "/view/AdminDashboard.fxml";
    }
}
