package service;

/*
Generates grade based on marks
*/

public class GradeService {

    public String generateGrade(double marks){

        if(marks >= 85) return "A+";
        if(marks >= 70) return "A";
        if(marks >= 55) return "B";
        if(marks >= 40) return "C";
        if(marks >= 30) return "D";

        return "E";
    }
}