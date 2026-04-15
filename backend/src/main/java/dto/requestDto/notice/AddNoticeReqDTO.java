package dto.requestDto.notice;

public class AddNoticeReqDTO {
    private String title;
    private String description;
    private String pdf_file_path;
    private Integer created_by;

    public AddNoticeReqDTO(){}

    public AddNoticeReqDTO(String title, String description,
                           String pdf_file_path, Integer created_by) {
        this.title = title;
        this.description = description;
        this.pdf_file_path = pdf_file_path;
        this.created_by = created_by;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPdf_file_path() {
        return pdf_file_path;
    }

    public void setPdf_file_path(String pdf_file_path) {
        this.pdf_file_path = pdf_file_path;
    }

    public Integer getCreated_by() {
        return created_by;
    }

    public void setCreated_by(Integer created_by) {
        this.created_by = created_by;
    }
}
