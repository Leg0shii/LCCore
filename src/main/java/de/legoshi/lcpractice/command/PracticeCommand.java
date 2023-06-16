package de.legoshi.lcpractice.command;

import java.util.Arrays;

import de.legoshi.lcpractice.util.Constants;
import de.legoshi.lcpractice.LCPractice;
import de.legoshi.lcpractice.util.LocationHelper;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PracticeCommand implements CommandExecutor {

    private final LCPractice plugin;
    private final FileConfiguration playerdataConfig;

    public PracticeCommand(LCPractice plugin) {
        this.plugin = plugin;
        this.playerdataConfig = plugin.playerdataConfigAccessor.getConfig();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        FileConfiguration config = this.plugin.getConfig();
        Player p = (Player) sender;
        String uuid = p.getUniqueId().toString();
        if (this.playerdataConfig.get(uuid) != null) {
            String alreadyPracticingMessage = config.getString(Constants.ALREADY_PRACTICING_MESSAGE);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', alreadyPracticingMessage));
            return true;
        }
        Double[] possibleYValues = {0.9375D, 0.875D, 0.75D, 0.625D, 0.5625D, 0.5D, 0.375D, 0.25D, 0.1875D, 0.125D, 0.0625D, 0.015625D, 0.0D};
        if (p.getVelocity().getY() == -0.0784000015258789D && Arrays.asList(possibleYValues).contains(p.getLocation().getY() % 1.0D)) {
            if (p.getInventory().firstEmpty() == -1) {
                String str = config.getString(Constants.FULL_INVENTORY_MESSAGE);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
                return true;
            }
            String str1 = LocationHelper.getStringFromLocation(p.getLocation());
            this.playerdataConfig.set(uuid, str1);
            p.getInventory().addItem(this.plugin.getReturnItem());

            // Set player to prac group... WELL NOW INDIVIDUAL PERMS...
            // atleast it doesnt spam anymore
            LuckPerms api = plugin.luckPerms;
            User user = api.getUserManager().getUser(p.getUniqueId());
            if (user != null) {
                user.data().add(InheritanceNode.builder(Constants.PRAC).build()); // currently just sets the tag NO PERMS BECAUSE OF BONUS AND STUFF
                user.data().add(PermissionNode.builder(Constants.SERVERSIGNS_USE_ALL).value(false).build());
                user.data().add(PermissionNode.builder(Constants.PS_SAVE).value(false).build());
                user.data().add(PermissionNode.builder(Constants.PS_SAVES).value(false).build());
                user.data().add(PermissionNode.builder(Constants.PKCP_SIGNS).value(false).build());
                user.data().add(PermissionNode.builder(Constants.PKCP_SIGNS).withContext("world", Constants.BONUS_WORLD).value(false).build());
                user.data().add(PermissionNode.builder(Constants.CHESTCOMMANDS_OPEN_BONUS_YML).value(false).build());
                user.data().add(PermissionNode.builder(Constants.CHESTCOMMANDS_OPEN_CHALLENGE_YML).value(false).build());
                user.data().add(PermissionNode.builder(Constants.CHESTCOMMANDS_OPEN_MAZE_YML).value(false).build());
                user.data().add(PermissionNode.builder(Constants.ESSENTIALS_WARP).value(false).build());
                user.data().add(PermissionNode.builder(Constants.ESSENTIALS_SPAWN).value(false).build());
                api.getUserManager().saveUser(user);
            }

            String practiceMessage = config.getString(Constants.PRACTICE_MESSAGE);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', practiceMessage));
            return true;
        }
        String location = config.getString(Constants.BLOCK_MESSAGE);
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', location));
        return true;
    }
}

