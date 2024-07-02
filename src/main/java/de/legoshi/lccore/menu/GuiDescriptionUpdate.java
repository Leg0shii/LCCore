package de.legoshi.lccore.menu;

import de.legoshi.lccore.util.GUIDescriptionBuilder;

@FunctionalInterface
public interface GuiDescriptionUpdate {
    GUIDescriptionBuilder update(GUIDescriptionBuilder desc);
}
