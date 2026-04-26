package com.example.frontend.service;

import com.example.frontend.model.CAMark;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CAMarkService {
    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private String lastMessage = "";

    public CAMarkService(ServerClient client) {
        this.client = client;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public CAMark uploadCAMarks(String studentId, Integer assessmentTypeId, Double marks) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("student_id", studentId);
            data.put("assessment_type_id", assessmentTypeId);
            data.put("marks", marks);
            JsonNode node = send("UploadCAMarks", data);
            if (node != null && node.path("success").asBoolean(false) && node.has("data")) {
                lastMessage = node.path("message").asText("CA mark uploaded");
                return mapper.convertValue(node.get("data"), CAMark.class);
            }
            lastMessage = node == null ? "No response from server" : node.path("message").asText("Upload failed");
        } catch (Exception e) {
            lastMessage = "Upload failed: " + (e.getMessage() == null ? "error" : e.getMessage());
        }
        return null;
    }

    public boolean updateCAMarks(Integer markId, Double marks) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("mark_id", markId);
            data.put("marks", marks);
            JsonNode node = send("UpdateCAMarks", data);
            if (node != null) {
                lastMessage = node.path("message").asText("Update failed");
                return node.path("success").asBoolean(false);
            }
        } catch (Exception e) {
            lastMessage = "Update failed: " + (e.getMessage() == null ? "error" : e.getMessage());
        }
        return false;
    }

    public List<CAMark> getStudentCAMarks(String studentId, String courseId) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("student_id", studentId);
            data.put("course_id", courseId);
            JsonNode node = send("GetStudentCAMarks", data);
            return readCAList(node, "Failed to load student CA marks");
        } catch (Exception e) {
            lastMessage = "Failed to load student CA marks: " + (e.getMessage() == null ? "error" : e.getMessage());
            return List.of();
        }
    }

    public List<CAMark> getBatchCAMarks(String batch, String courseId) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("batch", batch);
            data.put("course_id", courseId);
            JsonNode node = send("GetBatchCAMarks", data);
            return readCAList(node, "Failed to load batch CA marks");
        } catch (Exception e) {
            lastMessage = "Failed to load batch CA marks: " + (e.getMessage() == null ? "error" : e.getMessage());
            return List.of();
        }
    }

    public JsonNode checkCAEligibility(String studentId, String courseId) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("student_id", studentId);
            data.put("course_id", courseId);
            return send("CheckCAEligibility", data);
        } catch (Exception e) {
            lastMessage = "Failed to check CA eligibility: " + (e.getMessage() == null ? "error" : e.getMessage());
            return null;
        }
    }

    public JsonNode getBatchCAEligibilityReport(String batch, String courseId) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("batch", batch);
            data.put("course_id", courseId);
            return send("GetBatchCAEligibilityReport", data);
        } catch (Exception e) {
            lastMessage = "Failed to load CA eligibility report: " + (e.getMessage() == null ? "error" : e.getMessage());
            return null;
        }
    }

    public JsonNode getCourseCAReference(String courseId) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("course_id", courseId);
            JsonNode node = send("GetCourseCAReference", data);
            if (node != null) {
                lastMessage = node.path("message").asText("Course CA reference loaded");
            } else {
                lastMessage = "No response from server";
            }
            return node;
        } catch (Exception e) {
            lastMessage = "Failed to load course CA reference: " + (e.getMessage() == null ? "error" : e.getMessage());
            return null;
        }
    }

    private List<CAMark> readCAList(JsonNode node, String defaultError) {
        List<CAMark> list = new ArrayList<>();
        if (node == null) {
            lastMessage = "No response from server";
            return list;
        }
        if (!node.path("success").asBoolean(false) || !node.has("data") || !node.get("data").isArray()) {
            lastMessage = node.path("message").asText(defaultError);
            return list;
        }
        for (JsonNode row : node.get("data")) {
            list.add(mapper.convertValue(row, CAMark.class));
        }
        lastMessage = node.path("message").asText("Success");
        return list;
    }

    private JsonNode send(String command, Map<String, Object> data) throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("command", command);
        request.put("data", data);
        request.put("token", SessionManager.getToken());
        String json = mapper.writeValueAsString(request);
        String response = client.sendRequest(json);
        return mapper.readTree(response);
    }
}
