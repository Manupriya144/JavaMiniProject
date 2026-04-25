package com.example.frontend.controller.student;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.StudentExamEligibilityService;
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

public class StudentEligibilityController implements Initializable {

    @FXML private Label subtitleLabel;
    @FXML private Label studentNameLabel;
    @FXML private Label studentIdLabel;
    @FXML private Label profileInitial;

    @FXML private Label outcomeLabel;
    @FXML private Label attendanceLineLabel;
    @FXML private Label detailLabel;
    @FXML private VBox courseRowsBox;

    private final StudentExamEligibilityService eligibilityService =
            new StudentExamEligibilityService(ServerClient.getInstance());

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

        loadEligibilityDetails();
    }

    private void loadEligibilityDetails() {
        StudentExamEligibilityService.ExamEligibilityResult ev =
                eligibilityService.evaluate(LoginController.userId);

        if (ev.fullyEligible()) {
            outcomeLabel.setText("Eligible for final examinations");
            outcomeLabel.setStyle("-fx-text-fill: #2d8a44; -fx-font-size: 20px; -fx-font-weight: bold;");
        } else if (!ev.attendanceDataLoaded()) {
            outcomeLabel.setText("Could not verify eligibility");
            outcomeLabel.setStyle("-fx-text-fill: #c47a00; -fx-font-size: 20px; -fx-font-weight: bold;");
        } else {
            outcomeLabel.setText("Not eligible for one or more examinations");
            outcomeLabel.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 20px; -fx-font-weight: bold;");
        }

        if (ev.attendancePercentage() != null) {
            attendanceLineLabel.setText(String.format(Locale.ROOT,
                    "Combined attendance: %.1f%% (minimum %.0f%%)",
                    ev.attendancePercentage(),
                    StudentExamEligibilityService.MIN_ATTENDANCE_PERCENT));
        } else {
            attendanceLineLabel.setText("Combined attendance: — (could not load)");
        }

        detailLabel.setText(ev.detailExplanation());
        detailLabel.setWrapText(true);

        courseRowsBox.getChildren().clear();
        if (ev.courseRows().isEmpty()) {
            courseRowsBox.getChildren().add(buildCourseRow("—", "No modules listed for your level.", false));
            return;
        }
        for (StudentExamEligibilityService.CourseEligibilityRow r : ev.courseRows()) {
            String caLine = String.format(Locale.ROOT, "CA %.1f%% (min %.0f%%) — %s",
                    r.caPercentage(),
                    StudentExamEligibilityService.MIN_CA_PERCENT,
                    r.caEligible() ? "OK" : "below threshold");
            if (!r.responseOk()) {
                caLine = "CA could not be loaded — treated as not meeting threshold.";
            }
            courseRowsBox.getChildren().add(buildCourseRow(r.courseCode(), caLine, r.caEligible() && r.responseOk()));
        }
    }

    private HBox buildCourseRow(String title, String subtitle, boolean ok) {
        HBox row = new HBox(14);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 10; "
                        + "-fx-border-color: " + (ok ? "#cfead7" : "#f1c0c0") + "; "
                        + "-fx-border-radius: 10; -fx-border-width: 1;"
        );

        VBox text = new VBox(4);
        Label t = new Label(title);
        t.setStyle("-fx-text-fill: #1a3a52; -fx-font-size: 13px; -fx-font-weight: bold;");
        Label s = new Label(subtitle);
        s.setWrapText(true);
        s.setStyle("-fx-text-fill: #5f748a; -fx-font-size: 12px;");
        text.getChildren().addAll(t, s);

        row.getChildren().add(text);
        return row;
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/student/studentDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) outcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
