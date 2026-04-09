package com.example.frontend.service;

import com.example.frontend.model.Medical;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicalService {
    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private String lastMessage = "";

    public MedicalService(ServerClient client) {
        this.client = client;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public Medical addMedical(String studentId, String courseId, String examType, String dateSubmitted, String medicalCopy) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("student_id", studentId);
            data.put("course_id", courseId);
            data.put("exam_type", examType);
            data.put("date_submitted", dateSubmitted);
            data.put("medical_copy", medicalCopy);
            JsonNode node = send("AddMedical", data);
            if (node != null && node.path("success").asBoolean(false) && node.has("data")) {
                lastMessage = node.path("message").asText("Medical added successfully");
                return mapper.convertValue(node.get("data"), Medical.class);
            }
            lastMessage = node == null ? "No response from server" : node.path("message").asText("Medical add failed");
        } catch (Exception e) {
            lastMessage = "Medical add failed: " + (e.getMessage() == null ? "error" : e.getMessage());
        }
        return null;
    }

    public boolean updateMedical(Integer medicalId, String studentId, String courseId, String examType, String dateSubmitted, String medicalCopy) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("medical_id", medicalId);
            data.put("student_id", studentId);
            data.put("course_id", courseId);
            data.put("exam_type", examType);
            data.put("date_submitted", dateSubmitted);
            data.put("medical_copy", medicalCopy);
            JsonNode node = send("UpdateMedical", data);
            if (node != null) {
                lastMessage = node.path("message").asText("Medical update failed");
                return node.path("success").asBoolean(false);
            }
        } catch (Exception e) {
            lastMessage = "Medical update failed: " + (e.getMessage() == null ? "error" : e.getMessage());
        }
        return false;
    }

    public boolean approveMedical(Integer medicalId) {
        return changeStatus("ApproveMedical", medicalId);
    }

    public boolean rejectMedical(Integer medicalId) {
        return changeStatus("RejectMedical", medicalId);
    }

    public List<Medical> getStudentMedicalRecords(String studentId) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("student_id", studentId);
            JsonNode node = send("GetStudentMedicalRecords", data);
            return readMedicalList(node, "Failed to load student medical records");
        } catch (Exception e) {
            lastMessage = "Failed to load student medical records: " + (e.getMessage() == null ? "error" : e.getMessage());
            return List.of();
        }
    }

    public List<Medical> getBatchMedicalRecords(String batch) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("batch", batch);
            JsonNode node = send("GetBatchMedicalRecords", data);
            return readMedicalList(node, "Failed to load batch medical records");
        } catch (Exception e) {
            lastMessage = "Failed to load batch medical records: " + (e.getMessage() == null ? "error" : e.getMessage());
            return List.of();
        }
    }

    private boolean changeStatus(String command, Integer medicalId) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("medical_id", medicalId);
            JsonNode node = send(command, data);
            if (node != null) {
                lastMessage = node.path("message").asText("Status update failed");
                return node.path("success").asBoolean(false);
            }
        } catch (Exception e) {
            lastMessage = "Status update failed: " + (e.getMessage() == null ? "error" : e.getMessage());
        }
        return false;
    }

    private List<Medical> readMedicalList(JsonNode node, String defaultError) {
        List<Medical> list = new ArrayList<>();
        if (node == null) {
            lastMessage = "No response from server";
            return list;
        }
        if (!node.path("success").asBoolean(false) || !node.has("data") || !node.get("data").isArray()) {
            lastMessage = node.path("message").asText(defaultError);
            return list;
        }
        for (JsonNode row : node.get("data")) {
            list.add(mapper.convertValue(row, Medical.class));
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
