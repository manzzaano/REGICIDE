package Modelo;

public class CartaBase extends Carta {
    public CartaBase(String palo, int numero) {
        super(palo, numero, "CartaBase");
    }

    public CartaBase() {
        super("", 0, "CartaBase");
    }

    public int getValor() {
        return getNumero();
    }
}