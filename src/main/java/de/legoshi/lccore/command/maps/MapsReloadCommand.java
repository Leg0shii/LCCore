package de.legoshi.lccore.command.maps;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.MapManager;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.Utils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import team.unnamed.inject.Inject;


@Register
@Command(names = {"mapsreload"}, permission = "mapsreload", desc = "")
public class MapsReloadCommand implements CommandClass {

    @Inject private MapManager mapManager;

    @Command(names = "")
    public void mapsReload(CommandSender sender) {
        Linkcraft.async(() -> {
            try {
                mapManager.updateMaps();
                sender.sendMessage(Utils.chat("&aReloaded map data!"));
            } catch (Exception e) {
                sender.sendMessage(Utils.chat("&cError occurred while loading maps: " + e.getMessage()));
            }
        });
    }
}
