package service.techofficer;

import dao.techofficer.TechOfficerDAO;
import dto.requestDto.techofficer.UpdateTechOfficerProfileRequestDTO;
import dto.responseDto.techofficer.TechOfficerDashboardStatsResponseDTO;
import model.TechOfficer;

import java.util.Map;

public class TechOfficerService {
    private final TechOfficerDAO techOfficerDAO;

    public TechOfficerService(TechOfficerDAO techOfficerDAO) {
        this.techOfficerDAO = techOfficerDAO;
    }

    public TechOfficer getTechOfficerProfileByUserId(String userId) {
        return techOfficerDAO.getTechOfficerProfileByUserId(userId);
    }

    public boolean updateTechOfficerProfile(UpdateTechOfficerProfileRequestDTO requestDTO) {
        TechOfficer existing = techOfficerDAO.getTechOfficerProfileByUserId(requestDTO.getUserId());
        if (existing == null) {
            return false;
        }

        if (requestDTO.getUsername() != null) {
            existing.setUsername(requestDTO.getUsername().trim());
        }
        if (requestDTO.getEmail() != null) {
            existing.setEmail(requestDTO.getEmail().trim());
        }
        if (requestDTO.getPassword() != null && !requestDTO.getPassword().trim().isEmpty()) {
            existing.setPassword(requestDTO.getPassword().trim());
        }
        if (requestDTO.getContactNumber() != null) {
            existing.setContactNumber(requestDTO.getContactNumber().trim());
        }
        if (requestDTO.getProfilePicture() != null) {
            existing.setProfilePicture(requestDTO.getProfilePicture().trim());
        }
        if (requestDTO.getDepartmentId() != null) {
            existing.setDepartmentId(requestDTO.getDepartmentId().trim());
        }

        return techOfficerDAO.updateTechOfficerProfile(existing);
    }

    public TechOfficerDashboardStatsResponseDTO getDashboardStats() {
        Map<String, Integer> stats = techOfficerDAO.getDashboardStats();
        return new TechOfficerDashboardStatsResponseDTO(
                stats.getOrDefault("totalStudents", 0),
                stats.getOrDefault("attendanceSessions", 0),
                stats.getOrDefault("medicalRecords", 0),
                stats.getOrDefault("pendingApprovals", 0)
        );
    }
}
