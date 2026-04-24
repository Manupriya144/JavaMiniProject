package com.example.frontend.controller.medical;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.model.AttendanceCourseOption;
import com.example.frontend.model.AttendanceStudentOption;
import com.example.frontend.model.Medical;
import com.example.frontend.service.AttendanceService;
import com.example.frontend.service.MedicalService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Comparator;
import java.util.stream.Collectors;

public class AddMedicalController {

    @FXML private ComboBox<String> studentRegCombo;
    @FXML private ComboBox<String> courseIdCombo;
    @FXML private ComboBox<String> examTypeCombo;
    @FXML private DatePicker submittedDatePicker;
    @FXML private TextField medicalCopyField;
    @FXML private Label statusLabel;
    @FXML private Label officerNameLabel;
    @FXML private Label statusBarTime;

    private final MedicalService medicalService =
            new MedicalService(LoginController.client);

    private final AttendanceService attendanceService =
            new AttendanceService(LoginController.client);

    private final Map<String, String> regNoToUserId = new HashMap<>();
    private final Map<String, String> courseDisplayToId = new HashMap<>();
    private final List<String> allRegNos = new ArrayList<>();
    private final List<String> allCourseDisplays = new ArrayList<>();
    private final Set<String> validCourseIds = new HashSet<>();

    private boolean suppressStudentFilter = false;
    private boolean suppressCourseFilter = false;
    private boolean suppressStudentSelection = false;

