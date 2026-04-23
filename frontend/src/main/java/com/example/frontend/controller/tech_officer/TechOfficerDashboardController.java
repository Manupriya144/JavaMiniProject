package com.example.frontend.controller.tech_officer;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.model.TechOfficerDashboardStats;
import com.example.frontend.service.AttendanceService;
import com.example.frontend.service.AuthService;
import com.example.frontend.service.TechOfficerDashboardService;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TechOfficerDashboardController implements Initializable {

    @FXML private Label welcomeLabel;
    @FXML private Label dateLabel;
    @FXML private Label techNameLabel;
    @FXML private Label totalStudentsLabel;
    @FXML private Label attendanceSessionsLabel;
    @FXML private Label medicalRecordsLabel;
    @FXML private Label pendingApprovalsLabel;
    @FXML private Label statusBarTime;
    @FXML private VBox attendanceSummaryContainer;
    @FXML private TextField batchEligibilityField;
    @FXML private ComboBox<String> viewTypeEligibilityCombo;

    private String techName = LoginController.username;
    private final AttendanceService attendanceService = new AttendanceService(LoginController.client);
    private final TechOfficerDashboardService dashboardService = new TechOfficerDashboardService(LoginController.client);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String today = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        dateLabel.setText(today + "  •  Technical Officer Panel");
        statusBarTime.setText(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("d MMMM yyyy")));

        welcomeLabel.setText("Welcome, " + techName + " 👋");
        techNameLabel.setText(techName);

        loadStats();

        if (viewTypeEligibilityCombo != null) {
            viewTypeEligibilityCombo.getItems().addAll("Combined", "Theory", "Practical");
            viewTypeEligibilityCombo.getSelectionModel().selectFirst();
        }

        refreshAttendanceEligibilityReport();
    }

    public void setTechInfo(String name, int id) {
        this.techName = name;
    }

    private void loadStats() {
        TechOfficerDashboardStats stats = dashboardService.getDashboardStats();

        if (stats == null) {
            totalStudentsLabel.setText("0");
            attendanceSessionsLabel.setText("0");
            medicalRecordsLabel.setText("0");
            pendingApprovalsLabel.setText("0");
            return;
        }

        totalStudentsLabel.setText(String.valueOf(stats.getTotalStudents()));
        attendanceSessionsLabel.setText(String.valueOf(stats.getAttendanceSessions()));
        medicalRecordsLabel.setText(String.valueOf(stats.getMedicalRecords()));
        pendingApprovalsLabel.setText(String.valueOf(stats.getPendingApprovals()));
    }

    @FXML
    private void refreshAttendanceEligibilityReport() {
        if (attendanceSummaryContainer == null) {
            return;
        }

        attendanceSummaryContainer.getChildren().clear();

        String batch = batchEligibilityField != null ? batchEligibilityField.getText() : "";

        if (batch == null || batch.isBlank()) {
            Label hint = new Label("Enter batch and refresh. Eligibility uses 80% threshold with +20% medical bonus.");
            hint.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 12px;");
            attendanceSummaryContainer.getChildren().add(hint);
            return;
        }

        String viewType = viewTypeEligibilityCombo != null && viewTypeEligibilityCombo.getValue() != null
                ? viewTypeEligibilityCombo.getValue()
                : "Combined";

        JsonNode root = attendanceService.getBatchAttendanceEligibilityReport(batch.trim(), viewType);

        if (root == null || !root.path("success").asBoolean(false)) {
            String msg = root != null && root.hasNonNull("message")
                    ? root.path("message").asText()
                    : attendanceService.getLastMessage();

            Label err = new Label(msg);
            err.setStyle("-fx-text-fill: #e85d5d; -fx-font-size: 12px;");
            attendanceSummaryContainer.getChildren().add(err);
            return;
        }

        JsonNode data = root.get("data");

        if (data == null || !data.isArray() || data.isEmpty()) {
            Label empty = new Label("No attendance records for this batch and view.");
            empty.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 12px;");
            attendanceSummaryContainer.getChildren().add(empty);
            return;
        }

        for (JsonNode row : data) {
            String category = row.path("eligibilityCategory").asText("Below80");

            String dotColor = switch (category) {
                case "Above80" -> "#4cba52";
                case "Exactly80" -> "#f2c94c";
                case "NoData" -> "#95a5a6";
                default -> "#e85d5d";
            };

            String regNo = row.path("regNo").asText("—");
            String scenario = row.path("scenarioLabel").asText("");
            double effectiveAp = row.path("effectiveAttendancePercentage").asDouble(row.path("attendancePercentage").asDouble());
            double rawAp = row.path("rawAttendancePercentage").asDouble(effectiveAp);
            String pct = String.format("%.1f%%", effectiveAp);

            double medicalBonus = row.path("medicalBonusPercent").asDouble(0.0);
            String note = medicalBonus > 0
                    ? String.format("Raw %.1f%% + Medical %.0f%%", rawAp, medicalBonus)
                    : String.format("Raw %.1f%%", rawAp);

            attendanceSummaryContainer.getChildren().add(
                    buildAttendanceRow(regNo, scenario, "●", dotColor, pct, note)
            );
        }
    }

    private HBox buildAttendanceRow(String regNo, String scenario,
                                    String dot, String dotColor,
                                    String pct, String note) {
        HBox row = new HBox(16);
        row.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: #d4e4f7; " +
                        "-fx-border-radius: 8; " +
                        "-fx-border-width: 1;"
        );
        row.setPadding(new Insets(10, 16, 10, 16));

        Label dotLbl = new Label(dot);
        dotLbl.setStyle("-fx-text-fill: " + dotColor + "; -fx-font-size: 16px;");

        Label regLbl = new Label(regNo);
        regLbl.setPrefWidth(140);
        regLbl.setStyle("-fx-text-fill: #1a3a52; -fx-font-size: 12px;");

        Label scenLbl = new Label(scenario);
        scenLbl.setPrefWidth(260);
        scenLbl.setStyle("-fx-text-fill: #7f93a8; -fx-font-size: 12px;");

        Label pctLbl = new Label(pct);
        pctLbl.setPrefWidth(70);
        pctLbl.setStyle("-fx-text-fill: #1a3a52; -fx-font-size: 13px; -fx-font-weight: bold;");

        Label noteLbl = new Label(note);
        noteLbl.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 11px;");

        row.getChildren().addAll(dotLbl, regLbl, scenLbl, pctLbl, noteLbl);
        return row;
    }

    @FXML private void openAttendance() { loadView("techofficer/ViewAttendance.fxml"); }

    @FXML
    private void openAdvancedAttendanceView() {
        if (!loadView("techofficer/AdvancedAttendanceVisualView.fxml")) {
            loadView("techofficer/AdvancedAttendanceView.fxml");
        }
    }

    @FXML private void openMarkAttendance() { loadView("techofficer/MarkAttendance.fxml"); }
    @FXML private void openMedical() { loadView("techofficer/MedicalManagement.fxml"); }
    @FXML private void openAddMedical() { loadView("techofficer/AddMedical.fxml"); }
    @FXML private void openTimetables() { loadView("admin/DisplayTimeTable.fxml"); }
    @FXML private void openNotices() { loadView("admin/NoticeDisplay.fxml"); }
    @FXML private void openProfile() { loadView("techofficer/TechOfficerProfile.fxml"); }

    @FXML
    void logout(ActionEvent event) {
        try {
            AuthService authService = new AuthService(LoginController.client);
            boolean success = authService.logout(SessionManager.getToken());

            if (success) {
                System.out.println("Logout successful!");
                SessionManager.clear();

                Stage dashboardStage = (Stage) techNameLabel.getScene().getWindow();
                dashboardStage.close();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
                Parent root = loader.load();

                Stage loginStage = new Stage();
                loginStage.initStyle(StageStyle.UNDECORATED);
                loginStage.setScene(new Scene(root));
                loginStage.show();
            } else {
                System.out.println("Logout failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean loadView(String fxmlFile) {
        try {
            URL resource = getClass().getResource("/view/" + fxmlFile);
            if (resource == null) {
                showNotImplementedAlert(fxmlFile);
                return false;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            return true;

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot Open View");
            alert.setHeaderText("Failed to open: " + fxmlFile);
            alert.setContentText(e.getMessage() == null ? "Unknown error while loading view." : e.getMessage());
            alert.showAndWait();
            return false;
        }
    }

    private void showNotImplementedAlert(String fxmlFile) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("View Not Available");
        alert.setHeaderText("This module is not created yet");
        alert.setContentText("Missing file: /view/" + fxmlFile);
        alert.showAndWait();
    }
}