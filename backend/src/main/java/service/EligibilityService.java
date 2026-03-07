package service;

/*
Checks student eligibility for final exam
*/

public class EligibilityService {

    public boolean checkEligibility(double attendance,double ca){

        return attendance >= 80 && ca >= 40;
    }
}