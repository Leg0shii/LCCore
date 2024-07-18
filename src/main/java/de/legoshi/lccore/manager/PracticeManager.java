package de.legoshi.lccore.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.player.practice.PracticeItem;
import de.legoshi.lccore.util.CommandException;
import de.legoshi.lccore.util.LocationHelper;
import de.legoshi.lccore.util.message.MessageUtil;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.HashSet;
import java.util.Set;

public class PracticeManager {

    @Inject private LuckPermsManager lpManager;
    private final Set<Player> practicingPlayers = new HashSet<>();
    @Setter private String globalPracticeItem = "default";

    public void sendPracticeActionBarPacket(Player player) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.CHAT);

        packet.getChatComponents().write(0, WrappedChatComponent.fromText(getPracticeItemFor(player).getActionBarMessage()));
        packet.getBytes().write(0, (byte) 2);
        Linkcraft.getPlugin().protocolManager.sendServerPacket(player, packet);
    }

    private void sendRepeatingActionBar() {
        Linkcraft.syncRepeat(() -> {
            for(Player player : practicingPlayers) {
                sendPracticeActionBarPacket(player);
            }
        }, 40L, 0L);
    }

    public void init() {
        lpManager.createDefaultPracticeGroup();
        sendRepeatingActionBar();
    }

    public void addToPracticeActionBarQueue(Player player) {
        practicingPlayers.add(player);
    }

    public void removeFromPracticeActionBarQueue(Player player) {
        practicingPlayers.remove(player);
    }

    public boolean isInPractice(Player player) {
        FileConfiguration practiceData = Linkcraft.getPlugin().getPracticeData();
        return practiceData.get(player.getUniqueId().toString()) != null;
    }

    public void updatePracticeLocation(Player player, Location location) {
        FileConfiguration practiceData = Linkcraft.getPlugin().getPracticeData();
        practiceData.set(player.getUniqueId().toString(), LocationHelper.getStringFromLocation(location));
    }

    public void removePracticeLocation(Player player) {
        FileConfiguration practiceData = Linkcraft.getPlugin().getPracticeData();
        practiceData.set(player.getUniqueId().toString(), null);
    }

    public Location getPracticeLocation(Player player) {
        return getPracticeLocation(player.getUniqueId().toString());
    }

    public Location getPracticeLocation(String player) {
        FileConfiguration practiceData = Linkcraft.getPlugin().getPracticeData();
        String result = practiceData.getString(player);
        return result != null ? LocationHelper.getLocationFromString(result) : null;
    }

    public PracticeItem getPracticeItemFor(Player player) {
        return ConfigManager.practiceItems.get(globalPracticeItem);
    }

    public void givePracticeGroup(Player player) {
        lpManager.giveGroup(player, "practice");
    }

    public void removePracticeGroup(Player player) {
        lpManager.removeGroup(player, "practice");
    }

    public void usePracticeItem(Player player) {
        Location location = getPracticeLocation(player);
        if(location == null) {
            return;
        }

        PracticeItem practiceItem = getPracticeItemFor(player);
        MessageUtil.sendMessageIfNotNull(player, practiceItem.getUseMessage());
        MessageUtil.sendAudioIfNotNull(player, practiceItem.getUseAudio());
        player.teleport(location);
    }
}
