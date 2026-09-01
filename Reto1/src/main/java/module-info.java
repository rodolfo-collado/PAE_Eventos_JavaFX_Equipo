module com.example.reto1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.reto1 to javafx.fxml;
    exports com.example.reto1;
    exports com.example.reto1.controller;
    opens com.example.reto1.controller to javafx.fxml;
    exports com.example.reto1.model;
    opens com.example.reto1.model to javafx.fxml;
    exports com.example.reto1.service;
}