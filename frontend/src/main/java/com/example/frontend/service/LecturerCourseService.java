package com.example.frontend.service;

import com.example.frontend.dto.LecturerCourseRequestDTO;
import com.example.frontend.dto.LecturerCourseResponseDTO;
import com.example.frontend.dto.RequestDTO;
import com.example.frontend.model.LecturerCourseItem;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public class LecturerCourseService {

    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public LecturerCourseService(ServerClient client) {
        this.client = client;
    }

    public LecturerCourseResponseDTO assignLecturerToCourse(String lecturerId, String courseId) {
        try {
            LecturerCourseRequestDTO data = new LecturerCourseRequestDTO(lecturerId, courseId);

            RequestDTO requestDTO = new RequestDTO(
                    "AssignLecturerCourse",
                    data,
                    SessionManager.getToken()
            );

            String requestJson = mapper.writeValueAsString(requestDTO);
            String responseJson = client.sendRequest(requestJson);

            return mapper.readValue(responseJson, LecturerCourseResponseDTO.class);

        } catch (Exception e) {
            e.printStackTrace();
            return new LecturerCourseResponseDTO(false, "Frontend error while assigning lecturer to course");
        }
    }

    public List<LecturerCourseItem> getLecturerCourses() throws Exception {
        JsonNode root = mapper.createObjectNode()
                .put("command", "GET_LECTURER_COURSES")
                .put("token", SessionManager.getToken());

        ((com.fasterxml.jackson.databind.node.ObjectNode) root).putObject("data");

        String requestJson = mapper.writeValueAsString(root);
        String responseJson = client.sendRequest(requestJson);

        JsonNode responseNode = mapper.readTree(responseJson);

        boolean success = responseNode.path("success").asBoolean(false);
        if (!success) {
            return Collections.emptyList();
        }

        JsonNode dataNode = responseNode.path("courses");
        return mapper.readValue(
                dataNode.toString(),
                new TypeReference<List<LecturerCourseItem>>() {}
        );
    }
}