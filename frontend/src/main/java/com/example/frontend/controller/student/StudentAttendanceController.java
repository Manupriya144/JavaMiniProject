package com.example.frontend.controller.student;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.AttendanceService;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class StudentAttendanceController implements Initializable {

    @FXML private Label subtitleLabel;
    @FXML private Label studentNameLabel;
    @FXML private Label studentIdLabel;
    @FXML private Label profileInitial;

    @FXML private Label attendancePctLabel;
    @FXML private Label presentCountLabel;
    @FXML private Label absentCountLabel;
    @FXML private Label scenarioLabel;
    @FXML private Label statusLabel;

    @FXML private VBox recordsContainer;

    private final AttendanceService attendanceService = new AttendanceService(ServerClient.getInstance());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String name = LoginController.username;
        String regNo = LoginController.reNo;

        studentNameLabel.setText(name == null ? "Student" : name);
        studentIdLabel.setText("Reg No: " + (regNo == null ? "—" : regNo));

        if (name != null && !name.isBlank()) {
            profileInitial.setText(name.substring(0, 1).toUpperCase(Locale.ROOT));
        } else {
            profileInitial.setText("S");
        }

        loadSummaryAndRecords();
    }

    private void loadSummaryAndRecords() {
        String studentId = LoginController.userId;
        String viewType = "Combined";

        statusLabel.setText("Loading…");
        recordsContainer.getChildren().clear();

        // Summary
        JsonNode summaryRes = attendanceService.getStudentAttendanceSummary(studentId, viewType);
        if (summaryRes != null && summaryRes.path("success").asBoolean(false)) {
            JsonNode data = summaryRes.get("data");
            if (data != null && data.isObject()) {
                double pct = data.path("attendancePercentage").asDouble(0.0);
                int present = data.path("presentCount").asInt(0);
                int absent = data.path("absentCount").asInt(0);

                attendancePctLabel.setText(formatPct(pct));
                presentCountLabel.setText(String.valueOf(present));
                absentCountLabel.setText(String.valueOf(absent));
                scenarioLabel.setText(data.path("scenarioLabel").asText(""));

                String eligibility = data.path("eligibilityStatus").asText("");
                String ruleNote = data.path("ruleNote").asText("");
                statusLabel.setText((eligibility.isBlank() ? "" : eligibility + " • ") + ruleNote);
            } else {
                statusLabel.setText("No summary available.");
            }
        } else {
            statusLabel.setText("Failed to load summary: " + attendanceService.getLastMessage());
        }

        // Recent records (limit to 12)
        JsonNode recordsRes = attendanceService.getStudentAttendance(studentId, viewType);
        JsonNode records = recordsRes == null ? null : recordsRes.get("data");

        if (recordsRes == null || !recordsRes.path("success").asBoolean(false)) {
            if (recordsContainer.getChildren().isEmpty()) {
                recordsContainer.getChildren().add(buildEmptyRow("Failed to load attendance records."));
            }
            return;
        }

        if (records == null || !records.isArray() || records.size() == 0) {
            recordsContainer.getChildren().add(buildEmptyRow("No attendance records found."));
            return;
        }

        int limit = Math.min(records.size(), 12);
        for (int i = 0; i < limit; i++) {
            JsonNode r = records.get(i);
            recordsContainer.getChildren().add(
                    buildRow(
                            r.path("courseId").asText(""),
                            r.path("sessionDate").asText(""),
                            r.path("sessionType").asText(""),
                            r.path("status").asText(""),
                            r.path("hoursAttended").asText("")
                    )
            );
        }
    }

    private HBox buildRow(String courseId, String date, String type, String status, String hours) {
        HBox row = new HBox(0);
        row.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #e8eef5;" +
                        "-fx-border-radius: 6;" +
                        "-fx-border-width: 1;"
        );
        row.setPadding(new Insets(10, 12, 10, 12));

        Label c = cell(courseId, 160, "-fx-text-fill: #1a3a52; -fx-font-size: 12px;");
        Label d = cell(date, 140, "-fx-text-fill: #1a3a52; -fx-font-size: 12px;");
        Label t = cell(type, 110, "-fx-text-fill: #1a3a52; -fx-font-size: 12px;");

        String statusStyle = "Present".equalsIgnoreCase(status)
                ? "-fx-text-fill: #4cba52; -fx-font-size: 12px; -fx-font-weight: bold;"
                : "-fx-text-fill: #e85d5d; -fx-font-size: 12px; -fx-font-weight: bold;";
        Label s = cell(status, 110, statusStyle);

        Label h = cell(hours, 90, "-fx-text-fill: #1a3a52; -fx-font-size: 12px;");

        row.getChildren().addAll(c, d, t, s, h);
        return row;
    }

    private VBox buildEmptyRow(String message) {
        VBox box = new VBox(0);
        box.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #e8eef5;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-width: 1;"
        );
        box.setPadding(new Insets(12, 14, 12, 14));
        Label lbl = new Label(message);
        lbl.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 12px;");
        box.getChildren().add(lbl);
        return box;
    }

    private Label cell(String text, double width, String style) {
        Label l = new Label(text == null ? "" : text);
        l.setPrefWidth(width);
        l.setStyle(style);
        return l;
    }

    private String formatPct(double pct) {
        return String.format(Locale.ROOT, "%.2f%%", pct);
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/student/studentDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) recordsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

