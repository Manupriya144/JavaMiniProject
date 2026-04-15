package com.example.frontend.model;

public class Eligibility {
    private String studentId;
    private double attendance;
    private double caMarks;
    private boolean eligible;

    public Eligibility() {}

    public Eligibility(String studentId, double attendance, double caMarks, boolean eligible) {
        this.studentId = studentId;
        this.attendance = attendance;
        this.caMarks = caMarks;
        this.eligible = eligible;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public double getAttendance() {
        return attendance;
    }

    public void setAttendance(double attendance) {
        this.attendance = attendance;
    }

    public double getCaMarks() {
        return caMarks;
    }

    public void setCaMarks(double caMarks) {
        this.caMarks = caMarks;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }
}
