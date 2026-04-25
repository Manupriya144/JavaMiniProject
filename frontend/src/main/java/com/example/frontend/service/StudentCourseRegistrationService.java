package com.example.frontend.service;

import com.example.frontend.dto.RegisterCourseRequestDTO;
import com.example.frontend.dto.RequestDTO;
import com.example.frontend.model.StudentCourseRegistration;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Year;
import java.util.Collections;
import java.util.List;

public class StudentCourseRegistrationService {

    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public StudentCourseRegistrationService(ServerClient client) {
        this.client = client;
    }

    public RegistrationPageData getRegistrationCourses() {
        try {
            RequestDTO requestDTO = new RequestDTO(
                    "GET_STUDENT_REGISTRATION_COURSES",
                    null,
                    SessionManager.getToken()
            );

            String requestJson = mapper.writeValueAsString(requestDTO);
            String responseJson = client.sendRequest(requestJson);

            System.out.println("REGISTRATION COURSES RESPONSE: " + responseJson);

            if (responseJson == null || responseJson.isBlank()) {
                return RegistrationPageData.closed("Empty server response.");
            }

            JsonNode root = mapper.readTree(responseJson);

            if (!root.path("success").asBoolean(false)) {
                return RegistrationPageData.closed(root.path("message").asText("Failed to load courses."));
            }

            boolean open = root.path("registrationOpen").asBoolean(false);
            String message = root.path("message").asText("");

            List<StudentCourseRegistration> properCourses = mapper.readValue(
                    root.path("properCourses").toString(),
                    new TypeReference<List<StudentCourseRegistration>>() {}
            );

            List<StudentCourseRegistration> repeatCourses = mapper.readValue(
                    root.path("repeatCourses").toString(),
                    new TypeReference<List<StudentCourseRegistration>>() {}
            );

            return new RegistrationPageData(open, message, properCourses, repeatCourses);

        } catch (Exception e) {
            e.printStackTrace();
            return RegistrationPageData.closed("Failed to load registration courses.");
        }
    }

    public boolean registerCourse(StudentCourseRegistration course) {
        try {
            int academicYear = Year.now().getValue();

            RegisterCourseRequestDTO dto = new RegisterCourseRequestDTO(
                    course.getCourseId(),
                    academicYear,
                    course.getSemester(),
                    course.getRegistrationType()
            );

            RequestDTO requestDTO = new RequestDTO(
                    "REGISTER_STUDENT_COURSE",
                    dto,
                    SessionManager.getToken()
            );

            String requestJson = mapper.writeValueAsString(requestDTO);
            String responseJson = client.sendRequest(requestJson);

            System.out.println("REGISTER COURSE RESPONSE: " + responseJson);

            if (responseJson == null || responseJson.isBlank()) {
                return false;
            }

            JsonNode root = mapper.readTree(responseJson);
            return root.path("success").asBoolean(false);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class RegistrationPageData {
        private final boolean registrationOpen;
        private final String message;
        private final List<StudentCourseRegistration> properCourses;
        private final List<StudentCourseRegistration> repeatCourses;

        public RegistrationPageData(boolean registrationOpen,
                                    String message,
                                    List<StudentCourseRegistration> properCourses,
                                    List<StudentCourseRegistration> repeatCourses) {
            this.registrationOpen = registrationOpen;
            this.message = message;
            this.properCourses = properCourses == null ? Collections.emptyList() : properCourses;
            this.repeatCourses = repeatCourses == null ? Collections.emptyList() : repeatCourses;
        }

        public static RegistrationPageData closed(String message) {
            return new RegistrationPageData(false, message, Collections.emptyList(), Collections.emptyList());
        }

        public boolean isRegistrationOpen() { return registrationOpen; }
        public String getMessage() { return message; }
        public List<StudentCourseRegistration> getProperCourses() { return properCourses; }
        public List<StudentCourseRegistration> getRepeatCourses() { return repeatCourses; }
    }
}