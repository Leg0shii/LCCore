package de.legoshi.lccore.cosmetics.cosmetics.chestplate;

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
import team.unnamed.inject.Inject;

import java.util.HashMap;
import java.util.Map;

public class RainbowChestplate implements Cosmetic {

    private final Map<Player, BukkitRunnable> colorChangeTasks = new HashMap<>();

    @Override
    public void startAnimation(Player player) {

        ItemStack chestplate2 = new ItemBuilder(Material.LEATHER_CHESTPLATE).setName(getDisplayName())
                .setUnbreakable(true)
                .addFlags(ItemFlag.HIDE_UNBREAKABLE)
                .build();
        player.getInventory().setChestplate(chestplate2);

        new BukkitRunnable() {
            double t = 0;  // Variable to cycle through the rainbow
            @Override
            public void run() {
                ItemStack chestplate = player.getInventory().getChestplate();
                if (chestplate == null || chestplate.getType() != Material.LEATHER_CHESTPLATE || !player.isOnline()) {
                    cancel();
                    return;
                }

                Color color = getRainbowColor(t);
                t += 0.025;  // changing this changes the speed of the color change

                LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
                if (meta != null) {
                    meta.setColor(color);
                    chestplate.setItemMeta(meta);
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
        return "RainbowChestplate";
    }

    @Override
    public String getDisplayName() { return ChatColor.translateAlternateColorCodes('&', "&4R&ca&6i&en&2b&ao&bw &5C&dh&be&3s&2t&ap&el&6a&ct&4e"); }

    @Override
    public String getUncoloredName() { return "Rainbow Chestplate"; }

    @Override
    public CosmeticType getType() { return CosmeticType.CHESTPLATE; }

    @Override
    public ItemStack getDisplay() {
        ItemStack itemStack = new ItemStack(Material.LEATHER_CHESTPLATE);
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
