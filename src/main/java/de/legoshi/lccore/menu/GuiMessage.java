package de.legoshi.lccore.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GuiMessage {
    private GUIPane gui;
    private GuiMessageCallback callback;
}
