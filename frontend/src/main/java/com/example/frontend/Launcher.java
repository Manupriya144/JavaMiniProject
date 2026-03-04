package com.example.frontend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Launcher {
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();

        try (Socket socket = new Socket("localhost", 5000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // ead server ready message
            String serverMsg = in.readLine();
            System.out.println("Server: " + serverMsg);

            //  LOGIN
            String loginJson = "{\"command\":\"LOGIN\",\"data\":{\"username\":\"admin\",\"password\":\"1234\"}}";
            out.println(loginJson);

            // Read login response
            String loginResponse = in.readLine();
            System.out.println("Login Response: " + loginResponse);

            // Parse the token from response
            JsonNode loginNode = mapper.readTree(loginResponse);
            if (!loginNode.get("success").asBoolean()) {
                System.out.println("Login failed: " + loginNode.get("message").asText());
                return;
            }
            String token = loginNode.get("token").asText();
            System.out.println("Token: " + token);

            // CreateUser command using token
            String createUserJson = String.format(
                    "{\"command\":\"CreateUser\",\"token\":\"%s\",\"data\":{\"username\":\"laksiri\",\"password\":\"1234\",\"role\":\"Lecturer\",\"email\":\"lecturer15@example.com\",\"contactNumber\":\"0712345678\",\"profilePicture\":null}}",
                    token
            );
            out.println(createUserJson);
            String createUserResponse = in.readLine();
            System.out.println("CreateUser Response: " + createUserResponse);

            // GetAllUser command using token
            String getAllUsersJson = String.format(
                    "{\"command\":\"GetAllUser\",\"token\":\"%s\",\"data\":{}}",
                    token
            );
            out.println(getAllUsersJson);
            String getAllUsersResponse = in.readLine();
            System.out.println("GetAllUsers Response: " + getAllUsersResponse);


            // ===== LOGOUT =====
            String logoutJson = String.format(
                    "{\"command\":\"LOGOUT\",\"token\":\"%s\",\"data\":{}}",
                    token
            );
            out.println(logoutJson);

            String logoutResponse = in.readLine();
            System.out.println("Logout Response: " + logoutResponse);

            // ===== Try a command after logout (should fail) =====
            out.println(getAllUsersJson); // reuse GetAllUser JSON
            String postLogoutResponse = in.readLine();
            System.out.println("Post-Logout GetAllUser Response: " + postLogoutResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}