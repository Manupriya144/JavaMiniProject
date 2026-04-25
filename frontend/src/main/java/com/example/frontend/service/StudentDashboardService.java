package com.example.frontend.service;

import com.example.frontend.dto.RequestDTO;
import com.example.frontend.model.StudentDashboardData;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StudentDashboardService {

    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public StudentDashboardService(ServerClient client) {
        this.client = client;
    }

    public StudentDashboardData getDashboardData() {
        try {
            RequestDTO requestDTO = new RequestDTO(
                    "GET_STUDENT_DASHBOARD",
                    null,
                    SessionManager.getToken()
            );

            String requestJson = mapper.writeValueAsString(requestDTO);
            String responseJson = client.sendRequest(requestJson);

            JsonNode root = mapper.readTree(responseJson);

            if (!root.path("success").asBoolean(false)) {
                return null;
            }

            return mapper.treeToValue(
                    root.path("dashboard"),
                    StudentDashboardData.class
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}