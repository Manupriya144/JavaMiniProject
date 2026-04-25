package com.example.frontend.service;

import com.example.frontend.dto.RequestDTO;
import com.example.frontend.dto.UserRequestDTO;
import com.example.frontend.dto.UserResponseDTO;
import com.example.frontend.model.TechOfficerProfile;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public UserService(ServerClient client) {
        this.client = client;
    }

    public boolean createUser(UserRequestDTO dto) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("command", "CreateUser");
            request.put("data", dto);
            request.put("token", SessionManager.getToken());

            String json = mapper.writeValueAsString(request);

            String response = client.sendRequest(json);
            System.out.println("create user : " + response);

            Map<String, Object> map = mapper.readValue(response, Map.class);

            return Boolean.TRUE.equals(map.get("success"));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<UserResponseDTO> getAllUsers() {
        try {
            RequestDTO requestDTO = new RequestDTO(
                    "GetAllUser",
                    null,
                    SessionManager.getToken()
            );

            String requestJson = mapper.writeValueAsString(requestDTO);

            String responseJson = client.sendRequest(requestJson);

            if (responseJson == null || responseJson.isEmpty()) {
                return Collections.emptyList();
            }

            if (responseJson.contains("\"success\":false")) {
                System.out.println("Server error: " + responseJson);
                return Collections.emptyList();
            }

            return mapper.readValue(responseJson, new TypeReference<List<UserResponseDTO>>() {});

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    public TechOfficerProfile getMyTechOfficerProfile() {
        try {
            String token = SessionManager.getToken();
            if (token == null || token.isBlank()) {
                return null;
            }

            String requestJson = String.format(
                    "{\"command\":\"GET_MY_TECH_OFFICER_PROFILE\",\"data\":{},\"token\":\"%s\"}",
                    token
            );

            String response = client.sendRequest(requestJson);
            JsonNode node = mapper.readTree(response);

            if (!node.path("success").asBoolean(false)) {
                return null;
            }

            JsonNode data = node.get("data");
            if (data == null || data.isNull()) {
                return null;
            }

            TechOfficerProfile profile = new TechOfficerProfile();
            profile.setUserId(data.path("userId").asText(""));
            profile.setUsername(data.path("username").asText(""));
            profile.setEmail(data.path("email").asText(""));
            profile.setContactNumber(data.path("contactNumber").asText(""));
            profile.setProfilePicture(data.path("profilePicture").asText(""));
            profile.setRole(data.path("role").asText(""));
            profile.setDepartmentId(data.path("departmentId").asText(""));
            return profile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateTechOfficerProfile(TechOfficerProfile profile) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", profile.getUserId());
            payload.put("username", profile.getUsername());
            payload.put("email", profile.getEmail());
            payload.put("password", profile.getPassword());
            payload.put("contactNumber", profile.getContactNumber());
            payload.put("profilePicture", profile.getProfilePicture());
            payload.put("departmentId", profile.getDepartmentId());

            Map<String, Object> request = new HashMap<>();
            request.put("command", "UPDATE_TECH_OFFICER_PROFILE");
            request.put("data", payload);
            request.put("token", SessionManager.getToken());

            String response = client.sendRequest(mapper.writeValueAsString(request));
            JsonNode node = mapper.readTree(response);
            return node.has("success") && node.get("success").asBoolean();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String userId) {
        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("command", "DELETE_USER");
            root.put("token", SessionManager.getToken());

            ObjectNode data = root.putObject("data");
            data.put("userId", userId);

            String responseJson = client.sendRequest(mapper.writeValueAsString(root));
            JsonNode response = mapper.readTree(responseJson);

            return response.path("success").asBoolean(false);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(UserResponseDTO user) {
        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("command", "UPDATE_USER");
            root.put("token", SessionManager.getToken());

            ObjectNode data = root.putObject("data");
            data.put("userId", user.getUserId());
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("contactNo", user.getContactNo());
            data.put("role", user.getRole());
            data.put("profilePicture", user.getProfilePicture());

            String responseJson = client.sendRequest(mapper.writeValueAsString(root));
            JsonNode response = mapper.readTree(responseJson);

            return response.path("success").asBoolean(false);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
