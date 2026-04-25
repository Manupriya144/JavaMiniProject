package com.example.frontend.service;

import com.example.frontend.dto.RequestDTO;
import com.example.frontend.model.StudentRegisteredCourse;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public class StudentCourseService {

    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public StudentCourseService(ServerClient client) {
        this.client = client;
    }

    public List<StudentRegisteredCourse> getRegisteredCourses() {
        try {
            RequestDTO requestDTO = new RequestDTO(
                    "GET_STUDENT_REGISTERED_COURSES",
                    null,
                    SessionManager.getToken()
            );

            String requestJson = mapper.writeValueAsString(requestDTO);
            String responseJson = client.sendRequest(requestJson);

            JsonNode root = mapper.readTree(responseJson);

            if (!root.path("success").asBoolean(false)) {
                return Collections.emptyList();
            }

            return mapper.readValue(
                    root.path("courses").toString(),
                    new TypeReference<List<StudentRegisteredCourse>>() {}
            );

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}