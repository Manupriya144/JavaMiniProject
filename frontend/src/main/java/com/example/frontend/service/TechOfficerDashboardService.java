package com.example.frontend.service;

import com.example.frontend.controller.admin.LoginController;
import com.example.frontend.model.TechOfficerDashboardStats;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class TechOfficerDashboardService {
    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private String lastMessage = "";
    private final UserService userService = new UserService(LoginController.client);

    public TechOfficerDashboardService(ServerClient client) {
        this.client = client;
    }

    public TechOfficerDashboardStats getDashboardStats() {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("command", "GET_TECH_OFFICER_DASHBOARD_STATS");
            request.put("data", new HashMap<>());
            request.put("token", SessionManager.getToken());

            String response = client.sendRequest(mapper.writeValueAsString(request));
            JsonNode node = mapper.readTree(response);

            if (!node.path("success").asBoolean(false)) {
                lastMessage = node.path("message").asText("Failed to load dashboard stats");
                return null;
            }

            JsonNode data = node.path("data");
            TechOfficerDashboardStats stats = new TechOfficerDashboardStats();
            stats.setTotalStudents(data.path("totalStudents").asInt(0));
            stats.setAttendanceSessions(data.path("attendanceSessions").asInt(0));
            stats.setMedicalRecords(data.path("medicalRecords").asInt(0));
            stats.setPendingApprovals(data.path("pendingApprovals").asInt(0));
            return stats;
        } catch (Exception e) {
            lastMessage = "Failed to load dashboard stats: " + (e.getMessage() == null ? "error" : e.getMessage());
            return null;
        }
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
