package com.example.frontend.service;

import com.example.frontend.dto.AdminStatsResponseDTO;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class AdminDashboardService {

    private final ServerClient serverClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AdminDashboardService(ServerClient serverClient) {
        this.serverClient = serverClient;
    }

    public AdminStatsResponseDTO getAdminStats() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("command", "GET_ADMIN_STATS");
        request.put("token", SessionManager.getToken());
        request.put("data", new HashMap<>());

        String requestJson = objectMapper.writeValueAsString(request);
        String responseJson = serverClient.sendRequest(requestJson);

        return objectMapper.readValue(responseJson, AdminStatsResponseDTO.class);
    }
}