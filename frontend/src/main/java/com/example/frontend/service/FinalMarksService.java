package com.example.frontend.service;

import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FinalMarksService {

    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public FinalMarksService(ServerClient client) {
        this.client = client;
    }

    public JsonNode uploadMarks(String studentId, String courseId, double marks) {
        return send("UploadFinalMarks",
                String.format("{\"studentId\":\"%s\",\"courseId\":\"%s\",\"academicYear\":2026,\"semester\":1,\"marks\":%s}", studentId, courseId, marks));
    }

    public JsonNode updateMarks(String studentId, String courseId, double marks) {
        return send("UpdateFinalMarks",
                String.format("{\"studentId\":\"%s\",\"courseId\":\"%s\",\"academicYear\":2026,\"semester\":1,\"marks\":%s}", studentId, courseId, marks));
    }

    public JsonNode getStudentMarks(String studentId, String courseId) {
        return send("GetStudentFinalMarks",
                String.format("{\"studentId\":\"%s\",\"courseId\":\"%s\"}", studentId, courseId));
    }

    public JsonNode getBatchMarks(int academicYear, int semester) {
        return send("GetBatchFinalMarks",
                String.format("{\"academicYear\":%d,\"semester\":%d}", academicYear, semester));
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

