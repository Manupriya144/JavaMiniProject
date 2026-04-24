package com.example.frontend.service;

import com.example.frontend.model.CourseMaterial;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collections;
import java.util.List;

public class CourseMaterialService {

    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public CourseMaterialService(ServerClient client) {
        this.client = client;
    }

    public List<CourseMaterial> getMaterialsByCourseId(String courseId) {
        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("command", "GET_COURSE_MATERIALS");
            root.put("token", SessionManager.getToken());

            ObjectNode data = root.putObject("data");
            data.put("courseId", courseId);

            String requestJson = mapper.writeValueAsString(root);
            String responseJson = client.sendRequest(requestJson);

            JsonNode responseNode = mapper.readTree(responseJson);

            boolean success = responseNode.path("success").asBoolean(false);

            if (!success) {
                return Collections.emptyList();
            }

            JsonNode materialsNode = responseNode.path("materials");

            return mapper.readValue(
                    materialsNode.toString(),
                    new TypeReference<List<CourseMaterial>>() {}
            );

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public boolean addMaterial(String courseId, String title, String filePath) {
        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("command", "ADD_COURSE_MATERIAL");
            root.put("token", SessionManager.getToken());

            ObjectNode data = root.putObject("data");
            data.put("courseId", courseId);
            data.put("title", title);
            data.put("filePath", filePath);

            String requestJson = mapper.writeValueAsString(root);
            String responseJson = client.sendRequest(requestJson);

            JsonNode responseNode = mapper.readTree(responseJson);

            return responseNode.path("success").asBoolean(false);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMaterial(int materialId) {
        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("command", "DELETE_COURSE_MATERIAL");
            root.put("token", SessionManager.getToken());

            ObjectNode data = root.putObject("data");
            data.put("materialId", materialId);

            String requestJson = mapper.writeValueAsString(root);
            String responseJson = client.sendRequest(requestJson);

            JsonNode responseNode = mapper.readTree(responseJson);

            return responseNode.path("success").asBoolean(false);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}