    @FXML
    public void initialize() {
        if (officerNameLabel != null) {
            officerNameLabel.setText(
                    LoginController.username != null && !LoginController.username.isBlank()
                            ? LoginController.username
                            : "Tech Officer"
            );
        }

        if (statusBarTime != null) {
            statusBarTime.setText(
                    LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
            );
        }

        examTypeCombo.getItems().setAll("Mid", "Final", "Attendance");
        examTypeCombo.getSelectionModel().select("Attendance");

        studentRegCombo.setEditable(true);
        courseIdCombo.setEditable(true);
        courseIdCombo.setDisable(true);

        loadStudentOptions();

        studentRegCombo.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (!suppressStudentFilter) {
                applyStudentFilter(newText == null ? "" : newText);
            }
        });

        studentRegCombo.setOnAction(e -> {
            if (suppressStudentSelection || suppressStudentFilter) {
                return;
            }
            String selected = studentRegCombo.getValue();
            String candidateReg = selected != null ? selected : studentRegCombo.getEditor().getText();
            String userId = findUserIdByRegNo(candidateReg);
            if (userId != null) {
                onStudentSelected(candidateReg.trim());
            }
        });

        studentRegCombo.getEditor().focusedProperty().addListener((obs, oldFocus, focused) -> {
            if (!focused) {
                String typed = studentRegCombo.getEditor().getText();
                if (findUserIdByRegNo(typed) != null) {
                    onStudentSelected(typed.trim());
                }
            }
        });

        courseIdCombo.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (!suppressCourseFilter) {
                applyCourseFilter(newText == null ? "" : newText);
            }
        });

        showStatus("", StatusType.INFO);
    }

    private void loadStudentOptions() {
        List<AttendanceStudentOption> students = attendanceService.getMedicalEligibleStudents();

        regNoToUserId.clear();
        allRegNos.clear();

        for (AttendanceStudentOption student : students) {
            if (student.getRegNo() != null && !student.getRegNo().isBlank()) {
                String regNo = student.getRegNo().trim();
                regNoToUserId.put(regNo, student.getUserId());
                allRegNos.add(regNo);
            }
        }

        Collections.sort(allRegNos);
        studentRegCombo.setItems(FXCollections.observableArrayList(allRegNos));

        if (students.isEmpty()) {
            showStatus(attendanceService.getLastMessage(), StatusType.ERROR);
        }
    }

    private void onStudentSelected(String regNo) {
        String userId = findUserIdByRegNo(regNo);

        allCourseDisplays.clear();
        validCourseIds.clear();
        courseDisplayToId.clear();
        courseIdCombo.getItems().clear();
        courseIdCombo.getEditor().clear();

        if (userId == null) {
            courseIdCombo.setDisable(true);
            return;
        }

        List<AttendanceCourseOption> courses = attendanceService.getMedicalEligibleCourseIds(userId);
        for (AttendanceCourseOption option : courses) {
            String courseId = option.getCourseId() == null ? "" : option.getCourseId().trim();
            if (courseId.isBlank()) {
                continue;
            }
            validCourseIds.add(courseId);
            String display = option.getDisplayText().trim();
            allCourseDisplays.add(display);
            courseDisplayToId.put(display, courseId);
        }

        Collections.sort(allCourseDisplays);
        courseIdCombo.setItems(FXCollections.observableArrayList(allCourseDisplays));
        courseIdCombo.setDisable(allCourseDisplays.isEmpty());

        if (allCourseDisplays.isEmpty()) {
            showStatus(attendanceService.getLastMessage(), StatusType.ERROR);
        } else {
            showStatus("Courses loaded for selected student.", StatusType.SUCCESS);
        }
    }

    private void applyStudentFilter(String text) {
        if (suppressStudentSelection) {
            return;
        }
        if (studentRegCombo.isShowing() && studentRegCombo.getSelectionModel().getSelectedIndex() >= 0) {
            return;
        }

        List<String> filtered = filterList(allRegNos, text);
        String currentText = studentRegCombo.getEditor().getText();

        suppressStudentFilter = true;
        suppressStudentSelection = true;
        studentRegCombo.getItems().setAll(filtered);
        studentRegCombo.getEditor().setText(currentText);
        studentRegCombo.getEditor().positionCaret(currentText == null ? 0 : currentText.length());
        suppressStudentSelection = false;
        suppressStudentFilter = false;

        Platform.runLater(() -> {
            if (!studentRegCombo.isShowing() && studentRegCombo.isFocused()) {
                studentRegCombo.show();
            }
        });
    }

    private void applyCourseFilter(String text) {
        if (courseIdCombo.isShowing() && courseIdCombo.getSelectionModel().getSelectedIndex() >= 0) {
            return;
        }

        List<String> filtered = filterList(allCourseDisplays, text);
        String currentText = courseIdCombo.getEditor().getText();

        suppressCourseFilter = true;
        courseIdCombo.getItems().setAll(filtered);
        courseIdCombo.getEditor().setText(currentText);
        courseIdCombo.getEditor().positionCaret(currentText == null ? 0 : currentText.length());
        suppressCourseFilter = false;

        Platform.runLater(() -> {
            if (!courseIdCombo.isDisabled() && !courseIdCombo.isShowing() && courseIdCombo.isFocused()) {
                courseIdCombo.show();
            }
        });
    }

    private List<String> filterList(List<String> source, String text) {
        String key = text == null ? "" : text.trim().toLowerCase();

        if (key.isEmpty()) {
            return source.stream().sorted().collect(Collectors.toList());
        }

        return source.stream()
                .filter(item -> item.toLowerCase().contains(key))
                .sorted(Comparator
                        .comparing((String item) -> !item.toLowerCase().startsWith(key))
                        .thenComparing(String::compareToIgnoreCase))
                .collect(Collectors.toList());
    }

    @FXML
    private void submitMedical() {
        String regNo = studentRegCombo.getEditor().getText();
        String studentUserId = findUserIdByRegNo(regNo);

        String typedCourse = courseIdCombo.getEditor().getText();
        String courseId = resolveCourseId(typedCourse);
        String examType = examTypeCombo.getValue();
        String date = submittedDatePicker.getValue() == null
                ? null
                : submittedDatePicker.getValue().toString();
        String copy = medicalCopyField.getText();

        if (studentUserId == null || studentUserId.isBlank()) {
            showStatus("✖ Select valid student registration number.", StatusType.ERROR);
            return;
        }

        if (typedCourse == null || typedCourse.isBlank()) {
            showStatus("✖ Course ID is required.", StatusType.ERROR);
            return;
        }

        if (courseId == null || !validCourseIds.contains(courseId.trim())) {
            showStatus("✖ Select course from this student's course list.", StatusType.ERROR);
            return;
        }

        if (date == null || date.isBlank()) {
            showStatus("✖ Date is required.", StatusType.ERROR);
            return;
        }

        Medical added = medicalService.addMedical(
                studentUserId,
                courseId.trim(),
                examType,
                date,
                copy
        );

        if (added != null) {
            showStatus("✔ Medical record #" + added.getMedicalId() + " submitted — Pending.", StatusType.SUCCESS);
            clearFormFields();
        } else {
            showStatus("✖ " + medicalService.getLastMessage(), StatusType.ERROR);
        }
    }

    @FXML
    private void clearForm() {
        clearFormFields();
        showStatus("", StatusType.INFO);
    }

    private void clearFormFields() {
        suppressStudentFilter = true;
        studentRegCombo.getSelectionModel().clearSelection();
        studentRegCombo.getEditor().clear();
        studentRegCombo.getItems().setAll(allRegNos);
        suppressStudentFilter = false;

        suppressCourseFilter = true;
        courseIdCombo.getSelectionModel().clearSelection();
        courseIdCombo.getEditor().clear();
        courseIdCombo.getItems().clear();
        courseIdCombo.setDisable(true);
        suppressCourseFilter = false;

        allCourseDisplays.clear();
        validCourseIds.clear();
        courseDisplayToId.clear();

        medicalCopyField.clear();
        submittedDatePicker.setValue(null);
        examTypeCombo.getSelectionModel().select("Attendance");
    }

    @FXML
    private void backToDashboard() {
        loadView("/view/techofficer/techOfficerDashboard.fxml");
    }

    private void loadView(String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Cannot open view");
            alert.setContentText(path);
            alert.showAndWait();
        }
    }

    private enum StatusType {
        SUCCESS, ERROR, INFO
    }

    private void showStatus(String message, StatusType type) {
        statusLabel.setText(message == null ? "" : message);

        String color = switch (type) {
            case SUCCESS -> "#4cba52";
            case ERROR -> "#e85d5d";
            case INFO -> "#8fa3b8";
        };

        statusLabel.setStyle(
                "-fx-text-fill: " + color + ";" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-family: 'Segoe UI';" +
                        "-fx-font-weight: bold;"
        );
    }

    private String resolveCourseId(String typedCourse) {
        if (typedCourse == null) {
            return null;
        }
        String cleaned = typedCourse.trim();
        if (cleaned.isEmpty()) {
            return null;
        }
        if (validCourseIds.contains(cleaned)) {
            return cleaned;
        }
        String mapped = courseDisplayToId.get(cleaned);
        if (mapped != null && !mapped.isBlank()) {
            return mapped.trim();
        }
        int splitIndex = cleaned.indexOf(" - ");
        if (splitIndex > 0) {
            String candidate = cleaned.substring(0, splitIndex).trim();
            if (validCourseIds.contains(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private String findUserIdByRegNo(String regNo) {
        if (regNo == null) {
            return null;
        }
        String trimmed = regNo.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        String exact = regNoToUserId.get(trimmed);
        if (exact != null && !exact.isBlank()) {
            return exact;
        }
        for (Map.Entry<String, String> entry : regNoToUserId.entrySet()) {
            if (entry.getKey() != null && entry.getKey().trim().equalsIgnoreCase(trimmed)) {
                return entry.getValue();
            }
        }
        return null;
    }
}