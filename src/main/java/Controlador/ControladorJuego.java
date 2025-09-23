package Controlador;

import Modelo.*;
import Utilidades.Guardado;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.*;

public class ControladorJuego {
    @FXML private ImageView carta1, carta2, carta3, carta4, carta5, carta6, carta7, carta8, cartaEnemigo, comodin1, comodin2,
            sel1, sel2, sel3, sel4, sel5, sel6, sel7, sel8;
    @FXML private Label estadoMazos, vidaEnemigo, ataqueEnemigo;
    @FXML private Button confirmarAtaque, confirmarDefensa, nuevaPartida, cancelarCombi;

    private Juego juego;
    private List<CartaPueblo> cartasSeleccionadas = new ArrayList<>();
    private List<ImageView> cartasSeleccionadasIv = new ArrayList<>();
    private CartaEnemigo enemigoActual;
    private int enemigoVida;

    public void initialize() {
        // Al iniciar la vista se inicia una nueva partida
        nuevaPartida();
    }

    public void nuevaPartida() {
        // Se crea un nuevo juego
        juego = new Juego();
        desHabilitarBtnNuevaPartida();
        prepararAtaque();
    }

    public void habilitarBtnNuevaPartida() {
        nuevaPartida.setDisable(false);
    }

    public void desHabilitarBtnNuevaPartida() {
        nuevaPartida.setDisable(true);
    }

    public void habilitarMano() {
        ImageView cartas[] = {carta1, carta2, carta3, carta4, carta5, carta6, carta7, carta8, comodin1, comodin2};
        for (ImageView carta : cartas) {
            carta.setDisable(false);
        }
    }

    public void desHabilitarMano() {
        ImageView cartas[] = {carta1, carta2, carta3, carta4, carta5, carta6, carta7, carta8, comodin1, comodin2};
        for (ImageView carta : cartas) {
            carta.setDisable(true);
        }
    }

    // Métodos relacionados con la preparación de fases
    public void prepararAtaque() {
        habilitarMano();
        enemigoActual = juego.getCastillo().peek();
        if (enemigoActual != null) {
            enemigoVida = enemigoActual.getVida();
            juego.setDañoPorDefender(enemigoActual.getDaño());
        }
        actualizarVista();
    }

    public void prepararDefensa() {
        if (juego.getDañoPorDefender() <= 0) {
            juego.setPuntoGuardado("ataque");
            prepararAtaque();
            return;
        }
        actualizarVista();
    }

    // Métodos relacionados con acciones del juego
    public void ataque() {
        int sumaNum = 0;
        Set<String> palosUnicos = new HashSet<>(); // Para contar palos únicos

        for (CartaPueblo cartaSeleccionada : cartasSeleccionadas) {
            palosUnicos.add(cartaSeleccionada.getPalo()); // Registrar palo único
            sumaNum += cartaSeleccionada.getNumero() == 1 ? 1 : cartaSeleccionada.getNumero(); // Compañeros aportan 1
        }

        boolean trebolSel = palosUnicos.contains("Tréboles");
        boolean corazonSel = palosUnicos.contains("Corazones");
        boolean diamanteSel = palosUnicos.contains("Diamantes");
        boolean picaSel = palosUnicos.contains("Picas");

        int daño = trebolSel ? sumaNum * 2 : sumaNum; // Duplicar solo si hay tréboles
        int restarDaño = picaSel ? (enemigoActual.getPalo().equalsIgnoreCase("Picas") ? 0 : sumaNum) : 0;
        int robarCartas = diamanteSel ? (enemigoActual.getPalo().equalsIgnoreCase("Diamantes") ? 0 : sumaNum) : 0;
        int curarCartas = corazonSel ? (enemigoActual.getPalo().equalsIgnoreCase("Corazones") ? 0 : sumaNum) : 0;

        for (CartaPueblo cartaSeleccionada : cartasSeleccionadas) {
            juego.getMazoCartasJugadas().add(cartaSeleccionada);
            juego.getCartasJugadasDuranteCombate().add(cartaSeleccionada);
            juego.getMano().remove(cartaSeleccionada);
        }

        activarPoderes(restarDaño, robarCartas, curarCartas);

        enemigoVida -= daño;
        enemigoActual.recibirDaño(daño);

        if (enemigoActual.estaDerrotado()) {
            alertaEnemigoDerrotado();
            if (enemigoVida == 0) juego.getMazoPosada().add(enemigoActual);
            else {
                enemigoActual.setVida(0);
                juego.getMazoCartasDescartadas().addAll(juego.getMazoCartasJugadas());
                juego.getMazoCartasJugadas().clear();
                juego.getMazoCartasDescartadas().add(enemigoActual);
            }
            juego.getCastillo().poll();
            if (!juego.getCastillo().isEmpty()) {
                juego.setPuntoGuardado("ataque");
                prepararAtaque();
            }
        } else {
            juego.setPuntoGuardado("defensa");
            prepararDefensa();
        }
    }

