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

public class ChatFormatMenu extends GUIPane {

    @Inject private PlayerManager playerManager;
    @Inject private ChatManager chatManager;
    @Inject private DBManager db;
    private GuiToggleUpdateGroup updateGroup = new GuiToggleUpdateGroup();
    private PlayerPreferences prefs;


    private final String[] guiSetup = {
            "dmmmmmmmd",
            "m 12345 m",
            "dmmmqmmmd",
    };

    private final String[] noParentGuiSetup = {
            "dmmmmmmmd",
            "m 12345 m",
            "dmmmmmmmd",
    };

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Chat Formats", parent != null ? guiSetup : noParentGuiSetup);
        setColours(Dye.WHITE, Dye.PINK, Dye.BLACK);
        prefs = playerManager.getPlayerPrefs(holder);
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        GuiToggleElement bold = createToggleElement('1', ChatColor.BOLD, "lc.chat.l", prefs::setBoldChatFormat, prefs.isBoldChatFormat(), Message.SETTINGS_HAVE_NOT_UNLOCKED_CF);
        GuiToggleElement italic = createToggleElement('2', ChatColor.ITALIC, "lc.chat.n", prefs::setItalicChatFormat, prefs.isItalicChatFormat(), Message.SETTINGS_HAVE_NOT_UNLOCKED_CF);
        GuiToggleElement strike = createToggleElement('3', ChatColor.STRIKETHROUGH, "lc.chat.m", prefs::setStrikeChatFormat, prefs.isStrikeChatFormat(), Message.SETTINGS_UNOBTAINABLE);
        GuiToggleElement underline = createToggleElement('4', ChatColor.UNDERLINE, "lc.chat.o", prefs::setUnderlineChatFormat, prefs.isUnderlineChatFormat(), Message.SETTINGS_HAVE_NOT_UNLOCKED_CF);
        GuiToggleElement magic = createToggleElement('5', ChatColor.MAGIC, "lc.chat.k", prefs::setMagicChatFormat, prefs.isMagicChatFormat(), Message.SETTINGS_UNOBTAINABLE);
        updateGroup.add(bold, italic, strike, underline, magic);

        current.addElements(returnToParent, magic.build(), bold.build(), strike.build(), underline.build(), italic.build());
    }

    GuiToggleElement createToggleElement(char key, ChatColor color, String permission, Consumer<Boolean> toggleAction, boolean initialState, Message message) {
        GUIDescriptionBuilder desc = new GUIDescriptionBuilder()
                .raw(color + GUIUtil.formatEnum(color))
                .blank()
                .raw(chatManager.globalChatExample(holder))
                .action(GUIAction.LEFT_CLICK, "Enable/Disable");

        return new GuiToggleElement(key, desc, initialState).setInGroup(true).setOnEnable(() -> {
            if (!holder.hasPermission("lc.chatformat") || !holder.hasPermission(permission)) {
                MessageUtil.send(message, holder, color + GUIUtil.formatEnum(color));
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
