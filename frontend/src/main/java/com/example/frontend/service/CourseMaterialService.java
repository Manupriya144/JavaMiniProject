package com.example.frontend.service;

import com.example.frontend.dto.CourseMaterialRequestDTO;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class CourseMaterialService {

    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public CourseMaterialService(ServerClient client) {
        this.client = client;
    }

    public JsonNode uploadCourseMaterial(CourseMaterialRequestDTO dto) {
        return send("ADD_COURSE_MATERIAL", dto);
    }

    public JsonNode getCourseMaterials(String courseId) {
        Map<String, Object> data = new HashMap<>();
        data.put("courseId", courseId);
        return send("GET_COURSE_MATERIALS", data);
    }

    public JsonNode updateDeadline(int materialId, String deadline) {
        Map<String, Object> data = new HashMap<>();
        data.put("materialId", materialId);
        data.put("deadline", deadline);
        return send("UPDATE_COURSE_MATERIAL_DEADLINE", data);
    }

    public JsonNode deleteCourseMaterial(int materialId) {
        Map<String, Object> data = new HashMap<>();
        data.put("materialId", materialId);
        return send("DELETE_COURSE_MATERIAL", data);
    }

    private JsonNode send(String command, Object data) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("command", command);
            request.put("data", data);
            request.put("token", SessionManager.getToken());
            String json = mapper.writeValueAsString(request);
            return mapper.readTree(client.sendRequest(json));
        } catch (Exception e) {
            e.printStackTrace();
            ObjectNode error = mapper.createObjectNode();
            error.put("success", false);
            error.put("message", "Failed to contact backend service");
            return error;
        }
    }
}