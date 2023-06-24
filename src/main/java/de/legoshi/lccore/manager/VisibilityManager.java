package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
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
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer)toAdd).getHandle());
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }

    public void removeFromTabList(Player player, Player toRemove) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer)toRemove).getHandle());
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
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
            if(!VanishAPI.isInvisible(p)) {
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
            player.showPlayer(p);
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

    public void hideIfHiding(Player toHide) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(isHiding(player, toHide)) {
                Bukkit.getScheduler().runTask(Linkcraft.getInstance(), () -> {
                    player.hidePlayer(toHide);
                    if(!VanishAPI.isInvisible(toHide)) {
                        addToTabList(player, toHide);
                    }
                });
            }
        }
    }
}
