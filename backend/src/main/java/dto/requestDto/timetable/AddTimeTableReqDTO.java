package dto.requestDto.timetable;

public class AddTimeTableReqDTO {
    private String departmentId;
    private int academicLevel;
    private String semester;   // '1' or '2'
    private String title;
    private String pdfFilePath;


    public AddTimeTableReqDTO(){

    }

    public AddTimeTableReqDTO(String departmentId, int academicLevel,
                              String semester, String title,
                              String pdfFilePath) {

        this.departmentId = departmentId;
        this.academicLevel = academicLevel;
        this.semester = semester;
        this.title = title;
        this.pdfFilePath = pdfFilePath;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPdfFilePath() {
        return pdfFilePath;
    }

    public void setPdfFilePath(String pdfFilePath) {
        this.pdfFilePath = pdfFilePath;
    }
}
