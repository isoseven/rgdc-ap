# River Green Dental Care - Automation for Treatment Plans in OpenDental

This program aims to automate certain inputs (namely, priority and diagnosis) of treatments in the treatment plan section of OpenDental.


## Setup
To use the attached binary, first download the [Java Development Kit](https://www.oracle.com/java/technologies/downloads/#jdk24-windows). This will allow you to run Java files. Then download the .jar file down below.
You can save the file anywhere, but remember where it is and note the path.

In OpenDental, go to Setup > Program Links > Add

\[PatNum] jdbc:mysql://localhost:3306/opendental?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC root password -jar

Copy the info here, and replace the path of file to open with the path of the downloaded file.
<img width="771" height="591" alt="image" src="https://github.com/user-attachments/assets/54afe525-8347-4763-bb63-3b78b724eaaa" />

Clicking the AutoPriority button on the top bar should now open the application.

Make sure to also set up these treatment plan priority definitions in Setup > Definitions > Treat' Plan Priorities.
These can be changed, and it will show up in the AutoPriority application automatically.

<img width="477" height="365" alt="image" src="https://github.com/user-attachments/assets/aad5d11b-8fd1-4b34-a948-1e34a3d695f1" />

To get started, you can go into the AutoPriority application, go to File > Open CSV Files and paste the ruleset1.csv inside. The demo ruleset will be applied.

## Instructions
placeholder photos here, instructions soon
<img width="1102" height="831" alt="image" src="https://github.com/user-attachments/assets/c4c21190-8b2e-41e4-8f64-5bd51c48b322" />
<img width="253" height="155" alt="image" src="https://github.com/user-attachments/assets/43f7eadf-0c58-4827-894e-4807d067b9fa" />
<img width="1097" height="827" alt="image" src="https://github.com/user-attachments/assets/0fba8d32-22b0-477c-ace2-ce6ab3de1178" />
<img width="1098" height="828" alt="image" src="https://github.com/user-attachments/assets/6ff676e4-1fdc-4d95-b676-364488ccb2c9" />
<img width="593" height="423" alt="image" src="https://github.com/user-attachments/assets/52ac2293-45dc-4ff0-a499-4d7ea36aaf81" />
<img width="1095" height="830" alt="image" src="https://github.com/user-attachments/assets/b2e23610-34ce-498d-b395-e214aec23928" />


> [!NOTE]
> This software is an independent product and is not developed, endorsed, or affiliated with Open Dental Software, Inc. "OpenDental" is a trademark of Open Dental Software, Inc. All product and company names are trademarks™ or registered® trademarks of their respective holders. Use of them does not imply any affiliation with or endorsement by them.
> This software is designed to work alongside OpenDental but is not officially supported by or connected to Open Dental Software, Inc. Users should contact the developer of this software directly for support or questions.
