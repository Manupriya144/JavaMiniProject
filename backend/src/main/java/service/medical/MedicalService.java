package service.medical;

import dao.medical.MedicalDAO;
import model.Medical;

import java.time.LocalDate;
import java.util.List;

public class MedicalService {
    private final MedicalDAO medicalDAO;
    private String lastValidationMessage = "Validation failed";

    public MedicalService(MedicalDAO medicalDAO) {
        this.medicalDAO = medicalDAO;
    }

    public Medical addMedical(String studentId, String courseId, String examType, String dateSubmitted, String medicalCopy) {
        if (isBlank(studentId)) {
            lastValidationMessage = "Student ID is required";
            return null;
        }
        if (isBlank(courseId)) {
            lastValidationMessage = "Course ID is required";
            return null;
        }
        if (!isValidExamType(examType)) {
            lastValidationMessage = "Exam type must be Mid, Final, or Attendance";
            return null;
        }
        String date = normalizeDate(dateSubmitted);
        if (date == null) {
            lastValidationMessage = "Date submitted must be a valid date";
            return null;
        }
        if ("Attendance".equalsIgnoreCase(examType)
                && !medicalDAO.hasSessionForDate(courseId.trim(), date)) {
            lastValidationMessage = "Submitted date must match an existing attendance session date for this student and course";
            return null;
        }
        if ("Attendance".equalsIgnoreCase(examType)
                && !medicalDAO.hasAbsentAttendanceForDate(studentId.trim(), courseId.trim(), date)) {
            lastValidationMessage = "Medical can be submitted only when attendance status is Absent for the selected date";
            return null;
        }

        Medical medical = new Medical(
                null,
                studentId.trim(),
                courseId.trim(),
                normalizeExamType(examType),
                date,
                normalizeText(medicalCopy),
                "Pending"
        );
        lastValidationMessage = "Validation passed";
        return medicalDAO.addMedical(medical);
    }

    public boolean updateMedical(Integer medicalId, String studentId, String courseId, String examType, String dateSubmitted, String medicalCopy) {
        if (medicalId == null) {
            lastValidationMessage = "Medical ID is required";
            return false;
        }
        if (isBlank(studentId)) {
            lastValidationMessage = "Student ID is required";
            return false;
        }
        if (isBlank(courseId)) {
            lastValidationMessage = "Course ID is required";
            return false;
        }
        if (!isValidExamType(examType)) {
            lastValidationMessage = "Exam type must be Mid, Final, or Attendance";
            return false;
        }
        String date = normalizeDate(dateSubmitted);
        if (date == null) {
            lastValidationMessage = "Date submitted must be a valid date";
            return false;
        }
        if ("Attendance".equalsIgnoreCase(examType)
                && !medicalDAO.hasSessionForDate(courseId.trim(), date)) {
            lastValidationMessage = "Submitted date must match an existing attendance session date for this student and course";
            return false;
        }
        if ("Attendance".equalsIgnoreCase(examType)
                && !medicalDAO.hasAbsentAttendanceForDate(studentId.trim(), courseId.trim(), date)) {
            lastValidationMessage = "Medical can be submitted only when attendance status is Absent for the selected date";
            return false;
        }
        Medical medical = new Medical(
                medicalId,
                studentId.trim(),
                courseId.trim(),
                normalizeExamType(examType),
                date,
                normalizeText(medicalCopy),
                null
        );
        lastValidationMessage = "Validation passed";
        return medicalDAO.updateMedical(medical);
    }

    public boolean approveMedical(Integer medicalId) {
        return medicalId != null && medicalDAO.updateStatus(medicalId, "Approved");
    }

    public boolean rejectMedical(Integer medicalId) {
        return medicalId != null && medicalDAO.updateStatus(medicalId, "Rejected");
    }

    public List<Medical> getStudentMedicalRecords(String studentId) {
        if (isBlank(studentId)) {
            return List.of();
        }
        return medicalDAO.getStudentMedicalRecords(studentId.trim());
    }

    public List<Medical> getBatchMedicalRecords(String batch) {
        if (isBlank(batch)) {
            return List.of();
        }
        return medicalDAO.getBatchMedicalRecords(batch.trim());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String normalizeCourseId(String courseId) {
        return isBlank(courseId) ? null : courseId.trim();
    }

    private String normalizeText(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private boolean isValidExamType(String examType) {
        if (isBlank(examType)) {
            return false;
        }
        String t = examType.trim();
        return "Mid".equalsIgnoreCase(t) || "Final".equalsIgnoreCase(t) || "Attendance".equalsIgnoreCase(t);
    }

    private String normalizeExamType(String examType) {
        String t = examType.trim();
        if ("Mid".equalsIgnoreCase(t)) {
            return "Mid";
        }
        if ("Final".equalsIgnoreCase(t)) {
            return "Final";
        }
        return "Attendance";
    }

    private String normalizeDate(String dateSubmitted) {
        if (isBlank(dateSubmitted)) {
            return null;
        }
        try {
            return LocalDate.parse(dateSubmitted.trim()).toString();
        } catch (Exception e) {
            return null;
        }
    }

    public String getLastValidationMessage() {
        return lastValidationMessage;
    }
}
