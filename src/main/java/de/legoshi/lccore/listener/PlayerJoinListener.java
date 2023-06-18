package de.legoshi.lccore.listener;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Utils;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

// PARTLY REFACTORED
public class PlayerJoinListener implements Listener {

  private final Linkcraft plugin;
  
  public PlayerJoinListener(Linkcraft plugin) {
    this.plugin = plugin;
  }
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    final Player p = event.getPlayer();
    ConfigAccessor playerData = new ConfigAccessor(this.plugin, this.plugin.getPlayerdataFolder(), p.getUniqueId() + ".yml");
    FileConfiguration playerDataConfig = playerData.getConfig();
    if (playerDataConfig.getString("lastlocation") != null) {
      final Location loc = Utils.getLocationFromString(playerDataConfig.getString("lastlocation"));
      (new BukkitRunnable() {
          public void run() {
            p.teleport(loc);
          }
        }).runTaskLater(this.plugin, 5L);
    }

    clearTitle(p);
  }

  public void clearTitle(Player player) {
    PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatMessage(""));
    PacketPlayOutTitle subtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, new ChatMessage(""));
    PlayerConnection conn = ((CraftPlayer)player).getHandle().playerConnection;
    conn.sendPacket(title);
    conn.sendPacket(subtitle);
  }
}