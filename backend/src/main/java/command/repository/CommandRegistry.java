package command.repository;

import command.attendance.*;
import command.ca.*;
import command.course.*;
import command.courseMeterial.AddCourseMaterialCommand;
import command.courseMeterial.DeleteCourseMaterialCommand;
import command.courseMeterial.GetCourseMaterialsCommand;
import command.eligibility.GetCAEligibilityCommand;
import command.eligibility.GetFinalEligibilityCommand;
import command.eligibility.GetStudentEligibilityCommand;
import command.eligibility.GetStudentOwnEligibilityCommand;
import command.finalMarks.UploadFinalMarksCommand;
import command.lecturer.GetAllLecturersCommand;
import command.lecturer.GetLecturerDashboardStatsCommand;
import command.lecturer.GetLecturerProfileCommand;
import command.lecturer.UpdateLecturerProfileCommand;
import command.lecturerCourse.AssignLecturerCourseCommand;
import command.lecturerCourse.GetLecturerCoursesCommand;
import command.login.LoginCommand;
import command.login.LogoutCommand;
import command.medical.*;
import command.registration.GetStudentRegistrationCoursesCommand;
import command.registration.RegisterStudentCourseCommand;
import command.registration.SaveRegistrationPeriodCommand;
import command.result.GenerateCourseResultCommand;
import command.result.GenerateGradeGPACommand;
import command.result.GetStudentCourseMarksCommand;
import command.result.SaveSemesterResultsCommand;
import command.session.AddLectureSessionCommand;
import command.student.*;
import command.techofficer.GetTechOfficerDashboardStatsCommand;
import command.techofficer.GetTechOfficerProfileCommand;
import command.techofficer.UpdateTechOfficerProfileCommand;
import command.notice.AddNoticeCommand;
import command.ca.UploadCAMarksCommand;
import command.notice.GetAllNoticeCommand;
import command.timetable.AddTimeTableCommand;
import command.timetable.GetAllTimeTablesCommand;
import command.user.*;
import dao.attendance.AttendanceDAO;
import dao.finalMarks.FinalMarksDAO;
import dao.lecture.LecturerDashboardDAO;
import dao.lecture.LecturerProfileDAO;
import dao.lectureCourse.LecturerCourseDAO;
import dao.lecturerMeterial.CourseMaterialDAO;
import dao.registration.RegistrationPeriodDAO;
import dao.registration.StudentCourseRegistrationDAO;
import dao.session.SessionDAO;
import dao.techofficer.TechOfficerDAO;
import dao.ca.CAMarkDAO;
import command.techofficer.GetMyTechOfficerProfileCommand;


import dao.lecture.LecturerDAO;

import dao.medical.MedicalDAO;
import dao.notice.NoticeDAO;
import dao.student.StudentDAO;
import dao.timetable.TimeTableDAO;
import dao.user.AdminDashboardDAO;
import dao.user.AdminDashboardDAOImpl;
import dao.user.UserDAO;
import service.attendance.AttendanceService;
import service.ca.CAMarkService;
import service.courseMeterial.CourseMaterialService;
import service.eligibility.CAEligibilityService;
import service.eligibility.FinalEligibilityService;
import service.eligibility.StudentEligibilityService;
import service.finalMarks.FinalMarksService;
import service.lecture.LecturerDashboardService;
import service.lecture.LecturerProfileService;
import service.lecture.LecturerService;
import service.lecturerCourse.LecturerCourseService;
import service.login.AuthService;
import service.medical.MedicalService;
import service.notice.NoticeService;
import service.resgistration.RegistrationPeriodService;
import service.resgistration.StudentCourseRegistrationService;
import service.result.CourseResultGeneratorService;
import service.result.GradeGPAService;
import service.result.StudentCourseMarksService;
import service.session.SessionService;
import service.student.StudentService;
import service.timetable.TimeTableService;
import service.user.AdminDashboardService;
import service.user.UserService;
import service.techofficer.TechOfficerService;


import dao.course.CourseDAO;


import service.course.CourseService;


