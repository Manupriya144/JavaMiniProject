package com.example.frontend.service;

import com.example.frontend.dto.NoticeRequestDTO;
import com.example.frontend.dto.RequestDTO;
import com.example.frontend.dto.TimeTableRequestDTO;
import com.example.frontend.dto.TimeTableResponseDTO;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class TimeTableService {
    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();


    public TimeTableService(ServerClient client) {
        this.client = client;
    }

    public boolean createTimeTable(TimeTableRequestDTO dto) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("command", "CREATE_TIMETABLE");
            request.put("data", dto);
            request.put("token", SessionManager.getToken());
            System.out.println(SessionManager.getToken());

            String json = mapper.writeValueAsString(request);
            String response = client.sendRequest(json);

            System.out.println("create time table : " + response);

            Map<String, Object> map = mapper.readValue(response, Map.class);
            return Boolean.TRUE.equals(map.get("success"));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<TimeTableResponseDTO> getAllTimeTables() {
        try {
            RequestDTO requestDTO = new RequestDTO(
                    "GET_ALL_TIMETABLES",
                    null,
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
                return mapper.readValue(
                        responseJson,
                        new com.fasterxml.jackson.core.type.TypeReference<List<TimeTableResponseDTO>>() {}
                );
            }

            return Collections.emptyList();

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
