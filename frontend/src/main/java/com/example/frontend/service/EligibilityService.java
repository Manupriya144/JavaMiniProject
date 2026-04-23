package com.example.frontend.service;

import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EligibilityService {

    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public EligibilityService(ServerClient client) {
        this.client = client;
    }

    public JsonNode checkEligibility(String studentId) {
        return send("CheckFullEligibility",
                String.format("{\"studentId\":\"%s\"}", studentId));
    }

    public JsonNode getBatchEligibility(String batch) {
        return send("GetBatchFullEligibilityReport",
                String.format("{\"batch\":\"%s\"}", batch));
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
            return null;
        }
    }
}