package Utilidades;

import Modelo.Carta;
import Modelo.CartaBase;
import Modelo.CartaEnemigo;
import Modelo.CartaPueblo;
import com.google.gson.*;
import java.lang.reflect.Type;

public class CartaDeserializable implements JsonDeserializer<Carta> {
    @Override
    public Carta deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String tipoCarta = jsonObject.get("tipoCarta").getAsString();

        switch (tipoCarta) {
            case "CartaBase":
                return context.deserialize(json, CartaBase.class);
            case "CartaPueblo":
                return context.deserialize(json, CartaPueblo.class);
            case "CartaEnemigo":
                return context.deserialize(json, CartaEnemigo.class);
            default:
                throw new JsonParseException("Tipo de carta desconocido: " + tipoCarta);
        }
    }
}