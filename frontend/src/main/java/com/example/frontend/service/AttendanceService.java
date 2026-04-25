package com.example.frontend.service;

import com.example.frontend.dto.RequestDTO;
import com.example.frontend.model.*;
import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class AttendanceService {
    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private String lastMessage = "";

    public AttendanceService(ServerClient client) {
        this.client = client;
    }

    public Attendance addAttendance(String studentId, Integer sessionId, String status, Double hoursAttended) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("student_id", studentId);
            data.put("session_id", sessionId);
            data.put("status", status);
            data.put("hours_attended", hoursAttended);

            JsonNode node = send("AddAttendance", data);
            if (node != null && node.path("success").asBoolean(false) && node.has("data")) {
                lastMessage = node.path("message").asText("Attendance added successfully");
                JsonNode attendanceNode = node.get("data");
                Attendance attendance = new Attendance();
                attendance.setAttendanceId(attendanceNode.path("attendanceId").asInt());
                attendance.setStudentId(attendanceNode.path("studentId").asText());
                attendance.setSessionId(attendanceNode.path("sessionId").asInt());
                attendance.setStatus(attendanceNode.path("status").asText());
                attendance.setHoursAttended(attendanceNode.path("hoursAttended").asDouble());
                return attendance;
            }
            if (node != null) {
                lastMessage = node.path("message").asText("Add failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            lastMessage = "Add failed: " + e.getMessage();
        }
        return null;
    }

    public boolean updateAttendance(Integer attendanceId, String status, Double hoursAttended) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("attendance_id", attendanceId);
            data.put("status", status);
            data.put("hours_attended", hoursAttended);

            JsonNode node = send("UpdateAttendance", data);
            return node != null && node.path("success").asBoolean(false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAttendance(Integer attendanceId) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("attendance_id", attendanceId);

            JsonNode node = send("DeleteAttendance", data);
            return node != null && node.path("success").asBoolean(false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Attendance getAttendanceById(Integer attendanceId) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("attendance_id", attendanceId);

            JsonNode node = send("GetAttendanceById", data);
            if (node != null && node.path("success").asBoolean(false) && node.has("data")) {
                JsonNode attendanceNode = node.get("data");
                Attendance attendance = new Attendance();
                attendance.setAttendanceId(attendanceNode.path("attendanceId").asInt());
                attendance.setStudentId(attendanceNode.path("studentId").asText());
                attendance.setSessionId(attendanceNode.path("sessionId").asInt());
                attendance.setStatus(attendanceNode.path("status").asText());
                attendance.setHoursAttended(attendanceNode.path("hoursAttended").asDouble());
                return attendance;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private JsonNode send(String command, Map<String, Object> data) throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("command", command);
        request.put("data", data);
        request.put("token", SessionManager.getToken());
        String json = mapper.writeValueAsString(request);
        String response = client.sendRequest(json);
        return mapper.readTree(response);
    }

    public String getLastMessage() {
        return lastMessage;
    }
    /**
     * Students who have at least one attendance record (registration numbers for medical forms).
     */
    public List<AttendanceStudentOption> getMedicalEligibleStudents() {
        List<AttendanceStudentOption> result = new ArrayList<>();
        try {
            JsonNode node = send("GetMedicalEligibleStudents", new HashMap<>());
            if (node != null && node.path("success").asBoolean(false) && node.has("data")) {
                for (JsonNode item : node.get("data")) {
                    result.add(new AttendanceStudentOption(
                            item.path("userId").asText(),
                            item.path("regNo").asText(),
                            item.path("username").asText()
                    ));
                }
            } else if (node != null) {
                lastMessage = node.path("message").asText("Failed to load students");
            } else {
                lastMessage = "No response from server";
            }
        } catch (Exception e) {
            String detail = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            lastMessage = "Failed to load students: " + detail;
        }
        return result;
    }

    /**
     * Course IDs for medical form: sessions this student attended, scoped to their department timetable.
     */
    public List<AttendanceCourseOption> getMedicalEligibleCourseIds(String studentUserId) {
        List<AttendanceCourseOption> result = new ArrayList<>();
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("student_id", studentUserId);
            JsonNode node = send("GetMedicalEligibleCourses", data);
            if (node != null && node.path("success").asBoolean(false) && node.has("data") && node.get("data").isArray()) {
                for (JsonNode item : node.get("data")) {
                    if (item.isTextual()) {
                        String courseId = item.asText();
                        result.add(new AttendanceCourseOption(courseId, ""));
                    } else if (item.isObject()) {
                        String courseId = item.path("courseId").asText();
                        String courseName = item.path("courseName").asText("");
                        if (!courseId.isBlank()) {
                            result.add(new AttendanceCourseOption(courseId, courseName));
                        }
                    }
                }
            } else if (node != null) {
                lastMessage = node.path("message").asText("Failed to load courses");
            } else {
                lastMessage = "No response from server";
            }
        } catch (Exception e) {
            String detail = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            lastMessage = "Failed to load courses: " + detail;
        }
        return result;
    }

    public List<AttendanceCourseOption> getMyMedicalEligibleCourseIds() {
        return getMedicalEligibleCourseIds(SessionManager.getUserId());
    }

    public List<AttendanceStudentOption> getStudentOptions() {
        List<AttendanceStudentOption> result = new ArrayList<>();
        try {
            JsonNode node = send("GetAttendanceStudents", new HashMap<>());
            if (node != null && node.path("success").asBoolean(false) && node.has("data")) {
                for (JsonNode item : node.get("data")) {
                    result.add(new AttendanceStudentOption(
                            item.path("userId").asText(),
                            item.path("regNo").asText(),
                            item.path("username").asText()
                    ));
                }
            } else if (node != null) {
                lastMessage = node.path("message").asText("Failed to load students");
            } else {
                lastMessage = "No response from server";
            }
        } catch (Exception e) {
            String detail = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            lastMessage = "Failed to load students: " + detail;
        }
        return result;
    }

    public List<AttendanceSessionOption> getSessionOptions() {
        List<AttendanceSessionOption> result = new ArrayList<>();
        try {
            JsonNode node = send("GetAttendanceSessions", new HashMap<>());
            if (node != null && node.path("success").asBoolean(false) && node.has("data")) {
                for (JsonNode item : node.get("data")) {
                    result.add(new AttendanceSessionOption(
                            item.path("sessionId").asInt(),
                            item.path("courseId").asText(),
                            item.path("sessionDate").asText(),
                            item.path("type").asText()
                    ));
                }
            } else if (node != null) {
                lastMessage = node.path("message").asText("Failed to load sessions");
            } else {
                lastMessage = "No response from server";
            }
        } catch (Exception e) {
            String detail = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            lastMessage = "Failed to load sessions: " + detail;
        }
        return result;
    }

    public JsonNode getStudentAttendance(String studentId, String viewType) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("student_id", studentId);
            data.put("view_type", viewType);
            return send("GetStudentAttendance", data);
        } catch (Exception e) {
            lastMessage = "Failed to load student attendance: " + (e.getMessage() == null ? "error" : e.getMessage());
            return null;
        }
    }

    public JsonNode getBatchAttendance(String batch, String viewType) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("batch", batch);
            data.put("view_type", viewType);
            return send("GetBatchAttendance", data);
        } catch (Exception e) {
            lastMessage = "Failed to load batch attendance: " + (e.getMessage() == null ? "error" : e.getMessage());
            return null;
        }
    }

    public JsonNode getStudentAttendanceSummary(String studentId, String viewType) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("student_id", studentId);
            data.put("view_type", viewType);
            return send("GetStudentAttendanceSummary", data);
        } catch (Exception e) {
            lastMessage = "Failed to load student attendance summary: " + (e.getMessage() == null ? "error" : e.getMessage());
            return null;
        }
    }

    public JsonNode getBatchAttendanceSummary(String batch, String viewType) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("batch", batch);
            data.put("view_type", viewType);
            return send("GetBatchAttendanceSummary", data);
        } catch (Exception e) {
            lastMessage = "Failed to load batch attendance summary: " + (e.getMessage() == null ? "error" : e.getMessage());
            return null;
        }
    }

    public JsonNode checkAttendanceEligibility(String studentId, String viewType) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("student_id", studentId);
            data.put("view_type", viewType);
            return send("CheckAttendanceEligibility", data);
        } catch (Exception e) {
            lastMessage = "Failed to check attendance eligibility: " + (e.getMessage() == null ? "error" : e.getMessage());
            return null;
        }
    }

    public JsonNode getBatchAttendanceEligibilityReport(String batch, String viewType) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("batch", batch);
            data.put("view_type", viewType);
            return send("GetBatchAttendanceEligibilityReport", data);
        } catch (Exception e) {
            lastMessage = "Failed to load batch eligibility report: " + (e.getMessage() == null ? "error" : e.getMessage());
            return null;
        }
    }

    public List<StudentAttendanceSummary> getStudentAttendanceSummary() {
        try {
            RequestDTO requestDTO = new RequestDTO(
                    "GetStudentAttendanceSummaryById",
                    null,
                    SessionManager.getToken()
            );

            String requestJson = mapper.writeValueAsString(requestDTO);
            String responseJson = client.sendRequest(requestJson);

            JsonNode root = mapper.readTree(responseJson);

            if (!root.path("success").asBoolean(false)) {
                return Collections.emptyList();
            }

            return mapper.readValue(
                    root.path("attendance").toString(),
                    new TypeReference<List<StudentAttendanceSummary>>() {}
            );

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
