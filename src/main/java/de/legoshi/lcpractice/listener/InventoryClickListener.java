package de.legoshi.lcpractice.listener;

import de.legoshi.lcpractice.Linkcraft;
import de.legoshi.lcpractice.ui.AdminUI;
import de.legoshi.lcpractice.ui.SaveCreationUI;
import de.legoshi.lcpractice.ui.SavesUI;
import de.legoshi.lcpractice.util.ConfigAccessor;
import de.legoshi.lcpractice.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

// PARTLY REFACTORED
public class InventoryClickListener implements Listener {

    private final Linkcraft plugin;

    public InventoryClickListener(Linkcraft plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void eggThrow(PlayerEggThrowEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("chestcommands.open.staff.yml"))
            e.setHatching(false);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String title = e.getInventory().getTitle();
        int slot = e.getSlot();
        if (title.matches(SavesUI.inventory_name + " \\(page \\d*\\)")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            SavesUI.clicked((Player)e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
        } else if (title.equals(SaveCreationUI.inventory_name)) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            SaveCreationUI.clicked((Player)e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
        } else if (title.equals(AdminUI.inventory_name + " (page " + AdminUI.currentPage.get(e.getWhoClicked().getUniqueId().toString()) + ")")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            if (e.getClick().isShiftClick() && e.getSlot() < 36) {
                OfflinePlayer target = this.plugin.playerMap.get(e.getWhoClicked().getName());
                String uuid = target.getUniqueId().toString();
                ConfigAccessor playerData = new ConfigAccessor((JavaPlugin)this.plugin, this.plugin.getPlayerdataFolder(), uuid + ".yml");
                FileConfiguration playerDataConfig = playerData.getConfig();
                Set<String> keys = playerDataConfig.getConfigurationSection("Saves").getKeys(false);
                playerDataConfig.set("Saves." + keys.toArray()[slot + (AdminUI.currentPage.get(e.getWhoClicked().getUniqueId().toString()) - 1) * 36], null);
                playerData.saveConfig();
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().sendMessage(Utils.chat("&cDeleted " + target.getName() + "'s save."));
                ((Player)e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.ORB_PICKUP, 1.0F, 1.43F);
                return;
            }
            AdminUI.clicked((Player)e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
        }
    }
}
