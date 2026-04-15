package com.example.frontend.service;

import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GradeService {

    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public GradeService(ServerClient client) {
        this.client = client;
    }

    public JsonNode generateGrade(String studentId, String courseId) {
        return send("GenerateGrade",
                String.format("{\"studentId\":\"%s\",\"courseId\":\"%s\"}", studentId, courseId));
    }

    public JsonNode getStudentGrades(String studentId) {
        return send("GetStudentGrades",
                String.format("{\"studentId\":\"%s\"}", studentId));
    }

    public JsonNode getBatchGrades(String batch) {
        return send("GetBatchGrades",
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
