package com.example.frontend.service;

import com.example.frontend.dto.LecturerCourseRequestDTO;
import com.example.frontend.dto.LecturerCourseResponseDTO;
import com.example.frontend.dto.RequestDTO;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;

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
}