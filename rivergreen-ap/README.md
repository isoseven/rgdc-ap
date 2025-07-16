# RiverGreen Dental Application

## Building the Application

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Maven
- Launch4j (download from http://launch4j.sourceforge.net/)

### Build Steps

1. **Build the JAR file using Maven**

   Navigate to the project directory and run:
   ```
   mvn clean package
   ```
   This will create a JAR file in the `target` directory.

2. **Create an executable using Launch4j**

   There are two ways to create the executable:

   **Option 1: Using the Launch4j GUI**
   - Open Launch4j
   - Click "Open configuration" and select the `launch4j.xml` file in the project root
   - Click "Build wrapper"
   - The executable will be created in the `target` directory as `RiverGreenDental.exe`

   **Option 2: Using the Launch4j command line**
   - Navigate to the Launch4j installation directory
   - Run the following command:
     ```
     launch4jc.exe path\to\rivergreen-ap\launch4j.xml
     ```
   - The executable will be created in the `target` directory as `RiverGreenDental.exe`

## Running the Application

The executable requires a patient number as a command-line argument. You can run it from the command line:

```
RiverGreenDental.exe [patient_number]
```

Or create a shortcut with the patient number as an argument.

## Troubleshooting

- **Java version**: Make sure you have Java 17 or higher installed and available in your PATH.
- **Missing dependencies**: If you encounter errors about missing dependencies, make sure all required JAR files are in the same directory as the executable or in a `lib` subdirectory.
- **Launch4j errors**: If Launch4j fails to create the executable, check the Launch4j log for details.