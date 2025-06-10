module com.stkych.rivergreenap {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.stkych.rivergreenap to javafx.fxml;
    exports com.stkych.rivergreenap;

    exports com.stkych.rivergreenap.controller to javafx.fxml;
    opens com.stkych.rivergreenap.controller to javafx.fxml;

}