    public void activarPoderes(int restarDaño, int robarCartas, int curarCartas) {
        // Poder Corazones (primero)
        Collections.shuffle(juego.getMazoCartasDescartadas());
        while (curarCartas-- > 0 && !juego.getMazoCartasDescartadas().isEmpty()) {
            Carta cartaCurada = juego.getMazoCartasDescartadas().remove(0);
            juego.getMazoPosada().add(cartaCurada);
        }
        // Poder Diamantes (después)
        int max = 8 - juego.getMano().size();
        int robos = Math.min(robarCartas, max);
        for (int i = 0; i < robos && !juego.getMazoPosada().isEmpty(); i++) {
            Carta cartaPosada = juego.getMazoPosada().remove(juego.getMazoPosada().size() - 1);
            juego.getMano().add(new CartaPueblo(cartaPosada.getPalo(), cartaPosada.getNumero()));
        }
        // Poder Picas
        int dañoReducido = Math.max(0, enemigoActual.getDaño() - restarDaño);
        enemigoActual.setDaño(dañoReducido);
        juego.setDañoPorDefender(dañoReducido);
        actualizarVista();
    }

    public void defensa() {
        if (cartasSeleccionadas.isEmpty()) return;
        CartaPueblo c = cartasSeleccionadas.get(0); // Tomar la primera carta
        int restante = Math.max(0, juego.getDañoPorDefender() - c.getNumero());
        comodin1.setDisable(true);
        comodin2.setDisable(true);
        juego.setDañoPorDefender(restante);
        juego.getMazoCartasDescartadas().add(c);
        juego.getMano().remove(c);
        cartasSeleccionadas.clear(); // Limpiar selección
        if (restante <= 0) {
            juego.setPuntoGuardado("ataque");
            prepararAtaque();
        } else {
            desHabilitarBtnConfirmarAtaque();
            desHabilitarBtnConfirmarDefensa();
            mostrarEstadoMazos();
            mostrarManoActual();
            mostrarEnemigoActual();
            mostrarVidaAtaque();
            comprobarFinal();
        }
    }

    public void usarComodin(int numComodin) {
        desHabilitarBtnConfirmarAtaque();
        desHabilitarBtnConfirmarDefensa();
        if (juego.getComodinesUsados() >= 2) {
            return;
        }
        juego.setComodinesUsados(juego.getComodinesUsados() + 1);
        if (numComodin != 1) {
            desHabilitarComodin(comodin1);
        }

        juego.getMazoCartasDescartadas().addAll(juego.getMano());
        juego.getMano().clear();

        Random random = new Random();
        while (juego.getMano().size() < 8 && !juego.getMazoPosada().isEmpty()) {
            int i = random.nextInt(juego.getMazoPosada().size());
            Carta cartaPosada = juego.getMazoPosada().get(i);

            boolean tomada = false;
            for (Carta cartaMano : juego.getMano()) {
                if (cartaMano.getPalo().equals(cartaPosada.getPalo()) && cartaMano.getNumero() == cartaPosada.getNumero()) {
                    tomada = true;
                    break;
                }
            }

            if (cartaPosada.getNumero() <= 10 && !tomada) {
                juego.getMano().add(new CartaPueblo(cartaPosada.getPalo(), cartaPosada.getNumero()));
                juego.getMazoPosada().remove(i);
            }
        }

        actualizarVista();
    }

