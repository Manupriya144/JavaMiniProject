package com.example.frontend.controller.student;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.model.StudentAttendanceSummary;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.AttendanceService;
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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StudentAttendanceController implements Initializable {

    @FXML private Label overallPercentageLabel;
    @FXML private Label totalSessionsLabel;
    @FXML private Label attendedHoursLabel;
    @FXML private VBox attendanceContainer;

    private final AttendanceService attendanceService =
            new AttendanceService(ServerClient.getInstance());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAttendance();
    }

    private void loadAttendance() {
        attendanceContainer.getChildren().clear();

        List<StudentAttendanceSummary> list =
                attendanceService.getStudentAttendanceSummary();

        if (list == null || list.isEmpty()) {
            Label empty = new Label("No attendance records found.");
            empty.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 13px;");
            attendanceContainer.getChildren().add(empty);

            overallPercentageLabel.setText("0%");
            totalSessionsLabel.setText("0");
            attendedHoursLabel.setText("0");
            return;
        }

        int totalSessions = 0;
        double totalHours = 0;
        double attendedHours = 0;

        for (StudentAttendanceSummary item : list) {
            totalSessions += item.getTotalSessions();
            totalHours += item.getTotalHours();
            attendedHours += item.getAttendedHours();

            attendanceContainer.getChildren().add(buildRow(item));
        }

        double overall = totalHours == 0 ? 0 : (attendedHours / totalHours) * 100.0;

        overallPercentageLabel.setText(String.format("%.1f%%", overall));
        totalSessionsLabel.setText(String.valueOf(totalSessions));
        attendedHoursLabel.setText(String.format("%.1f", attendedHours));
    }

    private HBox buildRow(StudentAttendanceSummary item) {
        HBox row = new HBox(0);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #e8eef5;" +
                "-fx-border-radius: 10;" +
                "-fx-border-width: 1;"
        );

        Label codeLbl = createLabel(item.getCourseCode(), 120);
        Label nameLbl = createLabel(item.getCourseName(), 300);
        Label sessionsLbl = createLabel(String.valueOf(item.getTotalSessions()), 100);
        Label totalHoursLbl = createLabel(String.format("%.1f", item.getTotalHours()), 120);
        Label attendedLbl = createLabel(String.format("%.1f", item.getAttendedHours()), 120);
        Label percentageLbl = createLabel(String.format("%.1f%%", item.getAttendancePercentage()), 120);

        String status = item.getAttendancePercentage() >= 80 ? "Eligible" : "Low";
        Label statusLbl = createLabel(status, 120);

        if (item.getAttendancePercentage() >= 80) {
            statusLbl.setStyle("-fx-text-fill: #28a745; -fx-font-size: 12px; -fx-font-weight: bold;");
        } else {
            statusLbl.setStyle("-fx-text-fill: #e85d5d; -fx-font-size: 12px; -fx-font-weight: bold;");
        }

        row.getChildren().addAll(
                codeLbl,
                nameLbl,
                sessionsLbl,
                totalHoursLbl,
                attendedLbl,
                percentageLbl,
                statusLbl
        );

        return row;
    }

    private Label createLabel(String text, double width) {
        Label label = new Label(text == null ? "" : text);
        label.setPrefWidth(width);
        label.setStyle("-fx-text-fill: #1a3a52; -fx-font-size: 12px;");
        return label;
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/student/studentDashboard.fxml")
            );

            Parent root = loader.load();
            Stage stage = (Stage) attendanceContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}