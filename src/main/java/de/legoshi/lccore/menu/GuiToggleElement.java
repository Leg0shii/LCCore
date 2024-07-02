package de.legoshi.lccore.menu;

import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.legoshi.lccore.util.GUIUtil;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.GuiStateElement;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GuiToggleElement {
    private char slot;
    @Getter private GuiStateElement element;
    @Accessors(chain = true) @Setter private GUIDescriptionBuilder description;
    @Accessors(chain = true) @Setter private ItemStack enabledItem;
    @Accessors(chain = true) @Setter private ItemStack disabledItem;
    @Accessors(chain = true) @Setter private GuiToggleResult onEnable;
    @Accessors(chain = true) @Setter private GuiToggleResult onDisable;
    @Accessors(chain = true) @Setter private GuiDescriptionUpdate onDescriptionUpdate;
    @Accessors(chain = true) @Setter private boolean insertTFPair;
    @Accessors(chain = true) @Setter private boolean isInGroup;
    private boolean isFirstTFPair = true;
    private boolean initialState;
    private GuiStateElement.State onState;
    private GuiStateElement.State offState;


    public GuiToggleElement(char slot, GUIDescriptionBuilder description, boolean initialState) {
        this.slot = slot;
        this.description = description;
        this.onEnable = () -> true;
        this.onDisable = () -> true;
        this.insertTFPair = true;
        this.isInGroup = false;
        this.enabledItem = new ItemStack(Material.INK_SACK, 1, (short)10);
        this.disabledItem = new ItemStack(Material.INK_SACK, 1, (short)8);
        this.initialState = initialState;
        this.onDescriptionUpdate = (desc -> desc);
    }

    public GuiStateElement build() {
        updateTFPair(initialState);

        this.onState = new GuiStateElement.State(click -> {}, "enabled", enabledItem, description.build());
        this.offState = new GuiStateElement.State(click -> {}, "disabled", disabledItem, description.build());
        GUIUtil.setStateAction(onState, click -> {
            if(onEnable.run()) {
                updateTFPair(true);
                setState(click, true);
                if(!isInGroup) {
                    updateDescription((GuiStateElement) click.getElement(), false);
                }
            } else {
                setState(click, false);
            }
        });

        GUIUtil.setStateAction(offState, click -> {
            if(onDisable.run()) {
                setState(click, false);
                if(!isInGroup) {
                    updateDescription((GuiStateElement) click.getElement(), false);
                }
            } else {
                setState(click, true);
            }
        });
        GuiStateElement result = new GuiStateElement(slot, initialState ? "enabled" : "disabled", onState, offState);
        this.element = result;

        return result;
    }

    public void updateDescription(GuiStateElement element, boolean force) {
        if(force || !isInGroup) {
            this.description = onDescriptionUpdate.update(this.description);
            updateTFPair(true);
            onState.setText(this.description.build());
            updateTFPair(false);
            offState.setText(this.description.build());
            element.getGui().draw();
        }
    }

    public void setState(GuiElement.Click click, boolean enable) {
        GuiStateElement curr = (GuiStateElement)click.getElement();
        if(enable) {
            curr.setState("enabled");
        } else {
            curr.setState("disabled");
        }
    }

    public void updateTFPair(boolean state) {
        if(insertTFPair) {
            if(isFirstTFPair) {
                description.pairInsert("Enabled", GUIUtil.trueFalseDisplay(state), 1);
                isFirstTFPair = false;
            } else {
                description.pair("Enabled", GUIUtil.trueFalseDisplay(state), 1);
            }
        }
    }
}
