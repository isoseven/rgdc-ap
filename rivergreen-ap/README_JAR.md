# Creating an Executable JAR for RiverGreen Application

This document provides instructions on how to create an executable JAR file for the RiverGreen Application.

## Prerequisites

- Java Development Kit (JDK) 17 or later installed
- JAVA_HOME environment variable set to your JDK installation directory

## Building the JAR

1. Open a command prompt or terminal
2. Navigate to the project root directory (where the `pom.xml` file is located)
3. Run the following command:

```
.\mvnw.cmd clean package
```

This will create an executable JAR file in the `target` directory named `rivergreen-ap-1.0-SNAPSHOT.jar`.

## Running the JAR

To run the application, use the following command:

```
java -jar target\rivergreen-ap-1.0-SNAPSHOT.jar [patient_number]
```

Replace `[patient_number]` with the actual patient number you want to use.

For example:

```
java -jar target\rivergreen-ap-1.0-SNAPSHOT.jar 12345
```

## Troubleshooting

### JAVA_HOME not found

If you see an error like "JAVA_HOME not found in your environment", you need to set the JAVA_HOME environment variable:

1. Find your JDK installation directory (e.g., `C:\Program Files\Java\jdk-17`)
2. Set the JAVA_HOME environment variable:
   - Windows: Open Command Prompt as administrator and run:
     ```
     setx JAVA_HOME "C:\Program Files\Java\jdk-17"
     ```
   - Close and reopen Command Prompt after setting the variable

### JavaFX Runtime Components Missing

If you encounter errors related to missing JavaFX components, make sure you're using the JAR file created by the maven-shade-plugin, which includes all dependencies.

## Notes

- The executable JAR includes all necessary dependencies, including JavaFX components
- The application requires a patient number as a command-line argument
- The JAR file is self-contained and can be distributed to other machines with Java installed

## Creating a Windows Shortcut (No Command Prompt)

To create a shortcut that runs the application without showing the command prompt:

1. **Create a batch file**:
   - Create a new text file named `RiverGreen.bat` in any convenient location
   - Add the following content to the file:
     ```
     @echo off
     if "%~1"=="" (
         start javaw -jar %USERPROFILE%\Downloads\rivergreen-ap-1.0-SNAPSHOT.jar 12345
     ) else (
         start javaw -jar %USERPROFILE%\Downloads\rivergreen-ap-1.0-SNAPSHOT.jar %~1
     )
     ```
   - This batch file will use the default patient number (12345) if no argument is provided, or use the provided argument as the patient number
   - Save the file
   - Note: A sample batch file has been provided in the project directory that you can use as a template

2. **Create a shortcut to the batch file**:
   - Right-click on the batch file and select "Create shortcut"
   - Right-click on the newly created shortcut and select "Properties"
   - In the "Run" dropdown, select "Minimized" (this will minimize any brief flash of the command window)
   - Optionally, click "Change Icon" to select a custom icon for your shortcut
   - Click "OK" to save the changes

3. **Move the shortcut to a convenient location**:
   - You can move the shortcut to your Desktop, Start Menu, or any other location
   - To add to Start Menu: Copy the shortcut to `C:\ProgramData\Microsoft\Windows\Start Menu\Programs`

4. **Using the shortcut**:
   - Double-click the shortcut to run the application without seeing the command prompt
   - The application will start with the default patient number (12345)
   - To specify a different patient number, you can modify the shortcut's properties:
     - Right-click on the shortcut and select "Properties"
     - In the "Target" field, add the patient number after the path to the batch file
     - For example: `C:\Path\To\RiverGreen.bat 67890`
     - Click "OK" to save the changes

Note: The `javaw` command is used instead of `java` to run the application without a console window.

## Debugging and Testing

When testing the application, you may want to see command output and error messages. The default batch file uses `javaw`, which runs without showing a console window. For debugging purposes, you can:

1. **Use the debug batch file**:
   - A debug batch file named `RiverGreenDebug.bat` is provided in the project directory
   - This batch file uses `java` instead of `javaw`, which keeps the console window open
   - Run it the same way as the regular batch file:
     ```
     RiverGreenDebug.bat [patient_number]
     ```
   - All console output (System.out.println statements) and error messages will be visible in the console window

2. **Run the JAR directly with java**:
   - Open a command prompt
   - Navigate to the directory containing the JAR file or use the full path
   - Run the JAR with the `java` command:
     ```
     java -jar %USERPROFILE%\Downloads\rivergreen-ap-1.0-SNAPSHOT.jar 12345
     ```
   - This will show all console output and error messages

3. **Enable console output in your application code**:
   - Add System.out.println statements to your code for debugging
   - Make sure to run with one of the methods above to see the output

## Customizing the Batch File

You can customize the batch file for different scenarios:

1. **Different default patient number**:
   - Edit the batch file and change the default number in the if statement:
     ```
     if "%~1"=="" (
         start javaw -jar %USERPROFILE%\Downloads\rivergreen-ap-1.0-SNAPSHOT.jar 67890
     ) else (
         start javaw -jar %USERPROFILE%\Downloads\rivergreen-ap-1.0-SNAPSHOT.jar %~1
     )
     ```

2. **Different JAR location**:
   - If you've moved the JAR file to a different location, update the path in both places:
     ```
     if "%~1"=="" (
         start javaw -jar C:\Path\To\Your\rivergreen-ap-1.0-SNAPSHOT.jar 12345
     ) else (
         start javaw -jar C:\Path\To\Your\rivergreen-ap-1.0-SNAPSHOT.jar %~1
     )
     ```
   - For JAR files in your Downloads folder, use the %USERPROFILE% environment variable:
     ```
     if "%~1"=="" (
         start javaw -jar %USERPROFILE%\Downloads\rivergreen-ap-1.0-SNAPSHOT.jar 12345
     ) else (
         start javaw -jar %USERPROFILE%\Downloads\rivergreen-ap-1.0-SNAPSHOT.jar %~1
     )
     ```
   - This ensures the batch file will work regardless of the Windows username

3. **Creating multiple shortcuts for different patients**:
   - Create multiple shortcuts to the same batch file
   - For each shortcut, modify the "Target" field to include a different patient number
   - For example: `C:\Path\To\RiverGreen.bat 12345`, `C:\Path\To\RiverGreen.bat 67890`
   - Give each shortcut a descriptive name (e.g., "RiverGreen - Patient 12345")

4. **Running the batch file from the command line**:
   - Open a command prompt
   - Navigate to the directory containing the batch file
   - Run the batch file with a patient number as an argument:
     ```
     RiverGreen.bat 67890
     ```
   - If no argument is provided, the default patient number will be used:
     ```
     RiverGreen.bat
     ```
