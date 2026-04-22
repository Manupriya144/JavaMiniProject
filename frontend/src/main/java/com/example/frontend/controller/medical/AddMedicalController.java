package com.example.frontend.controller.medical;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.model.Medical;
import com.example.frontend.service.MedicalService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddMedicalController {

    // FXML nodes
    @FXML private TextField  studentIdField;
    @FXML private TextField  courseIdField;
    @FXML private ComboBox<String> examTypeCombo;
    @FXML private DatePicker submittedDatePicker;
    @FXML private TextField  medicalCopyField;
    @FXML private Label      statusLabel;
    @FXML private Label      officerNameLabel;
    @FXML private Label      statusBarTime;

    // ── Service
    private final MedicalService medicalService =
            new MedicalService(LoginController.client);

    // ── Shared cell styles (popup cannot be reached via FXML) ─────
    private static final String CELL_NORMAL =
            "-fx-background-color: transparent;" +
                    "-fx-text-fill: #c8dcf5;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-family: 'Segoe UI';" +
                    "-fx-padding: 10 14;";

    private static final String CELL_HOVER =
            "-fx-background-color: #1e3a5f;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-family: 'Segoe UI';" +
                    "-fx-padding: 10 14;";

    private static final String CELL_SELECTED =
            "-fx-background-color: #0891b2;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-family: 'Segoe UI';" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 10 14;";

    private static final String POPUP_STYLE =
            "-fx-background-color: #091527;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #1e3a5f;" +
                    "-fx-border-radius: 10;" +
                    "-fx-border-width: 1.5;";

    // ── Lifecycle ─────────────────────────────────────────────────
    @FXML
    public void initialize() {
        // Populate & style ComboBox
        examTypeCombo.getItems().addAll("Mid", "Final", "Attendance");
        examTypeCombo.getSelectionModel().select("Attendance");
        applyComboBoxStyle(examTypeCombo);

        // Style DatePicker editor
        applyDatePickerStyle(submittedDatePicker);

        // Status bar date
        if (statusBarTime != null) {
            statusBarTime.setText(
                    LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
            );
        }
    }

    // ── ComboBox Styling ──────────────────────────────────────────

    private void applyComboBoxStyle(ComboBox<String> box) {
        // Cell factory — styles each row in the popup list
        box.setCellFactory(lv -> new ListCell<>() {
            {
                selectedProperty().addListener((obs, o, n) -> refreshStyle());
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(CELL_NORMAL);
                } else {
                    setText(item);
                    refreshStyle();
                }
            }

            private void refreshStyle() {
                if (isSelected()) {
                    setStyle(CELL_SELECTED);
                } else {
                    setStyle(CELL_NORMAL);
                    setOnMouseEntered(e -> setStyle(CELL_HOVER));
                    setOnMouseExited(e  -> setStyle(isSelected() ? CELL_SELECTED : CELL_NORMAL));
                }
            }
        });

        // Style the popup ListView background once it opens
        box.showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (isShowing) {
                ComboBoxListViewSkin<?> skin = (ComboBoxListViewSkin<?>) box.getSkin();
                if (skin != null) {
                    Node popupContent = skin.getPopupContent();
                    if (popupContent != null) {
                        popupContent.setStyle(POPUP_STYLE);
                    }
                }
            }
        });

        // Button cell (visible when popup is closed)
        box.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setStyle("-fx-background-color: transparent;");
                if (empty || item == null) {
                    setText("— Select Exam Type —");
                    setTextFill(Color.web("#4a7ab5"));
                } else {
                    setText(item);
                    setTextFill(Color.WHITE);
                    setStyle("-fx-background-color: transparent;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-family: 'Segoe UI';");
                }
            }
        });
    }

    // ── DatePicker Styling ────────────────────────────────────────

    private void applyDatePickerStyle(DatePicker dp) {
        // Style the text editor inside the DatePicker
        dp.getEditor().setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #334d6e;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-family: 'Segoe UI';"
        );

        // Style the popup calendar once it opens
        dp.showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (isShowing && dp.getSkin() != null) {
                Node popupContent = dp.lookup(".date-picker-popup");
                if (popupContent != null) {
                    popupContent.setStyle(
                            "-fx-background-color: #091527;" +
                                    "-fx-border-color: #1e3a5f;" +
                                    "-fx-border-radius: 10;" +
                                    "-fx-background-radius: 10;"
                    );
                }
            }
        });
    }

    // ── Submit ────────────────────────────────────────────────────
    @FXML
    private void submitMedical() {
        String studentId = studentIdField.getText();
        String courseId  = courseIdField.getText();
        String examType  = examTypeCombo.getValue();
        String date      = submittedDatePicker.getValue() == null
                ? null
                : submittedDatePicker.getValue().toString();
        String copy      = medicalCopyField.getText();

        // Validation
        if (studentId == null || studentId.isBlank()) {
            showStatus("✖  Student ID is required.", StatusType.ERROR);
            return;
        }
        if (courseId == null || courseId.isBlank()) {
            showStatus("✖  Course ID is required.", StatusType.ERROR);
            return;
        }
        if (date == null || date.isBlank()) {
            showStatus("✖  Date is required and must match the session date.", StatusType.ERROR);
            return;
        }

        Medical added = medicalService.addMedical(studentId, courseId, examType, date, copy);
        if (added != null) {
            showStatus("✔  Medical record #" + added.getMedicalId() + " submitted — status: Pending.",
                    StatusType.SUCCESS);
            clearFormFields();
        } else {
            showStatus("✖  " + medicalService.getLastMessage(), StatusType.ERROR);
        }
    }

    // ── Clear ─────────────────────────────────────────────────────
    @FXML
    private void clearForm() {
        clearFormFields();
        showStatus("", StatusType.INFO);
    }

    private void clearFormFields() {
        studentIdField.clear();
        courseIdField.clear();
        medicalCopyField.clear();
        submittedDatePicker.setValue(null);
        examTypeCombo.getSelectionModel().select("Attendance");
    }

    // ── Navigation ────────────────────────────────────────────────
    @FXML
    private void backToDashboard() {
        loadView("/view/techOfficerDashboard.fxml");
    }

    private void loadView(String path) {
        try {
            Parent root  = FXMLLoader.load(getClass().getResource(path));
            Stage  stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Cannot open view");
            alert.setContentText(path);
            alert.showAndWait();
        }
    }

    // ── Status Helper ─────────────────────────────────────────────
    private enum StatusType { SUCCESS, ERROR, INFO }

    private void showStatus(String message, StatusType type) {
        statusLabel.setText(message);
        String color = switch (type) {
            case SUCCESS -> "#4ade80";
            case ERROR   -> "#f87171";
            case INFO    -> "";
        };
        if (color.isEmpty()) {
            statusLabel.setStyle("");
        } else {
            statusLabel.setStyle(
                    "-fx-text-fill: " + color + ";" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-family: 'Segoe UI';" +
                            "-fx-font-weight: bold;"
            );
        }
    }
}