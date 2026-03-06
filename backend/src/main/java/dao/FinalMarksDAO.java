package dao;

import utility.DBConnection; 
import utility.DBConnection;
import java.sql.*;
import java.util.*;

/*
DAO = Data Access Object
This class directly communicates with the database.
All SQL queries for FINAL MARKS are written here.
*/

public class FinalMarksDAO {

    // Insert final marks to database
    public boolean uploadFinalMarks(String studentId,String courseId,
                                    int year,int level,String semester,
                                    double marks) throws Exception {

        Connection conn = DBConnection.getConnection();

        String sql =
        "INSERT INTO course_result(student_id,course_id,academic_year,academic_level,semester,total_marks) VALUES(?,?,?,?,?,?)";

        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1,studentId);
        ps.setString(2,courseId);
        ps.setInt(3,year);
        ps.setInt(4,level);
        ps.setString(5,semester);
        ps.setDouble(6,marks);

        return ps.executeUpdate() > 0;
    }

    // Update marks
    public boolean updateFinalMarks(String studentId,String courseId,
                                    int year,String semester,double marks) throws Exception {

        Connection conn = DBConnection.getConnection();

        String sql =
        "UPDATE course_result SET total_marks=? WHERE student_id=? AND course_id=? AND academic_year=? AND semester=?";

        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setDouble(1,marks);
        ps.setString(2,studentId);
        ps.setString(3,courseId);
        ps.setInt(4,year);
        ps.setString(5,semester);

        return ps.executeUpdate() > 0;
    }

    // Get marks of one student
    public List<Map<String,Object>> getStudentMarks(String studentId) throws Exception {

        Connection conn = DBConnection.getConnection();

        String sql = "SELECT * FROM course_result WHERE student_id=?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1,studentId);

        ResultSet rs = ps.executeQuery();

        List<Map<String,Object>> list = new ArrayList<>();

        while(rs.next()){

            Map<String,Object> row = new HashMap<>();

            row.put("courseId",rs.getString("course_id"));
            row.put("marks",rs.getDouble("total_marks"));
            row.put("grade",rs.getString("grade"));

            list.add(row);
        }

        return list;
    }

}