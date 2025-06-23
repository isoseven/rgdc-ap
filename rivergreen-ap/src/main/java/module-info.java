module com.stkych.rivergreenap {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;
    requires org.jetbrains.annotations;


    opens com.stkych.rivergreenap to javafx.fxml;
    opens com.stkych.rivergreenap.model to javafx.base;
    exports com.stkych.rivergreenap;

    exports com.stkych.rivergreenap.controller to javafx.fxml;
    opens com.stkych.rivergreenap.controller to javafx.fxml;
    exports com.stkych.rivergreenap.controller.archive to javafx.fxml;
    opens com.stkych.rivergreenap.controller.archive to javafx.fxml;

    // Export and open the test package for our TableView application
    exports com.stkych.rivergreenap.test to javafx.fxml, javafx.graphics, javafx.base;
    opens com.stkych.rivergreenap.test to javafx.fxml, javafx.graphics, javafx.base;

}
