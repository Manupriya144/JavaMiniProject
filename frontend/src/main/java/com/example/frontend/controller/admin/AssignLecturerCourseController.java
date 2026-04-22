package com.example.frontend.controller.admin;

import com.example.frontend.dto.CourseResponseDTO;
import com.example.frontend.dto.LecturerCourseResponseDTO;
import com.example.frontend.dto.LecturerResponseDTO;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.CourseService;
import com.example.frontend.service.LecturerCourseService;
import com.example.frontend.service.LecturerService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AssignLecturerCourseController implements Initializable {

    // ── FXML Nodes ──────────────────────────────────────────────────
    @FXML private ComboBox<LecturerResponseDTO> lecturerBox;
    @FXML private ComboBox<CourseResponseDTO>   courseBox;
    @FXML private Label statusLabel;
    @FXML private Label adminNameLabel;
    @FXML private Label statusBarTime;

    // ── Services ─────────────────────────────────────────────────────
    private LecturerService       lecturerService;
    private CourseService         courseService;
    private LecturerCourseService lecturerCourseService;

    // ── Shared inline CSS for the popup list ─────────────────────────
    private static final String POPUP_STYLE =
            "-fx-background-color: #091527;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #1e3a5f;" +
                    "-fx-border-radius: 10;" +
                    "-fx-border-width: 1.5;";

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
            "-fx-background-color: #2563eb;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-family: 'Segoe UI';" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 10 14;";

    private static final String BUTTON_CELL_FILLED =
            "-fx-text-fill: white;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-family: 'Segoe UI';" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 0 14;";

    private static final String BUTTON_CELL_EMPTY =
            "-fx-text-fill: #4a7ab5;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-family: 'Segoe UI';" +
                    "-fx-padding: 0 14;";

    // ── Lifecycle ────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ServerClient client = ServerClient.getInstance();
        lecturerService       = new LecturerService(client);
        courseService         = new CourseService(client);
        lecturerCourseService = new LecturerCourseService(client);

        if (adminNameLabel != null) {
            adminNameLabel.setText(LoginController.username);
        }
        if (statusBarTime != null) {
            statusBarTime.setText(
                    LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
            );
        }

        applyComboBoxStyle(lecturerBox, "— Select a Lecturer —");
        applyComboBoxStyle(courseBox,   "— Select a Course —");

        loadLecturers();
        loadCourses();
    }

    // ── ComboBox Styling ─────────────────────────────────────────────

    /**
     * Applies a complete dark-themed skin to a ComboBox, including:
     *  • Popup list background
     *  • Per-cell hover / selected / normal states
     *  • Button cell (the value display area)
     *
     * The technique used here is the only reliable cross-platform way to
     * style JavaFX ComboBox popup lists without an external CSS file, because
     * the popup is a separate window/scene and inline FXML styles do NOT reach it.
     */
    private <T> void applyComboBoxStyle(ComboBox<T> box, String placeholder) {
        box.setVisibleRowCount(8);

        // ── Cell factory: styles each item in the dropdown list ──────
        Callback<ListView<T>, ListCell<T>> cellFactory = lv -> new ListCell<>() {

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle(CELL_NORMAL);
                } else {
                    setText(item.toString());
                    applyState();
                }
            }

            // Re-apply state on selection / focus changes
            private void applyState() {
                if (isSelected()) {
                    setStyle(CELL_SELECTED);
                } else {
                    setStyle(CELL_NORMAL);
                    setOnMouseEntered(e -> setStyle(CELL_HOVER));
                    setOnMouseExited(e  -> setStyle(isSelected() ? CELL_SELECTED : CELL_NORMAL));
                }
            }

            { // instance initializer — run once per cell
                selectedProperty().addListener((obs, o, n) -> applyState());
            }
        };

        box.setCellFactory(cellFactory);

        // ── Popup list background ─────────────────────────────────────
        // We hook into the popup after it is shown to style its inner ListView
        box.showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
            if (isNowShowing) {
                // The popup ListView is accessible via lookup after show()
                javafx.scene.control.skin.ComboBoxListViewSkin<?> skin =
                        (javafx.scene.control.skin.ComboBoxListViewSkin<?>) box.getSkin();
                if (skin != null) {
                    Node popupContent = skin.getPopupContent();
                    if (popupContent != null) {
                        popupContent.setStyle(POPUP_STYLE);
                    }
                }
            }
        });

        // ── Button cell: the visible part when popup is closed ────────
        box.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setStyle("-fx-background-color: transparent;");
                if (empty || item == null) {
                    setText(placeholder);
                    setTextFill(Color.web("#4a7ab5"));
                    setStyle(BUTTON_CELL_EMPTY + "-fx-background-color: transparent;");
                } else {
                    setText(item.toString());
                    setStyle(BUTTON_CELL_FILLED + "-fx-background-color: transparent;");
                }
            }
        });
    }

    // ── Data Loading ─────────────────────────────────────────────────
    private void loadLecturers() {
        List<LecturerResponseDTO> lecturers = lecturerService.getAllLecturers();
        lecturerBox.setItems(FXCollections.observableArrayList(lecturers));
    }

    private void loadCourses() {
        List<CourseResponseDTO> courses = courseService.getAllCourses();
        courseBox.setItems(FXCollections.observableArrayList(courses));
    }

    // ── Handlers ─────────────────────────────────────────────────────
    @FXML
    private void assignLecturerCourse() {
        LecturerResponseDTO lecturer = lecturerBox.getValue();
        CourseResponseDTO   course   = courseBox.getValue();

        if (lecturer == null || course == null) {
            showStatus("Please select both a lecturer and a course.", StatusType.ERROR);
            return;
        }

        LecturerCourseResponseDTO response =
                lecturerCourseService.assignLecturerToCourse(
                        lecturer.getUserId(),
                        course.getCourseId()
                );

        System.out.println("[AssignLecturerCourse] success=" + response.isSuccess()
                + "  message=" + response.getMessage());

        if (response.isSuccess()) {
            showStatus("✔  " + response.getMessage(), StatusType.SUCCESS);
            lecturerBox.getSelectionModel().clearSelection();
            courseBox.getSelectionModel().clearSelection();
        } else {
            showStatus("✖  " + response.getMessage(), StatusType.ERROR);
        }
    }

    @FXML
    private void clearSelections() {
        lecturerBox.getSelectionModel().clearSelection();
        courseBox.getSelectionModel().clearSelection();
        statusLabel.setText("");
        statusLabel.setStyle("");
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/AdminDashboard.fxml"));
            Parent root  = loader.load();
            Stage  stage = (Stage) lecturerBox.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Status Helper ─────────────────────────────────────────────────
    private enum StatusType { SUCCESS, ERROR, INFO }

    private void showStatus(String message, StatusType type) {
        statusLabel.setText(message);
        String color = switch (type) {
            case SUCCESS -> "#4ade80";
            case ERROR   -> "#f87171";
            case INFO    -> "#93c5fd";
        };
        statusLabel.setStyle(
                "-fx-text-fill: " + color + ";" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-family: 'Segoe UI';" +
                        "-fx-font-weight: bold;"
        );
    }
}