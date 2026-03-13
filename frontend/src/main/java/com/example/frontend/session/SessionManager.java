package com.example.frontend.session;

public class SessionManager {

    private static String token;
    private static String role;

    public static void setToken(String t){
        token = t;
    }

    public static String getToken(){
        return token;
    }

    public static void setRole(String r){
        role = r;
    }

    public static String getRole(){
        return role;
    }

    public static void clear(){
        SessionManager.token = "";
        SessionManager.role = "";

    }
}