    public void seleccionarCarta(CartaPueblo carta, int numCarta) {
        if (numCarta < 0 || numCarta >= 8) return; // Seguridad

        cancelarCombi.setDisable(false);

        // Determinar si se está en modo defensa
        boolean esDefensa = juego.getPuntoGuardado().equalsIgnoreCase("defensa");

        if (esDefensa) cancelarCombinacion();

        // Determinar si la carta nueva es un compañero animal
        boolean esCompañero = carta.getNumero() == 1;

        // Verificar el estado actual de la selección
        boolean tieneCompañeros = false;
        int numeroComun = -1; // Número común para cartas no compañeros
        boolean todasMismoNumero = true;
        Set<String> palosSeleccionados = new HashSet<>(); // Para verificar palos distintos
        int totalAtaque = esCompañero ? 1 : carta.getNumero(); // Iniciar con el valor de la nueva carta

        for (CartaPueblo c : cartasSeleccionadas) {
            if (c.getNumero() == 1) {
                tieneCompañeros = true;
            } else {
                if (numeroComun == -1) {
                    numeroComun = c.getNumero();
                } else if (c.getNumero() != numeroComun) {
                    todasMismoNumero = false;
                }
            }
            totalAtaque += c.getNumero() == 1 ? 1 : c.getNumero(); // Sumar valores correctamente
            palosSeleccionados.add(c.getPalo() + c.getNumero()); // Registrar palo y número
        }

        // Verificar si la nueva carta crea un duplicado palo-número
        boolean paloDuplicado = palosSeleccionados.contains(carta.getPalo() + carta.getNumero());

        // Determinar si se puede agregar la carta
        boolean puedeAgregar = false;

        if (esDefensa) {
            // En modo defensa, solo se permite seleccionar una carta si la selección está vacía
            if (cartasSeleccionadas.size() == 0) {
                puedeAgregar = true;
            }
        } else {
            // En modo ataque
            if (esCompañero) {
                // Los compañeros se añaden al menos una vez
                puedeAgregar = true;
            } else {
                // Si la selección está vacía, se puede agregar cualquier carta no compañero
                if (cartasSeleccionadas.size() == 0 && !paloDuplicado) {
                    puedeAgregar = true;
                }
                // Si la selección tiene un compañero, permitir añadir una carta no compañero (máximo 2 cartas)
                else if (tieneCompañeros && cartasSeleccionadas.size() == 1 && !paloDuplicado) {
                    puedeAgregar = true;
                }
                // Si la selección tiene cartas no compañeros, deben ser del mismo número, palos distintos, y total ≤ 10
                else if (!tieneCompañeros && todasMismoNumero && carta.getNumero() == numeroComun
                        && cartasSeleccionadas.size() < 4 && totalAtaque <= 10 && !paloDuplicado) {
                    puedeAgregar = true;
                }
            }
        }

        // Aplicar selección
        if (puedeAgregar) {
            cartasSeleccionadas.add(carta);
            ImageView[] imagenes = {carta1, carta2, carta3, carta4, carta5, carta6, carta7, carta8};
            ImageView[] imagenesSel = {sel1, sel2, sel3, sel4, sel5, sel6, sel7, sel8};
            cartasSeleccionadasIv.add(imagenes[numCarta]);

            // Limpiar todas las imágenes de selección
            for (ImageView imagen : imagenesSel) {
                imagen.setImage(new Image(getClass().getResource("/imgs/invisible.png").toExternalForm()));
            }
            for (ImageView imagen : imagenesSel) {
                imagen.setImage(new Image(getClass().getResource("/imgs/invisible.png").toExternalForm()));
            }
            // Asignar imágenes de las cartas seleccionadas
            for (int i = 0; i < cartasSeleccionadasIv.size(); i++) {
                imagenesSel[i].setImage(cartasSeleccionadasIv.get(i).getImage());
            }

            if (juego.getPuntoGuardado().equalsIgnoreCase("ataque")) {
                System.out.println(cartasSeleccionadas);
                habilitarBtnConfirmarAtaque();
            } else {
                System.out.println(cartasSeleccionadas);
                habilitarBtnConfirmarDefensa();
            }
        }
    }

