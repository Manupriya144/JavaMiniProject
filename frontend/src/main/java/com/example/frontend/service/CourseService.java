package com.example.frontend.service;

import com.example.frontend.dto.CourseAllResponseDTO;
import com.example.frontend.dto.CourseRequestDTO;
import com.example.frontend.dto.CourseResponseDTO;
import com.example.frontend.dto.RequestDTO;
import com.example.frontend.dto.StudentCoursesRequestDTO;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseService {

    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public CourseService(ServerClient client) {
        this.client = client;
    }

    public boolean addCourse(CourseRequestDTO dto) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("command", "ADD_COURSE");
            request.put("data", dto);
            request.put("token", SessionManager.getToken());

            String json = mapper.writeValueAsString(request);

            String response = client.sendRequest(json);
            System.out.println("add course : " + response);

            Map<String, Object> map = mapper.readValue(response, Map.class);

            return Boolean.TRUE.equals(map.get("success"));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CourseResponseDTO> getAllCourses() {
        try {
            RequestDTO requestDTO = new RequestDTO(
                    "GetAllCourses",
                    null,
                    SessionManager.getToken()
            );

            String requestJson = mapper.writeValueAsString(requestDTO);
            String responseJson = client.sendRequest(requestJson);

            if (responseJson == null || responseJson.isEmpty()) {
                return Collections.emptyList();
            }

            if (responseJson.contains("\"success\":false")) {
                return Collections.emptyList();
            }

            return mapper.readValue(responseJson, new TypeReference<List<CourseResponseDTO>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<CourseAllResponseDTO> getAllCoursesFull() {
        try {
            RequestDTO requestDTO = new RequestDTO(
                    "GET_ALL_COURSES_FULL",
                    null,
                    SessionManager.getToken()
            );

            String requestJson = mapper.writeValueAsString(requestDTO);
            String responseJson = client.sendRequest(requestJson);

            if (responseJson == null || responseJson.isEmpty()) {
                return Collections.emptyList();
            }

            JsonNode root = mapper.readTree(responseJson);

            // If backend returns error object
            if (root.isObject()) {
                if (root.has("success") && !root.get("success").asBoolean()) {
                    return Collections.emptyList();
                }
            }

            // If backend returns array directly
            if (root.isArray()) {
                return mapper.readValue(responseJson, new TypeReference<List<CourseAllResponseDTO>>() {});
            }

            return Collections.emptyList();

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<CourseAllResponseDTO> getStudentCourses(String userId) {
        try {
            RequestDTO requestDTO = new RequestDTO(
                    "GET_STUDENT_COURSES",
                    new StudentCoursesRequestDTO(userId),
                    SessionManager.getToken()
            );

            String requestJson = mapper.writeValueAsString(requestDTO);
            String responseJson = client.sendRequest(requestJson);

            if (responseJson == null || responseJson.isEmpty()) {
                return Collections.emptyList();
            }

            JsonNode root = mapper.readTree(responseJson);

            if (root.isObject()) {
                if (root.has("success") && !root.get("success").asBoolean()) {
                    return Collections.emptyList();
                }
            }

            if (root.isArray()) {
                return mapper.readValue(responseJson, new TypeReference<List<CourseAllResponseDTO>>() {});
            }

            return Collections.emptyList();

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}