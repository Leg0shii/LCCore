package de.legoshi.lccore.command.practice;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.Constants;
import de.legoshi.lccore.util.LocationHelper;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

// REFACTORED
public class UnpracticeCommand implements CommandExecutor {

    private final Linkcraft plugin;
    private final FileConfiguration playerdataConfig;
    private final PacketContainer ActionBarOffPacket;
    private FileConfiguration config;

    public UnpracticeCommand(Linkcraft plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.playerdataConfig = plugin.playerConfig.getConfig();

        ActionBarOffPacket = new PacketContainer(PacketType.Play.Server.CHAT);
        ActionBarOffPacket.getChatComponents().write(0, WrappedChatComponent.fromText(""));
        ActionBarOffPacket.getBytes().write(0, (byte) 2);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        this.config = this.plugin.getConfig();
        Player p = (Player) sender;
        String uuid = p.getUniqueId().toString();
        String saved = this.playerdataConfig.getString(uuid);
        if (saved == null) {
            String notPracticingMessage = this.config.getString(Constants.NOT_PRACTICING_MESSAGE);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', notPracticingMessage));
            return true;
        }
        Location loc = LocationHelper.getLocationFromString(saved);
        p.teleport(loc);
        this.playerdataConfig.set(uuid, null);
        ItemStack returnItem = this.plugin.getReturnItem();
        while (p.getInventory().containsAtLeast(returnItem, 1)) {
            p.getInventory().removeItem(this.plugin.getReturnItem());
        }

        // Set player from prac group
        LuckPerms api = plugin.luckPerms;
        User user = api.getUserManager().getUser(p.getUniqueId());

        if (user != null) {
            // Set permissions to true
            user.data().add(PermissionNode.builder(Constants.SERVERSIGNS_USE_ALL).value(true).build());
            user.data().add(PermissionNode.builder(Constants.PS_SAVE).value(true).build());
            user.data().add(PermissionNode.builder(Constants.PS_SAVES).value(true).build());
            user.data().add(PermissionNode.builder(Constants.PKCP_SIGNS).value(true).build());
            user.data().add(PermissionNode.builder(Constants.CHESTCOMMANDS_OPEN_CHALLENGE_YML).value(true).build());
            user.data().add(PermissionNode.builder(Constants.CHESTCOMMANDS_OPEN_MAZE_YML).value(true).build());
            user.data().add(PermissionNode.builder(Constants.CHESTCOMMANDS_OPEN_BONUS_YML).value(true).build());
            user.data().add(PermissionNode.builder(Constants.ESSENTIALS_WARP).value(true).build());
            user.data().add(PermissionNode.builder(Constants.ESSENTIALS_SPAWN).value(true).build());

            // Unset permissions
            user.data().remove(InheritanceNode.builder(Constants.PRAC).build()); // currently just sets the tag NO PERMS BECAUSE OF BONUS AND STUFF
            user.data().remove(PermissionNode.builder(Constants.PS_SAVE).withContext("world", Constants.BONUS_WORLD).build());
            user.data().remove(PermissionNode.builder(Constants.PS_SAVES).withContext("world", Constants.BONUS_WORLD).build());
            user.data().remove(PermissionNode.builder(Constants.PKCP_SIGNS).withContext("world", Constants.BONUS_WORLD).build());

            // Save changes
            api.getUserManager().saveUser(user);
        }

        String unpracticeMessage = this.config.getString(Constants.UNPRACTICE_MESSAGE);
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', unpracticeMessage));

        PracticeCommand.getPracticingPlayers().remove(p);


        ProtocolManager pm = Linkcraft.getInstance().protocolManager;

        if (pm != null) {
            pm.sendServerPacket(p, ActionBarOffPacket);
        }

        return true;
    }
}

