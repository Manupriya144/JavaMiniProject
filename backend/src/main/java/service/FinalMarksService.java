package service;

import dao.FinalMarksDAO;
import java.util.*;

/*
Service Layer = Business Logic
It calls DAO methods and processes results.
*/

public class FinalMarksService {

    FinalMarksDAO dao = new FinalMarksDAO();

    public boolean uploadFinalMarks(String studentId,String courseId,
                                    int year,int level,String semester,
                                    double marks) throws Exception {

        return dao.uploadFinalMarks(studentId,courseId,year,level,semester,marks);
    }

    public boolean updateFinalMarks(String studentId,String courseId,
                                    int year,String semester,double marks) throws Exception {

        return dao.updateFinalMarks(studentId,courseId,year,semester,marks);
    }

    public List<Map<String,Object>> getStudentMarks(String studentId) throws Exception {

        return dao.getStudentMarks(studentId);
    }

}