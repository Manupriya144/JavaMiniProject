package command.repository;

import command.login.LoginCommand;
import command.login.LogoutCommand;
import command.student.GetStudentByUserIdCommand;
import command.user.CreateUserCommand;
import command.user.GetAllUsersCommand;
import command.user.GetUserByIdCommand;
import dao.student.StudentDAO;
import dao.user.UserDAO;
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



        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize CommandRegistry: " + e.getMessage());
        }
    }

    public static Command getCommand(String name) {
        return commands.get(name);
    }
}