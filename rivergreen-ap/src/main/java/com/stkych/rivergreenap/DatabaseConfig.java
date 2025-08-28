package com.stkych.rivergreenap;

/**
 * Configuration values for connecting to the database.
 */
public class DatabaseConfig {
    public static String DB_URL = "jdbc:mysql://localhost:3306/opendental?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    public static String DB_USER = "root";
    public static String DB_PASSWORD = "test";

    public static void setConnection(String url, String user, String password) {
        if (url != null && !url.isBlank()) {
            DB_URL = url;
        }
        if (user != null && !user.isBlank()) {
            DB_USER = user;
        }
        if (password != null && !password.isBlank()) {
            DB_PASSWORD = password;
        }
    }
}

