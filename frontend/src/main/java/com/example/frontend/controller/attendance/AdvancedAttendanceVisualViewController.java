package com.example.frontend.controller.attendance;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.model.AttendanceViewDetailRow;
import com.example.frontend.model.AttendanceStudentOption;
import com.example.frontend.service.AttendanceService;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class AdvancedAttendanceVisualViewController {

    public static class BatchSummaryRow {
        private String studentId;
        private String regNo;
        private String studentName;
        private int totalSessions;
        private int presentCount;
        private int absentCount;
        private double attendancePercentage;
        private double totalHoursAttended;

        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }

        public String getRegNo() { return regNo; }
        public void setRegNo(String regNo) { this.regNo = regNo; }

        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }

        public int getTotalSessions() { return totalSessions; }
        public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }

        public int getPresentCount() { return presentCount; }
        public void setPresentCount(int presentCount) { this.presentCount = presentCount; }

        public int getAbsentCount() { return absentCount; }
        public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }

        public double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }

        public double getTotalHoursAttended() { return totalHoursAttended; }
        public void setTotalHoursAttended(double totalHoursAttended) { this.totalHoursAttended = totalHoursAttended; }
    }

    @FXML private ComboBox<String> modeComboBox;
    @FXML private ComboBox<String> viewTypeComboBox;
    @FXML private TextField targetField;
    @FXML private Label targetLabel;
    @FXML private Label statusLabel;

    @FXML private VBox individualSummaryPane;
    @FXML private Label overallStudentNameLabel;
    @FXML private Label overallRegNoLabel;
    @FXML private Label overallTotalSessionsLabel;
    @FXML private Label overallPresentCountLabel;
    @FXML private Label overallAbsentCountLabel;
    @FXML private Label overallAttendancePercentageLabel;
    @FXML private Label overallTotalHoursAttendedLabel;

    @FXML private VBox courseSummaryPane;
    @FXML private ComboBox<String> courseComboBox;
    @FXML private Label courseIdLabel;
    @FXML private Label courseTotalSessionsLabel;
    @FXML private Label coursePresentCountLabel;
    @FXML private Label courseAbsentCountLabel;
    @FXML private Label courseAttendancePercentageLabel;
    @FXML private Label courseTotalHoursAttendedLabel;

    @FXML private VBox batchSummaryPane;
    @FXML private TextArea batchSummaryTextArea;
    @FXML private TextArea detailsTextArea;

    private final AttendanceService attendanceService = new AttendanceService(LoginController.client);
    private List<AttendanceViewDetailRow> currentDetails = new ArrayList<>();
    private final Map<String, String> regNoToUserId = new HashMap<>();

    @FXML
    public void initialize() {
        modeComboBox.getItems().addAll("Individual", "Batch");
        modeComboBox.setValue("Individual");

        viewTypeComboBox.getItems().addAll("Theory", "Practical", "Combined");
        viewTypeComboBox.setValue("Combined");

        targetLabel.setText("Student Reg No");
        targetField.setPromptText("Enter student registration number");
        loadStudentRegNoMap();

        onModeChanged();
        modeComboBox.setOnAction(e -> onModeChanged());

        showStatus("Ready to load attendance data.", StatusType.INFO);
    }

    @FXML
    private void loadDetails() {
        String target = targetField.getText() == null ? "" : targetField.getText().trim();

        if (target.isEmpty()) {
            showStatus("Please enter target value.", StatusType.ERROR);
            return;
        }

        String viewType = viewTypeComboBox.getValue();

        if ("Individual".equals(modeComboBox.getValue())) {
            String studentUserId = resolveStudentUserId(target);
            if (studentUserId == null) {
                showStatus("Invalid student reg no. Use a valid registration number.", StatusType.ERROR);
                return;
            }
            loadIndividualDetails(studentUserId, viewType);
            showStatus("Individual details loaded.", StatusType.SUCCESS);
        } else {
            loadBatchDetails(target, viewType);
            showStatus("Batch details loaded.", StatusType.SUCCESS);
        }
    }

    @FXML
    private void loadSummary() {
        String target = targetField.getText() == null ? "" : targetField.getText().trim();

        if (target.isEmpty()) {
            showStatus("Please enter target value.", StatusType.ERROR);
            return;
        }

        String viewType = viewTypeComboBox.getValue();

        if ("Individual".equals(modeComboBox.getValue())) {
            String studentUserId = resolveStudentUserId(target);
            if (studentUserId == null) {
                showStatus("Invalid student reg no. Use a valid registration number.", StatusType.ERROR);
                return;
            }
            loadIndividualOverallSummary(studentUserId, viewType);
            showStatus("Overall summary loaded.", StatusType.SUCCESS);
        } else {
            loadBatchSummary(target, viewType);
            showStatus("Batch summary loaded.", StatusType.SUCCESS);
        }
    }

    @FXML
    private void backToDashboard() {
        loadView("/view/techofficer/techOfficerDashboard.fxml");
    }

    private void onModeChanged() {
        boolean individual = "Individual".equals(modeComboBox.getValue());

        targetLabel.setText(individual ? "Student Reg No" : "Batch");
        targetField.setPromptText(individual ? "Enter student registration number" : "Enter batch (e.g. 2023)");

        individualSummaryPane.setVisible(individual);
        individualSummaryPane.setManaged(individual);

        batchSummaryPane.setVisible(!individual);
        batchSummaryPane.setManaged(!individual);

        courseSummaryPane.setVisible(individual);
        courseSummaryPane.setManaged(individual);

        detailsTextArea.clear();
        if (batchSummaryTextArea != null) {
            batchSummaryTextArea.clear();
        }

        setOverallSummaryToDash();
        setCourseSummaryToDash();

        showStatus("Mode changed to " + modeComboBox.getValue() + ".", StatusType.INFO);
    }

    private void loadIndividualDetails(String studentId, String viewType) {
        JsonNode response = attendanceService.getStudentAttendance(studentId, viewType);
        JsonNode dataNode = response == null ? null : response.path("data");

        currentDetails = new ArrayList<>();

        if (dataNode != null && dataNode.isArray()) {
            for (JsonNode item : dataNode) {
                AttendanceViewDetailRow row = new AttendanceViewDetailRow();
                row.setStudentName(item.path("studentName").asText());
                row.setRegNo(item.path("regNo").asText());
                row.setCourseId(item.path("courseId").asText());
                row.setSessionDate(item.path("sessionDate").asText());
                row.setSessionType(item.path("sessionType").asText());
                row.setStatus(item.path("status").asText());
                row.setHoursAttended(item.path("hoursAttended").asDouble());
                currentDetails.add(row);
            }
        }

        detailsTextArea.setText(formatDetails(currentDetails));

        Set<String> courseIds = new HashSet<>();
        for (AttendanceViewDetailRow d : currentDetails) {
            courseIds.add(d.getCourseId());
        }

        List<String> sorted = new ArrayList<>(courseIds);
        sorted.sort(String::compareToIgnoreCase);

        if (sorted.isEmpty()) {
            courseComboBox.getItems().clear();
            courseComboBox.setValue(null);
            setCourseSummaryToDash();
            return;
        }

        courseComboBox.getItems().setAll(sorted);
        courseComboBox.setValue(sorted.get(0));
        updateCourseSummary(sorted.get(0));
        courseComboBox.setOnAction(e -> updateCourseSummary(courseComboBox.getValue()));
    }

    private void loadBatchDetails(String batch, String viewType) {
        JsonNode response = attendanceService.getBatchAttendance(batch, viewType);
        JsonNode dataNode = response == null ? null : response.path("data");

        currentDetails = new ArrayList<>();

        if (dataNode != null && dataNode.isArray()) {
            for (JsonNode item : dataNode) {
                AttendanceViewDetailRow row = new AttendanceViewDetailRow();
                row.setStudentName(item.path("studentName").asText());
                row.setRegNo(item.path("regNo").asText());
                row.setCourseId(item.path("courseId").asText());
                row.setSessionDate(item.path("sessionDate").asText());
                row.setSessionType(item.path("sessionType").asText());
                row.setStatus(item.path("status").asText());
                row.setHoursAttended(item.path("hoursAttended").asDouble());
                currentDetails.add(row);
            }
        }

        detailsTextArea.setText(formatDetails(currentDetails));
        setCourseSummaryToDash();
    }

    private void loadIndividualOverallSummary(String studentId, String viewType) {
        JsonNode eligibilityResponse = attendanceService.checkAttendanceEligibility(studentId, viewType);
        JsonNode dataNode = eligibilityResponse == null ? null : eligibilityResponse.path("data");

        if (dataNode == null || dataNode.isMissingNode() || dataNode.isNull()
                || (eligibilityResponse != null && !eligibilityResponse.path("success").asBoolean(false))) {
            setOverallSummaryToDash();
            showStatus("No summary found.", StatusType.ERROR);
            return;
        }

        overallStudentNameLabel.setText(dataNode.path("studentName").asText("-"));
        overallRegNoLabel.setText(dataNode.path("regNo").asText("-"));
        overallTotalSessionsLabel.setText(String.valueOf(dataNode.path("totalSessions").asInt(0)));
        overallPresentCountLabel.setText(String.valueOf(dataNode.path("presentCount").asInt(0)));
        overallAbsentCountLabel.setText(String.valueOf(dataNode.path("absentCount").asInt(0)));
        overallAttendancePercentageLabel.setText(dataNode.path("attendancePercentage").asDouble(0) + "%");
        overallTotalHoursAttendedLabel.setText(
                String.valueOf(Math.round(dataNode.path("totalHoursAttended").asDouble(0) * 100.0) / 100.0)
        );

        boolean eligible = dataNode.path("eligible").asBoolean(false);
        String eligibilityStatus = dataNode.path("eligibilityStatus").asText(eligible ? "Eligible" : "Not Eligible");
        String ruleNote = dataNode.path("ruleNote").asText("");
        showStatus("Student eligibility: " + eligibilityStatus + (ruleNote.isBlank() ? "" : " | " + ruleNote),
                eligible ? StatusType.SUCCESS : StatusType.ERROR);
    }

    private void loadBatchSummary(String batch, String viewType) {
        JsonNode response = attendanceService.getBatchAttendanceSummary(batch, viewType);
        JsonNode dataNode = response == null ? null : response.path("data");

        List<BatchSummaryRow> rows = new ArrayList<>();

        if (dataNode != null && dataNode.isArray()) {
            for (JsonNode item : dataNode) {
                BatchSummaryRow row = new BatchSummaryRow();
                row.setStudentId(item.path("studentId").asText());
                row.setRegNo(item.path("regNo").asText());
                row.setStudentName(item.path("studentName").asText());
                row.setTotalSessions(item.path("totalSessions").asInt(0));
                row.setPresentCount(item.path("presentCount").asInt(0));
                row.setAbsentCount(item.path("absentCount").asInt(0));
                row.setAttendancePercentage(item.path("attendancePercentage").asDouble(0));
                row.setTotalHoursAttended(item.path("totalHoursAttended").asDouble(0));
                rows.add(row);
            }
        }

        batchSummaryTextArea.setText(formatBatchSummary(rows));
    }

    private void updateCourseSummary(String courseId) {
        if (courseId == null) {
            setCourseSummaryToDash();
            return;
        }

        long total = currentDetails.stream()
                .filter(d -> courseId.equalsIgnoreCase(d.getCourseId()))
                .count();

        long present = currentDetails.stream()
                .filter(d -> courseId.equalsIgnoreCase(d.getCourseId()))
                .filter(d -> "Present".equalsIgnoreCase(d.getStatus()))
                .count();

        long absent = currentDetails.stream()
                .filter(d -> courseId.equalsIgnoreCase(d.getCourseId()))
                .filter(d -> "Absent".equalsIgnoreCase(d.getStatus()))
                .count();

        double totalHours = currentDetails.stream()
                .filter(d -> courseId.equalsIgnoreCase(d.getCourseId()))
                .mapToDouble(d -> d.getHoursAttended() == null ? 0.0 : d.getHoursAttended())
                .sum();

        double percentage = total == 0 ? 0.0 : (present * 100.0) / total;

        courseIdLabel.setText(courseId);
        courseTotalSessionsLabel.setText(String.valueOf(total));
        coursePresentCountLabel.setText(String.valueOf(present));
        courseAbsentCountLabel.setText(String.valueOf(absent));
        courseAttendancePercentageLabel.setText(Math.round(percentage * 100.0) / 100.0 + "%");
        courseTotalHoursAttendedLabel.setText(String.valueOf(Math.round(totalHours * 100.0) / 100.0));
    }

    private void setCourseSummaryToDash() {
        courseIdLabel.setText("-");
        courseTotalSessionsLabel.setText("-");
        coursePresentCountLabel.setText("-");
        courseAbsentCountLabel.setText("-");
        courseAttendancePercentageLabel.setText("-");
        courseTotalHoursAttendedLabel.setText("-");
    }

    private void setOverallSummaryToDash() {
        overallStudentNameLabel.setText("-");
        overallRegNoLabel.setText("-");
        overallTotalSessionsLabel.setText("-");
        overallPresentCountLabel.setText("-");
        overallAbsentCountLabel.setText("-");
        overallAttendancePercentageLabel.setText("-");
        overallTotalHoursAttendedLabel.setText("-");
    }

    private void loadView(String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot open view");
            alert.setContentText(path);
            alert.showAndWait();
        }
    }

    private String formatDetails(List<AttendanceViewDetailRow> rows) {
        if (rows == null || rows.isEmpty()) {
            return "No attendance records found for selected filters.";
        }

        StringBuilder sb = new StringBuilder();
        for (AttendanceViewDetailRow r : rows) {
            sb.append("Student: ").append(nullSafe(r.getStudentName()))
                    .append(" (").append(nullSafe(r.getRegNo())).append(")\n")
                    .append("Course: ").append(nullSafe(r.getCourseId()))
                    .append(" | Date: ").append(nullSafe(r.getSessionDate()))
                    .append(" | Type: ").append(nullSafe(r.getSessionType()))
                    .append(" | Status: ").append(nullSafe(r.getStatus()))
                    .append(" | Hours: ").append(r.getHoursAttended() == null ? 0.0 : r.getHoursAttended())
                    .append("\n--------------------------------------------------\n");
        }
        return sb.toString();
    }

    private String formatBatchSummary(List<BatchSummaryRow> rows) {
        if (rows == null || rows.isEmpty()) {
            return "No batch summary found for selected filters.";
        }

        StringBuilder sb = new StringBuilder();
        for (BatchSummaryRow r : rows) {
            sb.append(r.getRegNo()).append(" - ").append(r.getStudentName())
                    .append(" | Sessions: ").append(r.getTotalSessions())
                    .append(" | Present: ").append(r.getPresentCount())
                    .append(" | Absent: ").append(r.getAbsentCount())
                    .append(" | %: ").append(r.getAttendancePercentage())
                    .append(" | Hours: ").append(r.getTotalHoursAttended())
                    .append("\n");
        }
        return sb.toString();
    }

    private String nullSafe(String value) {
        return value == null ? "-" : value;
    }

    private enum StatusType {
        SUCCESS, ERROR, INFO
    }

    private void showStatus(String message, StatusType type) {
        statusLabel.setText(message);

        String color = switch (type) {
            case SUCCESS -> "#4cba52";
            case ERROR -> "#e85d5d";
            case INFO -> "#8fa3b8";
        };

        statusLabel.setStyle(
                "-fx-text-fill: " + color + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;"
        );
    }

    private void loadStudentRegNoMap() {
        regNoToUserId.clear();
        List<AttendanceStudentOption> options = attendanceService.getStudentOptions();
        for (AttendanceStudentOption option : options) {
            if (option.getRegNo() != null && option.getUserId() != null) {
                regNoToUserId.put(option.getRegNo().trim().toLowerCase(), option.getUserId().trim());
            }
        }
    }

    private String resolveStudentUserId(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        String trimmed = input.trim();
        if (trimmed.toUpperCase().startsWith("U")) {
            return trimmed;
        }
        return regNoToUserId.get(trimmed.toLowerCase());
    }
}