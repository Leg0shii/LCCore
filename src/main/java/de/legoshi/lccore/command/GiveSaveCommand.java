package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.Utils;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.stack.ArgumentStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Register
@Command(names = {"givesave"}, permission = "givesave", desc = "<player> [name]")
public class GiveSaveCommand implements CommandClass {

    @Inject private PlayerManager playerManager;

    @Command(names = "")
    public void giveSave(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String pName,
                        ArgumentStack as) {

        Player toGiveSave = playerManager.playerByName(pName);
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }

            Player player = (Player)sender;
            PlayerRecord record = playerManager.getPlayerRecord(toGiveSave, pName);

            if(record == null) {
                MessageUtil.send(Message.NEVER_JOINED, sender, pName);
                return;
            }

            ItemStack randomItem = Utils.getRandomVisibleBlock();

            int saveNumber;
            ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getPlugin(), Linkcraft.getPlugin().getPlayerdataFolder(), record.getUuid() + ".yml");
            FileConfiguration playerDataConfig = playerData.getConfig();
            String saveName = Utils.joinArguments(as);

            if (playerDataConfig.isConfigurationSection("Saves")) {
                Set<String> saves = playerDataConfig.getConfigurationSection("Saves").getKeys(false);
                if (!saves.isEmpty()) {
                    saveNumber = Integer.parseInt((String) saves.toArray()[saves.size() - 1]) + 1;
                } else {
                    saveNumber = 1;
                }
            } else {
                saveNumber = 1;
            }

            if(saveName.isEmpty()) {
                saveName = "Save #" + saveNumber;
            }

            Date now = new Date();
            String locStr = Utils.getStringFromLocation(player.getLocation());
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            playerDataConfig.set("Saves." + saveNumber + ".name", saveName);
            playerDataConfig.set("Saves." + saveNumber + ".block", randomItem.getTypeId() + ":" + randomItem.getDurability());
            playerDataConfig.set("Saves." + saveNumber + ".location", locStr);
            playerDataConfig.set("Saves." + saveNumber + ".date", format.format(now));
            playerData.saveConfig();

            MessageUtil.send(Message.SAVES_GAVE_SAVE, player, record.getName(), locStr);
            MessageUtil.log(Message.SAVES_GAVE_SAVE_LOGGING, true, player.getName(), record.getName(), locStr);
        });
    }
}
