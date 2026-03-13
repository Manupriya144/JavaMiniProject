package com.example.frontend.service;

import com.example.frontend.model.User;
import com.example.frontend.network.ServerClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthService {

    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public AuthService(ServerClient client){
        this.client = client;
    }

    public User login(String username, String password) throws Exception {

        String loginJson = String.format(
                "{\"command\":\"LOGIN\",\"data\":{\"username\":\"%s\",\"password\":\"%s\"}}",
                username, password
        );


        String response = client.sendRequest(loginJson);


        JsonNode node = mapper.readTree(response);

        if(node.get("success").asBoolean()){

            String role = node.has("role") ? node.get("role").asText() : "";
            String token = node.has("token") ? node.get("token").asText() : "";
            String userId = node.has("user_id") ? node.get("user_id").asText() : "";


            User user = new User(userId, username, "", role);
            user.setToken(token);
            return user;
        }

        return null;
    }
    public boolean logout(String token) {
        try {
            if (token == null || token.isEmpty()) return false;

            // Include token in logout JSON
            String logoutJson = String.format(
                    "{\"command\":\"LOGOUT\",\"data\":{},\"token\":\"%s\"}", token
            );

            String response = client.sendRequest(logoutJson);
            JsonNode node = mapper.readTree(response);

            return node.has("success") && node.get("success").asBoolean();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}