package de.legoshi.lccore.player.practice;

import de.legoshi.lccore.util.Audio;
import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.legoshi.lccore.util.ItemUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnpracticeItem {
    // Messages
    private String fullInventoryMessage = null;
    private String useMessage = null;

    // Audio
    private Audio fullInventoryAudio = null;
    private Audio useAudio = null;

    // Item
    private ItemStack item;
    private Material itemMat = null;
    private int itemAmt = 1;
    private short itemDamage = 0;
    private GUIDescriptionBuilder itemText = null;


    public void build() {
        this.item = new ItemStack(itemMat, itemAmt, itemDamage);
        this.item = ItemUtil.setItemText(this.item, itemText.build());
        this.item = ItemUtil.addNbtId(this.item, "unpractice");
    }
}
