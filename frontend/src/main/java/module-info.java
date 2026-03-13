module com.example.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires com.fasterxml.jackson.databind;
    requires javafx.graphics;

    opens com.example.frontend to javafx.fxml;
    opens com.example.frontend.controller to javafx.fxml;

    exports com.example.frontend;
}