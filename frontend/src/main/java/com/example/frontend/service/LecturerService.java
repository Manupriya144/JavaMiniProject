package com.example.frontend.service;

import com.example.frontend.dto.LecturerResponseDTO;
import com.example.frontend.dto.RequestDTO;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public class LecturerService {

    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public LecturerService(ServerClient client) {
        this.client = client;
    }

    public List<LecturerResponseDTO> getAllLecturers() {
        try {
            RequestDTO requestDTO = new RequestDTO(
                    "GetAllLecturers",
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

            return mapper.readValue(responseJson, new TypeReference<List<LecturerResponseDTO>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}