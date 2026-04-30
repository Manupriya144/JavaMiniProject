package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final Properties props = new Properties();

    static {
        try {
            File file = new File("backend.properties");

//            System.out.println("Searching config at: " + file.getAbsolutePath());
//            System.out.println("Exists: " + file.exists());

//            if (!file.exists()) {
//                throw new RuntimeException("backend.properties not found at " + file.getAbsolutePath());
//            }

            try (InputStream input = new FileInputStream(file)) {
                props.load(input);
            }

        } catch (Exception e) {
            throw new RuntimeException("Cannot load backend.properties file", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static long getLong(String key) {
        return Long.parseLong(get(key));
    }
}