package com.example.frontend.service;

import com.example.frontend.model.Student;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StudentService {

    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public StudentService(ServerClient client) {
        this.client = client;
    }

    /**
     * Fetch student by user_id from backend
     * @param userId the user's ID
     * @return Student object or null if not found
     */
    public Student getStudentByUserId(String userId) {
        try {
            String requestJson = String.format(
                    "{\"command\":\"GET_STUDENT_BY_USER_ID\",\"data\":{\"user_id\":\"%s\"},\"token\":\"%s\"}",
                    userId, SessionManager.getToken()
            );

            String response = client.sendRequest(requestJson);
            JsonNode node = mapper.readTree(response);

            if (node.get("success").asBoolean()) {
                JsonNode data = node.get("data");
                Student student = new Student();
                student.setUserId(data.get("userId").asText());
                student.setRegNo(data.get("regNo").asText());
                student.setBatch(data.get("batch").asText());
                student.setAcademicLevel(data.get("academicLevel").asInt());
                student.setDepartmentId(data.get("departmentId").asText());
                return student;
            }
            System.out.println("Backend response: " + response);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Student getStudentByIdAll(String userId) {
        try {
            String requestJson = String.format(
                    "{\"command\":\"GET_STUDENT_ALL_DETAILS\",\"data\":{\"user_id\":\"%s\"},\"token\":\"%s\"}",
                    userId, SessionManager.getToken()
            );

            String response = client.sendRequest(requestJson);
            JsonNode node = mapper.readTree(response);

            if (node.get("success").asBoolean()) {

                JsonNode data = node.get("data");

                Student student = new Student(
                        data.get("userId").asText(),
                        data.get("username").asText(),
                        data.get("email").asText(),
                        data.get("contactNumber").asText(),
                        data.get("profilePicture").asText(),
                        data.get("role").asText(),
                        data.get("regNo").asText(),
                        data.get("batch").asText(),
                        data.get("academicLevel").asInt(),
                        data.get("departmentId").asText()
                );

                return student;
            }

            System.out.println("Backend response: " + response);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateStudentProfile(String userId,
                                        String email,
                                        String contactNumber,
                                        String profilePicture,
                                        String password) {
        try {
            String safePassword = password == null ? "" : password;

            String requestJson = String.format(
                    "{\"command\":\"UPDATE_STUDENT_PROFILE\",\"data\":{\"userId\":\"%s\",\"email\":\"%s\",\"contactNumber\":\"%s\",\"profilePicture\":\"%s\",\"password\":\"%s\"},\"token\":\"%s\"}",
                    userId,
                    email,
                    contactNumber,
                    profilePicture,
                    safePassword,
                    SessionManager.getToken()
            );

            String response = client.sendRequest(requestJson);
            JsonNode node = mapper.readTree(response);
            return node.get("success").asBoolean();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
