package command;

import dao.UserDAO;
import service.AuthService;
import service.UserService;

import service.FinalMarksService;
import service.EligibilityService;
import service.GradeService;

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
            UserService userService = new UserService(userDAO);

            FinalMarksService finalMarksService = new FinalMarksService();
            EligibilityService eligibilityService = new EligibilityService();
            GradeService gradeService = new GradeService();


            commands.put("PING", (args, context) ->
                    context.getOutput().println("PONG")
            );

            //login
            commands.put("LOGIN", new LoginCommand(authService));
            commands.put("LOGOUT", new LogoutCommand(authService));

            //get all users
            
            commands.put("GetAllUser",new GetAllUsersCommand(userService,authService));

            // create new user
            commands.put("CreateUser",new CreateUserCommand(userService,authService));


           /* =========================
               FINAL MARKS COMMANDS
               ========================= */

            // Upload final marks
            commands.put("UploadFinalMarks",
                    new UploadFinalMarksCommand(finalMarksService));

            // Update final marks
            commands.put("UpdateFinalMarks",
                    new UpdateFinalMarksCommand(finalMarksService));

            // Get marks of one student
            commands.put("GetStudentFinalMarks",
                    new GetStudentFinalMarksCommand(finalMarksService));

            // Get marks of entire batch
            commands.put("GetBatchFinalMarks",
                    new GetBatchFinalMarksCommand(finalMarksService));


            /* =========================
                FULL ELIGIBILITY
               ========================= */

            // Check eligibility of one student
            commands.put("CheckFullEligibility",
                    new CheckFullEligibilityCommand(eligibilityService));

            // Get eligibility report of batch
            commands.put("GetBatchFullEligibilityReport",
                    new GetBatchFullEligibilityReportCommand(eligibilityService));


            /* =========================
                GRADING SYSTEM
               ========================= */

            // Generate grade for marks
            commands.put("GenerateGrade",
                    new GenerateGradeCommand(gradeService));

            // Get grades of one student
            commands.put("GetStudentGrades",
                    new GetStudentGradesCommand(gradeService));

            // Get grades of entire batch
            commands.put("GetBatchGrades",
                    new GetBatchGradesCommand(gradeService));



        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize CommandRegistry: " + e.getMessage());
        }
    }

    public static Command getCommand(String name) {
        return commands.get(name);
    }
}