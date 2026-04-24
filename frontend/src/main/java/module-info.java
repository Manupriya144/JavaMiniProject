module com.example.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires javafx.base;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;


    opens com.example.frontend to javafx.fxml;
    opens com.example.frontend.controller to javafx.fxml;
    opens com.example.frontend.controller.admin to javafx.fxml;
    opens com.example.frontend.controller.attendance to javafx.fxml;
    opens com.example.frontend.controller.lecturer to javafx.fxml;
    opens com.example.frontend.controller.student to javafx.fxml, javafx.base;
    opens com.example.frontend.controller.tech_officer to javafx.fxml;
    opens com.example.frontend.controller.medical to javafx.fxml;
    opens com.example.frontend.controller.ca to javafx.fxml;


    opens com.example.frontend.dto to com.fasterxml.jackson.databind, javafx.base;

    exports com.example.frontend;
    opens com.example.frontend.model to com.fasterxml.jackson.databind, javafx.fxml;
}