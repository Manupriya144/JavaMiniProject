package model;

import java.time.LocalDateTime;

public class Notice {

    private int notice_id;
    private String title;
    private String description;
    private String pdf_file_path;
    private Integer created_by;
    private LocalDateTime created_at;

    // Default Constructor
    public Notice() {
    }

    // Parameterized Constructor
    public Notice(int notice_id, String title, String description, String pdf_file_path,
                  Integer created_by, LocalDateTime created_at) {
        this.notice_id = notice_id;
        this.title = title;
        this.description = description;
        this.pdf_file_path = pdf_file_path;
        this.created_by = created_by;
        this.created_at = created_at;
    }

    // Getters and Setters
    public int getNotice_id() {
        return notice_id;
    }

    public void setNotice_id(int notice_id) {
        this.notice_id = notice_id;
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

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}