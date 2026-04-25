package com.example.frontend.service;

import com.example.frontend.dto.RegistrationPeriodRequestDTO;
import com.example.frontend.dto.RequestDTO;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RegistrationPeriodService {

    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public RegistrationPeriodService(ServerClient client) {
        this.client = client;
    }

    public boolean saveRegistrationPeriod(RegistrationPeriodRequestDTO dto) {
        try {
            RequestDTO requestDTO = new RequestDTO(
                    "SAVE_REGISTRATION_PERIOD",
                    dto,
                    SessionManager.getToken()
            );

            String requestJson = mapper.writeValueAsString(requestDTO);
            String responseJson = client.sendRequest(requestJson);

            System.out.println("SAVE REGISTRATION PERIOD RESPONSE: " + responseJson);

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
}