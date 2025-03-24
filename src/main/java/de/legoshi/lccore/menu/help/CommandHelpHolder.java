package de.legoshi.lccore.menu.help;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.composite.PlayerCompletionId;
import de.legoshi.lccore.database.models.PlayerCompletion;
import de.legoshi.lccore.manager.ConfigManager;
import de.legoshi.lccore.menu.GUIScrollablePane;
import de.legoshi.lccore.menu.maps.LCMap;
import de.legoshi.lccore.menu.maps.MapLeaderboard;
import de.legoshi.lccore.util.*;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CommandHelpHolder extends GUIScrollablePane {

    private final String[] guiSetup = {
            "ddmmcmmdd",
            "sgggggggs",
            "sgggggggs",
            "sgggggggs",
            "ddmmcmmdd",
            "l---q---r",
    };

    private List<CommandHelpDTO> commandHelp;

    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Command Help", guiSetup);
        setColours(Dye.BLUE, Dye.CYAN, Dye.LIGHT_BLUE);
        fullCloseOnEsc();
        registerGuiElements();
        current.show(holder);
    }

    @Override
    protected void registerGuiElements() {
        this.commandHelp = getCommandHelp();
        getPage();
        this.current.addElements(pageLeft, pageRight, returnToParent);
    }

    private StaticGuiElement addCommand(CommandHelpDTO command) {

        GUIDescriptionBuilder builder = new GUIDescriptionBuilder()
                .coloured(command.getCommand(), ChatColor.BOLD)
                .pair("Description", GUIUtil.wrap(GUIUtil.colorize(command.getDescription())));

        if(command.getAliases() != null && !command.getAliases().isEmpty()) {
            builder.blank();
            builder.pair("Aliases", command.getAliases());
        }

        return new StaticGuiElement('g', new ItemStack(Material.COMMAND_MINECART),click -> true, builder.build());
    }

    private List<CommandHelpDTO> getCommandHelp() {
        List<CommandHelpDTO> commandHelp = new ArrayList<>(ConfigManager.commandHelp.values());
        maxPages = (int) Math.ceil((double) commandHelp.size() / pageVolume) - 1;
        return commandHelp;
    }


    @Override
    protected void getPage() {
        if(isPageEmpty()) {
            noDataItem('g', "No commands found");
            return;
        }

        int startIndex = page * pageVolume;
        int endIndex = Math.min(startIndex + pageVolume, commandHelp.size());
        List<CommandHelpDTO> paginatedList = commandHelp.subList(startIndex, endIndex);

        this.current.removeElement('g');

        GuiElementGroup group = new GuiElementGroup('g');
        for (CommandHelpDTO command : paginatedList) {
            group.addElement(addCommand(command));
        }

        this.current.addElement(group);
    }
}
