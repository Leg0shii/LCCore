package de.legoshi.lcpractice.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

// PARTLY REFACTORED
public abstract class Menu implements InventoryHolder {

  protected final Inventory inv;
  public static final ItemStack SPACE = (new ItemBuilder(Material.STAINED_GLASS_PANE, 15)).setName(Utils.chat("&r")).build();
  
  public Menu(String title, int rows) {
    this(title, rows, 0);
  }
  
  public Menu(String title, int rows, int space) {
    this.inv = Bukkit.createInventory(this, rows * 9, Utils.chat(title));
    for (int i = space; i < rows * 9; i++)
      this.inv.setItem(i, SPACE); 
  }
  
  public abstract void onClick(InventoryClickEvent paramInventoryClickEvent);
  
  public Inventory getInventory() {
    return this.inv;
  }

}