package de.legoshi.lccore.listener;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import com.sk89q.worldedit.bukkit.selections.Selection;
import de.legoshi.lccore.util.CommandException;
import de.legoshi.lccore.util.Utils;
import de.legoshi.lccore.util.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FAWEManager {

    private WorldEditPlugin getWE() {
        return (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
    }

    public List<Location> getSelection(Player player) throws CommandException {
        WorldEditPlugin we = getWE();
        Selection selection = we.getSelection(player);
        if(selection == null) {
            throw new CommandException(Message.NO_WORLDEDIT_SELECTION);
        }

        return new ArrayList<>(Arrays.asList(selection.getMaximumPoint(), selection.getMinimumPoint()));
    }
}
