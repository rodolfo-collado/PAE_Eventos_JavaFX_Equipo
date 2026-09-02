module com.example.reto2 {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.reto2 to javafx.fxml;
    exports com.example.reto2;
    exports com.example.reto2.controller;
    opens com.example.reto2.controller to javafx.fxml;
    exports com.example.reto2.model;
    opens com.example.reto2.model to javafx.fxml, javafx.base;
    exports com.example.reto2.service;
}