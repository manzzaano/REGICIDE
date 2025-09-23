package Modelo;

public class CartaPueblo extends Carta {
    private int ataque;

    public CartaPueblo(String palo, int numero) {
        super(palo, numero, "CartaPueblo");
        this.ataque = palo.equalsIgnoreCase("Tréboles") ? numero * 2 : numero;
    }

    public CartaPueblo() {
        super("", 0, "CartaPueblo");
    }

    public int getAtaque() {
        return ataque;
    }

    public void setAtaque(int ataque) {
        this.ataque = ataque;
    }

    public int getValor() {
        return ataque;
    }
}