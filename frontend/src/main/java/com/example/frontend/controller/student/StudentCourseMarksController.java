package com.example.frontend.controller.student;

import com.example.frontend.model.StudentCourseMarksRow;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.CourseResultGeneratorService;
import com.example.frontend.service.StudentCourseMarksService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.util.List;

public class StudentCourseMarksController {

    @FXML private TextField searchField;
    @FXML private TextField academicYearField;

    @FXML private ComboBox<String> levelBox;
    @FXML private ComboBox<String> semesterBox;
    @FXML private ComboBox<String> departmentBox;
    @FXML private ComboBox<String> courseBox;

    @FXML private Label statusLabel;
    @FXML private Label summaryLabel;

    @FXML private TableView<StudentCourseMarksRow> marksTable;
    @FXML private TableColumn<StudentCourseMarksRow, String> regNoColumn;
    @FXML private TableColumn<StudentCourseMarksRow, String> nameColumn;
    @FXML private TableColumn<StudentCourseMarksRow, String> departmentColumn;
    @FXML private TableColumn<StudentCourseMarksRow, String> levelColumn;
    @FXML private TableColumn<StudentCourseMarksRow, String> semesterColumn;
    @FXML private TableColumn<StudentCourseMarksRow, String> courseColumn;
    @FXML private TableColumn<StudentCourseMarksRow, String> attendanceColumn;
    @FXML private TableColumn<StudentCourseMarksRow, String> caColumn;
    @FXML private TableColumn<StudentCourseMarksRow, String> finalMarksColumn;
    @FXML private TableColumn<StudentCourseMarksRow, String> medicalColumn;
    @FXML private TableColumn<StudentCourseMarksRow, String> statusColumn;

    private StudentCourseMarksService service;
    private CourseResultGeneratorService resultGeneratorService;

    private final ObservableList<StudentCourseMarksRow> masterList =
            FXCollections.observableArrayList();

    private FilteredList<StudentCourseMarksRow> filteredList;

    @FXML
    public void initialize() {
        service = new StudentCourseMarksService(ServerClient.getInstance());
        resultGeneratorService = new CourseResultGeneratorService(ServerClient.getInstance());

        setupTable();
        setupFilters();
        loadData();
        hideStatus();
    }

    private void setupTable() {
        regNoColumn.setCellValueFactory(data ->
                new SimpleStringProperty(safe(data.getValue().getRegNo())));

        nameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(safe(data.getValue().getStudentName())));

        departmentColumn.setCellValueFactory(data ->
                new SimpleStringProperty(safe(data.getValue().getDepartmentName())));

        levelColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getAcademicLevel())));

        semesterColumn.setCellValueFactory(data ->
                new SimpleStringProperty(safe(data.getValue().getSemester())));

        courseColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        safe(data.getValue().getCourseCode()) + " - " + safe(data.getValue().getCourseName())
                ));

        attendanceColumn.setCellValueFactory(data ->
                new SimpleStringProperty(format(data.getValue().getAttendancePercentage()) + "%"));

        caColumn.setCellValueFactory(data ->
                new SimpleStringProperty(format(data.getValue().getCaPercentage()) + "%"));

        finalMarksColumn.setCellValueFactory(data ->
                new SimpleStringProperty(format(data.getValue().getFinalExamMarks())));

        medicalColumn.setCellValueFactory(data ->
                new SimpleStringProperty(safe(data.getValue().getMedicalStatus())));

        statusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(safe(data.getValue().getResultStatus())));

        marksTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(StudentCourseMarksRow item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                    return;
                }

                if ("Allowed".equalsIgnoreCase(item.getResultStatus())) {
                    setStyle("-fx-background-color: #f0fff4;");
                } else if ("WH".equalsIgnoreCase(item.getResultStatus())) {
                    setStyle("-fx-background-color: #fff8e6;");
                } else {
                    setStyle("-fx-background-color: #fff5f5;");
                }
            }
        });

        filteredList = new FilteredList<>(masterList, p -> true);
        marksTable.setItems(filteredList);
    }

    private void setupFilters() {
        levelBox.setItems(FXCollections.observableArrayList("All", "1", "2", "3", "4"));
        levelBox.setValue("All");

        semesterBox.setItems(FXCollections.observableArrayList("All", "1", "2"));
        semesterBox.setValue("All");

        departmentBox.setItems(FXCollections.observableArrayList("All"));
        departmentBox.setValue("All");

        courseBox.setItems(FXCollections.observableArrayList("All"));
        courseBox.setValue("All");

        academicYearField.setText("2026");
    }

    @FXML
    private void loadData() {
        List<StudentCourseMarksRow> rows = service.getStudentCourseMarks();

        masterList.setAll(rows);

        loadDynamicFilters(rows);
        filterTable();

        showSuccess("Student marks loaded successfully.");
    }

    private void loadDynamicFilters(List<StudentCourseMarksRow> rows) {
        ObservableList<String> departments = FXCollections.observableArrayList("All");
        ObservableList<String> courses = FXCollections.observableArrayList("All");

        for (StudentCourseMarksRow row : rows) {
            String dept = safe(row.getDepartmentName());
            String course = safe(row.getCourseCode()) + " - " + safe(row.getCourseName());

            if (!dept.isBlank() && !departments.contains(dept)) {
                departments.add(dept);
            }

            if (!course.isBlank() && !courses.contains(course)) {
                courses.add(course);
            }
        }

        String oldDept = departmentBox.getValue();
        String oldCourse = courseBox.getValue();

        departmentBox.setItems(departments);
        courseBox.setItems(courses);

        departmentBox.setValue(departments.contains(oldDept) ? oldDept : "All");
        courseBox.setValue(courses.contains(oldCourse) ? oldCourse : "All");
    }

    @FXML
    private void filterTable() {
        String keyword = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase();

        String level = levelBox.getValue();
        String semester = semesterBox.getValue();
        String department = departmentBox.getValue();
        String course = courseBox.getValue();

        if (filteredList == null) return;

        filteredList.setPredicate(row -> {
            boolean keywordMatches = keyword.isEmpty()
                    || safe(row.getRegNo()).toLowerCase().contains(keyword)
                    || safe(row.getStudentName()).toLowerCase().contains(keyword)
                    || safe(row.getCourseName()).toLowerCase().contains(keyword)
                    || safe(row.getCourseCode()).toLowerCase().contains(keyword)
                    || safe(row.getResultStatus()).toLowerCase().contains(keyword);

            boolean levelMatches = level == null || level.equals("All")
                    || String.valueOf(row.getAcademicLevel()).equals(level);

            boolean semesterMatches = semester == null || semester.equals("All")
                    || safe(row.getSemester()).equalsIgnoreCase(semester);

            boolean departmentMatches = department == null || department.equals("All")
                    || safe(row.getDepartmentName()).equalsIgnoreCase(department);

            String rowCourse = safe(row.getCourseCode()) + " - " + safe(row.getCourseName());

            boolean courseMatches = course == null || course.equals("All")
                    || rowCourse.equalsIgnoreCase(course);

            return keywordMatches
                    && levelMatches
                    && semesterMatches
                    && departmentMatches
                    && courseMatches;
        });

        updateSummary();
    }

    @FXML
    private void generateResults() {
        try {
            String selectedCourse = courseBox.getValue();
            String selectedLevel = levelBox.getValue();
            String selectedSemester = semesterBox.getValue();
            String yearText = academicYearField.getText();

            if (selectedCourse == null || selectedCourse.equals("All")) {
                showError("Please select a course before generating results.");
                return;
            }

            if (selectedLevel == null || selectedLevel.equals("All")) {
                showError("Please select academic level.");
                return;
            }

            if (selectedSemester == null || selectedSemester.equals("All")) {
                showError("Please select semester.");
                return;
            }

            if (yearText == null || yearText.trim().isEmpty()) {
                showError("Please enter academic year.");
                return;
            }

            int academicYear;
            try {
                academicYear = Integer.parseInt(yearText.trim());
            } catch (NumberFormatException e) {
                showError("Academic year must be a valid number.");
                return;
            }

            int academicLevel = Integer.parseInt(selectedLevel);

            String courseId = getCourseIdFromSelectedCourse(selectedCourse);

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Generate Course Results");
            confirm.setHeaderText("Generate results for selected course?");
            confirm.setContentText(
                    "Course: " + selectedCourse +
                            "\nYear: " + academicYear +
                            "\nLevel: " + academicLevel +
                            "\nSemester: " + selectedSemester
            );

            ButtonType yes = new ButtonType("Generate", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirm.getButtonTypes().setAll(yes, cancel);

            confirm.showAndWait().ifPresent(type -> {
                if (type == yes) {
                    boolean success = resultGeneratorService.generate(
                            courseId,
                            academicYear,
                            academicLevel,
                            selectedSemester
                    );

                    if (success) {
                        showSuccess("Course results generated successfully.");
                        loadData();
                    } else {
                        showError("Failed to generate course results.");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error while generating results.");
        }
    }

    private String getCourseIdFromSelectedCourse(String selectedCourse) {
        for (StudentCourseMarksRow row : masterList) {
            String rowCourse = safe(row.getCourseCode()) + " - " + safe(row.getCourseName());

            if (rowCourse.equalsIgnoreCase(selectedCourse)) {
                return row.getCourseId();
            }
        }

        return selectedCourse;
    }

    @FXML
    private void clearFilters() {
        searchField.clear();
        levelBox.setValue("All");
        semesterBox.setValue("All");
        departmentBox.setValue("All");
        courseBox.setValue("All");
        filterTable();
    }

    private void updateSummary() {
        int total = filteredList == null ? 0 : filteredList.size();

        long allowed = filteredList == null ? 0 : filteredList.stream()
                .filter(r -> "Allowed".equalsIgnoreCase(r.getResultStatus()))
                .count();

        long wh = filteredList == null ? 0 : filteredList.stream()
                .filter(r -> "WH".equalsIgnoreCase(r.getResultStatus()))
                .count();

        long ee = filteredList == null ? 0 : filteredList.stream()
                .filter(r -> "EE".equalsIgnoreCase(r.getResultStatus()))
                .count();

        summaryLabel.setText(
                total + " records | " +
                        allowed + " allowed | " +
                        wh + " WH | " +
                        ee + " EE"
        );
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

    private String safe(String text) {
        return text == null ? "" : text;
    }

    private String format(double value) {
        return String.format("%.2f", value);
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
}