package com.example.frontend.service;

import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EligibilityService {

    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public EligibilityService(ServerClient client) {
        this.client = client;
    }

    public JsonNode checkEligibility(String studentId, String courseId) {
        return send("CheckFullEligibility",
                String.format("{\"studentId\":\"%s\",\"courseId\":\"%s\"}", studentId, courseId));
    }

    public JsonNode getBatchEligibility(String academicYear, String semester) {
        return send("GetBatchFullEligibilityReport",
                String.format("{\"academicYear\":%s,\"semester\":%s}", academicYear, semester));
    }

    private JsonNode send(String command, String data) {
        try {
            String request = String.format(
                    "{\"command\":\"%s\",\"data\":%s,\"token\":\"%s\"}",
                    command, data, SessionManager.getToken()
            );
            return mapper.readTree(client.sendRequest(request));
        } catch (Exception e) {
            e.printStackTrace();
            ObjectNode error = mapper.createObjectNode();
            error.put("success", false);
            error.put("message", "Failed to contact backend service");
            return error;
        }
    }
}
