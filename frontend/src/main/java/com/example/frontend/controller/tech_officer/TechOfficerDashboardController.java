package com.example.frontend.controller.tech_officer;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.service.AuthService;
import com.example.frontend.session.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
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

    private String techName = LoginController.username;
    private int techId = -1;

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
        loadAttendanceSummary();
    }

    public void setTechInfo(String name, int id) {
        this.techName = name;
        this.techId = id;
    }

    private void loadStats() {
        // TODO: replace with DB queries
        totalStudentsLabel.setText("20");
        attendanceSessionsLabel.setText("15");
        medicalRecordsLabel.setText("8");
        pendingApprovalsLabel.setText("3");
    }

    private void loadAttendanceSummary() {
        // TODO: replace with DB query — aggregate attendance per student
        // Simulate the 5 scenarios required by the spec
        String[][] rows = {
                {"ICT/2023/001", "Above 80%",     "●", "#28a745", "92%",  "No Medical"},
                {"ICT/2023/002", "Exactly 80%",   "●", "#ffc107", "80%",  "No Medical"},
                {"ICT/2023/003", "Below 80%",     "●", "#c0392b", "65%",  "No Medical"},
                {"ICT/2023/004", "Above 80% + 🏥","●", "#28a745", "85%",  "With Medical"},
                {"ICT/2023/005", "Below 80% + 🏥","●", "#e67e22", "72%",  "With Medical"},
        };

        for (String[] r : rows) {
            attendanceSummaryContainer.getChildren().add(
                    buildAttendanceRow(r[0], r[1], r[2], r[3], r[4], r[5]));
        }
    }

    private HBox buildAttendanceRow(String regNo, String scenario,
                                    String dot, String dotColor,
                                    String pct, String note) {
        HBox row = new HBox(16);
        row.setStyle("-fx-background-color: #1a2d50; -fx-background-radius: 8;");
        row.setPadding(new Insets(10, 16, 10, 16));

        Label dotLbl = new Label(dot);
        dotLbl.setStyle("-fx-text-fill: " + dotColor + "; -fx-font-size: 16px;");

        Label regLbl = new Label(regNo); regLbl.setPrefWidth(140);
        regLbl.setStyle("-fx-text-fill: #d0e4ff; -fx-font-size: 12px;");

        Label scenLbl = new Label(scenario); scenLbl.setPrefWidth(220);
        scenLbl.setStyle("-fx-text-fill: #a0b8e0; -fx-font-size: 12px;");

        Label pctLbl = new Label(pct); pctLbl.setPrefWidth(70);
        pctLbl.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");

        Label noteLbl = new Label(note);
        noteLbl.setStyle("-fx-text-fill: #6a90c8; -fx-font-size: 11px;");

        row.getChildren().addAll(dotLbl, regLbl, scenLbl, pctLbl, noteLbl);
        return row;
    }

    // ─── Navigation ──────────────────────────────────────────────────────────

    @FXML private void openAttendance()    { loadView("ViewAttendance.fxml"); }
    @FXML private void openAdvancedAttendanceView() { loadView("AdvancedAttendanceVisualView.fxml"); }
    @FXML private void openMarkAttendance(){ loadView("MarkAttendance.fxml"); }
    @FXML private void openMedical()       { loadView("MedicalManagement.fxml"); }
    @FXML private void openAddMedical()    { loadView("AddMedical.fxml"); }
    @FXML private void openTimetables()    { loadView("TimetableView.fxml"); }
    @FXML private void openNotices()       { loadView("NoticesView.fxml"); }
    @FXML private void openProfile()       { loadView("TechOfficerProfile.fxml"); }
    @FXML
    void logout(ActionEvent event) {
        try {
            // Use the same client instance from LoginController
            AuthService authService = new AuthService(LoginController.client); // make client static in LoginController

            boolean success = authService.logout(SessionManager.getToken());

            if (success) {
                System.out.println("Logout successful!");

                // Clear session locally
                SessionManager.clear();

                // Close dashboard window
                Stage dashboardStage = (Stage) techNameLabel.getScene().getWindow();
                dashboardStage.close();

                // Re-open login window
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

    private void loadView(String fxmlFile) {
        try {
            URL resource = getClass().getResource("/view/" + fxmlFile);
            if (resource == null) {
                showNotImplementedAlert(fxmlFile);
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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