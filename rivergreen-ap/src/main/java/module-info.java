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
    exports com.stkych.rivergreenap.archive to javafx.fxml;
    opens com.stkych.rivergreenap.archive to javafx.fxml;

    // Export and open the test package for our TableView application
    opens com.stkych.rivergreenap.archive.test to javafx.fxml, javafx.graphics, javafx.base;
    exports com.stkych.rivergreenap.archive.controller to javafx.fxml;
    opens com.stkych.rivergreenap.archive.controller to javafx.fxml;
    exports com.stkych.rivergreenap.controller.cells to javafx.fxml;
    opens com.stkych.rivergreenap.controller.cells to javafx.fxml;
    exports com.stkych.rivergreenap.archive.test;

}
