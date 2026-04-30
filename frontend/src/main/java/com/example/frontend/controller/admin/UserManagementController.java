package com.example.frontend.controller.admin;

import com.example.frontend.dto.UserResponseDTO;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.UserService;
import com.example.frontend.session.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserManagementController implements Initializable {

    @FXML private Label adminNameLabel;
    @FXML private Label totalUsersInfoLabel;
    @FXML private Label statusLabel;
    @FXML private Label statusBarTime;

    @FXML private ComboBox<String> roleFilterBox;
    @FXML private TextField searchField;

    @FXML private TableView<UserResponseDTO> tblUsers;
    @FXML private TableColumn<UserResponseDTO, String> colUserId;
    @FXML private TableColumn<UserResponseDTO, String> colUsername;
    @FXML private TableColumn<UserResponseDTO, String> colEmail;
    @FXML private TableColumn<UserResponseDTO, String> colRole;
    @FXML private TableColumn<UserResponseDTO, String> colContactNo;

    @FXML private Label avatarLabel;
    @FXML private Label detailUsernameLabel;
    @FXML private Label detailRoleBadge;
    @FXML private Label detailUserIdLabel;
    @FXML private Label detailEmailLabel;
    @FXML private Label detailContactLabel;
    @FXML private Label detailProfilePictureLabel;

    @FXML private ImageView avatarImage;
    @FXML private Circle avatarCircle;

    @FXML private Button updateUserButton;
    @FXML private Button deleteUserButton;

    private UserService userService;
    private UserResponseDTO selectedUser;

    private final ObservableList<UserResponseDTO> masterList =
            FXCollections.observableArrayList();

    private FilteredList<UserResponseDTO> filteredList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userService = new UserService(ServerClient.getInstance());

        statusBarTime.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM yyyy")));
        adminNameLabel.setText(LoginController.username);

        setupTableColumns();
        setupRoleFilter();
        setupActionButtonsVisibility();
        setupSelectionListener();

        loadUsers();
    }

    private boolean isAdminUser() {
        return "Admin".equalsIgnoreCase(SessionManager.getRole());
    }

    private void setupActionButtonsVisibility() {
        boolean admin = isAdminUser();

        updateUserButton.setVisible(admin);
        updateUserButton.setManaged(admin);
        deleteUserButton.setVisible(admin);
        deleteUserButton.setManaged(admin);

        updateUserButton.setDisable(true);
        deleteUserButton.setDisable(true);
    }

    private void setupTableColumns() {
        colUserId.setCellValueFactory(cellData ->
                new SimpleStringProperty(valueOrDash(cellData.getValue().getUserId())));

        colUsername.setCellValueFactory(cellData ->
                new SimpleStringProperty(valueOrDash(cellData.getValue().getUsername())));

        colEmail.setCellValueFactory(cellData ->
                new SimpleStringProperty(valueOrDash(cellData.getValue().getEmail())));

        colRole.setCellValueFactory(cellData ->
                new SimpleStringProperty(valueOrDash(cellData.getValue().getRole())));

        colContactNo.setCellValueFactory(cellData ->
                new SimpleStringProperty(valueOrDash(cellData.getValue().getContactNo())));
    }

    private void setupRoleFilter() {
        if (Objects.equals(SessionManager.getRole(), "Lecturer")) {
            roleFilterBox.setItems(FXCollections.observableArrayList("Student"));
            roleFilterBox.setValue("Student");
        } else {
            roleFilterBox.setItems(FXCollections.observableArrayList(
                    "All", "Admin", "Dean", "Lecturer", "Student", "Tech_Officer"
            ));
            roleFilterBox.setValue("All");
        }
    }

    private void setupSelectionListener() {
        tblUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            selectedUser = selected;

            if (selectedUser != null) {
                showUserDetails(selectedUser);

                if (isAdminUser()) {
                    updateUserButton.setDisable(false);
                    deleteUserButton.setDisable(false);
                }
            } else {
                clearDetails();

                if (isAdminUser()) {
                    updateUserButton.setDisable(true);
                    deleteUserButton.setDisable(true);
                }
            }
        });
    }

    @FXML
    public void refreshUsers() {
        loadUsers();
    }

    private void loadUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();

        if (users == null) {
            users = List.of();
        }

        if (Objects.equals(SessionManager.getRole(), "Lecturer")) {
            users = users.stream()
                    .filter(u -> "Student".equalsIgnoreCase(u.getRole()))
                    .toList();
        }

        masterList.setAll(users);

        filteredList = new FilteredList<>(masterList, p -> true);
        tblUsers.setItems(filteredList);

        totalUsersInfoLabel.setText(users.size() + " users loaded");
        statusLabel.setText("Users loaded successfully");

        selectedUser = null;
        clearDetails();

        if (isAdminUser()) {
            updateUserButton.setDisable(true);
            deleteUserButton.setDisable(true);
        }
    }

    @FXML
    public void filterUsers() {
        if (filteredList == null) return;

        String selectedRole = roleFilterBox.getValue();
        String searchText = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase();

        filteredList.setPredicate(user -> {
            boolean roleMatches =
                    selectedRole == null
                            || selectedRole.equals("All")
                            || valueOrDash(user.getRole()).equalsIgnoreCase(selectedRole);

            boolean searchMatches =
                    searchText.isEmpty()
                            || valueOrDash(user.getUsername()).toLowerCase().contains(searchText)
                            || valueOrDash(user.getEmail()).toLowerCase().contains(searchText);

            return roleMatches && searchMatches;
        });

        totalUsersInfoLabel.setText(filteredList.size() + " users shown");
        statusLabel.setText("Filter applied");

        selectedUser = null;
        clearDetails();

        if (isAdminUser()) {
            updateUserButton.setDisable(true);
            deleteUserButton.setDisable(true);
        }
    }

    private void showUserDetails(UserResponseDTO user) {
        detailUsernameLabel.setText(valueOrDash(user.getUsername()));
        detailRoleBadge.setText(valueOrDash(user.getRole()));
        detailUserIdLabel.setText(valueOrDash(user.getUserId()));
        detailEmailLabel.setText(valueOrDash(user.getEmail()));
        detailContactLabel.setText(valueOrDash(user.getContactNo()));
        detailProfilePictureLabel.setText(valueOrDash(user.getProfilePicture()));

        loadAvatar(user);

        statusLabel.setText("Selected user: " + valueOrDash(user.getUsername()));
    }

    private void clearDetails() {
        detailUsernameLabel.setText("Select a user");
        detailRoleBadge.setText("Role");
        detailUserIdLabel.setText("—");
        detailEmailLabel.setText("—");
        detailContactLabel.setText("—");
        detailProfilePictureLabel.setText("—");

        avatarImage.setImage(null);
        avatarImage.setVisible(false);
        avatarLabel.setText("U");
        avatarLabel.setVisible(true);
        avatarCircle.setVisible(true);
    }

    @FXML
    private void updateSelectedUser() {
        if (!isAdminUser()) {
            statusLabel.setText("Only admin can update users.");
            return;
        }

        if (selectedUser == null) {
            statusLabel.setText("Please select a user first.");
            return;
        }

        Dialog<UserResponseDTO> dialog = new Dialog<>();

        Stage ownerStage = (Stage) tblUsers.getScene().getWindow();
        dialog.initOwner(ownerStage);
        dialog.initModality(Modality.WINDOW_MODAL);

        dialog.setTitle("Update User");
        dialog.setHeaderText("Update user: " + selectedUser.getUsername());

        ButtonType updateButtonType =
                new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        TextField usernameField = new TextField(valueOrEmpty(selectedUser.getUsername()));
        TextField emailField = new TextField(valueOrEmpty(selectedUser.getEmail()));
        TextField contactField = new TextField(valueOrEmpty(selectedUser.getContactNo()));
        TextField profileField = new TextField(valueOrEmpty(selectedUser.getProfilePicture()));

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.setItems(FXCollections.observableArrayList(
                "Admin", "Dean", "Lecturer", "Student", "Tech_Officer"
        ));
        roleBox.setValue(valueOrEmpty(selectedUser.getRole()));

        VBox form = new VBox(10);
        form.setPadding(new Insets(12));
        form.getChildren().addAll(
                new Label("Username"), usernameField,
                new Label("Email"), emailField,
                new Label("Contact Number"), contactField,
                new Label("Role"), roleBox,
                new Label("Profile Picture Path"), profileField
        );

        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(button -> {
            if (button == updateButtonType) {
                UserResponseDTO updatedUser = new UserResponseDTO();

                updatedUser.setUserId(selectedUser.getUserId());
                updatedUser.setUsername(usernameField.getText().trim());
                updatedUser.setEmail(emailField.getText().trim());
                updatedUser.setContactNo(contactField.getText().trim());
                updatedUser.setRole(roleBox.getValue());
                updatedUser.setProfilePicture(profileField.getText().trim());

                return updatedUser;
            }
            return null;
        });

        Optional<UserResponseDTO> result = dialog.showAndWait();

        result.ifPresent(updatedUser -> {
            boolean updated = userService.updateUser(updatedUser);

            if (updated) {
                statusLabel.setText("User updated successfully.");
                loadUsers();
            } else {
                statusLabel.setText("Failed to update user.");
            }
        });
    }

    @FXML
    private void deleteSelectedUser() {
        if (!isAdminUser()) {
            statusLabel.setText("Only admin can delete users.");
            return;
        }

        if (selectedUser == null) {
            statusLabel.setText("Please select a user first.");
            return;
        }

        if (Objects.equals(selectedUser.getUserId(), LoginController.userId)) {
            statusLabel.setText("You cannot delete your own account.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);

        Stage ownerStage = (Stage) tblUsers.getScene().getWindow();
        confirm.initOwner(ownerStage);
        confirm.initModality(Modality.WINDOW_MODAL);

        confirm.setTitle("Delete User");
        confirm.setHeaderText("Delete selected user?");
        confirm.setContentText(
                "Are you sure you want to delete:\n\n" +
                        selectedUser.getUsername() + " (" + selectedUser.getUserId() + ")"
        );

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = userService.deleteUser(selectedUser.getUserId());

            if (deleted) {
                statusLabel.setText("User deleted successfully.");
                loadUsers();
            } else {
                statusLabel.setText("Failed to delete user.");
            }
        }
    }

    private void loadAvatar(UserResponseDTO user) {
        String imagePath = user.getProfilePicture();

        if (imagePath == null || imagePath.isBlank()) {
            showDefaultAvatar(user);
            return;
        }

        try {
            String finalPath = imagePath;

            if (!imagePath.startsWith("http://")
                    && !imagePath.startsWith("https://")
                    && !imagePath.startsWith("file:")) {

                File file = new File(imagePath);

                if (!file.exists()) {
                    showDefaultAvatar(user);
                    return;
                }

                finalPath = file.toURI().toString();
            }

            Image image = new Image(finalPath, false);

            if (image.isError()) {
                showDefaultAvatar(user);
                return;
            }

            avatarImage.setImage(image);
            avatarImage.setVisible(true);
            avatarLabel.setVisible(false);
            avatarCircle.setVisible(false);

        } catch (Exception e) {
            e.printStackTrace();
            showDefaultAvatar(user);
        }
    }

    private void showDefaultAvatar(UserResponseDTO user) {
        String username = valueOrDash(user.getUsername());
        avatarLabel.setText(username.equals("—") ? "U" : username.substring(0, 1).toUpperCase());

        avatarImage.setImage(null);
        avatarImage.setVisible(false);

        avatarLabel.setVisible(true);
        avatarCircle.setVisible(true);
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }

    private String valueOrEmpty(String value) {
        return value == null || value.equals("—") ? "" : value;
    }

    @FXML
    private void openAddUser() {
        if (isAdminUser()) {
            loadView("admin/createUser.fxml");
        } else {
            statusLabel.setText("Only admin can add users.");
        }
    }

    @FXML
    public void goBack() {
        try {
            String fxml = Objects.equals(SessionManager.getRole(), "Lecturer")
                    ? "/view/lecturer/lecturerDashboard.fxml"
                    : "/view/admin/AdminDashboard.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Stage stage = (Stage) adminNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to go back.");
        }
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) tblUsers.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to load view: " + fxmlFile);
        }
    }
}