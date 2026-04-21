package com.example.frontend.service;

import com.example.frontend.dto.RequestDTO;
import com.example.frontend.dto.UserRequestDTO;
import com.example.frontend.dto.UserResponseDTO;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
}