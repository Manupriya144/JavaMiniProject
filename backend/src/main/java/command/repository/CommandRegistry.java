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
import command.student.GetStudentByUserIdCommand;
import command.attendance.UpdateAttendanceCommand;
import command.user.CreateUserCommand;
import command.user.GetAllUsersCommand;
import command.user.GetUserByIdCommand;
import dao.attendance.AttendanceDAO;
import dao.student.StudentDAO;
import dao.user.UserDAO;
import service.attendance.AttendanceService;
import service.login.AuthService;
import service.student.StudentService;
import service.user.UserService;
import utility.HikariCPDataSource;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {

    private static final Map<String, Command> commands = new HashMap<>();

    public static void init() {
        try {
            Connection connection = HikariCPDataSource.getInstance().getConnection();

            UserDAO userDAO = new UserDAO(connection);
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


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize CommandRegistry: " + e.getMessage());
        }
    }

    public static Command getCommand(String name) {
        return commands.get(name);
    }
}