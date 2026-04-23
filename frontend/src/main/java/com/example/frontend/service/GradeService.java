package com.example.frontend.service;

import com.example.frontend.network.ServerClient;
import com.example.frontend.session.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class GradeService {

    private final ServerClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public GradeService(ServerClient client) {
        this.client = client;
    }

    public JsonNode generateGrade(String studentId, String courseId, int academicYear, int semester, String grade) {
        return send("GenerateGrade",
                String.format("{\"studentId\":\"%s\",\"courseId\":\"%s\",\"academicYear\":%d,\"semester\":%d,\"grade\":\"%s\"}", studentId, courseId, academicYear, semester, grade));
    }

        public JsonNode getStudentGrades(String studentId, String courseId) {
        return send("GetStudentGrades",
            String.format("{\"studentId\":\"%s\",\"courseId\":\"%s\"}", studentId, courseId));
    }

    public JsonNode getStudentGrades(String studentId, String courseId, Integer academicYear, Integer semester) {
        String yearPart = academicYear == null ? "null" : String.valueOf(academicYear);
        String semesterPart = semester == null ? "null" : String.valueOf(semester);
        return send("GetStudentGrades",
                String.format("{\"studentId\":\"%s\",\"courseId\":\"%s\",\"academicYear\":%s,\"semester\":%s}",
                        studentId, courseId, yearPart, semesterPart));
    }

        public JsonNode getBatchGrades(String batch, int academicYear, int semester) {
        return send("GetBatchGrades",
            String.format("{\"batch\":\"%s\",\"academicYear\":%d,\"semester\":%d}", batch, academicYear, semester));
    }

        public JsonNode getAllUsers() {
            return send("GetAllUser", "{}");
        }

        public JsonNode getAllCoursesFull() {
            return send("GET_ALL_COURSES_FULL", "{}");
        }

        public JsonNode getStudentDetails(String userId) {
            return send("GET_STUDENT_ALL_DETAILS", String.format("{\"userId\":\"%s\"}", userId));
        }

        public JsonNode getGradeLookupOptions(String studentId, String courseId, Integer academicYear, Integer semester) {
            String safeStudentId = studentId == null ? "" : studentId;
            String safeCourseId = courseId == null ? "" : courseId;
            String yearPart = academicYear == null ? "null" : String.valueOf(academicYear);
            String semesterPart = semester == null ? "null" : String.valueOf(semester);
            return send("GetGradeLookupOptions",
                    String.format("{\"studentId\":\"%s\",\"courseId\":\"%s\",\"academicYear\":%s,\"semester\":%s}",
                            safeStudentId, safeCourseId, yearPart, semesterPart));
        }

        public Set<String> fetchResultStudentIds() {
            return readTextOptions(getGradeLookupOptions(null, null, null, null), "studentIds");
        }

        public Set<String> fetchResultCourseIds(String studentId) {
            return readTextOptions(getGradeLookupOptions(studentId, null, null, null), "courseIds");
        }

        public Set<Integer> fetchResultAcademicYears(String studentId, String courseId) {
            return readIntOptions(getGradeLookupOptions(studentId, courseId, null, null), "academicYears");
        }

        public Set<Integer> fetchResultSemesters(String studentId, String courseId, Integer academicYear) {
            return readIntOptions(getGradeLookupOptions(studentId, courseId, academicYear, null), "semesters");
        }

        public Set<String> fetchResultGrades(String studentId, String courseId, Integer academicYear, Integer semester) {
            return readTextOptions(getGradeLookupOptions(studentId, courseId, academicYear, semester), "grades");
        }

        public Set<String> fetchStudentIds() {
            Set<String> ids = fetchResultStudentIds();
            if (ids.isEmpty()) {
                JsonNode users = getAllUsers();
                if (users != null && users.isArray()) {
                    for (JsonNode user : users) {
                        String role = user.path("role").asText("");
                        if ("Student".equalsIgnoreCase(role)) {
                            String id = user.path("userId").asText("");
                            if (!id.isBlank()) {
                                ids.add(id);
                            }
                        }
                    }
                }
            }
            return ids;
        }

        public Set<String> fetchCourseIds() {
            Set<String> ids = new TreeSet<>();
            JsonNode courses = getAllCoursesFull();
            if (courses != null && courses.isArray()) {
                for (JsonNode course : courses) {
                    String id = course.path("courseId").asText("");
                    if (!id.isBlank()) {
                        ids.add(id);
                    }
                }
            }
            return ids;
        }

        public Set<String> fetchBatches() {
            Set<String> batches = new TreeSet<>();
            for (String studentId : fetchStudentIds()) {
                JsonNode studentResponse = getStudentDetails(studentId);
                String batch = studentResponse == null ? "" : studentResponse.path("data").path("batch").asText("");
                if (!batch.isBlank()) {
                    batches.add(batch);
                }
            }
            return batches;
        }

        private Set<String> readTextOptions(JsonNode response, String key) {
            Set<String> values = new TreeSet<>();
            if (response == null) {
                return values;
            }
            JsonNode arr = response.path("data").path(key);
            if (arr.isArray()) {
                for (JsonNode node : arr) {
                    String text = node.asText("");
                    if (!text.isBlank()) {
                        values.add(text);
                    }
                }
            }
            return values;
        }

        private Set<Integer> readIntOptions(JsonNode response, String key) {
            Set<Integer> values = new TreeSet<>();
            if (response == null) {
                return values;
            }
            JsonNode arr = response.path("data").path(key);
            if (arr.isArray()) {
                for (JsonNode node : arr) {
                    if (node.isInt() || node.isLong() || node.isTextual()) {
                        try {
                            values.add(Integer.parseInt(node.asText()));
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
            return values;
        }

    private JsonNode send(String command, String data) {
        try {
            String request = String.format(
                    "{\"command\":\"%s\",\"data\":%s,\"token\":\"%s\"}",
                    command, data, SessionManager.getToken()
            );
            return mapper.readTree(client.sendRequest(request));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
