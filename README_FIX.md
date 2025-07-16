# Fix for "no main manifest attribute" Error

## Issue
The error "no main manifest attribute, in C:\Users\River\Documents\GitHub\rgdc-ap\out\artifacts\rgdc_ap_jar\rgdc-ap.jar" occurs when trying to run a JAR file that doesn't have a properly configured manifest file with a Main-Class attribute.

## Solution
I've updated the IntelliJ IDEA artifact configuration to explicitly include the MANIFEST.MF file in the META-INF directory. This should ensure that when the JAR file is built by IntelliJ IDEA, it includes the MANIFEST.MF file with the Main-Class attribute set to "com.stkych.rivergreenap.RiverGreenApplication".

## Steps to Rebuild the JAR File
1. Open the project in IntelliJ IDEA
2. Go to Build > Build Artifacts > rgdc-ap:jar > Rebuild
3. This will create a new JAR file in the out/artifacts/rgdc_ap_jar directory with the correct manifest file

## Verify the Fix
To verify that the issue is resolved, try running the JAR file again:
```
java -jar C:\Users\River\Documents\GitHub\rgdc-ap\out\artifacts\rgdc_ap_jar\rgdc-ap.jar
```

If the issue is resolved, the application should start without the "no main manifest attribute" error.

## Alternative Solution
If the above solution doesn't work, you can also try the following:

1. Open the project in IntelliJ IDEA
2. Go to File > Project Structure > Artifacts
3. Select the rgdc-ap:jar artifact
4. In the "JAR Files from Libraries" section, make sure "Extract to the Target JAR" is selected
5. In the "Output Layout" tab, make sure the META-INF directory is included and contains the MANIFEST.MF file
6. Click "Apply" and "OK"
7. Rebuild the artifact as described above

## Manual Solution
If you can't use IntelliJ IDEA to rebuild the JAR file, you can also manually create a JAR file with the correct manifest:

1. Create a text file named "manifest.txt" with the following content:
```
Manifest-Version: 1.0
Main-Class: com.stkych.rivergreenap.RiverGreenApplication
Class-Path: .
```
Make sure there's a blank line at the end of the file.

2. Open a command prompt and navigate to the directory containing the compiled classes
3. Run the following command to create a JAR file with the manifest:
```
jar cfm rgdc-ap.jar manifest.txt com
```
This will create a JAR file named "rgdc-ap.jar" with the manifest file and all the compiled classes in the "com" directory.

4. Copy the JAR file to the desired location