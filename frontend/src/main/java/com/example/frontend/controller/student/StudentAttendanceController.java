package com.example.frontend.controller.student;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.model.AttendanceSummaryRow;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.AcademicEndpointService;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StudentAttendanceController implements Initializable {

    @FXML private Label overallPctLabel;
    @FXML private Label courseCountLabel;
    @FXML private Label totalPresentLabel;
    @FXML private Label totalAbsentLabel;
    @FXML private Label eligibilityLabel;

    @FXML private TableView<AttendanceSummaryRow> attendanceTable;
    @FXML private TableColumn<AttendanceSummaryRow, String>  colCourseId;
    @FXML private TableColumn<AttendanceSummaryRow, String>  colCourseName;
    @FXML private TableColumn<AttendanceSummaryRow, Integer> colTotal;
    @FXML private TableColumn<AttendanceSummaryRow, Integer> colPresent;
    @FXML private TableColumn<AttendanceSummaryRow, Integer> colAbsent;
    @FXML private TableColumn<AttendanceSummaryRow, Double>  colHours;
    @FXML private TableColumn<AttendanceSummaryRow, Double>  colPct;

    private final AcademicEndpointService academicService =
            new AcademicEndpointService(ServerClient.getInstance());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupColumns();
        loadAttendance();
    }

    private void setupColumns() {
        colCourseId.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourseId()));
        colCourseName.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourseName()));
        colTotal.setCellValueFactory(
                data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getTotalSessions()));
        colPresent.setCellValueFactory(
                data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPresentCount()));
        colAbsent.setCellValueFactory(
                data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getAbsentCount()));
        colHours.setCellValueFactory(
                data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getTotalHoursAttended()));
        colPct.setCellValueFactory(
                data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getAttendancePercentage()));

        // Colour-code the percentage column
        colPct.setCellFactory(col -> new javafx.scene.control.TableCell<AttendanceSummaryRow, Double>() {
            @Override
            protected void updateItem(Double pct, boolean empty) {
                super.updateItem(pct, empty);
                if (empty || pct == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.1f%%", pct));
                    if (pct >= 80) {
                        setStyle("-fx-text-fill: #4cba52; -fx-font-weight: bold;");
                    } else if (pct >= 60) {
                        setStyle("-fx-text-fill: #e8a835; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #e85d5d; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void loadAttendance() {
        JsonNode response = academicService.getMyAttendanceSummaryByCourse();
        System.out.println("[ATTENDANCE-DEBUG] raw response: " + response);

        if (response == null || !response.path("success").asBoolean(false)) {
            System.out.println("[ATTENDANCE-DEBUG] response null or success=false");
            overallPctLabel.setText("N/A");
            return;
        }

        JsonNode data = response.get("data");
        System.out.println("[ATTENDANCE-DEBUG] data node: " + data);
        System.out.println("[ATTENDANCE-DEBUG] isArray=" + (data != null && data.isArray()) + " size=" + (data != null ? data.size() : 0));

        if (data == null || !data.isArray() || data.size() == 0) {
            overallPctLabel.setText("N/A");
            courseCountLabel.setText("0");
            return;
        }

        ObservableList<AttendanceSummaryRow> rows = FXCollections.observableArrayList();
        int grandTotal   = 0;
        int grandPresent = 0;
        int grandAbsent  = 0;

        for (JsonNode node : data) {
            String courseId   = node.path("courseId").asText();
            String courseName = node.path("courseName").asText();
            int    total      = node.path("totalSessions").asInt();
            int    present    = node.path("presentCount").asInt();
            int    absent     = node.path("absentCount").asInt();
            double hours      = node.path("totalHoursAttended").asDouble();
            double pct        = node.path("attendancePercentage").asDouble();

            rows.add(new AttendanceSummaryRow(courseId, courseName, total, present, absent, hours, pct));

            grandTotal   += total;
            grandPresent += present;
            grandAbsent  += absent;
        }

        attendanceTable.setItems(rows);

        // Update summary bar
        courseCountLabel.setText(String.valueOf(rows.size()));
        totalPresentLabel.setText(String.valueOf(grandPresent));
        totalAbsentLabel.setText(String.valueOf(grandAbsent));

        double overallPct = grandTotal == 0 ? 0.0 : (grandPresent * 100.0) / grandTotal;
        overallPctLabel.setText(String.format("%.1f%%", overallPct));

        // Eligibility badge (≥80% = eligible)
        if (overallPct >= 80.0) {
            eligibilityLabel.setText("✅ Eligible");
            eligibilityLabel.setStyle(
                    "-fx-text-fill: #4cba52; -fx-background-color: #f0fff2; " +
                    "-fx-border-color: #4cba52; -fx-border-radius: 8; " +
                    "-fx-background-radius: 8; -fx-border-width: 1; " +
                    "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 6 14 6 14;");
        } else {
            eligibilityLabel.setText("❌ Not Eligible");
            eligibilityLabel.setStyle(
                    "-fx-text-fill: #e85d5d; -fx-background-color: #fff5f5; " +
                    "-fx-border-color: #e85d5d; -fx-border-radius: 8; " +
                    "-fx-background-radius: 8; -fx-border-width: 1; " +
                    "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 6 14 6 14;");
        }
    }

    @FXML
    private void backToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/student/studentDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) attendanceTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
