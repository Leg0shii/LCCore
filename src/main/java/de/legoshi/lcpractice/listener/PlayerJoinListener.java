package de.legoshi.lcpractice.listener;

import de.legoshi.lcpractice.Linkcraft;
import de.legoshi.lcpractice.util.ConfigAccessor;
import de.legoshi.lcpractice.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
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
    Bukkit.getPluginManager().registerEvents(this, plugin);
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
  }
}