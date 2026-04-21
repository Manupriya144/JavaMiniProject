package dto.responseDto.course;

public class CourseAllResponseDTO {
    private String courseId;
    private String courseCode;
    private String name;
    private int courseCredit;
    private int academicLevel;
    private String semester;
    private String departmentId;

    public CourseAllResponseDTO() {
    }

    public CourseAllResponseDTO(String courseId, String courseCode, String name,
                             int courseCredit, int academicLevel,
                             String semester, String departmentId) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.name = name;
        this.courseCredit = courseCredit;
        this.academicLevel = academicLevel;
        this.semester = semester;
        this.departmentId = departmentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCourseCredit() {
        return courseCredit;
    }

    public void setCourseCredit(int courseCredit) {
        this.courseCredit = courseCredit;
    }

    public int getAcademicLevel() {
        return academicLevel;
    }

    public void setAcademicLevel(int academicLevel) {
        this.academicLevel = academicLevel;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public String toString() {
        return courseCode + " - " + name;
    }
}
