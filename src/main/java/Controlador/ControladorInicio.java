package Controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControladorInicio {
    @FXML
    private TextField nombreJugador;

    @FXML
    private void validarNombreJugador() {
        String nombre = nombreJugador.getText().trim();
        if (nombre.isEmpty()) {
            nombreJugador.setStyle("-fx-max-width: 250; -fx-border-color: red; -fx-border-width: 2px;");
            nombreJugador.setPromptText("El nombre no puede estar vacío");
        } else {
            iniciarJuego();
        }
    }

    @FXML
    private void iniciarJuego() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/VistaJuego.fxml"));
            Scene escena = new Scene(loader.load(), 1500, 705);
            Stage stage = (Stage) nombreJugador.getScene().getWindow();
            stage.setTitle("REGICIDE - Modo Solitario | Jugando como: " + nombreJugador.getText());
            stage.setScene(escena);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirReglas() {
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create("https://devirinvestments.s3.eu-west-1.amazonaws.com/media/8436589627284-Rules-ES-1.pdf"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
