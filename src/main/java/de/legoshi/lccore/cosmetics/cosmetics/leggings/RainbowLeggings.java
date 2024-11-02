package de.legoshi.lccore.cosmetics.cosmetics.leggings;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.cosmetics.Cosmetic;
import de.legoshi.lccore.cosmetics.CosmeticType;
import de.legoshi.lccore.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class RainbowLeggings implements Cosmetic {

    private final Map<Player, BukkitRunnable> colorChangeTasks = new HashMap<>();

    @Override
    public void startAnimation(Player player) {

        ItemStack leggings2 = new ItemBuilder(Material.LEATHER_LEGGINGS).setName(getDisplayName())
                .setUnbreakable(true)
                .addFlags(ItemFlag.HIDE_UNBREAKABLE)
                .build();
        player.getInventory().setLeggings(leggings2);

        new BukkitRunnable() {
            double t = 0;  // Variable to cycle through the rainbow
            @Override
            public void run() {
                ItemStack leggings = player.getInventory().getLeggings();
                if (leggings == null || leggings.getType() != Material.LEATHER_LEGGINGS || !player.isOnline()) {
                    cancel();
                    return;
                }

                Color color = getRainbowColor(t);
                t += 0.025;  // changing this changes the speed of the color change

                LeatherArmorMeta meta = (LeatherArmorMeta) leggings.getItemMeta();
                if (meta != null) {
                    meta.setColor(color);
                    leggings.setItemMeta(meta);
                }
            }
        }.runTaskTimer(Linkcraft.getPlugin(), 0, 1);

    }

    @Override
    public void stopAnimation(Player player) {
        BukkitRunnable task = colorChangeTasks.remove(player);
        if (task != null) {
            task.cancel();
        }

    }

    @Override
    public String getName() {
        return "RainbowLeggings";
    }

    @Override
    public String getDisplayName() { return ChatColor.translateAlternateColorCodes('&', "&4R&ca&6i&en&2b&ao&bw &5L&de&2g&ag&ei&6n&cg&4s"); }

    @Override
    public String getUncoloredName() { return "Rainbow Boots"; }

    @Override
    public CosmeticType getType() { return CosmeticType.LEGGINGS; }

    @Override
    public ItemStack getDisplay() {
        ItemStack itemStack = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
        meta.setColor(Color.RED);
        meta.setDisplayName(getDisplayName());
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public Color getRainbowColor(double time) {
        int red = (int) (Math.sin(time) * 127 + 128);
        int green = (int) (Math.sin(time + 2 * Math.PI / 3) * 127 + 128);
        int blue = (int) (Math.sin(time + 4 * Math.PI / 3) * 127 + 128);

        return Color.fromRGB(red, green, blue);
    }
}
