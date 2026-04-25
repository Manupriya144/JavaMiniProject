package com.example.frontend.controller.student;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.dto.CourseAllResponseDTO;
import com.example.frontend.network.ServerClient;
import com.example.frontend.service.CourseService;
import com.example.frontend.service.GradeService;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class StudentCoursesController implements Initializable {

    @FXML private Label subtitleLabel;
    @FXML private Label studentNameLabel;
    @FXML private Label studentIdLabel;
    @FXML private Label profileInitial;
    @FXML private Label infoLabel;
    @FXML private Label headerCoursesLabel;
    @FXML private GridPane timetableGrid;

    private final CourseService courseService = new CourseService(ServerClient.getInstance());
    private final GradeService gradeService = new GradeService(ServerClient.getInstance());

    private List<CourseAllResponseDTO> loadedCourses = new ArrayList<>();
    private final Map<String, String> gradesByCourseId = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String name = LoginController.username;
        String regNo = LoginController.reNo;

        studentNameLabel.setText(name == null ? "Student" : name);
        studentIdLabel.setText("Reg No: " + (regNo == null ? "—" : regNo));

        if (name != null && !name.isBlank()) {
            profileInitial.setText(name.substring(0, 1).toUpperCase(Locale.ROOT));
        } else {
            profileInitial.setText("S");
        }

        loadCourses();
    }

    private void loadCourses() {
        List<CourseAllResponseDTO> courses = courseService.getStudentCourses(LoginController.userId);
        loadedCourses = courses != null ? new ArrayList<>(courses) : new ArrayList<>();

        gradesByCourseId.clear();
        gradesByCourseId.putAll(fetchGradesByCourseId(LoginController.userId));

        updateHeaderCoursesAndGrades(loadedCourses);
        rebuildTimetableGrid();
    }

    private void updateHeaderCoursesAndGrades(List<CourseAllResponseDTO> courses) {
        if (headerCoursesLabel == null) {
            return;
        }
        if (courses == null || courses.isEmpty()) {
            headerCoursesLabel.setText("No enrolled courses for this level.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < courses.size(); i++) {
            if (i > 0) {
                sb.append("  ·  ");
            }
            CourseAllResponseDTO c = courses.get(i);
            String code = safe(c.getCourseCode());
            if (code.isEmpty()) {
                code = "—";
            }
            String grade = resolveGradeForCourse(c);
            sb.append(code).append(" (").append(grade).append(")");
        }
        String full = sb.toString();
        headerCoursesLabel.setText(full.length() > 220 ? full.substring(0, 217) + "…" : full);
    }

    private String resolveGradeForCourse(CourseAllResponseDTO c) {
        String cid = c.getCourseId();
        if (cid != null && !cid.isBlank()) {
            String g = gradesByCourseId.get(cid.trim());
            if (g != null && !g.isBlank()) {
                return g;
            }
        }
        String code = c.getCourseCode();
        if (code != null && !code.isBlank()) {
            String g = gradesByCourseId.get(code.trim());
            if (g != null && !g.isBlank()) {
                return g;
            }
        }
        return "—";
    }

    private Map<String, String> fetchGradesByCourseId(String studentId) {
        Map<String, String> map = new HashMap<>();
        if (studentId == null || studentId.isBlank()) {
            return map;
        }
        try {
            JsonNode root = gradeService.getStudentGrades(studentId);
            if (root == null || root.isNull()) {
                return map;
            }
            ingestGradeNodes(map, root.path("data"));
            ingestGradeNodes(map, root.path("grades"));
            if (root.isArray()) {
                ingestGradeNodes(map, root);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private void ingestGradeNodes(Map<String, String> map, JsonNode node) {
        if (node == null || !node.isArray()) {
            return;
        }
        for (JsonNode n : node) {
            if (!n.isObject()) {
                continue;
            }
            String courseId = firstText(n, "courseId", "course_id", "courseID");
            String courseCode = firstText(n, "courseCode", "course_code", "code");
            String grade = firstText(n,
                    "grade", "letterGrade", "letter_grade", "finalGrade");
            if (grade == null || grade.isBlank()) {
                continue;
            }
            String g = grade.trim();
            if (courseId != null && !courseId.isBlank()) {
                map.putIfAbsent(courseId.trim(), g);
            }
            if (courseCode != null && !courseCode.isBlank()) {
                map.putIfAbsent(courseCode.trim(), g);
            }
        }
    }

    private String firstText(JsonNode n, String... fieldNames) {
        for (String f : fieldNames) {
            if (n.has(f) && !n.get(f).isNull()) {
                JsonNode v = n.get(f);
                if (v.isTextual()) {
                    return v.asText();
                }
                if (v.isNumber()) {
                    return v.asText();
                }
            }
        }
        return null;
    }

    private void rebuildTimetableGrid() {
        timetableGrid.getChildren().clear();

        if (loadedCourses.isEmpty()) {
            infoLabel.setText("No courses found for your department & level.");
            timetableGrid.add(buildEmptyCard("No courses to show"), 0, 0);
            return;
        }

        infoLabel.setText("Showing " + loadedCourses.size() + " course(s) for your current level.");

        List<CourseAllResponseDTO> sem1 = new ArrayList<>();
        List<CourseAllResponseDTO> sem2 = new ArrayList<>();

        for (CourseAllResponseDTO c : loadedCourses) {
            if (isSemester1(c.getSemester())) {
                sem1.add(c);
            } else if (isSemester2(c.getSemester())) {
                sem2.add(c);
            } else {
                sem1.add(c);
            }
        }

        timetableGrid.add(buildHeader("Semester 1"), 0, 0);
        timetableGrid.add(buildHeader("Semester 2"), 1, 0);

        timetableGrid.add(buildSemesterColumn(sem1), 0, 1);
        timetableGrid.add(buildSemesterColumn(sem2), 1, 1);
    }

    private VBox buildHeader(String text) {
        VBox box = new VBox(0);
        box.setStyle(
                "-fx-background-color: #f5f9ff;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #d7e7f8;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;"
        );
        box.setPadding(new Insets(12, 14, 12, 14));

        Label title = new Label(text);
        title.setStyle("-fx-text-fill: #4c8fce; -fx-font-size: 13px; -fx-font-weight: bold;");

        box.getChildren().add(title);
        return box;
    }

    private VBox buildSemesterColumn(List<CourseAllResponseDTO> list) {
        VBox col = new VBox(10);
        col.setPadding(new Insets(6, 0, 0, 0));

        if (list == null || list.isEmpty()) {
            col.getChildren().add(buildEmptyCard("No courses"));
            return col;
        }

        for (CourseAllResponseDTO c : list) {
            col.getChildren().add(buildCourseBlock(c));
        }

        return col;
    }

    private VBox buildCourseBlock(CourseAllResponseDTO c) {
        VBox card = new VBox(6);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: #d7e7f8;" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(91,159,217,0.14), 12, 0, 0, 4);"
        );
        card.setPadding(new Insets(14, 14, 14, 14));

        Label code = new Label(safe(c.getCourseCode()));
        code.setStyle("-fx-text-fill: #4c8fce; -fx-font-size: 12px; -fx-font-weight: bold;");

        Label name = new Label(safe(c.getName()));
        name.setWrapText(true);
        name.setStyle("-fx-text-fill: #1a3a52; -fx-font-size: 14px; -fx-font-weight: bold;");

        HBox meta = new HBox(10);
        Label credits = new Label("Credits: " + c.getCourseCredit());
        credits.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 11px;");

        Label level = new Label("Level: " + c.getAcademicLevel());
        level.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 11px;");

        meta.getChildren().addAll(credits, level);

        card.getChildren().addAll(code, name, meta);

        return card;
    }

    private VBox buildEmptyCard(String text) {
        VBox card = new VBox(0);
        card.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: #e8eef5;" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-width: 1;"
        );
        card.setPadding(new Insets(14, 14, 14, 14));

        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #8fa3b8; -fx-font-size: 12px;");
        card.getChildren().add(lbl);
        return card;
    }

    private boolean isSemester1(String sem) {
        if (sem == null) return false;
        String s = sem.trim().toLowerCase(Locale.ROOT);
        return s.equals("1") || s.contains("semester 1") || s.contains("sem 1") || s.contains("first") || s.equals("i");
    }

    private boolean isSemester2(String sem) {
        if (sem == null) return false;
        String s = sem.trim().toLowerCase(Locale.ROOT);
        return s.equals("2") || s.contains("semester 2") || s.contains("sem 2") || s.contains("second") || s.equals("ii");
    }

    private String safe(String v) {
        return v == null ? "" : v;
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/student/studentDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) timetableGrid.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

