package com.example.frontend.controller.lecturer;

import com.example.frontend.model.LecturerProfile;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.LecturerService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class LecturerProfileController {

    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;

    @FXML private TextField userIdField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField contactNumberField;
    @FXML private TextField specializationField;
    @FXML private TextField designationField;
    @FXML private TextField profilePictureField;

    @FXML private ImageView profileImage;
    @FXML private Label profileInitial;

    @FXML private ImageView headerAvatarImage;
    @FXML private Label headerAvatarInitial;

    @FXML private Label statusLabel;
    @FXML private Button saveBtn;

    private LecturerProfile lecturerProfile;

    private final LecturerService lecturerService =
            new LecturerService(ServerClient.getInstance());

    @FXML
    public void initialize() {
        loadProfile();
    }

    private void loadProfile() {
        lecturerProfile = lecturerService.getLecturerProfile();

        if (lecturerProfile == null) {
            showStatus("Failed to load lecturer profile.", false);
            return;
        }

        usernameLabel.setText(value(lecturerProfile.getUsername()));
        emailLabel.setText(value(lecturerProfile.getEmail()));

        userIdField.setText(value(lecturerProfile.getUserId()));
        usernameField.setText(value(lecturerProfile.getUsername()));
        emailField.setText(value(lecturerProfile.getEmail()));
        contactNumberField.setText(value(lecturerProfile.getContactNumber()));
        specializationField.setText(value(lecturerProfile.getSpecialization()));
        designationField.setText(value(lecturerProfile.getDesignation()));
        profilePictureField.setText(value(lecturerProfile.getProfilePicture()));

        userIdField.setEditable(false);
        usernameField.setEditable(false);
        emailField.setEditable(false);
        contactNumberField.setEditable(false);
        specializationField.setEditable(false);

        setupProfileImage();
    }

    private void setupProfileImage() {
        String profilePicture = profilePictureField.getText();
        String username = usernameField.getText();

        String initial = username != null && !username.isBlank()
                ? username.substring(0, 1).toUpperCase()
                : "L";

        if (profilePicture != null && !profilePicture.isBlank()) {
            try {
                File file = new File(profilePicture);

                if (file.exists()) {
                    Image image = new Image(file.toURI().toString(), false);

                    profileImage.setImage(image);
                    profileImage.setFitWidth(84);
                    profileImage.setFitHeight(84);
                    profileImage.setPreserveRatio(false);
                    profileImage.setClip(new Circle(42, 42, 42));
                    profileImage.setVisible(true);
                    profileInitial.setVisible(false);

                    headerAvatarImage.setImage(image);
                    headerAvatarImage.setFitWidth(36);
                    headerAvatarImage.setFitHeight(36);
                    headerAvatarImage.setPreserveRatio(false);
                    headerAvatarImage.setClip(new Circle(18, 18, 18));
                    headerAvatarImage.setVisible(true);
                    headerAvatarInitial.setVisible(false);

                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        profileImage.setImage(null);
        profileImage.setVisible(false);
        profileInitial.setText(initial);
        profileInitial.setVisible(true);

        headerAvatarImage.setImage(null);
        headerAvatarImage.setVisible(false);
        headerAvatarInitial.setText(initial);
        headerAvatarInitial.setVisible(true);
    }

    @FXML
    private void browseProfilePicture() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Profile Picture");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(profilePictureField.getScene().getWindow());

        if (file != null) {
            profilePictureField.setText(file.getAbsolutePath());
            setupProfileImage();
        }
    }

    @FXML
    private void saveProfile() {
        if (lecturerProfile == null) {
            showStatus("Profile not loaded.", false);
            return;
        }

        String designation = designationField.getText() == null
                ? ""
                : designationField.getText().trim();

        String profilePicture = profilePictureField.getText() == null
                ? ""
                : profilePictureField.getText().trim();

        if (designation.isBlank()) {
            showStatus("Designation is required.", false);
            return;
        }

        saveBtn.setDisable(true);

        boolean updated = lecturerService.updateLecturerProfile(designation, profilePicture);

        if (updated) {
            lecturerProfile.setDesignation(designation);
            lecturerProfile.setProfilePicture(profilePicture);
            setupProfileImage();
            showStatus("Profile updated successfully.", true);
        } else {
            showStatus("Failed to update profile.", false);
        }

        saveBtn.setDisable(false);
    }

    @FXML
    private void goBack() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/view/lecturer/lecturerDashboard.fxml")
            );

            Stage stage = (Stage) userIdField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showStatus("Failed to load dashboard.", false);
        }
    }

    private String value(String value) {
        return value == null ? "" : value;
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle(success
                ? "-fx-text-fill: #4cba52; -fx-font-weight: bold;"
                : "-fx-text-fill: #e85d5d; -fx-font-weight: bold;");
    }
}