package com.example.frontend.service;

import com.example.frontend.dto.RequestDTO;
import com.example.frontend.model.StudentEligibilityRes;
import com.example.frontend.model.StudentEligibilityRow;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collections;
import java.util.List;

public class StudentEligibilityService {

    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public StudentEligibilityService(ServerClient client) {
        this.client = client;
    }

    public List<StudentEligibilityRow> getEligibilityByCourse(String courseId) {
        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("command", "GET_STUDENT_ELIGIBILITY");
            root.put("token", SessionManager.getToken());

            ObjectNode data = root.putObject("data");
            data.put("courseId", courseId);

            String responseJson = client.sendRequest(mapper.writeValueAsString(root));
            JsonNode responseNode = mapper.readTree(responseJson);

            if (!responseNode.path("success").asBoolean(false)) {
                return Collections.emptyList();
            }

            return mapper.readValue(
                    responseNode.path("data").toString(),
                    new TypeReference<List<StudentEligibilityRow>>() {}
            );

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<StudentEligibilityRes> getStudentOwnEligibility() {
        try {
            RequestDTO requestDTO = new RequestDTO(
                    "GET_STUDENT_OWN_ELIGIBILITY",
                    null,
                    SessionManager.getToken()
            );

            String requestJson = mapper.writeValueAsString(requestDTO);
            String responseJson = client.sendRequest(requestJson);

            System.out.println("STUDENT ELIGIBILITY RESPONSE: " + responseJson);

            if (responseJson == null || responseJson.isBlank()) {
                return Collections.emptyList();
            }

            JsonNode root = mapper.readTree(responseJson);

            if (!root.path("success").asBoolean(false)) {
                System.out.println(root.path("message").asText());
                return Collections.emptyList();
            }

            return mapper.readValue(
                    root.path("eligibility").toString(),
                    new TypeReference<List<StudentEligibilityRes>>() {}
            );

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}