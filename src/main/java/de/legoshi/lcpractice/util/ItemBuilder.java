package de.legoshi.lcpractice.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

// PARTLY REFACTORED
public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material type) {
        this.item = new ItemStack(type);
        this.meta = this.item.getItemMeta();
    }

    public ItemBuilder(Material type, int id) {
        this.item = new ItemStack(type, 1, (short) id);
        this.meta = this.item.getItemMeta();
    }

    public ItemBuilder(Material type, int id, int amount) {
        this.item = new ItemStack(type, amount, (short) id);
        this.meta = this.item.getItemMeta();
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = this.item.getItemMeta();
    }

    public ItemBuilder addEnchant(Enchantment enchant, int level) {
        this.meta.addEnchant(enchant, level, true);
        return this;
    }

    public ItemBuilder addFlags(ItemFlag... flags) {
        this.meta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder setName(String name) {
        this.meta.setDisplayName(Utils.chat(name));
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        this.meta.setLore(Utils.chat(lore));
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        this.meta.spigot().setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder setOwner(String name) {
        if (this.meta instanceof SkullMeta)
            ((SkullMeta) this.meta).setOwner(name);
        return this;
    }

    public ItemStack build() {
        this.item.setItemMeta(this.meta);
        return this.item;
    }
}
