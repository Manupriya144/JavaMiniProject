package dto.requestDto.login;

public class RequestDTO {
    private String command;
    private Object data;
    private String token;

    public String getCommand() {
        return command;
    }

    public Object getData() {
        return data;
    }

    public String getToken() {
        return token;
    }
}
