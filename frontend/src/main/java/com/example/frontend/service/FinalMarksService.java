package com.example.frontend.service;

import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FinalMarksService {

    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public FinalMarksService(ServerClient client){
        this.client = client;
    }

    public boolean uploadFinalMarks(String regNo, String courseId, double marks) {

        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("command", "UPLOAD_FINAL_MARKS");
            root.put("token", SessionManager.getToken());

            ObjectNode data = root.putObject("data");
            data.put("regNo", regNo);
            data.put("courseId", courseId);
            data.put("marks", marks);

            String res = client.sendRequest(mapper.writeValueAsString(root));

            JsonNode node = mapper.readTree(res);

            return node.path("success").asBoolean(false);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

