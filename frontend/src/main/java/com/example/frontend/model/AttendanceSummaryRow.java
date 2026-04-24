package com.example.frontend.model;

import javafx.beans.property.*;

public class AttendanceSummaryRow {

    private final StringProperty courseId;
    private final StringProperty courseName;
    private final IntegerProperty totalSessions;
    private final IntegerProperty presentCount;
    private final IntegerProperty absentCount;
    private final DoubleProperty totalHoursAttended;
    private final DoubleProperty attendancePercentage;

    public AttendanceSummaryRow(String courseId, String courseName,
                                int totalSessions, int presentCount,
                                int absentCount, double totalHoursAttended,
                                double attendancePercentage) {
        this.courseId             = new SimpleStringProperty(courseId);
        this.courseName           = new SimpleStringProperty(courseName);
        this.totalSessions        = new SimpleIntegerProperty(totalSessions);
        this.presentCount         = new SimpleIntegerProperty(presentCount);
        this.absentCount          = new SimpleIntegerProperty(absentCount);
        this.totalHoursAttended   = new SimpleDoubleProperty(totalHoursAttended);
        this.attendancePercentage = new SimpleDoubleProperty(attendancePercentage);
    }

    public String getCourseId()             { return courseId.get(); }
    public String getCourseName()           { return courseName.get(); }
    public int    getTotalSessions()        { return totalSessions.get(); }
    public int    getPresentCount()         { return presentCount.get(); }
    public int    getAbsentCount()          { return absentCount.get(); }
    public double getTotalHoursAttended()   { return totalHoursAttended.get(); }
    public double getAttendancePercentage() { return attendancePercentage.get(); }

    public StringProperty  courseIdProperty()             { return courseId; }
    public StringProperty  courseNameProperty()           { return courseName; }
    public IntegerProperty totalSessionsProperty()        { return totalSessions; }
    public IntegerProperty presentCountProperty()         { return presentCount; }
    public IntegerProperty absentCountProperty()          { return absentCount; }
    public DoubleProperty  totalHoursAttendedProperty()   { return totalHoursAttended; }
    public DoubleProperty  attendancePercentageProperty() { return attendancePercentage; }
}
