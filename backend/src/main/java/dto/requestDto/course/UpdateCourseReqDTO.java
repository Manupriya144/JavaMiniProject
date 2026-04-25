package dto.requestDto.course;

public class UpdateCourseReqDTO {
    private String courseId;
    private String courseCode;
    private String name;
    private int courseCredit;
    private int academicLevel;
    private String semester;
    private String departmentId;

    public UpdateCourseReqDTO() {}

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCourseCredit() { return courseCredit; }
    public void setCourseCredit(int courseCredit) { this.courseCredit = courseCredit; }

    public int getAcademicLevel() { return academicLevel; }
    public void setAcademicLevel(int academicLevel) { this.academicLevel = academicLevel; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
}