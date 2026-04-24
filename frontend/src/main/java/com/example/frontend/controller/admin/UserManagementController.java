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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
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

    private UserService userService;
    private final ObservableList<UserResponseDTO> masterList = FXCollections.observableArrayList();
    private FilteredList<UserResponseDTO> filteredList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userService = new UserService(ServerClient.getInstance());

        statusBarTime.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM yyyy")));
        adminNameLabel.setText(LoginController.username);

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

        tblUsers.setRowFactory(tv -> {
            TableRow<UserResponseDTO> row = new TableRow<>() {
                @Override
                protected void updateItem(UserResponseDTO item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setStyle("-fx-background-color: transparent;");
                    } else if (isSelected()) {
                        setStyle("-fx-background-color: #e8f0ff; -fx-background-insets: 2 0 2 0;");
                    } else {
                        setStyle("-fx-background-color: #ffffff; -fx-background-insets: 2 0 2 0;");
                    }
                }
            };

            row.hoverProperty().addListener((obs, oldVal, isHovering) -> {
                if (!row.isEmpty() && !row.isSelected()) {
                    if (isHovering) {
                        row.setStyle("-fx-background-color: #f5f9ff; -fx-background-insets: 2 0 2 0;");
                    } else {
                        row.setStyle("-fx-background-color: #ffffff; -fx-background-insets: 2 0 2 0;");
                    }
                }
            });

            row.selectedProperty().addListener((obs, oldVal, selected) -> {
                if (!row.isEmpty()) {
                    if (selected) {
                        row.setStyle("-fx-background-color: #e8f0ff; -fx-background-insets: 2 0 2 0;");
                    } else if (row.isHover()) {
                        row.setStyle("-fx-background-color: #f5f9ff; -fx-background-insets: 2 0 2 0;");
                    } else {
                        row.setStyle("-fx-background-color: #ffffff; -fx-background-insets: 2 0 2 0;");
                    }
                }
            });

            return row;
        });

        styleTableColumns();
        if(Objects.equals(SessionManager.getRole(), "Lecturer")){
            roleFilterBox.setItems(FXCollections.observableArrayList(
                    "Student"
            ));
            roleFilterBox.setValue("Student");
        }
        else{
            roleFilterBox.setItems(FXCollections.observableArrayList(
                    "All", "Admin", "Dean", "Lecturer", "Student", "Tech_Officer"
            ));
            roleFilterBox.setValue("All");
        }



        loadUsers();

        tblUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selectedUser) -> {
            if (selectedUser != null) {
                showUserDetails(selectedUser);
            }
        });
    }

    private void styleTableColumns() {
        colUserId.setStyle("-fx-alignment: CENTER-LEFT; -fx-text-fill: #1a3a52; -fx-font-size: 12px;");
        colUsername.setStyle("-fx-alignment: CENTER-LEFT; -fx-text-fill: #1a3a52; -fx-font-size: 12px;");
        colEmail.setStyle("-fx-alignment: CENTER-LEFT; -fx-text-fill: #1a3a52; -fx-font-size: 12px;");
        colRole.setStyle("-fx-alignment: CENTER; -fx-text-fill: #1a3a52; -fx-font-size: 12px;");
        colContactNo.setStyle("-fx-alignment: CENTER-LEFT; -fx-text-fill: #1a3a52; -fx-font-size: 12px;");
    }

    @FXML
    public void refreshUsers() {
        loadUsers();
    }

    private void loadUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();

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

        clearDetailsIfEmpty();
    }

    @FXML
    public void filterUsers() {
        String selectedRole = roleFilterBox.getValue();
        String searchText = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();

        if (filteredList == null) return;

        filteredList.setPredicate(user -> {
            boolean roleMatches = selectedRole == null
                    || selectedRole.equals("All")
                    || valueOrDash(user.getRole()).equalsIgnoreCase(selectedRole);

            boolean searchMatches = searchText.isEmpty()
                    || valueOrDash(user.getUsername()).toLowerCase().contains(searchText)
                    || valueOrDash(user.getEmail()).toLowerCase().contains(searchText);

            return roleMatches && searchMatches;
        });

        totalUsersInfoLabel.setText(filteredList.size() + " users shown");
        statusLabel.setText("Filter applied");
    }

    private void showUserDetails(UserResponseDTO user) {
        detailUsernameLabel.setText(valueOrDash(user.getUsername()));
        detailRoleBadge.setText(valueOrDash(user.getRole()));
        detailUserIdLabel.setText(valueOrDash(user.getUserId()));
        detailEmailLabel.setText(valueOrDash(user.getEmail()));
        detailContactLabel.setText(valueOrDash(user.getContactNo()));
        detailProfilePictureLabel.setText(valueOrDash(user.getProfilePicture()));

        loadAvatar(user);

        statusLabel.setText("Viewing details for " + valueOrDash(user.getUsername()));
    }

    private void loadAvatar(UserResponseDTO user) {
        String imagePath = user.getProfilePicture();
        System.out.println("Profile path: " + imagePath);

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
                    System.out.println("Profile image file not found: " + imagePath);
                    showDefaultAvatar(user);
                    return;
                }

                finalPath = file.toURI().toString();
            }

            System.out.println("Converted URI: " + finalPath);

            Image image = new Image(finalPath, false);

            if (image.isError()) {
                System.out.println("Error loading profile image.");
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

    private void clearDetailsIfEmpty() {
        if (masterList.isEmpty()) {
            detailUsernameLabel.setText("No users found");
            detailRoleBadge.setText("Role");
            detailUserIdLabel.setText("—");
            detailEmailLabel.setText("—");
            detailContactLabel.setText("—");
            detailProfilePictureLabel.setText("—");
            avatarLabel.setText("U");
            avatarImage.setImage(null);
            avatarImage.setVisible(false);
            avatarLabel.setVisible(true);
            avatarCircle.setVisible(true);
            statusLabel.setText("No user records available");
        }
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }

    @FXML
    private void openAddUser() {
        loadView("admin/createUser.fxml");
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/AdminDashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) adminNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();

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