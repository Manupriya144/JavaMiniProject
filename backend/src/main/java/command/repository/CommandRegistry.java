package command.repository;

import command.login.LoginCommand;
import command.login.LogoutCommand;
import command.attendance.AddAttendanceCommand;
import command.attendance.DeleteAttendanceCommand;
import command.attendance.GetAttendanceByIdCommand;
import command.attendance.GetAttendanceSessionsCommand;
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
import command.student.GetStudentByUserIdCommand;
import command.attendance.UpdateAttendanceCommand;
import command.timetable.AddTimeTableCommand;
import command.user.CreateUserCommand;
import command.user.GetAllUsersCommand;
import command.user.GetUserByIdCommand;
import dao.attendance.AttendanceDAO;
import dao.medical.MedicalDAO;
import dao.notice.NoticeDAO;
import dao.student.StudentDAO;
import dao.timetable.TimeTableDAO;
import dao.user.UserDAO;
import service.attendance.AttendanceService;
import service.login.AuthService;
import service.medical.MedicalService;
import service.notice.AddNoticeService;
import service.student.StudentService;
import service.timetable.TimeTableService;
import service.user.UserService;
import utility.DataSource;

import command.finalMarks.UploadFinalMarksCommand;
import command.finalMarks.UpdateFinalMarksCommand;
import command.finalMarks.GetStudentFinalMarksCommand;
import command.finalMarks.GetBatchFinalMarksCommand;

import command.eligibility.CheckFullEligibilityCommand;
import command.eligibility.GetBatchFullEligibilityReportCommand;

import command.grade.GenerateGradeCommand;
import command.grade.GetStudentGradesCommand;
import command.grade.GetBatchGradesCommand;

import dao.finalMarks.FinalMarksDAO;
import dao.eligibility.EligibilityDAO;
import dao.grade.GradeDAO;

import service.finalMarks.FinalMarksService;
import service.eligibility.EligibilityService;
import service.grade.GradeService;



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
            StudentDAO studentDAO = new StudentDAO(connection);
            StudentService studentService = new StudentService(studentDAO);
            commands.put("GET_STUDENT_BY_USER_ID",new GetStudentByUserIdCommand(studentService));

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



            // notice related
            NoticeDAO noticeDAO = new NoticeDAO();
            AddNoticeService addNoticeService = new AddNoticeService(noticeDAO);
            commands.put("CREATE_NOTICE",new AddNoticeCommand(addNoticeService,authService));

            //time table related
            TimeTableDAO timeTableDAO = new TimeTableDAO();
            TimeTableService timeTableService = new TimeTableService(timeTableDAO);
            commands.put("CREATE_TIMETABLE",new AddTimeTableCommand(timeTableService,authService));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize CommandRegistry: " + e.getMessage());
        }
    }

    public static Command getCommand(String name) {
        return commands.get(name);
    }
}