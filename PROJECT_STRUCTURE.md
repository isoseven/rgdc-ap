# Project Structure

```
.
├── .gitattributes
├── .gitignore
├── .idea
│   ├── .gitignore
│   ├── artifacts
│   │   └── rgdc_ap_jar.xml
│   ├── compiler.xml
│   ├── dataSources.xml
│   ├── dictionaries
│   │   └── project.xml
│   ├── encodings.xml
│   ├── jarRepositories.xml
│   ├── misc.xml
│   ├── modules.xml
│   ├── rgdc-ap.iml
│   └── vcs.xml
├── PROJECT_STRUCTURE.md
├── README.md
├── README_FIX.md
├── TASKS_TO_FIX.md
├── rivergreen-ap
│   ├── .gitignore
│   ├── .idea
│   │   ├── .gitignore
│   │   ├── dataSources.xml
│   │   ├── encodings.xml
│   │   ├── inspectionProfiles
│   │   │   └── Project_Default.xml
│   │   ├── misc.xml
│   │   └── vcs.xml
│   ├── .mvn
│   │   └── wrapper
│   │       ├── maven-wrapper.jar
│   │       └── maven-wrapper.properties
│   ├── README.md
│   ├── README_JAR.md
│   ├── RiverGreenAutoPriority.bat
│   ├── RiverGreenDebug.bat
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   ├── rivergreen-ap
│   │   └── README_JAR.md
│   ├── shorthand.txt
│   └── src
│       └── main
│           ├── java
│           │   ├── com
│           │   │   └── stkych
│           │   │       └── rivergreenap
│           │   │           ├── DatabaseConfig.java
│           │   │           ├── Launcher.java
│           │   │           ├── RiverGreenApplication.java
│           │   │           ├── RiverGreenDB.java
│           │   │           ├── SceneSwitcher.java
│           │   │           ├── controller
│           │   │           │   ├── Controller.java
│           │   │           │   ├── ControllerMain.java
│           │   │           │   ├── ControllerRuleset.java
│           │   │           │   ├── ErrorController.java
│           │   │           │   ├── RulesetConfigController.java
│           │   │           │   ├── RulesetDialogController.java
│           │   │           │   ├── RulesetDialogControllerCompat.java
│           │   │           │   ├── RulesetNameDialogController.java
│           │   │           │   └── cells
│           │   │           │       ├── RulesetItemCellFactory.java
│           │   │           │       ├── RulesetItemHeaderCell.java
│           │   │           │       ├── RulesetItemListCell.java
│           │   │           │       ├── TreatmentPlanProcedureCellFactory.java
│           │   │           │       ├── TreatmentPlanProcedureHeaderCell.java
│           │   │           │       └── TreatmentPlanProcedureListCell.java
│           │   │           ├── model
│           │   │           │   ├── RulesetItem.java
│           │   │           │   └── TreatmentPlanProcedure.java
│           │   │           └── util
│           │   │               ├── DentalCodeUtil.java
│           │   │               ├── ExecutionLogger.java
│           │   │               ├── FileUtils.java
│           │   │               └── TeethNotationUtil.java
│           │   └── module-info.java
│           └── resources
│               ├── META-INF
│               │   └── MANIFEST.MF
│               └── com
│                   └── stkych
│                       └── rivergreenap
│                           ├── main.fxml
│                           ├── main_column.fxml
│                           ├── ruleset.fxml
│                           ├── ruleset_column.fxml
│                           ├── ruleset_config.fxml
│                           ├── ruleset_dialog.fxml
│                           └── ruleset_name_dialog.fxml
└── ruleset1.csv

25 directories, 70 files
```
