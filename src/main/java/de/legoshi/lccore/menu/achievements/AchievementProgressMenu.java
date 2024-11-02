package de.legoshi.lccore.menu.achievements;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.menu.GUIScrollableSnake;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AchievementProgressMenu extends GUIScrollableSnake {

    private Player target;
    private int scrolls = 0;

    public String[] guiSetup = {
            "---------",
            "gg-ggg-gg",
            "-g-g-g-g-",
            "-g-g-g-g-",
            "-ggg-ggg-",
            "l-------r"
    };

    public void openGui(Player player, Player target, InventoryGui parent) {
        super.openGui(player, parent);
        this.target = target;
        String title = "Achievement Progress";

        this.current = new InventoryGui(Linkcraft.getPlugin(), player, title, guiSetup);
        registerGuiElements();
        fullCloseOnEsc();

        this.current.addElements(this.scrollLeft, this.scrollRight);
        this.current.show(this.holder);
    }

    @Override
    protected void getPage() {
        this.current.removeElement('g');
        GuiElementGroup group = new GuiElementGroup('g');
        for (int i = 0; i < 100; i++) {
            StaticGuiElement pane = new StaticGuiElement('g', createGlassPane(i));
            group.addElement(pane);
        }
        this.current.addElement(group);
    }

    private ItemStack createGlassPane(int value) {
        ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE);
        ItemMeta meta = glassPane.getItemMeta();
        meta.setDisplayName(String.valueOf(value));
        glassPane.setItemMeta(meta);
        return glassPane;
    }

    public void scroll() {
        scrolls++;
        guiSetup = transformGuiSetup(guiSetup);
        getPage();
    }

    public String[] transformGuiSetup(String[] guiSetup) {
        int rowCount = guiSetup.length;
        int colCount = guiSetup[0].length();
        String[] transformedSetup = new String[rowCount];
        int newGCounter = 1;

        for (int i = 0; i < rowCount; i++) {
            if (i == rowCount - 1) {
                transformedSetup[i] = guiSetup[i];
            } else {
                StringBuilder newLine = new StringBuilder();

                for (int j = 1; j < colCount; j++) {
                    newLine.append(guiSetup[i].charAt(j));
                }
                newLine.append(guiSetup[i].charAt(1));

                for (int j = 0; j < newLine.length(); j++) {
                    if (newLine.charAt(j) == 'g') {
                        newLine.setCharAt(j, (char) ('0' + newGCounter++));
                    }
                }

                transformedSetup[i] = newLine.toString();
            }
        }

        return transformedSetup;
    }

    @Override
    protected void registerGuiElements() {
        getPage();

        this.scrollLeft.setAction(click -> {
            scroll();
            refreshGuiWithNewSetup(holder);
            return true;
        });

        this.scrollRight.setAction(click -> {
            scroll();
            refreshGuiWithNewSetup(holder);
            return true;
        });
    }

    private void refreshGuiWithNewSetup(Player player) {
        super.openGui(player, null);
        guiSetup = transformGuiSetup(guiSetup);

        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Achievement Progress", guiSetup);

        registerGuiElements();
        fullCloseOnEsc();

        this.scrollLeft = createScrollButton('l', "< Previous");
        this.scrollRight = createScrollButton('r', "Next >");

        this.current.addElements(this.scrollLeft, this.scrollRight);
        this.current.show(player);
    }

    private StaticGuiElement createScrollButton(char slotChar, String label) {
        return new StaticGuiElement(slotChar, new ItemStack(Material.ARROW), click -> {
            refreshGuiWithNewSetup(holder);
            return true;
        }, label);
    }
}
