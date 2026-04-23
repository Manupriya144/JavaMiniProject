package command.repository;

import command.course.GetAllCoursesCommand;
import command.course.GetAllCoursesCommandFull;
import command.courseMaterial.AddCourseMaterialCommand;
import command.courseMaterial.DeleteCourseMaterialCommand;
import command.courseMaterial.GetCourseMaterialsCommand;
import command.courseMaterial.UpdateCourseMaterialDeadlineCommand;
import command.lecturer.GetAllLecturersCommand;
import command.lecturerCourse.AssignLecturerCourseCommand;
import command.login.LoginCommand;
import command.login.LogoutCommand;
import command.attendance.AddAttendanceCommand;
import command.attendance.DeleteAttendanceCommand;
import command.attendance.GetAttendanceByIdCommand;
import command.attendance.GetAttendanceSessionsCommand;
import command.techofficer.GetTechOfficerDashboardStatsCommand;
import command.techofficer.GetTechOfficerProfileCommand;
import command.techofficer.UpdateTechOfficerProfileCommand;
import command.attendance.GetAttendanceStudentsCommand;
import command.attendance.CheckAttendanceEligibilityCommand;
import command.attendance.GetBatchAttendanceCommand;
import command.attendance.GetBatchAttendanceEligibilityReportCommand;
import command.attendance.GetBatchAttendanceSummaryCommand;
import command.attendance.GetStudentAttendanceCommand;
import command.attendance.GetStudentAttendanceSummaryCommand;
import command.medical.AddMedicalCommand;
import command.medical.ApproveMedicalCommand;
import command.medical.GetBatchMedicalRecordsCommand;
import command.medical.GetStudentMedicalRecordsCommand;
import command.medical.RejectMedicalCommand;
import command.medical.UpdateMedicalCommand;
import command.notice.AddNoticeCommand;
import command.course.AddCourseCommand;
import command.ca.CheckCAEligibilityCommand;
import command.ca.GetBatchCAEligibilityReportCommand;
import command.ca.GetBatchCAMarksCommand;
import command.ca.GetStudentCAMarksCommand;
import command.ca.UpdateCAMarksCommand;
import command.ca.UploadCAMarksCommand;
import command.notice.GetAllNoticeCommand;
import command.student.GetStudentByUserIdCommand;
import command.attendance.UpdateAttendanceCommand;
import command.student.UpdateStudentProfileCommand;
import command.timetable.AddTimeTableCommand;
import command.timetable.GetAllTimeTablesCommand;
import command.user.CreateUserCommand;
import command.user.GetAllUsersCommand;
import command.student.GetStudentByIdCommand;
import command.user.GetUserByIdCommand;
import dao.attendance.AttendanceDAO;
import dao.techofficer.TechOfficerDAO;
import dao.ca.CAMarkDAO;
import command.techofficer.GetMyTechOfficerProfileCommand;


import dao.lecture.LecturerDAO;
import dao.lecturerCourse.LecturerCourseDAO;
import dao.medical.MedicalDAO;
import dao.notice.NoticeDAO;
import dao.student.StudentDAO;
import dao.timetable.TimeTableDAO;
import dao.user.UserDAO;
import service.attendance.AttendanceService;
import service.ca.CAMarkService;
import service.lecture.LecturerService;
import service.lecturerCourse.LecturerCourseService;
import service.login.AuthService;
import service.medical.MedicalService;
import service.notice.NoticeService;
import service.student.StudentService;
import service.timetable.TimeTableService;
import service.user.UserService;
import service.techofficer.TechOfficerService;
import utility.DataSource;
import service.techofficer.TechOfficerService;

import command.finalMarks.UploadFinalMarksCommand;
import command.finalMarks.UpdateFinalMarksCommand;
import command.finalMarks.GetStudentFinalMarksCommand;
import command.finalMarks.GetBatchFinalMarksCommand;

import command.eligibility.CheckFullEligibilityCommand;
import command.eligibility.GetBatchFullEligibilityReportCommand;

