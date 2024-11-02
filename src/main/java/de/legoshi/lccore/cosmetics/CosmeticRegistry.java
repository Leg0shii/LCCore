package de.legoshi.lccore.cosmetics;

import org.bukkit.Color;
import org.reflections.Reflections;

import java.util.Set;


public class CosmeticRegistry {

    public void init() {
        Reflections reflections = new Reflections("de.legoshi.lccore.cosmetics");

        Set<Class<? extends Cosmetic>> cosmeticClasses = reflections.getSubTypesOf(Cosmetic.class);

        for (Class<? extends Cosmetic> cosmeticClass : cosmeticClasses) {
            try {
                // Falls es sich um ein ColorCosmetic handelt, keine zusätzliche Farbinstanziierung
                if (ColorCosmetic.class.isAssignableFrom(cosmeticClass)) {
                    ColorCosmetic colorCosmetic = (ColorCosmetic) cosmeticClass.getDeclaredConstructor().newInstance();
                    Cosmetic.registerCosmetic(colorCosmetic.getName());
                } else {
                    // Normales Cosmetic ohne Farbe registrieren
                    Cosmetic cosmetic = cosmeticClass.getDeclaredConstructor().newInstance();
                    Cosmetic.registerCosmetic(cosmetic.getName());
                }
            } catch (Exception e) {
                e.printStackTrace(); // Fehler bei der Instanziierung behandeln
            }
        }
    }
}



