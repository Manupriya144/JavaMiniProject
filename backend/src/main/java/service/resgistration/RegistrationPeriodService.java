package service.resgistration;

import dao.registration.RegistrationPeriodDAO;
import dto.requestDto.registration.RegistrationPeriodReqDTO;

public class RegistrationPeriodService {

    private final RegistrationPeriodDAO registrationPeriodDAO;

    public RegistrationPeriodService(RegistrationPeriodDAO registrationPeriodDAO) {
        this.registrationPeriodDAO = registrationPeriodDAO;
    }

    public boolean saveOrUpdatePeriod(RegistrationPeriodReqDTO dto) {
        return registrationPeriodDAO.saveOrUpdatePeriod(dto);
    }
}