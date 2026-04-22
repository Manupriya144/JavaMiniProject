package com.example.frontend.controller.tech_officer;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.model.TechOfficerProfile;
import com.example.frontend.service.UserService;
import com.example.frontend.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TechOfficerProfileController {

    @FXML private Label usernameLabel;
    @FXML private Label subtitleLabel;
    @FXML private TextField userIdField;
    @FXML private TextField usernameTextField;
    @FXML private TextField roleField;
    @FXML private TextField departmentIdField;
    @FXML private TextField emailField;
    @FXML private TextField contactNumberField;
    @FXML private TextField profilePictureField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ImageView profileImage;
    @FXML private Label profileInitial;
    @FXML private Label statusLabel;

    private final UserService userService = new UserService(LoginController.client);
    private String profileUserId;

    @FXML
    public void initialize() {
        // Prefer session userId as the source of truth; profile fetch may fail transiently.
        profileUserId = trimOrNull(SessionManager.getUserId());
        loadProfile();
    }

    private void loadProfile() {
        if (SessionManager.getToken() == null || SessionManager.getToken().isBlank()) {
            profileUserId = null;
            showStatus("No active session. Log in again.", false);
            return;
        }

        TechOfficerProfile profile = userService.getMyTechOfficerProfile();
        if (profile == null) {
            showStatus("Could not load profile from server.", false);
            // Keep user id from session so updates can still proceed when profile fetch fails.
            userIdField.setText(profileUserId != null ? profileUserId : "");
            usernameTextField.setText(LoginController.username != null ? LoginController.username : "");
            roleField.setText(SessionManager.getRole() != null ? SessionManager.getRole() : "");
            departmentIdField.clear();
            emailField.clear();
            contactNumberField.clear();
            profilePictureField.clear();
            passwordField.clear();
            confirmPasswordField.clear();
            usernameLabel.setText(headerDisplayName(usernameTextField.getText()));
            updateSubtitle(roleField.getText(), departmentIdField.getText());
            setupAvatar(usernameLabel.getText(), null);
            return;
        }

        profileUserId = firstNonBlank(trimOrNull(profile.getUserId()), trimOrNull(SessionManager.getUserId()));
        userIdField.setText(profileUserId);
        usernameTextField.setText(safeText(profile.getUsername()));
        String role = safeText(profile.getRole());
        if (role.isEmpty()) {
            role = safeText(SessionManager.getRole());
        }
        roleField.setText(role);
        departmentIdField.setText(safeText(profile.getDepartmentId()));
        emailField.setText(safeText(profile.getEmail()));
        contactNumberField.setText(safeText(profile.getContactNumber()));
        profilePictureField.setText(normalizePicturePath(profile.getProfilePicture()));
        passwordField.clear();
        confirmPasswordField.clear();

        usernameLabel.setText(headerDisplayName(usernameTextField.getText()));
        updateSubtitle(role, departmentIdField.getText());

        setupAvatar(usernameLabel.getText(), profilePictureField.getText());
        hideStatus();
    }

    private void setupAvatar(String username, String profilePicture) {
        if (profilePicture != null && !profilePicture.isBlank()) {
            try {
                File file = new File(profilePicture);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    profileImage.setImage(image);
                    profileImage.setClip(new Circle(34, 34, 34));
                    profileImage.setVisible(true);
                    profileInitial.setVisible(false);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        profileInitial.setText(
                username != null && !username.isBlank()
                        ? username.substring(0, 1).toUpperCase()
                        : "T"
        );
        profileImage.setVisible(false);
        profileInitial.setVisible(true);
    }

    @FXML
    private void browseProfilePicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(profilePictureField.getScene().getWindow());
        if (file != null) {
            profilePictureField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void saveProfile() {
        profileUserId = resolveProfileUserId();
        if (profileUserId == null || profileUserId.isBlank()) {
            profileUserId = extractUserIdFromToken(SessionManager.getToken());
        }
        if (profileUserId == null || profileUserId.isBlank()) {
            TechOfficerProfile latest = userService.getMyTechOfficerProfile();
            if (latest != null) {
                profileUserId = trimOrNull(latest.getUserId());
            }
        }
        if (profileUserId == null || profileUserId.isBlank()) {
            showStatus("Invalid session. Log in again.", false);
            return;
        }

        String username = usernameTextField.getText() == null ? "" : usernameTextField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String contactNumber = contactNumberField.getText() == null ? "" : contactNumberField.getText().trim();
        String profilePicture = profilePictureField.getText() == null ? "" : profilePictureField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText() == null ? "" : confirmPasswordField.getText().trim();

        if (username.isEmpty()) {
            showStatus("Username is required.", false);
            return;
        }
        if (email.isEmpty()) {
            showStatus("Email is required.", false);
            return;
        }
        if (contactNumber.isEmpty()) {
            showStatus("Contact number is required.", false);
            return;
        }
        if (!password.isEmpty() && !password.equals(confirmPassword)) {
            showStatus("Passwords do not match.", false);
            return;
        }

        TechOfficerProfile p = new TechOfficerProfile();
        p.setUserId(profileUserId);
        p.setUsername(username);
        p.setEmail(email);
        p.setPassword(password.isEmpty() ? null : password);
        p.setContactNumber(contactNumber);
        p.setDepartmentId(trimOrNull(departmentIdField.getText()));
        p.setProfilePicture(profilePicture.isEmpty() ? null : profilePicture);

        boolean ok = userService.updateTechOfficerProfile(p);
        if (ok) {
            LoginController.username = username;
            passwordField.clear();
            confirmPasswordField.clear();
            usernameLabel.setText(headerDisplayName(username));
            updateSubtitle(roleField.getText(), departmentIdField.getText());
            setupAvatar(username, profilePictureField.getText());
            showStatus("Profile updated successfully.", true);
        } else {
            showStatus("Failed to update profile.", false);
        }
    }

    @FXML
    private void goBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/techOfficerDashboard.fxml"));
            Stage stage = (Stage) userIdField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showStatus("Failed to load dashboard.", false);
        }
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a;
        }
        if (b != null && !b.isBlank()) {
            return b;
        }
        return null;
    }

    private String resolveProfileUserId() {
        String fromProfile = trimOrNull(profileUserId);
        String fromSession = trimOrNull(SessionManager.getUserId());
        String fromLogin = trimOrNull(LoginController.userId);
        String fromField = trimOrNull(userIdField.getText());

        String resolved = firstNonBlank(fromProfile, fromSession);
        resolved = firstNonBlank(resolved, fromLogin);
        resolved = firstNonBlank(resolved, fromField);
        return resolved;
    }

    private static String headerDisplayName(String usernameFromForm) {
        String u = safeText(usernameFromForm);
        if (!u.isEmpty()) {
            return u;
        }
        u = safeText(LoginController.username);
        return u.isEmpty() ? "—" : u;
    }

    private void updateSubtitle(String role, String dept) {
        String r = role == null ? "" : role.trim();
        String d = dept == null ? "" : dept.trim();
        String sub;
        if (r.isEmpty() && d.isEmpty()) {
            sub = safeText(SessionManager.getRole());
        } else if (d.isEmpty()) {
            sub = r;
        } else if (r.isEmpty()) {
            sub = d;
        } else {
            sub = r + " · " + d;
        }
        subtitleLabel.setText(sub.isEmpty() ? "—" : sub);
    }

    private static String safeText(String s) {
        if (s == null || s.isBlank()) {
            return "";
        }
        if ("null".equalsIgnoreCase(s.trim())) {
            return "";
        }
        return s;
    }

    private static String normalizePicturePath(String s) {
        String t = safeText(s);
        return t;
    }

    private static String trimOrNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private String extractUserIdFromToken(String token) {
        try {
            if (token == null || token.isBlank()) {
                return null;
            }
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return null;
            }
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            String marker = "\"userId\"";
            int key = payloadJson.indexOf(marker);
            if (key < 0) {
                return null;
            }
            int colon = payloadJson.indexOf(':', key + marker.length());
            if (colon < 0) {
                return null;
            }
            int firstQuote = payloadJson.indexOf('"', colon + 1);
            if (firstQuote < 0) {
                return null;
            }
            int secondQuote = payloadJson.indexOf('"', firstQuote + 1);
            if (secondQuote < 0) {
                return null;
            }
            String userId = payloadJson.substring(firstQuote + 1, secondQuote).trim();
            return userId.isEmpty() ? null : userId;
        } catch (Exception ignored) {
            return null;
        }
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle(success
                ? "-fx-text-fill: #28a745; -fx-font-size: 12px; -fx-font-weight: bold;"
                : "-fx-text-fill: #ff6b6b; -fx-font-size: 12px; -fx-font-weight: bold;");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }

    private void hideStatus() {
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
        statusLabel.setText("");
    }
}
