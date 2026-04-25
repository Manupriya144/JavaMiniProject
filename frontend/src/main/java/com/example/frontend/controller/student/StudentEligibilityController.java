package com.example.frontend.controller.student;

import com.example.frontend.model.StudentEligibilityRes;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.StudentEligibilityService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class StudentEligibilityController {

    @FXML private Label eligibleCountLabel;
    @FXML private Label notEligibleCountLabel;
    @FXML private Label totalSubjectsLabel;
    @FXML private VBox eligibilityContainer;

    private final StudentEligibilityService eligibilityService =
            new StudentEligibilityService(ServerClient.getInstance());

    @FXML
    public void initialize() {
        loadEligibility();
    }

    private void loadEligibility() {
        eligibilityContainer.getChildren().clear();

        List<StudentEligibilityRes> list =
                eligibilityService.getStudentOwnEligibility();

        if (list == null || list.isEmpty()) {
            eligibleCountLabel.setText("0");
            notEligibleCountLabel.setText("0");
            totalSubjectsLabel.setText("0");

            Label empty = new Label("No eligibility data found.");
            empty.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 13px;");
            eligibilityContainer.getChildren().add(empty);
            return;
        }

        int eligibleCount = 0;
        int notEligibleCount = 0;

        for (StudentEligibilityRes item : list) {
            if ("Eligible".equalsIgnoreCase(item.getFinalEligibilityStatus())) {
                eligibleCount++;
            } else {
                notEligibleCount++;
            }

            eligibilityContainer.getChildren().add(buildEligibilityCard(item));
        }

        eligibleCountLabel.setText(String.valueOf(eligibleCount));
        notEligibleCountLabel.setText(String.valueOf(notEligibleCount));
        totalSubjectsLabel.setText(String.valueOf(list.size()));
    }

    private VBox buildEligibilityCard(StudentEligibilityRes item) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16, 18, 16, 18));

        boolean finalEligible =
                "Eligible".equalsIgnoreCase(item.getFinalEligibilityStatus());

        card.setStyle(
                "-fx-background-color: " + (finalEligible ? "#f6fff7" : "#fff8f8") + ";" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: " + (finalEligible ? "#4cba52" : "#e85d5d") + ";" +
                "-fx-border-radius: 14;" +
                "-fx-border-width: 1;"
        );

        HBox titleRow = new HBox(12);

        Label courseLabel = new Label(
                item.getCourseCode() + " - " + item.getCourseName()
        );
        courseLabel.setStyle(
                "-fx-text-fill: #1a3a52; -fx-font-size: 15px; -fx-font-weight: bold;"
        );

        Label finalStatusLabel = new Label(
                finalEligible ? "FINAL ELIGIBLE" : "FINAL NOT ELIGIBLE"
        );
        finalStatusLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-background-color: " + (finalEligible ? "#35a053" : "#e85d5d") + ";" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 5 10 5 10;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );

        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        titleRow.getChildren().addAll(courseLabel, spacer, finalStatusLabel);

        HBox detailsRow = new HBox(18);

        VBox attendanceBox = buildInfoBox(
                "Attendance",
                String.format("%.1f%%", item.getAttendancePercentage()),
                "Hours: " + item.getFinalAttendanceHours() + "/" + item.getTotalHours()
                        + " | Medical: " + item.getMedicalHours(),
                item.getAttendanceStatus()
        );

        VBox caBox = buildInfoBox(
                "CA Marks",
                String.format("%.1f%%", item.getCaPercentage()),
                "Marks: " + item.getCaMarks() + "/" + item.getCaMaxMarks(),
                item.getCaStatus()
        );

        detailsRow.getChildren().addAll(attendanceBox, caBox);

        card.getChildren().addAll(titleRow, detailsRow);

        return card;
    }

    private VBox buildInfoBox(String title, String value, String subText, String status) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(12));
        box.setPrefWidth(360);
        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #e8eef5;" +
                "-fx-border-radius: 10;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 12px; -fx-font-weight: bold;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: #1a3a52; -fx-font-size: 24px; -fx-font-weight: bold;");

        Label subLabel = new Label(subText);
        subLabel.setStyle("-fx-text-fill: #5f748a; -fx-font-size: 11px;");

        boolean ok = "Eligible".equalsIgnoreCase(status);

        Label statusLabel = new Label(status);
        statusLabel.setStyle(
                "-fx-text-fill: " + (ok ? "#35a053" : "#e85d5d") + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );

        box.getChildren().addAll(titleLabel, valueLabel, subLabel, statusLabel);
        return box;
    }

    @FXML
    private void goBack() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/view/student/studentDashboard.fxml")
            );

            Stage stage = (Stage) eligibilityContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}