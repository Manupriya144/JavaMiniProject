package com.example.frontend.network;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class FrontendConfig {

    private static final Properties props = new Properties();

    static {
        try {
            InputStream input = new FileInputStream("frontend.properties");
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Cannot load frontend.properties file", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }
}