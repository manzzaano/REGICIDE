package Utilidades;

import Modelo.Carta;
import Modelo.Juego;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Guardado {

    public static Juego cargarPartida() {
        Juego juego = null;
        File dir = new File("src/main/resources/PartidasGuardadas");
        File[] archivos = dir.listFiles((d, n) -> n.startsWith("partida_") && n.endsWith(".json"));
        if (archivos == null || archivos.length == 0) {
            new Alert(Alert.AlertType.INFORMATION, "No hay partidas guardadas.").showAndWait();
            return null;
        }

        List<String> opciones = new ArrayList<>();
        for (File f : archivos) {
            opciones.add(f.getName().replace("partida_", "").replace(".json", ""));
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(opciones.get(0), opciones);
        dialog.setTitle("Cargar Partida");
        dialog.setHeaderText("Selecciona partida guardada:");
        dialog.setContentText("Número:");
        Optional<String> res = dialog.showAndWait();
        if (res.isEmpty()) {
            return null;
        }

        File sel = new File(dir, "partida_" + res.get() + ".json");
        try (Scanner sc = new Scanner(sel)) {
            StringBuilder sb = new StringBuilder();
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine());
            }

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Carta.class, new CartaDeserializable())
                    .create();

            juego = gson.fromJson(sb.toString(), Juego.class);

            // Validar el objeto cargado
            if (juego == null || juego.getBaraja() == null || juego.getMano() == null || juego.getCastillo() == null) {
                throw new IllegalStateException("El objeto Juego cargado es inválido o incompleto.");
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo cargar: " + e.getMessage()).showAndWait();
            return null;
        }

        return juego;
    }

    public static void guardarPartida(Juego juego) {
        try {
            // Configurar Gson para serialización
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            File dir = new File("src/main/resources/PartidasGuardadas");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, "partida_" + juego.getIdPartida() + ".json");
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(juego, writer);
                guardarEstadisticas(juego);
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Error al guardar: " + e.getMessage()).showAndWait();
        }
    }

    public static void guardarEstadisticas(Juego juego) {
        File archivo = new File("src/main/resources/estadisticas.csv");
        int id = juego.getIdPartida();
        String fecha = java.time.LocalDateTime.now().toString();
        int cartas = juego.getCartasJugadasDuranteCombate().size();
        int vida = juego.getVidaRestanteEnemigo();
        String mano = juego.getMano().toString();
        String res = juego.getResultado();
        String lineaNueva = id + ";" + fecha + ";" + cartas + ";" + vida + ";" + mano + ";" + res;
        try {
            List<String> lineas = new ArrayList<>();
            boolean insertado = false;
            if (archivo.exists()) {
                try (Scanner sc = new Scanner(archivo)) {
                    while (sc.hasNextLine()) {
                        String l = sc.nextLine();
                        int idLinea = Integer.parseInt(l.split(";")[0]);
                        if (idLinea == id && !insertado) {
                            lineas.add(lineaNueva);
                            insertado = true;
                        } else if (!insertado && idLinea > id) {
                            lineas.add(lineaNueva);
                            lineas.add(l);
                            insertado = true;
                        } else {
                            lineas.add(l);
                        }
                    }
                }
            }
            if (!insertado) lineas.add(lineaNueva);
            try (FileWriter fw = new FileWriter(archivo)) {
                for (String l : lineas) fw.write(l + "\n");
            }
        } catch (Exception e) {
            System.out.println("Error guardando estadísticas: " + e.getMessage());
        }
    }

    public static List<String> obtenerResumenesDeEstadisticas() {
        List<String> res = new ArrayList<>();
        File archivo = new File("src/main/resources/estadisticas.csv");
        if (!archivo.exists()) return res;
        try {
            List<String> lineas = Files.readAllLines(archivo.toPath());
            for (int i=0; i<lineas.size(); i++) {
                String l = lineas.get(i);
                if (!l.trim().isEmpty() && l.contains(";")) res.add("Partida Nº" + (i+1));
            }
        } catch (Exception ignored) {}
        return res;
    }

    public static String obtenerEstadisticaPorIndice(int indice) {
        File archivo = new File("src/main/resources/estadisticas.csv");
        if (!archivo.exists()) return null;
        try {
            List<String> lineas = Files.readAllLines(archivo.toPath());
            int c = -1;
            for (String l : lineas) {
                if (!l.trim().isEmpty() && l.contains(";")) {
                    c++;
                    if (c == indice) return l;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }
}
