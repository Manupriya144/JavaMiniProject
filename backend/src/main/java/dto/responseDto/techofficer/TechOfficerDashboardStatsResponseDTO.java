package dto.responseDto.techofficer;

public class TechOfficerDashboardStatsResponseDTO {
    private int totalStudents;
    private int attendanceSessions;
    private int medicalRecords;
    private int pendingApprovals;

    public TechOfficerDashboardStatsResponseDTO() {
    }

    public TechOfficerDashboardStatsResponseDTO(int totalStudents, int attendanceSessions, int medicalRecords, int pendingApprovals) {
        this.totalStudents = totalStudents;
        this.attendanceSessions = attendanceSessions;
        this.medicalRecords = medicalRecords;
        this.pendingApprovals = pendingApprovals;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getAttendanceSessions() {
        return attendanceSessions;
    }

    public void setAttendanceSessions(int attendanceSessions) {
        this.attendanceSessions = attendanceSessions;
    }

    public int getMedicalRecords() {
        return medicalRecords;
    }

    public void setMedicalRecords(int medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    public int getPendingApprovals() {
        return pendingApprovals;
    }

    public void setPendingApprovals(int pendingApprovals) {
        this.pendingApprovals = pendingApprovals;
    }
}