    public void cancelarCombinacion() {
        desHabilitarBtnConfirmarAtaque();
        desHabilitarBtnConfirmarDefensa();
        cartasSeleccionadas.clear();
        cartasSeleccionadasIv.clear();
        ImageView[] imagenesSel = {sel1, sel2, sel3, sel4, sel5, sel6, sel7, sel8};
        for (ImageView imagen : imagenesSel) {
            imagen.setImage(new Image(getClass().getResource("/imgs/invisible.png").toExternalForm()));
        }
        cancelarCombi.setDisable(true);
    }

    public void mostrarEstadoMazos() {
        estadoMazos.setText(
                "| CASTILLO: "+ juego.getCastillo().size()+
                        "\n| POSADA: "+ juego.getMazoPosada().size()+
                        "\n| JUGADAS: "+ juego.getMazoCartasJugadas().size()+
                        "\n| DESCARTES: "+ juego.getMazoCartasDescartadas().size()
        );
    }

    public void mostrarManoActual() {
        if (juego.getMano().isEmpty()) alertaManoVacia();
        ImageView[] imgs = {carta1,carta2,carta3,carta4,carta5,carta6,carta7,carta8};
        for (ImageView imagen : imgs) {
            imagen.setImage(new Image(getClass().getResource("/imgs/invisible.png").toExternalForm()));
            imagen.getStyleClass().remove("seleccionada");
        }
        int contador = 0;
        String paloE = enemigoActual.getPalo();
        for (CartaPueblo carta : juego.getMano()) {
            if (contador >= imgs.length) break;
            String ruta = "/imgs/"+carta.getPalo()+"/"+carta.getNumero()+".png";
            ImageView imagen = imgs[contador];
            imagen.setImage(new Image(getClass().getResource(ruta).toExternalForm()));

            imagen.getStyleClass().add("card-image");
            imagen.getStyleClass().remove("sin-poder");
            if (paloE.equalsIgnoreCase(carta.getPalo()) && "ataque".equalsIgnoreCase(juego.getPuntoGuardado()))
                imagen.getStyleClass().add("sin-poder");
            int posicion = contador;
            imagen.setOnMouseClicked(e -> seleccionarCarta(carta,posicion));
            contador++;
        }
    }

    public void mostrarEnemigoActual() {
        if (!juego.getCastillo().isEmpty()) {
            String r = "/imgs/"+enemigoActual.getPalo()+"/"+enemigoActual.getNumero()+".png";
            cartaEnemigo.setImage(new Image(getClass().getResource(r).toExternalForm()));
        } else {
            cartaEnemigo.setImage(new Image(getClass().getResource("/imgs/invisible.png").toExternalForm()));
        }
    }

    public void mostrarComodines() {
        if (comodin1.getImage() == null) {
            comodin1.setImage(new Image(getClass().getResource("/imgs/comodin.png").toExternalForm()));
            comodin1.setOnMouseClicked(mouseEvent -> usarComodin(1));
        }
        if (comodin2.getImage() == null) {
            comodin2.setImage(new Image(getClass().getResource("/imgs/comodin.png").toExternalForm()));
            comodin2.setOnMouseClicked(mouseEvent -> usarComodin(2));
        }
        int usados = juego.getComodinesUsados();
        comodin1.setDisable(usados >= 1);
        comodin2.setDisable(usados >= 2);
    }

    public void mostrarVidaAtaque() {
        vidaEnemigo.setText("VIDA\n"+ (enemigoActual!=null ? enemigoVida:0));
        ataqueEnemigo.setText("ATAQUE\n"+ (enemigoActual!=null ? juego.getDañoPorDefender():0));
    }

    // Métodos relacionados con la gestión de partidas
    public void guardarPartida() {
        juego.setVidaRestanteEnemigo(enemigoActual.getVida());
        Guardado.guardarPartida(juego);
        new Alert(Alert.AlertType.INFORMATION,"¡Partida guardada!").showAndWait();
    }

