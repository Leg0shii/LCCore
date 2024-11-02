package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.message.MessageUtil;
import de.myzelyam.api.vanish.VanishAPI;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;

public class VisibilityManager {
    private final HashSet<String> hiding = new HashSet<>();
    private final HashMap<String, HashSet<String>> shownPlayers = new HashMap<>();
    private final HashMap<String, HashSet<String>> hiddenPlayers = new HashMap<>();

    private void addToTabList(Player player, Player toAdd) {
        try {
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) toAdd).getHandle());
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        } catch (Exception e) {
            MessageUtil.log("Exception while adding from tab list", true);
        }
    }

    public void removeFromTabList(Player player, Player toRemove) {
        try {
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) toRemove).getHandle());
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        } catch (Exception e) {
            MessageUtil.log("Exception while removing from tab list", true);
        }
    }

    public void onLeave(Player player) {
        String uuid = player.getUniqueId().toString();
        hiding.remove(uuid);
        shownPlayers.remove(uuid);
        hiddenPlayers.remove(uuid);

        for(Player p : Bukkit.getOnlinePlayers()) {
            removeFromTabList(p, player);
        }
    }

    public void hideAll(Player player) {
        String uuid = player.getUniqueId().toString();
        shownPlayers.remove(uuid);
        hiddenPlayers.remove(uuid);
        hiding.add(uuid);

        for(Player p : Bukkit.getOnlinePlayers()) {
            player.hidePlayer(p);
            if(VanishAPI.canSee(player, p)) {
                addToTabList(player, p);
            }
        }
    }

    public void showAll(Player player) {
        String uuid = player.getUniqueId().toString();
        shownPlayers.remove(uuid);
        hiddenPlayers.remove(uuid);
        hiding.remove(uuid);

        for(Player p : Bukkit.getOnlinePlayers()) {
            if(VanishAPI.canSee(player, p)) {
                player.showPlayer(p);
            }
        }
    }

    public void hideOne(Player player, Player toHide) {
        String uuid = player.getUniqueId().toString();
        if(shownPlayers.get(uuid) != null) {
            shownPlayers.get(uuid).remove(toHide.getUniqueId().toString());
        }

        hiddenPlayers.computeIfAbsent(uuid, k -> new HashSet<>());
        hiddenPlayers.get(uuid).add(toHide.getUniqueId().toString());
        player.hidePlayer(toHide);

        if(!VanishAPI.isInvisible(toHide)) {
            addToTabList(player, toHide);
        }
    }

    public void showOne(Player player, Player toShow) {
        String uuid = player.getUniqueId().toString();
        if(hiddenPlayers.get(uuid) != null) {
            hiddenPlayers.get(uuid).remove(toShow.getUniqueId().toString());
        }

        shownPlayers.computeIfAbsent(uuid, k -> new HashSet<>());
        shownPlayers.get(uuid).add(toShow.getUniqueId().toString());
        player.showPlayer(toShow);
    }

    private boolean isHiding(Player player, Player toHide) {
        String uuid = player.getUniqueId().toString();
        boolean isHiding = false;

        if(hiding.contains(uuid) && (shownPlayers.get(uuid) == null || !shownPlayers.get(uuid).contains(toHide.getUniqueId().toString()))) {
            isHiding = true;
        }
        else if(hiddenPlayers.get(uuid) != null && hiddenPlayers.get(uuid).contains(toHide.getUniqueId().toString())) {
            isHiding = true;
        }

        return isHiding;
    }

    public void setVanishState(Player player, boolean vanished) {
        Linkcraft.sync(() -> {
            if(vanished && !VanishAPI.isInvisible(player)) {
                VanishAPI.hidePlayer(player);
            } else if(!vanished && VanishAPI.isInvisible(player)) {
                VanishAPI.showPlayer(player);
            }
        });
    }

    public void hideIfHiding(Player toHide) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(isHiding(player, toHide)) {
                Bukkit.getScheduler().runTask(Linkcraft.getPlugin(), () -> {
                    player.hidePlayer(toHide);
                    if(!VanishAPI.isInvisible(toHide)) {
                        addToTabList(player, toHide);
                    }
                });
            }
        }
    }

    public boolean canSee(Player viewer, Player other) {
        return VanishAPI.canSee(viewer, other);
    }
}
