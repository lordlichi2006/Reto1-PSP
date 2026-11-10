module reto {
    requires javafx.controls;
    requires javafx.fxml;

    opens reto.client to javafx.fxml;
    exports reto.client;
    exports reto.model;
    exports reto.server;
}
