package com.example.frontend.controller.admin;

import com.example.frontend.dto.UserRequestDTO;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateUserController implements Initializable {

    // Service
    private final UserService userService =
            new UserService(ServerClient.getInstance());

    // Nav / labels
    @FXML private Label adminNameLabel;
    @FXML private Label statusBarTime;

    // Core fields
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField contactField;
    @FXML private TextField roleField;

    // Role toggle buttons
    @FXML private ToggleButton roleStudent;
    @FXML private ToggleButton roleLecturer;
    @FXML private ToggleButton roleDean;
    @FXML private ToggleButton roleTechOfficer;
    @FXML private ToggleButton roleAdmin;

    // Extra field panes
    @FXML private VBox studentFields;
    @FXML private VBox lecturerFields;
    @FXML private VBox techOfficerFields;

    // Student extras
    @FXML private TextField regNoField;
    @FXML private TextField batchField;
    @FXML private ComboBox<String> academicLevelBox;
    @FXML private ComboBox<String> departmentBox;

    // Lecturer extras
    @FXML private TextField specializationField;
    @FXML private TextField designationField;

    // Tech Officer extras
    @FXML private ComboBox<String> techDepartmentBox;

    // Feedback
    @FXML private Label statusLabel;

    private static final String ACTIVE_STYLE =
            "-fx-background-color: #1e3c72; -fx-text-fill: white;" +
                    "-fx-font-size: 12px; -fx-background-radius: 8;" +
                    "-fx-border-color: #4a90d9; -fx-border-radius: 8;" +
                    "-fx-border-width: 2; -fx-cursor: hand; -fx-font-weight: bold;";

    private static final String INACTIVE_STYLE =
            "-fx-background-color: #0f1b35; -fx-text-fill: #a0b8e0;" +
                    "-fx-font-size: 12px; -fx-background-radius: 8;" +
                    "-fx-border-color: #2a4a7f; -fx-border-radius: 8;" +
                    "-fx-border-width: 1; -fx-cursor: hand;";

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        academicLevelBox.getItems().addAll("1", "2", "3", "4");

        String[] departments = {"ICT", "ET", "BST"};
        departmentBox.getItems().addAll(departments);
        techDepartmentBox.getItems().addAll(departments);

        ToggleButton[] roles = {
                roleStudent,
                roleLecturer,
                roleDean,
                roleTechOfficer,
                roleAdmin
        };

        for (ToggleButton btn : roles) {
            btn.setOnAction(e -> handleRoleToggle(btn));
            btn.setStyle(INACTIVE_STYLE);
        }
        adminNameLabel.setText(LoginController.username);

        statusBarTime.setText(java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy")));
    }

    //Role Toggle

    private void handleRoleToggle(ToggleButton selected) {

        ToggleButton[] roles = {
                roleStudent,
                roleLecturer,
                roleDean,
                roleTechOfficer,
                roleAdmin
        };

        for (ToggleButton btn : roles) {
            btn.setSelected(btn == selected);
            btn.setStyle(btn == selected ? ACTIVE_STYLE : INACTIVE_STYLE);
        }

        String role = "";

        if (selected == roleStudent) role = "Student";
        else if (selected == roleLecturer) role = "Lecturer";
        else if (selected == roleDean) role = "Dean";
        else if (selected == roleTechOfficer) role = "Tech_Officer";
        else if (selected == roleAdmin) role = "Admin";

        roleField.setText(role);

        setExtraPanel("Student", role.equals("Student"));
        setExtraPanel("Lecturer", role.equals("Lecturer"));
        setExtraPanel("Tech_Officer", role.equals("Tech_Officer"));
    }

    private void setExtraPanel(String role, boolean visible) {

        VBox panel = switch (role) {
            case "Student" -> studentFields;
            case "Lecturer" -> lecturerFields;
            case "Tech_Officer" -> techOfficerFields;
            default -> null;
        };

        if (panel != null) {
            panel.setVisible(visible);
            panel.setManaged(visible);
        }
    }

    //

    @FXML
    private void createUser() {

        String role = roleField.getText();

        // Basic validation
        if (usernameField.getText().isBlank() ||
                emailField.getText().isBlank() ||
                passwordField.getText().isBlank() ||
                role.isBlank()) {
            showStatus("⚠ Please fill required fields.", false);
            return;
        }

        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername(usernameField.getText().trim());
        dto.setEmail(emailField.getText().trim());
        dto.setPassword(passwordField.getText());
        dto.setContactNumber(contactField.getText().trim());
        dto.setRole(role);

        // Role-specific data
        switch (role) {
            case "Student":
                if (regNoField.getText().isBlank() ||
                        batchField.getText().isBlank() ||
                        academicLevelBox.getSelectionModel().isEmpty() ||
                        departmentBox.getSelectionModel().isEmpty()) {
                    showStatus("⚠ Please fill all Student fields.", false);
                    return;
                }

                dto.setRegNo(regNoField.getText().trim());
                dto.setBatch(batchField.getText().trim());
                dto.setAcademicLevel(Integer.parseInt(
                        academicLevelBox.getSelectionModel().getSelectedItem()));
                dto.setDepartmentId(departmentBox.getSelectionModel().getSelectedItem());
                break;

            case "Lecturer":
                if (specializationField.getText().isBlank() ||
                        designationField.getText().isBlank()) {
                    showStatus("⚠ Please fill all Lecturer fields.", false);
                    return;
                }

                dto.setSpecialization(specializationField.getText().trim());
                dto.setDesignation(designationField.getText().trim());
                break;

            case "Tech_Officer":
                if (techDepartmentBox.getSelectionModel().isEmpty()) {
                    showStatus("⚠ Please select Tech Officer department.", false);
                    return;
                }
                dto.setTechDepartmentId(techDepartmentBox.getSelectionModel().getSelectedItem());
                break;


        }

        try {
            boolean created = userService.createUser(dto);

            if (created) {
                showStatus("✅ User created successfully!", true);
                clearForm();

                // Show alert box
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("User has been created successfully!");
                alert.showAndWait();
            } else {
                showStatus("❌ Failed to create user.", false);
            }

        } catch (Exception e) {
            showStatus("❌ Failed to create user.", false);
            e.printStackTrace();
        }
    }
    //Clear Form

    @FXML
    private void clearForm() {

        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        contactField.clear();
        roleField.clear();

        regNoField.clear();
        batchField.clear();

        academicLevelBox.getSelectionModel().clearSelection();
        departmentBox.getSelectionModel().clearSelection();

        specializationField.clear();
        designationField.clear();

        techDepartmentBox.getSelectionModel().clearSelection();

        ToggleButton[] roles = {
                roleStudent,
                roleLecturer,
                roleDean,
                roleTechOfficer,
                roleAdmin
        };

        for (ToggleButton btn : roles) {
            btn.setSelected(false);
            btn.setStyle(INACTIVE_STYLE);
        }

        setExtraPanel("Student", false);
        setExtraPanel("Lecturer", false);
        setExtraPanel("Tech_Officer", false);

        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
    }

    //Back Button

    @FXML
    private void goBack() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/adminDashboard.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) usernameField.getScene().getWindow();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }



    private void showStatus(String msg, boolean success) {

        statusLabel.setText(msg);

        statusLabel.setStyle(
                (success ? "-fx-text-fill: #28a745;" : "-fx-text-fill: #ff6b6b;") +
                        "-fx-font-size: 12px; -fx-font-weight: bold;"
        );

        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
}