import command.grade.GenerateGradeCommand;
import command.grade.GetGradeLookupOptionsCommand;
import command.grade.GetStudentGradesCommand;
import command.grade.GetBatchGradesCommand;
import command.gpa.CalculateCGPACommand;
import command.gpa.CalculateSGPACommand;
import command.gpa.GetBatchGPAReportCommand;
import command.gpa.GetStudentGPAReportCommand;
import command.report.GetBatchFullAcademicReportCommand;
import command.report.GetStudentFullAcademicReportCommand;
import command.undergraduate.GetAllNoticesCommand;
import command.undergraduate.GetMyAttendanceCommand;
import command.undergraduate.GetMyCoursesCommand;
import command.undergraduate.GetMyGPACommand;
import command.undergraduate.GetMyGradesCommand;
import command.undergraduate.GetMyMarksCommand;
import command.undergraduate.GetMyMedicalRecordsCommand;
import command.undergraduate.GetMyTimetableCommand;

import dao.finalMarks.FinalMarksDAO;
import dao.eligibility.EligibilityDAO;
import dao.grade.GradeDAO;
import dao.gpa.GPADAO;
import dao.course.CourseDAO;
import dao.courseMaterial.CourseMaterialDAO;
import dao.report.AcademicReportDAO;
import dao.report.UndergraduateViewDAO;

