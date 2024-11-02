package de.legoshi.lccore.menu;

import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.util.GUIAction;
import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class GUIScrollableSnake extends GUIPane {

    protected StaticGuiElement scrollLeft;
    protected StaticGuiElement scrollRight;
    protected int scrolls = 0;
    protected int maxScrolls = 0;

    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        registerScrollElements();
    }

    protected abstract void getPage();

    @Override
    protected abstract void registerGuiElements();

    private void registerScrollElements() {
        this.scrolls = 0;
        this.scrollLeft = createScrollElement('l', "Scroll Left", -1);
        this.scrollRight = createScrollElement('r', "Scroll Right", 1);
    }

    protected void changeOption(char slotToRemove, GuiElement.Click click) {
        scrolls = 0;
        this.current.removeElement(slotToRemove);
        getPage();
        scrollLeft.setNumber(1);
        scrollRight.setNumber(2);
        click.getGui().setPageNumber(0);
        click.getGui().playClickSound();
    }





    private StaticGuiElement createScrollElement(char slot, String display, int increment) {
        ItemStack arrowItem = new ItemStack(Material.ARROW, scrolls + 1 + increment);
        GUIDescriptionBuilder descriptionBuilder = new GUIDescriptionBuilder()
                .raw(display)
                .action(GUIAction.LEFT_CLICK, "Scroll by 1")
                .action(GUIAction.SHIFT_LEFT_CLICK, "Scroll by 5");

        return new StaticGuiElement(slot, arrowItem, click -> {
            int newScroll;
//            if(increment == 1 && page == maxPages) {
//                return true;
//            }
//
//            if(increment == -1 && page == 0) {
//                return true;
//            }

            if (click.getType().isShiftClick()) {
                newScroll = Math.max(Math.min(scrolls + (5 * increment), maxScrolls), 0);
            } else {
                newScroll = Math.max(Math.min(scrolls + increment, maxScrolls), 0);
            }

            if (newScroll != scrolls) {
                scrolls = newScroll;
                getPage();
                scrollRight.setNumber(scrolls + 2);
                scrollLeft.setNumber(scrolls + 1);
                click.getGui().setPageNumber(scrolls);
                click.getGui().playClickSound();
            }
            return true;
        }, descriptionBuilder.build());
    }

    String[] guiSetup = {
            "---------",
            "12-101112-20-",
            "-3-9-13-19-",
            "-4-8-14-18-",
            "-567-151617-",
            "l-------r"
    };

    String[] guiSetup2 = {
            "---------",
            "2-101112-202122",
            "3-9-13-19-23",
            "4-8-14-18-24",
            "567-151617-25",
            "l-------r"
    };
    public int[][] assignGValues(String[] guiSetup, int scrolls) {
        int[][] gValues = new int[guiSetup.length][guiSetup[0].length()];

        for (int i = 0; i < guiSetup.length; i++) {
            for (int j = 0; j < guiSetup[i].length(); j++) {
                char currentChar = guiSetup[i].charAt(j);
                if (currentChar == 'g') {
                    gValues[i][j] = getValueForG(i, j, scrolls);
                } else {
                    gValues[i][j] = 0;
                }
            }
        }
        return gValues;
    }

    private int getValueForG(int i, int j, int scrolls) {

        if (i == 1) {
            if (j == 1) return 1 + scrolls;
            if (j == 3) return 0 + scrolls;
            if (j == 4) return 1 + scrolls;
            if (j == 5) return 1 + scrolls;
            if (j == 7) return 2 + scrolls;
            return 0;
        }
        if (i == 2) {
            if (j == 1) return 3 + scrolls;
            if (j == 3) return 9 + scrolls;
            if (j == 5) return 13 + scrolls;
            if (j == 7) return 19 + scrolls;
            return 0;
        }
        if (i == 3) {
            if (j == 1) return 4 + scrolls;
            if (j == 3) return 8 + scrolls;
            if (j == 5) return 14 + scrolls;
            if (j == 7) return 18 + scrolls;
            return 0;
        }
        if (i == 4) {
            if (j == 1) return 5 + scrolls;
            if (j == 3) return 15 + scrolls;
            if (j == 5) return 16 + scrolls;
            if (j == 7) return 17 + scrolls;
            return 0;
        }
        return 0;
    }

    public String[] transformGuiSetup(String[] guiSetup, int[][] gValues) {
        int rowCount = guiSetup.length;
        int colCount = guiSetup[0].length();
        String[] transformedSetup = new String[rowCount];
        int[][] newGValues = new int[rowCount][colCount];

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
                        newGValues[i][j] = newGCounter++;
                    } else {
                        newGValues[i][j] = 0;
                    }
                }

                transformedSetup[i] = newLine.toString();
            }
        }

        return transformedSetup;
    }

    private int getValueForNewG(int i, int j, int counter, int scrolls) {

        if (i == 1) {
            if (j == 1) return 2 + scrolls;
            if (j == 3) return 10 + scrolls;
            if (j == 4) return 11 + scrolls;
            if (j == 5) return 12 + scrolls;
            if (j == 6) return 20 + scrolls;
            if (j == 7) return 22 + scrolls;
            return 0;
        }
        if (i == 2) {
            if (j == 1) return 3 + scrolls;
            if (j == 3) return 9 + scrolls;
            if (j == 5) return 13 + scrolls;
            if (j == 6) return 19 + scrolls;
            if (j == 7) return 23 + scrolls;
            return 0;
        }
        if (i == 3) {
            if (j == 1) return 4 + scrolls;
            if (j == 3) return 8 + scrolls;
            if (j == 5) return 14 + scrolls;
            if (j == 6) return 18 + scrolls;
            if (j == 7) return 24 + scrolls;
            return 0;
        }
        if (i == 4) {
            if (j == 1) return 5 + scrolls;
            if (j == 3) return 15 + scrolls;
            if (j == 5) return 16 + scrolls;
            if (j == 6) return 17 + scrolls;
            if (j == 7) return 25 + scrolls;
            return 0;
        }
        return 0;
    }


}
