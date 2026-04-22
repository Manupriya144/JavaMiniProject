package com.example.frontend.session;

public class SessionManager {

    private static String token;
    private static String role;
    private static String userId;

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
        SessionManager.userId = null;

    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        SessionManager.userId = userId;
    }
}