import service.finalMarks.FinalMarksService;
import service.eligibility.EligibilityService;
import service.grade.GradeService;
import service.gpa.GPAService;
import service.course.CourseService;
import service.courseMaterial.CourseMaterialService;
import service.report.AcademicReportService;
import service.report.UndergraduateViewService;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {

    private static final Map<String, Command> commands = new HashMap<>();

    public static void init() {
        try {
            Connection connection = DataSource.getInstance().getConnection();

            UserDAO userDAO = new UserDAO();
            AuthService authService = new AuthService(userDAO);

            commands.put("PING", (args, context) ->
                    context.getOutput().println("PONG")
            );

            //login
            commands.put("LOGIN", new LoginCommand(authService));
            commands.put("LOGOUT", new LogoutCommand(authService));

            // users related
            UserService userService = new UserService(userDAO);


            commands.put("GetAllUser",new GetAllUsersCommand(userService,authService));
            commands.put("CreateUser",new CreateUserCommand(userService,authService));
            commands.put("GetUserById", new GetUserByIdCommand(userService, authService));

            // student related
            StudentDAO studentDAO = new StudentDAO();
            StudentService studentService = new StudentService(studentDAO);
            commands.put("GET_STUDENT_BY_USER_ID",new GetStudentByUserIdCommand(studentService));
            commands.put("GET_STUDENT_ALL_DETAILS",new GetStudentByIdCommand(studentService,authService));
            commands.put("UPDATE_STUDENT_PROFILE",new UpdateStudentProfileCommand(studentService, authService));

            // attendance related
            AttendanceDAO attendanceDAO = new AttendanceDAO(connection);
            AttendanceService attendanceService = new AttendanceService(attendanceDAO);
            commands.put("AddAttendance", new AddAttendanceCommand(attendanceService));
            commands.put("UpdateAttendance", new UpdateAttendanceCommand(attendanceService));
            commands.put("DeleteAttendance", new DeleteAttendanceCommand(attendanceService));
            commands.put("GetAttendanceById", new GetAttendanceByIdCommand(attendanceService));
            commands.put("GetAttendanceStudents", new GetAttendanceStudentsCommand(attendanceService));
            commands.put("GetAttendanceSessions", new GetAttendanceSessionsCommand(attendanceService));
            commands.put("GetStudentAttendance", new GetStudentAttendanceCommand(attendanceService));
            commands.put("GetBatchAttendance", new GetBatchAttendanceCommand(attendanceService));
            commands.put("GetStudentAttendanceSummary", new GetStudentAttendanceSummaryCommand(attendanceService));
            commands.put("GetBatchAttendanceSummary", new GetBatchAttendanceSummaryCommand(attendanceService));
            commands.put("CheckAttendanceEligibility", new CheckAttendanceEligibilityCommand(attendanceService));
            commands.put("GetBatchAttendanceEligibilityReport", new GetBatchAttendanceEligibilityReportCommand(attendanceService));

            // CA marks related
            CAMarkDAO caMarkDAO = new CAMarkDAO(connection);
            CAMarkService caMarkService = new CAMarkService(caMarkDAO);
            commands.put("UploadCAMarks", new UploadCAMarksCommand(caMarkService));
            commands.put("UpdateCAMarks", new UpdateCAMarksCommand(caMarkService));
            commands.put("GetStudentCAMarks", new GetStudentCAMarksCommand(caMarkService));
            commands.put("GetBatchCAMarks", new GetBatchCAMarksCommand(caMarkService));
            commands.put("CheckCAEligibility", new CheckCAEligibilityCommand(caMarkService));
            commands.put("GetBatchCAEligibilityReport", new GetBatchCAEligibilityReportCommand(caMarkService));

            // medical related
            MedicalDAO medicalDAO = new MedicalDAO();
            MedicalService medicalService = new MedicalService(medicalDAO);
            commands.put("AddMedical", new AddMedicalCommand(medicalService));
            commands.put("UpdateMedical", new UpdateMedicalCommand(medicalService));
            commands.put("ApproveMedical", new ApproveMedicalCommand(medicalService));
            commands.put("RejectMedical", new RejectMedicalCommand(medicalService));
            commands.put("GetStudentMedicalRecords", new GetStudentMedicalRecordsCommand(medicalService));
            commands.put("GetBatchMedicalRecords", new GetBatchMedicalRecordsCommand(medicalService));


              // ---------------- Final Marks ----------------
            FinalMarksDAO finalMarksDAO = new FinalMarksDAO(connection);
            FinalMarksService finalMarksService = new FinalMarksService(finalMarksDAO);

            commands.put("UploadFinalMarks", new UploadFinalMarksCommand(finalMarksService));
            commands.put("UpdateFinalMarks", new UpdateFinalMarksCommand(finalMarksService));
            commands.put("GetStudentFinalMarks", new GetStudentFinalMarksCommand(finalMarksService));
            commands.put("GetBatchFinalMarks", new GetBatchFinalMarksCommand(finalMarksService));

            // ---------------- Full Eligibility ----------------
            EligibilityDAO eligibilityDAO = new EligibilityDAO(connection);
            EligibilityService eligibilityService = new EligibilityService(eligibilityDAO);

            commands.put("CheckFullEligibility", new CheckFullEligibilityCommand(eligibilityService));
            commands.put("GetBatchFullEligibilityReport", new GetBatchFullEligibilityReportCommand(eligibilityService));

            // ---------------- Grading ----------------
            GradeDAO gradeDAO = new GradeDAO(connection);
            GradeService gradeService = new GradeService(gradeDAO);

            commands.put("GenerateGrade", new GenerateGradeCommand(gradeService));
            commands.put("GetStudentGrades", new GetStudentGradesCommand(gradeService));
            commands.put("GetBatchGrades", new GetBatchGradesCommand(gradeService));
            commands.put("GetGradeLookupOptions", new GetGradeLookupOptionsCommand(gradeService));

            // ---------------- GPA ----------------
            GPADAO gpaDAO = new GPADAO();
            GPAService gpaService = new GPAService(gpaDAO);

            commands.put("CalculateSGPA", new CalculateSGPACommand(gpaService));
            commands.put("CalculateCGPA", new CalculateCGPACommand(gpaService));
            commands.put("GetStudentGPAReport", new GetStudentGPAReportCommand(gpaService));
            commands.put("GetBatchGPAReport", new GetBatchGPAReportCommand(gpaService));

            // ---------------- Undergraduate View ----------------
            UndergraduateViewDAO undergraduateViewDAO = new UndergraduateViewDAO();
            UndergraduateViewService undergraduateViewService = new UndergraduateViewService(undergraduateViewDAO);

            commands.put("GetMyAttendance", new GetMyAttendanceCommand(undergraduateViewService));
            commands.put("GetMyMedicalRecords", new GetMyMedicalRecordsCommand(undergraduateViewService));
            commands.put("GetMyCourses", new GetMyCoursesCommand(undergraduateViewService));
            commands.put("GetMyMarks", new GetMyMarksCommand(undergraduateViewService));
            commands.put("GetMyGrades", new GetMyGradesCommand(undergraduateViewService));
            commands.put("GetMyGPA", new GetMyGPACommand(gpaService));
            commands.put("GetMyTimetable", new GetMyTimetableCommand(undergraduateViewService));
            commands.put("GetAllNotices", new GetAllNoticesCommand(undergraduateViewService));

            // ---------------- Full Academic Reports ----------------
            AcademicReportDAO academicReportDAO = new AcademicReportDAO();
            AcademicReportService academicReportService = new AcademicReportService(academicReportDAO);

            commands.put("GetStudentFullAcademicReport", new GetStudentFullAcademicReportCommand(academicReportService));
            commands.put("GetBatchFullAcademicReport", new GetBatchFullAcademicReportCommand(academicReportService));



            // notice related
            NoticeDAO noticeDAO = new NoticeDAO();
            NoticeService noticeService = new NoticeService(noticeDAO);
            commands.put("CREATE_NOTICE",new AddNoticeCommand(noticeService,authService));
            commands.put("GET_ALL_NOTICE",new GetAllNoticeCommand(noticeService,authService));

            // technical officer profile related
            TechOfficerDAO techOfficerDAO = new TechOfficerDAO(connection);
            TechOfficerService techOfficerService = new TechOfficerService(techOfficerDAO);
            commands.put("GET_TECH_OFFICER_PROFILE", new GetTechOfficerProfileCommand(techOfficerService));
            commands.put("GET_MY_TECH_OFFICER_PROFILE", new GetMyTechOfficerProfileCommand(techOfficerService));
            commands.put("GET_TECH_OFFICER_DASHBOARD_STATS", new GetTechOfficerDashboardStatsCommand(techOfficerService));
            commands.put("UPDATE_TECH_OFFICER_PROFILE", new UpdateTechOfficerProfileCommand(techOfficerService));

            // course related
            CourseDAO courseDAO = new CourseDAO();
            CourseService courseService = new CourseService(courseDAO);
            commands.put("ADD_COURSE", new AddCourseCommand(courseService, authService));
            LecturerDAO lecturerDAO = new LecturerDAO();
            LecturerService lecturerService = new LecturerService(lecturerDAO);
            commands.put("GetAllLecturers", new GetAllLecturersCommand(lecturerService, authService));
            commands.put("GetAllCourses", new GetAllCoursesCommand(courseService, authService));
            LecturerCourseDAO lecturerCourseDAO = new LecturerCourseDAO();
            LecturerCourseService lecturerCourseService = new LecturerCourseService(lecturerCourseDAO);
            commands.put("AssignLecturerCourse", new AssignLecturerCourseCommand(lecturerCourseService, authService));
            commands.put("GET_ALL_COURSES_FULL", new GetAllCoursesCommandFull(courseService, authService));

            CourseMaterialDAO courseMaterialDAO = new CourseMaterialDAO();
            CourseMaterialService courseMaterialService = new CourseMaterialService(courseMaterialDAO);
            commands.put("ADD_COURSE_MATERIAL", new AddCourseMaterialCommand(courseMaterialService));
            commands.put("GET_COURSE_MATERIALS", new GetCourseMaterialsCommand(courseMaterialService));
            commands.put("UPDATE_COURSE_MATERIAL_DEADLINE", new UpdateCourseMaterialDeadlineCommand(courseMaterialService));
            commands.put("DELETE_COURSE_MATERIAL", new DeleteCourseMaterialCommand(courseMaterialService));



            //timetable related
            TimeTableDAO timeTableDAO = new TimeTableDAO();
            TimeTableService timeTableService = new TimeTableService(timeTableDAO);
            commands.put("CREATE_TIMETABLE",new AddTimeTableCommand(timeTableService,authService));
            commands.put("GET_ALL_TIMETABLES", new GetAllTimeTablesCommand(timeTableService, authService));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize CommandRegistry: " + e.getMessage());
        }
    }

    public static Command getCommand(String name) {
        return commands.get(name);
    }
}