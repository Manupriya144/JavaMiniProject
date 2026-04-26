package service.ca;

import dao.ca.CAMarkDAO;
import model.ca.CAMark;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CAMarkService {
    private static final double CA_ELIGIBILITY_THRESHOLD = 40.0;
    private final CAMarkDAO caMarkDAO;

    public CAMarkService(CAMarkDAO caMarkDAO) {
        this.caMarkDAO = caMarkDAO;
    }

    public CAMark uploadCAMarks(String studentRegNo, Integer assessmentTypeId, Double marks) {
        if (isBlank(studentRegNo) || assessmentTypeId == null || !isValidMarks(marks)) {
            return null;
        }

        String normalizedRegNo = studentRegNo.trim();
        String studentId = caMarkDAO.findStudentIdByRegNo(normalizedRegNo);

        if (isBlank(studentId)) {
            return null;
        }

        return caMarkDAO.uploadCAMark(studentId, assessmentTypeId, marks);
    }

    public boolean updateCAMarks(Integer markId, Double marks) {
        if (markId == null || !isValidMarks(marks)) {
            return false;
        }
        return caMarkDAO.updateCAMark(markId, marks);
    }

    public List<CAMark> getStudentCAMarks(String studentId, String courseId) {
        if (isBlank(studentId)) {
            return List.of();
        }
        return caMarkDAO.getStudentCAMarks(studentId.trim(), normalizeOptional(courseId));
    }

    public List<CAMark> getBatchCAMarks(String batch, String courseId) {
        if (isBlank(batch)) {
            return List.of();
        }
        return caMarkDAO.getBatchCAMarks(batch.trim(), normalizeOptional(courseId));
    }

    public Map<String, Object> checkCAEligibility(String studentId, String courseId) {
        if (isBlank(studentId) || isBlank(courseId)) {
            return null;
        }
        Map<String, Object> row = caMarkDAO.getStudentCAEligibility(studentId.trim(), courseId.trim());
        if (row == null) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("studentId", studentId.trim());
            empty.put("courseId", courseId.trim());
            empty.put("caPercentage", 0.0);
            empty.put("eligible", false);
            empty.put("thresholdPercent", CA_ELIGIBILITY_THRESHOLD);
            return empty;
        }
        return enrichEligibility(row);
    }

    public List<Map<String, Object>> getBatchCAEligibilityReport(String batch, String courseId) {
        if (isBlank(batch) || isBlank(courseId)) {
            return List.of();
        }
        List<Map<String, Object>> rows = caMarkDAO.getBatchCAEligibilityReport(batch.trim(), courseId.trim());
        for (Map<String, Object> row : rows) {
            enrichEligibility(row);
        }
        return rows;
    }

    public boolean canLecturerManageCourse(String lecturerId, String courseId) {
        if (isBlank(lecturerId) || isBlank(courseId)) {
            return false;
        }
        return caMarkDAO.isCourseAssignedToLecturer(lecturerId.trim(), courseId.trim());
    }

    public boolean canLecturerManageAssessmentType(String lecturerId, Integer assessmentTypeId) {
        if (isBlank(lecturerId) || assessmentTypeId == null) {
            return false;
        }
        return caMarkDAO.isAssessmentTypeAssignedToLecturer(lecturerId.trim(), assessmentTypeId);
    }

    public boolean canLecturerManageMark(String lecturerId, Integer markId) {
        if (isBlank(lecturerId) || markId == null) {
            return false;
        }
        return caMarkDAO.isMarkAssignedToLecturer(lecturerId.trim(), markId);
    }

    public Map<String, Object> getCourseCAReference(String courseId) {
        if (isBlank(courseId)) {
            return Map.of(
                    "courseId", "",
                    "assessmentTypes", List.of(),
                    "markEntries", List.of()
            );
        }

        String normalizedCourseId = courseId.trim();
        Map<String, Object> data = new HashMap<>();
        data.put("courseId", normalizedCourseId);
        data.put("assessmentTypes", caMarkDAO.getCourseCAAssessmentTypes(normalizedCourseId));
        data.put("markEntries", caMarkDAO.getCourseCAMarkEntries(normalizedCourseId));
        return data;
    }

    private Map<String, Object> enrichEligibility(Map<String, Object> row) {
        double weightedScore = ((Number) row.getOrDefault("weightedScore", 0.0)).doubleValue();
        double totalWeight = ((Number) row.getOrDefault("totalCAWeight", 0.0)).doubleValue();
        double caPercentage = totalWeight <= 0.0 ? 0.0 : (weightedScore / totalWeight) * 100.0;
        caPercentage = round2(caPercentage);
        boolean eligible = caPercentage + 1e-9 >= CA_ELIGIBILITY_THRESHOLD;

        row.put("caPercentage", caPercentage);
        row.put("eligible", eligible);
        row.put("thresholdPercent", CA_ELIGIBILITY_THRESHOLD);
        row.put("eligibilityCategory", eligible ? "Eligible" : "NotEligible");
        return row;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String normalizeOptional(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private boolean isValidMarks(Double marks) {
        return marks != null && marks >= 0.0 && marks <= 100.0;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
