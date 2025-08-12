package com.stkych.rivergreenap.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for file operations.
 * Provides methods to get the ruleset directory and file paths.
 */
public class FileUtils {

    private static final String APP_DIR_NAME = "RiverGreenAP";
    private static final String RULESET_DIR_NAME = "rulesets";
    private static final Logger LOGGER = Logger.getLogger(FileUtils.class.getName());

    /**
     * Gets the application data directory.
     * This is a directory in the user's home directory where the application can store its data.
     *
     * @return The application data directory
     */
    public static File getAppDataDirectory() {
        // Get the user's home directory
        String userHome = System.getProperty("user.home");
        File appDir = new File(userHome, APP_DIR_NAME);

        // Create the directory if it doesn't exist
        if (!appDir.exists()) {
            if (!appDir.mkdir()) {
                LOGGER.severe("Failed to create application data directory: " + appDir.getAbsolutePath());
            }
        }

        return appDir;
    }

    /**
     * Gets the ruleset directory.
     * This is a directory in the application data directory where ruleset files are stored.
     *
     * @return The ruleset directory
     */
    public static File getRulesetDirectory() {
        File appDir = getAppDataDirectory();
        File rulesetDir = new File(appDir, RULESET_DIR_NAME);

        // Create the directory if it doesn't exist
        if (!rulesetDir.exists()) {
            if (!rulesetDir.mkdir()) {
                LOGGER.severe("Failed to create ruleset directory: " + rulesetDir.getAbsolutePath());
            }
        }

        return rulesetDir;
    }

    /**
     * Gets a ruleset file.
     *
     * @param rulesetName The name of the ruleset
     * @return The ruleset file
     */
    public static File getRulesetFile(String rulesetName) {
        File rulesetDir = getRulesetDirectory();
        return new File(rulesetDir, "ruleset" + rulesetName + ".csv");
    }

    /**
     * Migrates ruleset files from the current directory to the ruleset directory.
     * This is useful when upgrading from an older version of the application that stored ruleset files in the current directory.
     */
    public static void migrateRulesetFiles() {
        // Look for ruleset files in the current directory
        File currentDir = new File(".");
        File[] files = currentDir.listFiles((dir, name) -> name.startsWith("ruleset") && name.endsWith(".csv"));

        if (files != null) {
            for (File file : files) {
                String filename = file.getName();
                // Extract the ruleset name (between "ruleset" and ".csv")
                String rulesetName = filename.substring(7, filename.length() - 4);

                // Get the destination file in the ruleset directory
                File destFile = getRulesetFile(rulesetName);

                // Copy the file if it doesn't already exist in the ruleset directory
                if (!destFile.exists()) {
                    try {
                        // Create the parent directory if it doesn't exist
                        File parent = destFile.getParentFile();
                        if (parent != null && !parent.exists()) {
                            parent.mkdirs();
                        }

                        // Copy the file
                        java.nio.file.Files.copy(file.toPath(), destFile.toPath());
                        LOGGER.info("Migrated ruleset file: " + filename);
                    } catch (IOException e) {
                          LOGGER.log(Level.WARNING, "Failed to migrate ruleset file: " + filename, e);
                    }
                }
            }
        }
    }
}