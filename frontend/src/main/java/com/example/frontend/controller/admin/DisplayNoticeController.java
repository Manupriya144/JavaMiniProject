package com.example.frontend.controller.admin;

import com.example.frontend.dto.NoticeResponseDTO;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.NoticeService;
import com.example.frontend.session.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DisplayNoticeController implements Initializable {

    @FXML private Label adminNameLabel;
    @FXML private TextField searchField;
    @FXML private VBox noticeListContainer;
    @FXML private Label totalNoticesInfoLabel;
    @FXML private Label statusLabel;
    @FXML private Label statusBarTime;

    private final NoticeService noticeService = new NoticeService(ServerClient.getInstance());
    private final List<NoticeResponseDTO> allNotices = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        adminNameLabel.setText(LoginController.username);
        startClock();
        loadNotices();
    }

    private void loadNotices() {
        allNotices.clear();

        List<NoticeResponseDTO> notices = noticeService.getAllNotices();
        if (notices != null) {
            allNotices.addAll(notices);
        }

        renderNoticeCards(allNotices);
        statusLabel.setText("Notices loaded successfully.");
    }

    private void renderNoticeCards(List<NoticeResponseDTO> notices) {
        noticeListContainer.getChildren().clear();

        if (notices == null || notices.isEmpty()) {
            Label empty = new Label("No notices available.");
            empty.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 13px;");
            noticeListContainer.getChildren().add(empty);
            totalNoticesInfoLabel.setText("0 notices loaded");
            return;
        }

        for (NoticeResponseDTO notice : notices) {
            noticeListContainer.getChildren().add(createNoticeRow(notice));
        }

        totalNoticesInfoLabel.setText(notices.size() + " notices loaded");
    }

    private HBox createNoticeRow(NoticeResponseDTO notice) {
        HBox row = new HBox(14);
        row.setPadding(new Insets(14, 16, 14, 16));
        row.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-border-color: #d4e4f7;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(three-pass-box,rgba(91,159,217,0.08),8,0,0,2);"
        );

        Circle dot = new Circle(5);
        dot.setStyle("-fx-fill: #5b9fd9;");

        VBox textBox = new VBox(5);

        Label title = new Label(safe(notice.getTitle()));
        title.setWrapText(true);
        title.setStyle("-fx-text-fill: #1a3a52; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label description = new Label(shortText(safe(notice.getDescription()), 140));
        description.setWrapText(true);
        description.setStyle("-fx-text-fill: #5f748a; -fx-font-size: 12px;");

        Label meta = new Label(
                "By: " + safe(notice.getCreated_by()) +
                        "  •  " + formatDateTime(safe(notice.getCreated_at()))
        );
        meta.setStyle("-fx-text-fill: #a8b8ca; -fx-font-size: 11px;");

        textBox.getChildren().addAll(title, description, meta);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        Button openBtn = new Button("Open");
        openBtn.setPrefWidth(100);
        openBtn.setStyle(
                "-fx-background-color: #5b9fd9;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8 14 8 14;"
        );

        openBtn.setOnAction(e -> openFileOrLink(notice.getPdf_file_path()));

        row.getChildren().addAll(dot, textBox, openBtn);
        return row;
    }

    @FXML
    private void filterNotices() {
        String keyword = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            renderNoticeCards(allNotices);
            return;
        }

        List<NoticeResponseDTO> filtered = new ArrayList<>();

        for (NoticeResponseDTO notice : allNotices) {
            String title = safe(notice.getTitle()).toLowerCase();
            String description = safe(notice.getDescription()).toLowerCase();

            if (title.contains(keyword) || description.contains(keyword)) {
                filtered.add(notice);
            }
        }

        renderNoticeCards(filtered);
        statusLabel.setText(filtered.size() + " matching notices found.");
    }

    @FXML
    private void refreshNotices() {
        searchField.clear();
        loadNotices();
    }

    private void openFileOrLink(String originalPath) {
        new Thread(() -> {
            try {
                System.out.println("PDF PATH FROM DB: " + originalPath);

                if (originalPath == null || originalPath.trim().isEmpty()) {
                    Platform.runLater(() -> statusLabel.setText("No file available."));
                    return;
                }

                String path = originalPath.trim();

                if (path.startsWith("http://") || path.startsWith("https://")) {
                    if (Desktop.isDesktopSupported()
                            && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(new URI(path));
                        Platform.runLater(() -> statusLabel.setText("Opening PDF link..."));
                    } else {
                        Platform.runLater(() -> statusLabel.setText("Browse action is not supported."));
                    }
                    return;
                }

                File file = new File(path);

                if (!file.isAbsolute()) {
                    file = new File(System.getProperty("user.dir"), path);
                }

                File finalFile = file;
                System.out.println("Resolved path: " + finalFile.getAbsolutePath());

                if (!finalFile.exists()) {
                    Platform.runLater(() ->
                            statusLabel.setText("File not found: " + finalFile.getName())
                    );
                    return;
                }

                String os = System.getProperty("os.name").toLowerCase();

                if (os.contains("linux")) {
                    new ProcessBuilder("xdg-open", finalFile.getAbsolutePath()).start();
                    Platform.runLater(() -> statusLabel.setText("Opening PDF..."));
                    return;
                }

                if (Desktop.isDesktopSupported()
                        && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(finalFile);
                    Platform.runLater(() -> statusLabel.setText("Opening PDF..."));
                    return;
                }

                Platform.runLater(() -> statusLabel.setText("Open action is not supported."));

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() ->
                        statusLabel.setText("Error opening PDF: " + e.getMessage())
                );
            }
        }).start();
    }

    @FXML
    private void goBack() {
        try {
            String role = SessionManager.getRole();
            String dashboardFxml;

            switch (role) {
                case "Admin":
                    dashboardFxml = "/view/admin/AdminDashboard.fxml";
                    break;
                case "Tech_Officer":
                    dashboardFxml = "/view/techofficer/techOfficerDashboard.fxml";
                    break;
                case "Lecturer":
                    dashboardFxml = "/view/lecturer/lecturerDashboard.fxml";
                    break;
                case "Student":
                    dashboardFxml = "/view/student/studentDashboard.fxml";
                    break;
                default:
                    statusLabel.setText("Unknown role: " + role);
                    return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboardFxml));
            Parent root = loader.load();

            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Failed to go back.");
        }
    }

    private void startClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e ->
                        statusBarTime.setText(LocalDateTime.now().format(formatter))
                ),
                new KeyFrame(Duration.seconds(1))
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String formatDateTime(String value) {
        if (value == null || value.isBlank()) {
            return "—";
        }
        return value.replace("T", " ");
    }

    private String shortText(String text, int max) {
        if (text == null) return "";
        return text.length() > max ? text.substring(0, max) + "..." : text;
    }
}