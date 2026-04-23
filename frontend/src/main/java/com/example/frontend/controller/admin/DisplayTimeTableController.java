package com.example.frontend.controller.admin;

import com.example.frontend.dto.TimeTableResponseDTO;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.TimeTableService;
import com.example.frontend.session.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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

public class DisplayTimeTableController implements Initializable {

    @FXML private Label adminNameLabel;
    @FXML private TextField searchField;
    @FXML private VBox timetableListContainer;
    @FXML private Label totalTimeTablesInfoLabel;
    @FXML private Label statusLabel;
    @FXML private Label statusBarTime;

    private final TimeTableService timeTableService = new TimeTableService(ServerClient.getInstance());
    private final List<TimeTableResponseDTO> allTimeTables = new ArrayList<>();

    private VBox expandedDetailsBox = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String username = LoginController.username;
        adminNameLabel.setText(username == null || username.isBlank() ? "Administrator" : username);
        startClock();
        loadTimeTables();
    }

    private void loadTimeTables() {
        allTimeTables.clear();

        List<TimeTableResponseDTO> fetched = timeTableService.getAllTimeTables();
        if (fetched != null) {
            allTimeTables.addAll(fetched);
        }

        renderRows(allTimeTables);
        totalTimeTablesInfoLabel.setText(allTimeTables.size() + " time tables loaded");

        if (allTimeTables.isEmpty()) {
            statusLabel.setText("No time tables found.");
        } else {
            statusLabel.setText("Time tables loaded successfully.");
        }
    }

    private void renderRows(List<TimeTableResponseDTO> list) {
        expandedDetailsBox = null;
        timetableListContainer.getChildren().clear();

        for (TimeTableResponseDTO tt : list) {
            timetableListContainer.getChildren().add(buildRow(tt));
        }

        totalTimeTablesInfoLabel.setText(list.size() + " time tables loaded");
    }

    private VBox buildRow(TimeTableResponseDTO tt) {
        VBox row = new VBox(0);
        row.setStyle(
                "-fx-background-color: #fafbff;" +
                        "-fx-border-color: #d4e4f7;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(three-pass-box,rgba(91,159,217,0.08),8,0,0,2);"
        );

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 16, 14, 16));
        header.setStyle("-fx-cursor: hand; -fx-background-radius: 10;");

        Label dot = new Label("●");
        dot.setStyle("-fx-text-fill: #5b9fd9; -fx-font-size: 10px;");

        Label title = new Label(safe(tt.getTitle()));
        title.setStyle(
                "-fx-text-fill: #1a3a52;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );
        title.setWrapText(true);
        HBox.setHgrow(title, Priority.ALWAYS);

        Label meta = new Label(
                safe(tt.getDepartmentId()) +
                        "  ·  L" + tt.getAcademicLevel() +
                        "  ·  " + safe(tt.getSemester())
        );
        meta.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 11px;");

        Label chevron = new Label("▾");
        chevron.setStyle("-fx-text-fill: #5b9fd9; -fx-font-size: 13px;");

        header.getChildren().addAll(dot, title, meta, chevron);

        VBox details = buildDetailsPanel(tt);
        details.setVisible(false);
        details.setManaged(false);

        row.getChildren().addAll(header, details);
        header.setOnMouseClicked(e -> toggleRow(row, details, chevron));

        return row;
    }

    private VBox buildDetailsPanel(TimeTableResponseDTO tt) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(0, 16, 16, 36));
        panel.setStyle(
                "-fx-background-color: #f5f9ff;" +
                        "-fx-background-radius: 0 0 10 10;"
        );

        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setStyle("-fx-background-color: #e8eef5;");
        divider.setPadding(new Insets(0, 0, 8, 0));

        HBox infoRow = new HBox(30);
        infoRow.setAlignment(Pos.CENTER_LEFT);
        infoRow.setPadding(new Insets(10, 0, 0, 0));

        infoRow.getChildren().addAll(
                infoChip("ID", String.valueOf(tt.getTimetableId())),
                infoChip("Department", safe(tt.getDepartmentId())),
                infoChip("Level", "Level " + tt.getAcademicLevel()),
                infoChip("Semester", safe(tt.getSemester())),
                infoChip("Uploaded", formatDateTime(safe(tt.getUploadedAt())))
        );

        HBox pdfRow = new HBox(10);
        pdfRow.setAlignment(Pos.CENTER_LEFT);

        boolean hasPdf = tt.getPdfFilePath() != null && !tt.getPdfFilePath().isBlank();

        if (hasPdf) {
            Button openBtn = new Button("📄  Open PDF");
            openBtn.setStyle(
                    "-fx-background-color: #5b9fd9;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-border-color: transparent;" +
                            "-fx-border-radius: 8;" +
                            "-fx-background-radius: 8;" +
                            "-fx-cursor: hand;" +
                            "-fx-padding: 8 16 8 16;"
            );
            openBtn.setOnAction(e -> openFileOrLink(tt.getPdfFilePath()));
            pdfRow.getChildren().add(openBtn);
        } else {
            Label noPdf = new Label("No PDF attached");
            noPdf.setStyle("-fx-text-fill: #a8b8ca; -fx-font-size: 11px; -fx-font-style: italic;");
            pdfRow.getChildren().add(noPdf);
        }

        panel.getChildren().addAll(divider, infoRow, pdfRow);
        return panel;
    }

    private VBox infoChip(String label, String value) {
        VBox box = new VBox(2);

        Label lbl = new Label(label.toUpperCase());
        lbl.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 10px; -fx-font-weight: bold;");

        Label val = new Label(value == null || value.isEmpty() ? "—" : value);
        val.setStyle("-fx-text-fill: #1a3a52; -fx-font-size: 12px;");

        box.getChildren().addAll(lbl, val);
        return box;
    }

    private void toggleRow(VBox row, VBox details, Label chevron) {
        boolean willExpand = !details.isVisible();

        if (expandedDetailsBox != null && expandedDetailsBox != details) {
            expandedDetailsBox.setVisible(false);
            expandedDetailsBox.setManaged(false);

            Label oldChevron = findChevron((javafx.scene.Parent) expandedDetailsBox.getParent());
            if (oldChevron != null) {
                oldChevron.setText("▾");
            }

            ((VBox) expandedDetailsBox.getParent()).setStyle(
                    "-fx-background-color: #fafbff;" +
                            "-fx-border-color: #d4e4f7;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;" +
                            "-fx-effect: dropshadow(three-pass-box,rgba(91,159,217,0.08),8,0,0,2);"
            );
        }

        if (willExpand) {
            details.setVisible(true);
            details.setManaged(true);
            chevron.setText("▴");
            expandedDetailsBox = details;

            row.setStyle(
                    "-fx-background-color: #fafbff;" +
                            "-fx-border-color: #5b9fd9;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;" +
                            "-fx-effect: dropshadow(three-pass-box,rgba(91,159,217,0.15),10,0,0,3);"
            );
        } else {
            details.setVisible(false);
            details.setManaged(false);
            chevron.setText("▾");
            expandedDetailsBox = null;

            row.setStyle(
                    "-fx-background-color: #fafbff;" +
                            "-fx-border-color: #d4e4f7;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;" +
                            "-fx-effect: dropshadow(three-pass-box,rgba(91,159,217,0.08),8,0,0,2);"
            );
        }
    }
    private Label findChevron(javafx.scene.Parent rowParent) {
        if (rowParent instanceof VBox rowBox) {
            if (!rowBox.getChildren().isEmpty() && rowBox.getChildren().get(0) instanceof HBox header) {
                for (javafx.scene.Node n : header.getChildren()) {
                    if (n instanceof Label l) {
                        if ("▾".equals(l.getText()) || "▴".equals(l.getText())) {
                            return l;
                        }
                    }
                }
            }
        }
        return null;
    }

    @FXML
    private void filterTimeTables() {
        String kw = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();

        if (kw.isEmpty()) {
            renderRows(allTimeTables);
            return;
        }

        List<TimeTableResponseDTO> filtered = new ArrayList<>();
        for (TimeTableResponseDTO tt : allTimeTables) {
            if (safe(tt.getTitle()).toLowerCase().contains(kw)
                    || safe(tt.getDepartmentId()).toLowerCase().contains(kw)
                    || safe(tt.getSemester()).toLowerCase().contains(kw)
                    || String.valueOf(tt.getAcademicLevel()).contains(kw)) {
                filtered.add(tt);
            }
        }

        renderRows(filtered);
        statusLabel.setText(filtered.size() + " matching timetables found.");
    }

    @FXML
    private void refreshTimeTables() {
        searchField.clear();
        loadTimeTables();
    }

    private void openFileOrLink(String path) {
        new Thread(() -> {
            try {
                if (path == null || path.isBlank()) {
                    Platform.runLater(() -> statusLabel.setText("No file available."));
                    return;
                }

                if (path.startsWith("http://") || path.startsWith("https://")) {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(new URI(path));
                        Platform.runLater(() -> statusLabel.setText("Opened PDF link in browser."));
                    } else {
                        Platform.runLater(() -> statusLabel.setText("Browse action is not supported."));
                    }
                    return;
                }

                File file = new File(path);
                if (!file.isAbsolute()) {
                    file = new File(System.getProperty("user.dir"), path);
                }

                if (!file.exists()) {
                    File f = file;
                    Platform.runLater(() -> statusLabel.setText("File not found: " + f.getAbsolutePath()));
                    return;
                }

                String os = System.getProperty("os.name").toLowerCase();

                if (os.contains("linux")) {
                    new ProcessBuilder("xdg-open", file.getAbsolutePath()).start();
                } else if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(file);
                } else {
                    Platform.runLater(() -> statusLabel.setText("Open action is not supported on this system."));
                    return;
                }

                Platform.runLater(() -> statusLabel.setText("Opening PDF..."));

            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> statusLabel.setText("Error: " + ex.getMessage()));
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
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO, e -> statusBarTime.setText(LocalDateTime.now().format(fmt))),
                new KeyFrame(Duration.seconds(1))
        );
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();
    }

    private String safe(String v) {
        return v == null ? "" : v;
    }

    private String formatDateTime(String v) {
        return v == null || v.isBlank() ? "" : v.replace("T", " ");
    }
}