    public void cargarPartida() {
        Juego j2 = Guardado.cargarPartida();
        if (j2==null) return;
        juego = j2;
        enemigoActual = juego.getCastillo().peek();
        enemigoVida = enemigoActual.getVida();
        new Alert(Alert.AlertType.INFORMATION,"¡Partida cargada!").showAndWait();
        if ("ataque".equalsIgnoreCase(juego.getPuntoGuardado())) prepararAtaque();
        else prepararDefensa();
    }

    public void mostrarEstadisticas() {
        List<String> opts = Guardado.obtenerResumenesDeEstadisticas();
        if (opts.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION,"No hay estadísticas.").showAndWait();
            return;
        }
        ChoiceDialog<String> d = new ChoiceDialog<>(opts.get(0), opts);
        d.setTitle("Estadísticas");
        Optional<String> sel = d.showAndWait();
        if (!sel.isPresent()) return;
        int idx = opts.indexOf(sel.get());
        String linea = Guardado.obtenerEstadisticaPorIndice(idx);
        if (linea==null) {
            new Alert(Alert.AlertType.INFORMATION,"No encontrada.").showAndWait();
            return;
        }
        String[] p = linea.split(";");
        new Alert(Alert.AlertType.INFORMATION,
                "Partida "+p[0]+"\nFecha: "+p[1]+"\nCartas: "+p[2]+"\nVida: "+p[3]+"\nMano: "+p[4]+"\nResultado: "+p[5]
        ).showAndWait();
    }

    // Métodos relacionados con la actualización de la vista
    private void actualizarVista() {
        desHabilitarBtnConfirmarAtaque();
        desHabilitarBtnConfirmarDefensa();
        mostrarEstadoMazos();
        mostrarManoActual();
        mostrarEnemigoActual();
        mostrarVidaAtaque();
        mostrarComodines();
        comprobarFinal();
        cancelarCombinacion();
    }

    public void habilitarBtnConfirmarAtaque() {
        confirmarAtaque.setDisable(false);
    }

    public void desHabilitarBtnConfirmarAtaque() {
        confirmarAtaque.setDisable(true);
    }

    public void habilitarBtnConfirmarDefensa() {
        confirmarDefensa.setDisable(false);
    }

    public void desHabilitarBtnConfirmarDefensa() {
        confirmarDefensa.setDisable(true);
    }

    public void desHabilitarComodin(ImageView comodin) {
        comodin.setDisable(true);
    }

    // Métodos relacionados con alertas
    private void comprobarFinal() {
        int suma = 0;
        for (Carta c : juego.getMano()) suma += c.getNumero();

        if ((juego.getMano().isEmpty() && juego.getComodinesUsados() == 2 && !juego.getCastillo().isEmpty())
                || (suma < juego.getDañoPorDefender() && juego.getComodinesUsados() == 2 && juego.getPuntoGuardado().equalsIgnoreCase("defensa"))) {
            juego.setResultado("Derrota");
        }

        if (juego.getMano().isEmpty() && comodin1.isDisable() && comodin2.isDisable()) {
            juego.setResultado("Derrota");
        }

        if (juego.getCastillo().isEmpty()) {
            juego.setResultado("Victoria");
        }

        if (juego.getResultado().equalsIgnoreCase("Victoria") || juego.getResultado().equalsIgnoreCase("Derrota")) {
            desHabilitarMano();
            mostrarAlertaFinPartida(juego.getResultado());
        }
    }

    private void mostrarAlertaFinPartida(String resultado) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        habilitarBtnNuevaPartida();
        alerta.setTitle("Fin de Partida");
        alerta.setHeaderText(null);
        alerta.setContentText("La partida ha terminado: " + resultado);
        alerta.showAndWait();
    }

    private void alertaEnemigoDerrotado() {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Enemigo Derrotado");
        alerta.setHeaderText(null);
        alerta.setContentText("¡Has derrotado al enemigo!");
        alerta.showAndWait();
    }

    private void alertaManoVacia() {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Mano Vacía");
        alerta.setHeaderText(null);
        alerta.setContentText("No te quedan cartas en la mano.");
        alerta.showAndWait();
    }
}