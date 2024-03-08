package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.Utils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.node.Node;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class GiveUnlocksCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("lc.addcompletions")) {
            sender.sendMessage(Utils.chat("&cInsufficient permission!"));
            return true;
        }

        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (!player.getUniqueId().toString().equals("57344e2d-488a-428c-8537-0f22dfca2ec7")) {
            sender.sendMessage(Utils.chat("&cThis command is one time use only. Only Dkayee can use it as a precaution."));
            return true;
        }

        HashSet<String> hashSet = new HashSet<>();
        hashSet.add("deluxetags.tag.nostalgia");
        hashSet.add("deluxetags.tag.rewind_victory");
        hashSet.add("deluxetags.tag.space_win");
        hashSet.add("deluxetags.tag.strangelab2_victoryblue");
        hashSet.add("deluxetags.tag.strangelab2_victoryred");
        hashSet.add("deluxetags.tag.deserticreliefs_win");
        hashSet.add("deluxetags.tag.classroom_win");
        hashSet.add("deluxetags.tag.clashofclans_end");
        hashSet.add("deluxetags.tag.evilxiv_victory");
        hashSet.add("deluxetags.tag.geo3_complete");
        hashSet.add("deluxetags.tag.hors");
        hashSet.add("deluxetags.tag.tianxia2_end");
        hashSet.add("deluxetags.tag.abyss_win");
        hashSet.add("deluxetags.tag.exotic");
        hashSet.add("deluxetags.tag.geometrics2");
        hashSet.add("deluxetags.tag.hell3_victory");
        hashSet.add("deluxetags.tag.kpbox_win");
        hashSet.add("deluxetags.tag.arcade3_victory");
        hashSet.add("deluxetags.tag.playroom_win");
        hashSet.add("deluxetags.tag.elvenation_win");
        hashSet.add("deluxetags.tag.ascension_win");
        hashSet.add("deluxetags.tag.pizzeria_end");
        hashSet.add("deluxetags.tag.house2_end");
        hashSet.add("deluxetags.tag.eh_win");
        hashSet.add("deluxetags.tag.pandorasbox_victorytag");
        hashSet.add("deluxetags.tag.roc_win");

        LuckPerms api = Linkcraft.getPlugin().luckPerms;
        api.getUserManager().getUniqueUsers().thenAcceptAsync((list) -> {
            list.forEach((uuid) -> {
                api.getUserManager().loadUser(uuid).thenAcceptAsync((user ->  {
                    for(String entry : hashSet) {
                        if(user.getCachedData().getPermissionData().checkPermission(entry).asBoolean()) {
                            if(!user.getCachedData().getPermissionData().checkPermission("essentials.kittycannon").asBoolean()) {
                                user.data().add(Node.builder("essentials.kittycannon").build());
                            }

                            if(!user.getCachedData().getPermissionData().checkPermission("essentials.chat.format").asBoolean()) {
                                user.data().add(Node.builder("essentials.chat.format").build());
                            }
                            break;
                        }
                    }
                    api.getUserManager().saveUser(user);
                }));
            });
        });

        return true;
    }
}
