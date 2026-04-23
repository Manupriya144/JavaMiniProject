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
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
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

    @FXML private Label detailTitleLabel;
    @FXML private Label detailIdLabel;
    @FXML private Label detailDescriptionLabel;
    @FXML private Label detailCreatedByLabel;
    @FXML private Label detailCreatedAtLabel;
    @FXML private Hyperlink detailPdfLink;

    @FXML private Label statusLabel;
    @FXML private Label statusBarTime;

    private final NoticeService noticeService = new NoticeService(ServerClient.getInstance());
    private final List<NoticeResponseDTO> allNotices = new ArrayList<>();
    private NoticeResponseDTO selectedNotice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        adminNameLabel.setText(LoginController.username);
        detailPdfLink.setDisable(true);
        startClock();
        loadNotices();
    }

    private void loadNotices() {
        allNotices.clear();
        allNotices.addAll(noticeService.getAllNotices());
        renderNoticeCards(allNotices);

        if (!allNotices.isEmpty()) {
            showNoticeDetails(allNotices.get(0));
        } else {
            clearDetails();
        }

        totalNoticesInfoLabel.setText(allNotices.size() + " notices loaded");
        statusLabel.setText("Notices loaded successfully.");
    }

    private void renderNoticeCards(List<NoticeResponseDTO> notices) {
        noticeListContainer.getChildren().clear();

        for (NoticeResponseDTO notice : notices) {
            VBox card = createNoticeCard(notice);
            noticeListContainer.getChildren().add(card);
        }

        totalNoticesInfoLabel.setText(notices.size() + " notices loaded");
    }

    private VBox createNoticeCard(NoticeResponseDTO notice) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14));
        card.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-border-color: #d4e4f7;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box,rgba(91,159,217,0.08),8,0,0,2);"
        );

        Label title = new Label(safe(notice.getTitle()));
        title.setWrapText(true);
        title.setStyle("-fx-text-fill: #1a3a52; -fx-font-size: 13px; -fx-font-weight: bold;");

        Label description = new Label(shortText(safe(notice.getDescription()), 110));
        description.setWrapText(true);
        description.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 12px;");

        Label meta = new Label("By: " + safe(notice.getCreated_by()) + "   |   " + formatDateTime(safe(notice.getCreated_at())));
        meta.setWrapText(true);
        meta.setStyle("-fx-text-fill: #a8b8ca; -fx-font-size: 11px;");

        Hyperlink openLink = new Hyperlink("Open PDF");
        openLink.setStyle("-fx-text-fill: #5b9fd9; -fx-font-size: 12px; -fx-font-weight: 600;");
        openLink.setOnAction(e -> {
            e.consume();
            openFileOrLink(notice.getPdf_file_path());
        });

        Button detailsBtn = new Button("View Details");
        detailsBtn.setStyle(
                "-fx-background-color: #5b9fd9;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 6 12 6 12;"
        );
        detailsBtn.setOnAction(e -> {
            e.consume();
            showNoticeDetails(notice);
        });

        card.getChildren().addAll(title, description, meta, openLink, detailsBtn);
        card.setOnMouseClicked(e -> showNoticeDetails(notice));

        return card;
    }

    private void showNoticeDetails(NoticeResponseDTO notice) {
        selectedNotice = notice;

        detailTitleLabel.setText(safe(notice.getTitle()));
        detailIdLabel.setText(String.valueOf(notice.getNotice_id()));
        detailDescriptionLabel.setText(safe(notice.getDescription()));
        detailCreatedByLabel.setText(safe(notice.getCreated_by()));
        detailCreatedAtLabel.setText(formatDateTime(safe(notice.getCreated_at())));

        boolean hasPdf = notice.getPdf_file_path() != null && !notice.getPdf_file_path().isBlank();
        detailPdfLink.setDisable(!hasPdf);
        detailPdfLink.setText(hasPdf ? "Open attached PDF" : "No PDF available");

        statusLabel.setText("Showing notice: " + safe(notice.getTitle()));
    }

    private void clearDetails() {
        selectedNotice = null;
        detailTitleLabel.setText("Select a notice");
        detailIdLabel.setText("—");
        detailDescriptionLabel.setText("—");
        detailCreatedByLabel.setText("—");
        detailCreatedAtLabel.setText("—");
        detailPdfLink.setText("No PDF available");
        detailPdfLink.setDisable(true);
        statusLabel.setText("No notices found.");
    }

    @FXML
    private void filterNotices() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();

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

    @FXML
    private void openSelectedNoticePdf() {
        if (selectedNotice == null) {
            statusLabel.setText("Select a notice first.");
            return;
        }
        openFileOrLink(selectedNotice.getPdf_file_path());
    }

    private void openFileOrLink(String path) {
        new Thread(() -> {
            try {
                System.out.println("PDF PATH FROM DB: " + path);

                if (path == null || path.trim().isEmpty()) {
                    Platform.runLater(() -> statusLabel.setText("No file available."));
                    return;
                }

                // URL case
                if (path.startsWith("http://") || path.startsWith("https://")) {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(new URI(path));
                        Platform.runLater(() -> statusLabel.setText("Opened PDF link."));
                    } else {
                        Platform.runLater(() -> statusLabel.setText("Browse action is not supported."));
                    }
                    return;
                }

                File file = new File(path);

                if (!file.isAbsolute()) {
                    file = new File(System.getProperty("user.dir"), path);
                }

                System.out.println("Resolved path: " + file.getAbsolutePath());

                if (!file.exists()) {
                    File finalFile1 = file;
                    Platform.runLater(() -> statusLabel.setText("File not found: " + finalFile1.getAbsolutePath()));
                    return;
                }

                String os = System.getProperty("os.name").toLowerCase();

                // Linux
                if (os.contains("linux")) {
                    new ProcessBuilder("xdg-open", file.getAbsolutePath()).start();
                    Platform.runLater(() -> statusLabel.setText("Opening PDF..."));
                    return;
                }

                // Windows / others
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(file);
                    Platform.runLater(() -> statusLabel.setText("Opening PDF..."));
                    return;
                }

                Platform.runLater(() -> statusLabel.setText("Open action is not supported on this system."));

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> statusLabel.setText("Error opening PDF: " + e.getMessage()));
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
                    dashboardFxml = "/view/LecturerDashboard.fxml";
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
                new KeyFrame(Duration.seconds(0), e -> statusBarTime.setText(LocalDateTime.now().format(formatter))),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String formatDateTime(String value) {
        return value == null ? "" : value.replace("T", " ");
    }

    private String shortText(String text, int max) {
        if (text == null) return "";
        return text.length() > max ? text.substring(0, max) + "..." : text;
    }
}