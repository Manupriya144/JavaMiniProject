package dto.responseDto.registration;

public class StudentCourseRegistrationDTO {
    private String courseId;
    private String courseCode;
    private String courseName;
    private int courseCredit;
    private int academicLevel;
    private String semester;
    private String departmentId;
    private String registrationType;
    private boolean alreadyRegistered;

    public StudentCourseRegistrationDTO() {}

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getCourseCredit() { return courseCredit; }
    public void setCourseCredit(int courseCredit) { this.courseCredit = courseCredit; }

    public int getAcademicLevel() { return academicLevel; }
    public void setAcademicLevel(int academicLevel) { this.academicLevel = academicLevel; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getRegistrationType() { return registrationType; }
    public void setRegistrationType(String registrationType) { this.registrationType = registrationType; }

    public boolean isAlreadyRegistered() { return alreadyRegistered; }
    public void setAlreadyRegistered(boolean alreadyRegistered) { this.alreadyRegistered = alreadyRegistered; }
}