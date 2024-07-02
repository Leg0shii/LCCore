package de.legoshi.lccore.menu;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@NoArgsConstructor
public class GuiToggleUpdateGroup {
    private final List<GuiToggleElement> elements = new ArrayList<>();

    public void add(GuiToggleElement element) {
        elements.add(element);
    }

    public void add(GuiToggleElement ... elementList) {
        elements.addAll(Arrays.asList(elementList));
    }

    public void update() {
        for(GuiToggleElement element : elements) {
            element.updateDescription(element.getElement(), true);
        }
    }
}
