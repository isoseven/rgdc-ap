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