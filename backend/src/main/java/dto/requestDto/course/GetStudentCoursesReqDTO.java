package dto.requestDto.course;

public class GetStudentCoursesReqDTO {
    private String userId;

    public GetStudentCoursesReqDTO() {}

    public GetStudentCoursesReqDTO(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

