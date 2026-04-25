package com.example.frontend.controller.lecturer;

import com.example.frontend.network.ServerClient;
import com.example.frontend.service.GradeGPAService;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.util.*;

public class GradesGPAController {

    @FXML private TextField yearField;
    @FXML private ComboBox<String> levelBox;
    @FXML private ComboBox<String> semesterBox;
    @FXML private ComboBox<String> departmentBox;
    @FXML private TextField searchField;

    @FXML private TableView<Map<String, String>> gpaTable;
    @FXML private Label statusLabel;
    @FXML private Label summaryLabel;

    private GradeGPAService service;
    private final List<Map<String, String>> allRows = new ArrayList<>();

    @FXML
    public void initialize() {
        service = new GradeGPAService(ServerClient.getInstance());

        yearField.setText("2026");

        levelBox.setItems(FXCollections.observableArrayList("1", "2", "3", "4"));
        levelBox.setValue("2");

        semesterBox.setItems(FXCollections.observableArrayList("1", "2"));
        semesterBox.setValue("1");

        departmentBox.setItems(FXCollections.observableArrayList(
                "BST","ET", "ICT"
        ));
        departmentBox.setValue("ICT");

        hideStatus();
    }

    @FXML
    private void loadGPA() {
        try {
            String yearText = yearField.getText();

            if (yearText == null || yearText.trim().isEmpty()) {
                showError("Enter academic year.");
                return;
            }

            int year = Integer.parseInt(yearText.trim());
            int level = Integer.parseInt(levelBox.getValue());
            String semester = semesterBox.getValue();
            String departmentId = departmentBox.getValue();

            JsonNode response = service.loadGPAReport(year, level, semester, departmentId);

            JsonNode dataNode = response.has("data") ? response.path("data") : response;

            JsonNode coursesNode = dataNode.path("courses");
            JsonNode rowsNode = dataNode.path("rows");

            buildDynamicColumns(coursesNode);
            loadRows(rowsNode);

            if (allRows.isEmpty()) {
                showError("No GPA records found.");
            } else {
                showSuccess("GPA report loaded successfully.");
            }

            summaryLabel.setText(allRows.size() + " students loaded");

        } catch (NumberFormatException e) {
            showError("Academic year must be a number.");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load GPA report.");
        }
    }

