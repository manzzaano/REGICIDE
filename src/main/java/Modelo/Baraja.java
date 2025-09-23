package Modelo;

import java.util.*;

public class Baraja {
    public static void iniciarPartida(Juego juego) {
        List<Carta> baraja = new ArrayList<>();
        String[] palos = {"Picas", "Corazones", "Tréboles", "Diamantes"};
        int[] numeros = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};

        for (String palo : palos) {
            for (int num : numeros) {
                baraja.add(new CartaBase(palo, num));
            }
        }
        juego.setBaraja(baraja);

        List<CartaPueblo> mano = new ArrayList<>();
        Set<String> tomadas = new HashSet<>();
        Random r = new Random();

        while (mano.size() < 8) {
            int i = r.nextInt(baraja.size());
            Carta c = baraja.get(i);
            if (c.getNumero() <= 10 && !tomadas.contains(c.getPalo() + c.getNumero())) {
                mano.add(new CartaPueblo(c.getPalo(), c.getNumero()));
                tomadas.add(c.getPalo() + c.getNumero());
                baraja.remove(i);
            }
        }
        juego.setMano(mano);

        Queue<CartaEnemigo> castillo = new LinkedList<>();
        List<Carta> jotas = new ArrayList<>();
        List<Carta> reinas = new ArrayList<>();
        List<Carta> reyes = new ArrayList<>();

        for (int i = 0; i < baraja.size(); i++) {
            Carta c = baraja.get(i);
            int num = c.getNumero();
            if (num == 11 && jotas.size() < 4) {
                jotas.add(c);
                baraja.remove(i);
                i--;
            } else if (num == 12 && reinas.size() < 4) {
                reinas.add(c);
                baraja.remove(i);
                i--;
            } else if (num == 13 && reyes.size() < 4) {
                reyes.add(c);
                baraja.remove(i);
                i--;
            }
        }

        Collections.shuffle(jotas);
        Collections.shuffle(reinas);
        Collections.shuffle(reyes);

        for (Carta c : jotas) {
            castillo.add(new CartaEnemigo(c.getPalo(), c.getNumero(), 10, 20));
        }
        for (Carta c : reinas) {
            castillo.add(new CartaEnemigo(c.getPalo(), c.getNumero(), 15, 30));
        }
        for (Carta c : reyes) {
            castillo.add(new CartaEnemigo(c.getPalo(), c.getNumero(), 20, 40));
        }
        juego.setCastillo(castillo);

        Stack<Carta> mazoPosada = new Stack<>();
        for (Carta c : baraja) {
            mazoPosada.push(c);
        }
        Collections.shuffle(mazoPosada);
        juego.setMazoPosada(mazoPosada);
    }
}