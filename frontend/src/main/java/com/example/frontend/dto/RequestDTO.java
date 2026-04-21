package com.example.frontend.dto;

public class RequestDTO {
    private String command;
    private Object data;
    private String token;

    public RequestDTO() {
    }

    public RequestDTO(String command, Object data, String token) {
        this.command = command;
        this.data = data;
        this.token = token;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}