import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {

    private static final Map<String, Command> commands = new HashMap<>();

    public static void init() {
        try {

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
            AdminDashboardDAO adminDashboardDAO = new AdminDashboardDAOImpl();
            AdminDashboardService adminDashboardService = new AdminDashboardService(adminDashboardDAO);
            commands.put("GET_ADMIN_STATS",new GetAdminStatsCommand(adminDashboardService,authService));


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
            AttendanceDAO attendanceDAO = new AttendanceDAO();
            AttendanceService attendanceService = new AttendanceService(attendanceDAO);
            commands.put("AddAttendance", new AddAttendanceCommand(attendanceService));
            commands.put("UpdateAttendance", new UpdateAttendanceCommand(attendanceService));
            commands.put("DeleteAttendance", new DeleteAttendanceCommand(attendanceService));
            commands.put("GetAttendanceById", new GetAttendanceByIdCommand(attendanceService));
            commands.put("GetAttendanceStudents", new GetAttendanceStudentsCommand(attendanceService));
            commands.put("GetAttendanceSessions", new GetAttendanceSessionsCommand(attendanceService));
            commands.put("GetStudentAttendance", new GetStudentAttendanceCommand(attendanceService));
            commands.put("GetBatchAttendance", new GetBatchAttendanceCommand(attendanceService));
            commands.put("GetAllAttendance", new GetAllAttendanceCommand(attendanceService));
            commands.put("GetStudentAttendanceSummary", new GetStudentAttendanceSummaryCommand(attendanceService));
            commands.put("GetBatchAttendanceSummary", new GetBatchAttendanceSummaryCommand(attendanceService));
            commands.put("CheckAttendanceEligibility", new CheckAttendanceEligibilityCommand(attendanceService));
            commands.put("GetBatchAttendanceEligibilityReport", new GetBatchAttendanceEligibilityReportCommand(attendanceService));
            commands.put("GetMedicalEligibleStudents", new GetMedicalEligibleStudentsCommand(attendanceService));
            commands.put("GetMedicalEligibleCourses", new GetMedicalEligibleCoursesCommand(attendanceService));
            StudentEligibilityService studentEligibilityService = new StudentEligibilityService();
            commands.put("GET_STUDENT_ELIGIBILITY",
                    new GetStudentEligibilityCommand(studentEligibilityService, authService));




            // CA marks related
            CAMarkDAO caMarkDAO = new CAMarkDAO();
            CAEligibilityService caEligibilityService = new CAEligibilityService();
            FinalEligibilityService finalEligibilityService = new FinalEligibilityService();
            StudentCourseMarksService studentCourseMarksService = new StudentCourseMarksService();
            CAMarkService caMarkService = new CAMarkService(caMarkDAO);
            commands.put("UploadCAMarks", new UploadCAMarksCommand(caMarkService));
            commands.put("UpdateCAMarks", new UpdateCAMarksCommand(caMarkService));
            commands.put("GetStudentCAMarks", new GetStudentCAMarksCommand(caMarkService));
            commands.put("GetBatchCAMarks", new GetBatchCAMarksCommand(caMarkService));
            commands.put("CheckCAEligibility", new CheckCAEligibilityCommand(caMarkService));
            commands.put("GetBatchCAEligibilityReport", new GetBatchCAEligibilityReportCommand(caMarkService));
            commands.put("GetCourseCAReference", new GetCourseCAReferenceCommand(caMarkService));
            commands.put("GET_CA_ELIGIBILITY",
                    new GetCAEligibilityCommand(caEligibilityService, authService));

            commands.put("GET_FINAL_ELIGIBILITY",
                    new GetFinalEligibilityCommand(finalEligibilityService, authService));
            commands.put("GET_STUDENT_COURSE_MARKS",
                    new GetStudentCourseMarksCommand(studentCourseMarksService, authService));

            CourseResultGeneratorService courseResultGeneratorService =
                    new CourseResultGeneratorService();

            commands.put("GENERATE_COURSE_RESULT",
                    new GenerateCourseResultCommand(courseResultGeneratorService, authService));
            GradeGPAService gradeGPAService = new GradeGPAService();

            commands.put("GENERATE_GPA",
                    new GenerateGradeGPACommand(gradeGPAService, authService));

            commands.put("SAVE_SEMESTER_RESULTS",
                    new SaveSemesterResultsCommand(gradeGPAService, authService));

            // medical related
            MedicalDAO medicalDAO = new MedicalDAO();
            MedicalService medicalService = new MedicalService(medicalDAO);
            commands.put("AddMedical", new AddMedicalCommand(medicalService));
            commands.put("UpdateMedical", new UpdateMedicalCommand(medicalService));
            commands.put("ApproveMedical", new ApproveMedicalCommand(medicalService));
            commands.put("RejectMedical", new RejectMedicalCommand(medicalService));
            commands.put("GetStudentMedicalRecords", new GetStudentMedicalRecordsCommand(medicalService));
            commands.put("GetMyMedicalRecords", new GetStudentMedicalRecordsCommand(medicalService));





            // notice related
            NoticeDAO noticeDAO = new NoticeDAO();
            NoticeService noticeService = new NoticeService(noticeDAO);
            commands.put("CREATE_NOTICE",new AddNoticeCommand(noticeService,authService));
            commands.put("GET_ALL_NOTICE",new GetAllNoticeCommand(noticeService,authService));

            // technical officer profile related
            TechOfficerDAO techOfficerDAO = new TechOfficerDAO();
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
            commands.put("UPDATE_COURSE", new UpdateCourseCommand(courseService, authService));
            commands.put("DELETE_COURSE", new DeleteCourseCommand(courseService, authService));


            LecturerCourseDAO lecturerCourseDAO = new LecturerCourseDAO();
            LecturerCourseService lecturerCourseService = new LecturerCourseService(lecturerCourseDAO);
            commands.put("AssignLecturerCourse", new AssignLecturerCourseCommand(lecturerCourseService, authService));
            commands.put("GET_ALL_COURSES_FULL", new GetAllCoursesCommandFull(courseService, authService));
            SessionService sessionService = new SessionService(new SessionDAO());
            commands.put("ADD_LECTURE_SESSION", new AddLectureSessionCommand(sessionService,authService));
            commands.put("GET_LECTURER_COURSES",
                    new GetLecturerCoursesCommand(lecturerCourseService, authService));

            CourseMaterialDAO courseMaterialDAO = new CourseMaterialDAO();
            CourseMaterialService courseMaterialService = new CourseMaterialService(courseMaterialDAO);
            commands.put("ADD_COURSE_MATERIAL", new AddCourseMaterialCommand(courseMaterialService,authService));
            commands.put("GET_COURSE_MATERIALS", new GetCourseMaterialsCommand(courseMaterialService,authService));
            commands.put("GET_STUDENT_COURSE_MATERIALS",
                    new GetStudentCourseMaterialsCommand(courseMaterialService, authService));


            //timetable related
            TimeTableDAO timeTableDAO = new TimeTableDAO();
            TimeTableService timeTableService = new TimeTableService(timeTableDAO);
            commands.put("CREATE_TIMETABLE",new AddTimeTableCommand(timeTableService,authService));
            commands.put("GET_ALL_TIMETABLES", new GetAllTimeTablesCommand(timeTableService, authService));
            commands.put("DELETE_COURSE_MATERIAL",
                    new DeleteCourseMaterialCommand(courseMaterialService, authService));


            // general END POINTS
            commands.put("GetStudentAttendanceSummaryById",
                    new GetStudentAttendanceSummaryByIdCommand(attendanceService, authService));

            commands.put(
                    "GET_STUDENT_DASHBOARD",
                    new GetStudentDashboardCommand(studentService, authService));

            commands.put("GET_STUDENT_REGISTERED_COURSES",
                    new GetStudentRegisteredCoursesCommand(studentService, authService));

            commands.put("GET_STUDENT_OWN_ELIGIBILITY",
                    new GetStudentOwnEligibilityCommand(studentEligibilityService, authService)
            );
            LecturerDashboardDAO lecturerDashboardDAO = new LecturerDashboardDAO();
            LecturerDashboardService lecturerDashboardService = new LecturerDashboardService(lecturerDashboardDAO);
            commands.put("UPDATE_USER", new UpdateUserCommand(userService, authService));
            commands.put("DELETE_USER", new DeleteUserCommand(userService, authService));
            commands.put("GET_LECTURER_DASHBOARD_STATS",
                    new GetLecturerDashboardStatsCommand(lecturerDashboardService, authService)
            );


            //final marks related
            FinalMarksDAO finalMarksDAO = new FinalMarksDAO();
            FinalMarksService finalMarksService = new FinalMarksService(finalMarksDAO);
            commands.put("UPLOAD_FINAL_MARKS",new UploadFinalMarksCommand(finalMarksService,authService));


            //course registration related
            RegistrationPeriodDAO registrationPeriodDAO = new RegistrationPeriodDAO();
            RegistrationPeriodService registrationPeriodService =
                    new RegistrationPeriodService(registrationPeriodDAO);

            commands.put("SAVE_REGISTRATION_PERIOD",
                    new SaveRegistrationPeriodCommand(registrationPeriodService, authService)
            );
            StudentCourseRegistrationDAO studentCourseRegistrationDAO =
                    new StudentCourseRegistrationDAO();

            StudentCourseRegistrationService studentCourseRegistrationService =
                    new StudentCourseRegistrationService(studentCourseRegistrationDAO);

            commands.put("GET_STUDENT_REGISTRATION_COURSES",
                    new GetStudentRegistrationCoursesCommand(studentCourseRegistrationService, authService)
            );

            commands.put("REGISTER_STUDENT_COURSE",
                    new RegisterStudentCourseCommand(studentCourseRegistrationService, authService)
            );

            LecturerProfileDAO lecturerProfileDAO = new LecturerProfileDAO();
            LecturerProfileService lecturerProfileService =
                    new LecturerProfileService(lecturerProfileDAO);

            commands.put(
                    "GET_LECTURER_PROFILE",
                    new GetLecturerProfileCommand(lecturerProfileService, authService)
            );

            commands.put(
                    "UPDATE_LECTURER_PROFILE",
                    new UpdateLecturerProfileCommand(lecturerProfileService, authService)
            );

            commands.put("GetBatchMedicalRecords", new GetBatchMedicalRecordsCommand(medicalService));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize CommandRegistry: " + e.getMessage());
        }
    }

    public static Command getCommand(String name) {
        return commands.get(name);
    }
}