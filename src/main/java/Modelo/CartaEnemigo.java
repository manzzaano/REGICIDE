package Modelo;

public class CartaEnemigo extends Carta {
    private int daño;
    private int vida;

    public CartaEnemigo(String palo, int numero, int daño, int vida) {
        super(palo, numero, "CartaEnemigo");
        this.daño = daño;
        this.vida = vida;
    }

    public CartaEnemigo() {
        super("", 0, "CartaEnemigo");
    }

    public int getDaño() {
        return daño;
    }

    public void setDaño(int daño) {
        this.daño = daño;
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public int getValor() {
        return daño;
    }

    public boolean estaDerrotado() {
        return vida <= 0;
    }

    public void recibirDaño(int daño) {
        this.vida -= daño;
    }
}