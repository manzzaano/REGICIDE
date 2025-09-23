package Modelo;

import java.io.File;
import java.io.Serializable;
import java.util.*;

public class Juego implements Serializable {
    private int idPartida;
    private int vidaRestanteEnemigo;
    private int comodinesUsados;
    private String puntoGuardado;
    private int dañoPorDefender;
    private String resultado;

    private List<Carta> cartasJugadasDuranteCombate;
    private List<Carta> baraja;
    private List<CartaPueblo> mano;
    private Queue<CartaEnemigo> castillo;
    private Stack<Carta> mazoPosada;
    private List<Carta> mazoCartasJugadas;
    private List<Carta> mazoCartasDescartadas;

    public Juego() {
        this.puntoGuardado = "ataque";
        this.resultado = "En curso";
        this.cartasJugadasDuranteCombate = new ArrayList<>();
        this.baraja = new ArrayList<>();
        this.mano = new ArrayList<>();
        this.castillo = new LinkedList<>();
        this.mazoPosada = new Stack<>();
        this.mazoCartasJugadas = new ArrayList<>();
        this.mazoCartasDescartadas = new ArrayList<>();
        Baraja.iniciarPartida(this);
        this.idPartida = asignarIdPartida();
    }

    private int asignarIdPartida() {
        int num = 0;
        File dir = new File("src/main/resources/PartidasGuardadas");
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    String n = f.getName();
                    if (n.startsWith("partida_") && n.endsWith(".json")) {
                        num++;
                    }
                }
            }
        }
        return num + 1;
    }

    public int getIdPartida() { return idPartida; }
    public void setIdPartida(int id) { this.idPartida = id; }
    public int getVidaRestanteEnemigo() { return vidaRestanteEnemigo; }
    public void setVidaRestanteEnemigo(int v) { this.vidaRestanteEnemigo = v; }
    public String getPuntoGuardado() { return puntoGuardado; }
    public void setPuntoGuardado(String p) { this.puntoGuardado = p; }
    public int getDañoPorDefender() { return dañoPorDefender; }
    public void setDañoPorDefender(int d) { this.dañoPorDefender = d; }
    public String getResultado() { return resultado; }
    public void setResultado(String r) { this.resultado = r; }
    public List<Carta> getCartasJugadasDuranteCombate() { return cartasJugadasDuranteCombate; }
    public void setCartasJugadasDuranteCombate(List<Carta> l) { this.cartasJugadasDuranteCombate = l; }
    public List<Carta> getBaraja() { return baraja; }
    public void setBaraja(List<Carta> b) { this.baraja = b; }
    public List<CartaPueblo> getMano() { return mano; }
    public void setMano(List<CartaPueblo> m) { this.mano = m; }
    public Queue<CartaEnemigo> getCastillo() { return castillo; }
    public void setCastillo(Queue<CartaEnemigo> c) { this.castillo = c; }
    public Stack<Carta> getMazoPosada() { return mazoPosada; }
    public void setMazoPosada(Stack<Carta> m) { this.mazoPosada = m; }
    public List<Carta> getMazoCartasJugadas() { return mazoCartasJugadas; }
    public void setMazoCartasJugadas(List<Carta> l) { this.mazoCartasJugadas = l; }
    public List<Carta> getMazoCartasDescartadas() { return mazoCartasDescartadas; }
    public void setMazoCartasDescartadas(List<Carta> l) { this.mazoCartasDescartadas = l; }
    public int getComodinesUsados() { return comodinesUsados; }
    public void setComodinesUsados(int c) { this.comodinesUsados = c; }
}