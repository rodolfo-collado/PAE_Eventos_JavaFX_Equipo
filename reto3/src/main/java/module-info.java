module ni.edu.uam.reto3 {
    requires javafx.controls;
    requires javafx.fxml;


    exports ni.edu.uam.reto3;
    exports ni.edu.uam.reto3.controller;
    exports ni.edu.uam.reto3.model;
    opens ni.edu.uam.reto3.controller to javafx.fxml;
}
