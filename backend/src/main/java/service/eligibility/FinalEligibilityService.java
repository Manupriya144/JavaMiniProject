package service.eligibility;

import dao.eligibility.FinalEligibilityDAO;
import dto.responseDto.eligibility.FinalEligibilityDTO;

import java.util.List;

public class FinalEligibilityService {

    private final FinalEligibilityDAO finalEligibilityDAO;

    public FinalEligibilityService() {
        this.finalEligibilityDAO = new FinalEligibilityDAO();
    }

    public List<FinalEligibilityDTO> getFinalEligibility(String lecturerId, String courseId) {
        return finalEligibilityDAO.getFinalEligibility(lecturerId, courseId);
    }


}