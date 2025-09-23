module org.example.regicidefx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.google.gson;

    opens Controlador to javafx.fxml;
    opens Modelo to com.google.gson;
    exports Controlador;
    exports Modelo to com.google.gson;
}