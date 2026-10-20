module reto {
    requires javafx.controls;
    requires javafx.fxml;

    opens reto to javafx.fxml;
    exports reto;
}
