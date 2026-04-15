package dto.requestDto.finalMarks;

public class FinalMarksRequestDTO {
    private String studentId;
    private String courseId;
    private int academicYear;
    private int semester;
    private double marks;

    public FinalMarksRequestDTO() {}

    public FinalMarksRequestDTO(String studentId, String courseId, int academicYear, int semester, double marks) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.academicYear = academicYear;
        this.semester = semester;
        this.marks = marks;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public int getAcademicYear() { return academicYear; }
    public void setAcademicYear(int academicYear) { this.academicYear = academicYear; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public double getMarks() { return marks; }
    public void setMarks(double marks) { this.marks = marks; }
}