package de.legoshi.lccore.manager;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.DefaultContextKeys;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

public class LuckPermsManager {

    private LuckPerms getLP() { return LuckPermsProvider.get(); }

    public EventBus getEventBus() {
        return getLP().getEventBus();
    }


    public boolean hasAnyGroup(Player player, String... groups) {
        return getUserGroups(getUserForPlayer(player)).stream().anyMatch(group -> Arrays.stream(groups)
                .anyMatch(toCheck -> group.getName().equalsIgnoreCase(toCheck)));
    }

    private Collection<Group> getUserGroups(User user) {
        return user.getInheritedGroups(user.getQueryOptions());
    }

    private User getUserForPlayer(Player player) {
        return getLP().getPlayerAdapter(Player.class).getUser(player);
    }

    // BLOCKING (CALL FROM ASYNC ONLY!!)
    private User getUserForPlayer(String player) {
        UUID playerUUID = UUID.fromString(player);
        return getLP().getUserManager().loadUser(playerUUID).join();
    }

    public boolean hasPermission(Player player, String permission) {
        return getUserForPlayer(player).getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

    public boolean hasPermission(String player, String permission) {
        Player p = Bukkit.getPlayer(UUID.fromString(player));
        if(p != null && p.isOnline())
            return hasPermission(p, permission);
        return getUserForPlayer(player).getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

    public void givePermission(Player player, String permission) {
        User user = getUserForPlayer(player);
        user.data().add(Node.builder(permission).build());
        getLP().getUserManager().saveUser(user);
    }

    public void giveNegativePermission(Player player, String permission) {
        User user = getUserForPlayer(player);
        user.data().add(Node.builder(permission).value(false).build());
        getLP().getUserManager().saveUser(user);
    }

    public void giveWorldPermission(Player player, String permission, String world, boolean value) {
        User user = getUserForPlayer(player);
        user.data().add(Node.builder(permission).withContext(DefaultContextKeys.WORLD_KEY, world).value(value).build());
        getLP().getUserManager().saveUser(user);
    }

    public void giveWorldPermission(Player player, String permission, String world) {
        giveWorldPermission(player, permission, world, true);
    }

    public boolean doesGroupExist(String group) {
        return getLP().getGroupManager().getGroup(group) != null;
    }

    public boolean hasGroup(Player player, String group) {
        return getUserGroups(getUserForPlayer(player)).stream().anyMatch(toCheck -> toCheck.getName().equalsIgnoreCase(group));
    }

    public boolean hasGroup(String player, String group) {
        Player p = Bukkit.getPlayer(UUID.fromString(player));
        if(p != null && p.isOnline())
            return hasGroup(p, group);
        return getUserGroups(getUserForPlayer(player)).stream().anyMatch(toCheck -> toCheck.getName().equalsIgnoreCase(group));
    }
}