    private void buildDynamicColumns(JsonNode coursesNode) {
        gpaTable.getColumns().clear();

        TableColumn<Map<String, String>, String> regNoCol = new TableColumn<>("Reg No");
        regNoCol.setPrefWidth(140);
        regNoCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getOrDefault("regNo", "")));

        TableColumn<Map<String, String>, String> nameCol = new TableColumn<>("Student Name");
        nameCol.setPrefWidth(180);
        nameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getOrDefault("studentName", "")));

        gpaTable.getColumns().addAll(regNoCol, nameCol);

        for (JsonNode courseNode : coursesNode) {
            String courseCode = courseNode.asText();

            TableColumn<Map<String, String>, String> courseCol = new TableColumn<>(courseCode);
            courseCol.setPrefWidth(110);
            courseCol.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().getOrDefault(courseCode, "-")));

            gpaTable.getColumns().add(courseCol);
        }

        TableColumn<Map<String, String>, String> creditsCol = new TableColumn<>("Credits");
        creditsCol.setPrefWidth(90);
        creditsCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getOrDefault("totalCredits", "0")));

        TableColumn<Map<String, String>, String> sgpaCol = new TableColumn<>("SGPA");
        sgpaCol.setPrefWidth(90);
        sgpaCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getOrDefault("sgpa", "0.00")));

        TableColumn<Map<String, String>, String> cgpaCol = new TableColumn<>("CGPA");
        cgpaCol.setPrefWidth(90);
        cgpaCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getOrDefault("cgpa", "0.00")));

        gpaTable.getColumns().addAll(creditsCol, sgpaCol, cgpaCol);
    }

    private void loadRows(JsonNode rowsNode) {
        allRows.clear();

        for (JsonNode rowNode : rowsNode) {
            Map<String, String> row = new LinkedHashMap<>();

            row.put("studentId", rowNode.path("studentId").asText(""));
            row.put("regNo", rowNode.path("regNo").asText(""));
            row.put("studentName", rowNode.path("studentName").asText(""));
            row.put("totalCredits", String.valueOf(rowNode.path("totalCredits").asInt(0)));
            row.put("sgpa", String.format("%.2f", rowNode.path("sgpa").asDouble(0)));
            row.put("cgpa", String.format("%.2f", rowNode.path("cgpa").asDouble(0)));

            JsonNode gradeMap = rowNode.path("courseGrades");
            Iterator<String> fields = gradeMap.fieldNames();

            while (fields.hasNext()) {
                String course = fields.next();
                row.put(course, gradeMap.path(course).asText("-"));
            }

            allRows.add(row);
        }

        gpaTable.setItems(FXCollections.observableArrayList(allRows));
    }

    @FXML
    private void saveSemesterResults() {
        try {
            if (allRows.isEmpty()) {
                showError("Load GPA data before saving.");
                return;
            }

            int year = Integer.parseInt(yearField.getText().trim());
            int level = Integer.parseInt(levelBox.getValue());
            String semester = semesterBox.getValue();
            String departmentId = departmentBox.getValue();

            boolean saved = service.saveSemesterResults(
                    year,
                    level,
                    semester,
                    departmentId,
                    allRows
            );

            if (saved) {
                showSuccess("Semester results saved successfully.");
            } else {
                showError("Failed to save semester results.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error while saving semester results.");
        }
    }

    @FXML
    private void exportPdf() {
        try {
            if (gpaTable.getItems().isEmpty()) {
                showError("No data to export.");
                return;
            }

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save GPA Report PDF");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            chooser.setInitialFileName("GPA_Report.pdf");

            File file = chooser.showSaveDialog(gpaTable.getScene().getWindow());
            if (file == null) return;

            try (PDDocument document = new PDDocument()) {

                PDRectangle landscape = new PDRectangle(
                        PDRectangle.A4.getHeight(),
                        PDRectangle.A4.getWidth()
                );

                PDPage page = new PDPage(landscape);
                document.addPage(page);

                PDPageContentStream content = new PDPageContentStream(document, page);

                float margin = 30;
                float y = page.getMediaBox().getHeight() - 40;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                float rowHeight = 22;
                float fontSize = 7;

                drawTitle(content, "FoT Portal - Grades and GPA Report", margin, y);
                y -= 28;

                drawText(content,
                        "Year: " + yearField.getText()
                                + " | Level: " + levelBox.getValue()
                                + " | Semester: " + semesterBox.getValue()
                                + " | Department: " + departmentBox.getValue(),
                        margin, y, 9);

                y -= 25;

                List<TableColumn<Map<String, String>, ?>> columns = gpaTable.getColumns();
                float colWidth = tableWidth / columns.size();

                // Header
                y = drawTableRow(content, columns, null, margin, y, colWidth, rowHeight, fontSize, true);

                // Rows
                for (Map<String, String> row : gpaTable.getItems()) {

                    if (y < 45) {
                        content.close();

                        page = new PDPage(landscape);
                        document.addPage(page);
                        content = new PDPageContentStream(document, page);

                        y = page.getMediaBox().getHeight() - 40;
                        y = drawTableRow(content, columns, null, margin, y, colWidth, rowHeight, fontSize, true);
                    }

                    y = drawTableRow(content, columns, row, margin, y, colWidth, rowHeight, fontSize, false);
                }

                content.close();
                document.save(file);
            }

            showSuccess("PDF table exported successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to export PDF.");
        }
    }
    private float drawTableRow(PDPageContentStream content,
                               List<TableColumn<Map<String, String>, ?>> columns,
                               Map<String, String> row,
                               float startX,
                               float y,
                               float colWidth,
                               float rowHeight,
                               float fontSize,
                               boolean header) throws Exception {

        float x = startX;

        for (TableColumn<Map<String, String>, ?> column : columns) {
            String text;

            if (header) {
                text = column.getText();
            } else {
                text = getCellValue(row, column.getText());
            }

            content.addRect(x, y - rowHeight, colWidth, rowHeight);
            content.stroke();

            content.beginText();
            content.setFont(header ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, fontSize);
            content.newLineAtOffset(x + 3, y - 14);
            content.showText(cleanPdfText(text, 13));
            content.endText();

            x += colWidth;
        }

        return y - rowHeight;
    }

    private String getCellValue(Map<String, String> row, String colName) {
        if (row == null) return "";

        if (colName.equals("Reg No")) return row.getOrDefault("regNo", "");
        if (colName.equals("Student Name")) return row.getOrDefault("studentName", "");
        if (colName.equals("Credits")) return row.getOrDefault("totalCredits", "");
        if (colName.equals("SGPA")) return row.getOrDefault("sgpa", "");
        if (colName.equals("CGPA")) return row.getOrDefault("cgpa", "");

        return row.getOrDefault(colName, "-");
    }

    private String cleanPdfText(String text, int limit) {
        if (text == null) return "";
        text = text.replace("\n", " ").replace("\r", " ");

        if (text.length() > limit) {
            return text.substring(0, limit);
        }

        return text;
    }

    private void drawTitle(PDPageContentStream content, String text, float x, float y) throws Exception {
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 14);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
    }




    private void drawText(PDPageContentStream content, String text, float x, float y, float size) throws Exception {
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, size);
        content.newLineAtOffset(x, y);
        content.showText(cleanPdfText(text, 100));
        content.endText();
    }

    @FXML
    private void filterTable() {
        String keyword = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            gpaTable.setItems(FXCollections.observableArrayList(allRows));
            summaryLabel.setText(allRows.size() + " students loaded");
            return;
        }

        List<Map<String, String>> filtered = allRows.stream()
                .filter(row ->
                        row.getOrDefault("regNo", "").toLowerCase().contains(keyword)
                                || row.getOrDefault("studentName", "").toLowerCase().contains(keyword)
                )
                .toList();

        gpaTable.setItems(FXCollections.observableArrayList(filtered));
        summaryLabel.setText(filtered.size() + " students shown");
    }

    @FXML
    private void clearFilters() {
        searchField.clear();
        gpaTable.setItems(FXCollections.observableArrayList(allRows));
        summaryLabel.setText(allRows.size() + " students loaded");
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/lecturer/lecturerDashboard.fxml")
            );

            Parent root = loader.load();
            statusLabel.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to go back.");
        }
    }

    private void showSuccess(String message) {
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
    }

    private void showError(String message) {
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
    }

    private void hideStatus() {
        statusLabel.setText("");
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
    }
}