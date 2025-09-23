package Modelo;

public abstract class Carta {
    private String palo;
    private int numero;
    private String tipoCarta;

    public Carta() {
    }

    public Carta(String palo, int numero, String tipoCarta) {
        this.palo = palo;
        this.numero = numero;
        this.tipoCarta = tipoCarta;
    }

    public String getPalo() {
        return palo;
    }

    public int getNumero() {
        return numero;
    }

    public String getTipoCarta() {
        return tipoCarta;
    }

    public abstract int getValor();

    public String toString() {
        return numero + " de " + palo;
    }
}