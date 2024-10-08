package de.legoshi.lccore.menu.settings;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.PlayerPreferences;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.menu.GuiToggleElement;
import de.legoshi.lccore.menu.GuiToggleUpdateGroup;
import de.legoshi.lccore.util.Dye;
import de.legoshi.lccore.util.GUIAction;
import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.legoshi.lccore.util.GUIUtil;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.function.Consumer;

public class NameFormatMenu extends GUIPane {

    @Inject private PlayerManager playerManager;
    @Inject private ChatManager chatManager;
    @Inject private DBManager db;
    private GuiToggleUpdateGroup updateGroup = new GuiToggleUpdateGroup();
    private PlayerPreferences prefs;

    private final String[] guiSetup = {
            "dmmmmmmmd",
            "m 1234  m",
            "dmmmqmmmd",
    };

    private final String[] noParentGuiSetup = {
            "dmmmmmmmd",
            "m 1234  m",
            "dmmmmmmmd",
    };

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Name Formats", parent != null ? guiSetup : noParentGuiSetup);
        setColours(Dye.WHITE, Dye.PINK, Dye.BLACK);
        prefs = playerManager.getPlayerPrefs(holder);
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        GuiToggleElement bold = createToggleElement('1', ChatColor.BOLD, "linkcraft.bold", prefs::setBoldNameFormat, prefs.isBoldNameFormat());
        GuiToggleElement italic = createToggleElement('2', ChatColor.ITALIC, "linkcraft.italics", prefs::setItalicNameFormat, prefs.isItalicNameFormat());
        GuiToggleElement strike = createToggleElement('3', ChatColor.STRIKETHROUGH, "linkcraft.strike", prefs::setStrikeNameFormat, prefs.isStrikeNameFormat());
        GuiToggleElement underline = createToggleElement('4', ChatColor.UNDERLINE, "linkcraft.underline", prefs::setUnderlineNameFormat, prefs.isUnderlineNameFormat());
        updateGroup.add(bold, italic, strike, underline);
        current.addElements(returnToParent, bold.build(), italic.build(), strike.build(), underline.build());
    }

    GuiToggleElement createToggleElement(char key, ChatColor color, String permission, Consumer<Boolean> toggleAction, boolean initialState) {
        GUIDescriptionBuilder desc = new GUIDescriptionBuilder()
                .raw(color + GUIUtil.formatEnum(color))
                .blank()
                .raw(chatManager.globalChatExample(holder))
                .action(GUIAction.LEFT_CLICK, "Enable/Disable");

        return new GuiToggleElement(key, desc, initialState).setInGroup(true).setOnEnable(() -> {
            if (!holder.hasPermission(permission)) {
                MessageUtil.send(Message.SETTINGS_HAVE_NOT_UNLOCKED_NF, holder, color + GUIUtil.formatEnum(color));
                return false;
            }
            toggleAction.accept(true);
            db.update(prefs);
            updateGroup.update();
            return true;
        }).setOnDisable(() -> {
            toggleAction.accept(false);
            db.update(prefs);
            updateGroup.update();
            return true;
        }).setOnDescriptionUpdate(updatedDesc -> updatedDesc.replaceAt(chatManager.globalChatExample(holder), 3));
    }
}
