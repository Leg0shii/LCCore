package de.legoshi.lccore.menu.settings;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.util.ColorHelper;
import de.legoshi.lccore.util.PlayerDisplayBuilder;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

public class NameFormatMenu extends GUIPane {
    private final String[] guiSetup = {
            "         ",
            "    i    ",
            "         ",
    };

    private ColorHelper.ChatFormat format;
    private ItemStack item;
    private String description;
    @Inject private Injector injector;
    @Inject private PlayerManager playerManager;

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "§a§lConfirm", guiSetup);
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    public void openGui(Player player, InventoryGui parent, ColorHelper.ChatFormat format, ItemStack item, String description) {
        this.format = format;
        this.item = item;
        this.description = description;
        openGui(player, parent);
    }

    @Override
    protected void registerGuiElements() {
        current.addElements(new StaticGuiElement('i', item, click -> {
            if(ColorHelper.getNameFormats(holder).contains(format)) {
                ColorHelper.removeNameFormat(holder, format);
            } else {
                ColorHelper.addNameFormat(holder, format);
            }

            playerManager.updatePlayer(holder);
            MessageUtil.send(Message.NICK_SET_TO, holder, injector.getInstance(PlayerDisplayBuilder.class).setPlayer(holder).nick().build());
            current.close();
            return true;
        }, ChatColor.GREEN + description));
    }
}
