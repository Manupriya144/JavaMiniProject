package com.example.frontend.service;

import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AcademicEndpointService {

    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public AcademicEndpointService(ServerClient client) {
        this.client = client;
    }

    public JsonNode calculateSGPA(String studentId, int academicYear, int semester) {
        return send("CalculateSGPA", String.format("{\"studentId\":\"%s\",\"academicYear\":%d,\"semester\":%d}", studentId, academicYear, semester));
    }

    public JsonNode calculateCGPA(String studentId) {
        return send("CalculateCGPA", String.format("{\"studentId\":\"%s\"}", studentId));
    }

    public JsonNode getStudentGPAReport(String studentId) {
        return send("GetStudentGPAReport", String.format("{\"studentId\":\"%s\"}", studentId));
    }

    public JsonNode getBatchGPAReport(String batch, int academicYear, int semester) {
        return send("GetBatchGPAReport", String.format("{\"batch\":\"%s\",\"academicYear\":%d,\"semester\":%d}", batch, academicYear, semester));
    }

    public JsonNode getMyAttendance() { return send("GetMyAttendance", "{}"); }
    public JsonNode getMyAttendanceSummaryByCourse() { return send("GetMyAttendanceSummaryByCourse", "{}"); }
    public JsonNode getMyMedicalRecords() { return send("GetMyMedicalRecords", "{}"); }
    public JsonNode getMyCourses() { return send("GetMyCourses", "{}"); }
    public JsonNode getMyMarks() { return send("GetMyMarks", "{}"); }
    public JsonNode getMyGrades() { return send("GetMyGrades", "{}"); }
    public JsonNode getMyGPA() { return send("GetMyGPA", "{}"); }
    public JsonNode getMyTimetable() { return send("GetMyTimetable", "{}"); }
    public JsonNode getAllNotices() { return send("GetAllNotices", "{}"); }
    public JsonNode getCourseMaterials(String courseId) { 
        return send("GetCourseMaterials", String.format("{\"courseId\":\"%s\"}", courseId)); 
    }

    public JsonNode getStudentFullAcademicReport(String studentId) {
        return send("GetStudentFullAcademicReport", String.format("{\"studentId\":\"%s\"}", studentId));
    }

    public JsonNode getBatchFullAcademicReport(String batch) {
        return send("GetBatchFullAcademicReport", String.format("{\"batch\":\"%s\"}", batch));
    }

    private JsonNode send(String command, String data) {
        try {
            String request = String.format(
                    "{\"command\":\"%s\",\"data\":%s,\"token\":\"%s\"}",
                    command, data, SessionManager.getToken()
            );
            String response = client.sendRequest(request);
            return mapper.readTree(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
