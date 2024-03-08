package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.player.display.LCPlayer;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Utils;
import me.clip.deluxetags.DeluxeTag;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerManager {
    @Inject private ChatManager chatManager;
    @Inject private VisibilityManager visibilityManager;

    private final HashMap<String, LCPlayer> players = new HashMap<>();

    public void playerJoin(Player player) {
        updateName(player);
        players.put(player.getUniqueId().toString(), loadPlayer(player));
        visibilityManager.hideIfHiding(player);
        chatManager.onJoin(player);
        clearTitle(player);
    }



    public void playerLeave(Player player) {
        players.remove(player.getUniqueId().toString());
        visibilityManager.onLeave(player);
        chatManager.onLeave(player);
        saveConfigDataTemp(player);
    }


    // TODO: ON MAP COMPLETE
    public void updatePlayer(Player player) {
        players.put(player.getUniqueId().toString(), loadPlayer(player));
    }

    private LCPlayer loadPlayer(Player player) {
        return loadPlayer(player.getUniqueId().toString());
    }

    public LCPlayer loadPlayer(String player) {
        LCPlayer lcPlayer = new LCPlayer();
        // TODO: set nickname somehow and save :skull:
        lcPlayer.setName(getName(player));
        lcPlayer.setNick(getNick(player));
        lcPlayer.setRank(chatManager.determineRank(player));
        lcPlayer.setBonus(chatManager.determineBonus(player));
        lcPlayer.setWolf(chatManager.determineWolf(player));
        lcPlayer.setMaze(chatManager.determineMaze(player));
        lcPlayer.setStar(chatManager.determineStar(player));
        lcPlayer.setChatFormats(chatManager.determineChatFormats(player));
        lcPlayer.setChatColor(chatManager.determineChatColor(player));
        lcPlayer.setNameFormats(chatManager.determineNameFormat(player));
        lcPlayer.setManualChatColors(chatManager.determineManualChatColors(player));
        lcPlayer.setManualChatFormats(chatManager.determineManualChatFormats(player));
        return lcPlayer;
    }

    public void clearTitle(Player player) {
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatMessage(""));
        PacketPlayOutTitle subtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, new ChatMessage(""));
        PlayerConnection conn = ((CraftPlayer) player).getHandle().playerConnection;
        conn.sendPacket(title);
        conn.sendPacket(subtitle);
    }

    private void updateName(Player player) {
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getPlugin(), Linkcraft.getPlugin().getPlayerdataFolder(), player.getUniqueId().toString() + ".yml");
        FileConfiguration config = playerData.getConfig();
        config.set("username", player.getName());
        playerData.saveConfig();
    }

    public void updateNick(Player player, String nick) {
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getPlugin(), Linkcraft.getPlugin().getPlayerdataFolder(), player.getUniqueId().toString() + ".yml");
        FileConfiguration config = playerData.getConfig();
        config.set("nick", nick);
        playerData.saveConfig();
        updatePlayer(player);
    }

    public List<String> getIgnores(Player player) {
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getPlugin(), Linkcraft.getPlugin().getPlayerdataFolder(), player.getUniqueId().toString() + ".yml");
        FileConfiguration config = playerData.getConfig();
        List<String> ignores = config.getStringList("ignores");

        return ignores != null ? ignores : new ArrayList<>();
    }

    public void updateIgnore(Player player, Player toIgnore, boolean ignore) {
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getPlugin(), Linkcraft.getPlugin().getPlayerdataFolder(), player.getUniqueId().toString() + ".yml");
        FileConfiguration config = playerData.getConfig();
        List<String> ignores = getIgnores(player);

        if(!ignore) {
            ignores.remove(toIgnore.getUniqueId().toString());
        } else {
            ignores.add(toIgnore.getUniqueId().toString());
        }

        config.set("ignores", ignores);
        playerData.saveConfig();
    }

    private String getName(String uuid) {
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getPlugin(), Linkcraft.getPlugin().getPlayerdataFolder(), uuid + ".yml");
        FileConfiguration config = playerData.getConfig();
        return config.getString("username");
    }

    private String getNick(String uuid) {
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getPlugin(), Linkcraft.getPlugin().getPlayerdataFolder(), uuid + ".yml");
        FileConfiguration config = playerData.getConfig();
        String nick = config.getString("nick");
        return nick != null ? nick : "";
    }


    private void saveConfigDataTemp(Player player) {
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getPlugin(), Linkcraft.getPlugin().getPlayerdataFolder(), player.getUniqueId() + ".yml");
        FileConfiguration playerDataConfig = playerData.getConfig();
        playerDataConfig.set("lastlocation", Utils.getStringFromLocation(player.getLocation()));
        playerDataConfig.set("jumps", player.getStatistic(Statistic.JUMP));
        playerDataConfig.set("tags", DeluxeTag.getAvailableTagIdentifiers(player));
        playerData.saveConfig();
    }

    public LCPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId().toString());
    }

    public LCPlayer getPlayer(String player) {
        return players.get(player);
    }

    public int getPing(Player player) {
        return ((CraftPlayer)player).getHandle().ping;
    }

    public List<String> getPlayers(Player p) {
        List<String> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach((player) -> {
            if(visibilityManager.canSee(p, player)) {
                players.add(player.getName());
            }
        });

        return players;
    }

    @SuppressWarnings("unused")
    public List<String> getNicks() {
        List<String> nicks = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach((player) -> {
            String nick = getPlayer(player).getNick();
            if(!nick.isEmpty()) {
                nicks.add(chatManager.removeChatColour(nick));
            }
        });

        return nicks;
    }
}
