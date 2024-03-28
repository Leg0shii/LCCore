package de.legoshi.lccore.menu.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.Tag;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.TagManager;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.menu.GuiMessage;
import de.legoshi.lccore.tag.TagRarity;
import de.legoshi.lccore.tag.TagType;
import de.legoshi.lccore.util.*;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.GuiStateElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.inject.Inject;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiConsumer;

public class TagCreate extends GUIPane {

    @Inject private DBManager db;
    @Inject private TagManager tagManager;
    @Inject private ChatManager chatManager;

    private final String[] guiSetup = {
            "ddmmcmmdd",
            "s       s",
            "s itrov s",
            "s ne    s",
            "s       s",
            "ddmmqmmdd",
    };

    private Tag tag;
    private boolean edit;

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        edit = tag != null;
        String title = edit ? "Tag Editor - " + tag.getName() : "Tag Creator";
        if(tag == null) {
            tag = new Tag();
        }
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, title, guiSetup);
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    public GUIPane openGui(Player player, InventoryGui parent, Tag tag) {
        this.tag = tag;
        openGui(player, parent);
        return this;
    }

    private void update() {
        if(edit) {
            db.update(tag);
        }
    }

    private final BiConsumer<GuiElement.Click, TagType> tagTypeSetter = (click, type) -> {
        tag.setType(type);
        update();
        click.getGui().playClickSound();
    };

    private final BiConsumer<GuiElement.Click, TagRarity> tagRaritySetter = (click, rarity) -> {
        tag.setRarity(rarity);
        update();
        click.getGui().playClickSound();
    };

    // s = display
    // t = type
    // r = rarity
    // n = name (optional)
    // o = obtainable (default TRUE)
    // v = visible (default TRUE)

    @Override
    protected void registerGuiElements() {
        setColours(Dye.CYAN, Dye.LIME, Dye.YELLOW);

        StaticGuiElement tagSetDisplay = new StaticGuiElement('i', new ItemStack(Material.NAME_TAG), click -> true,
                new GUIDescriptionBuilder()
                        .raw("Display")
                        .pair("Display", tag.getDisplay())
                        .action(GUIAction.LEFT_CLICK, "Edit Display (Anvil)")
                        .action(GUIAction.RIGHT_CLICK, "Edit Display (Chat)")
                        .build());

        tagSetDisplay.setAction(click -> {
            if(click.getType().isLeftClick()) {
                new AnvilGUI.Builder().onComplete((completion) -> {
                    tag.setDisplay(completion.getText());
                    tagSetDisplay.setText(new GUIDescriptionBuilder()
                            .raw("Display")
                            .pair("Display", GUIUtil.colorize(completion.getText()))
                            .action(GUIAction.LEFT_CLICK, "Edit Display (Anvil)")
                            .action(GUIAction.RIGHT_CLICK, "Edit Display (Chat)")
                            .build());
                    update();
                    LCSound.SUCCESS.playLater(holder);

                    return Collections.singletonList(AnvilGUI.ResponseAction.run(() -> {
                        current.show(holder);
                    }));
                })
                        .onClose(close -> current.show(holder))
                        .title("Tag Display")
                        .itemLeft(ItemUtil.setItemText(new ItemStack(Material.PAPER), tag.getDisplayRaw()))
                        .plugin(Linkcraft.getPlugin())
                        .open(holder);
            } else if(click.getType().isRightClick()) {
                current.close();
                chatManager.listenForGuiMessage(holder, new GuiMessage(this, (response) -> {
                    tag.setDisplay(response);
                    tagSetDisplay.setText(new GUIDescriptionBuilder()
                            .raw("Display")
                            .pair("Display", GUIUtil.colorize(response))
                            .action(GUIAction.LEFT_CLICK, "Edit Display (Anvil)")
                            .action(GUIAction.RIGHT_CLICK, "Edit Display (Chat)")
                            .build());
                    update();
                    current.show(holder);
                }));
            }
            return true;
        });

        GuiStateElement tagSetType = new GuiStateElement('t',
                GUIUtil.createSelectionMenu(TagType.class, new ItemStack(Material.IRON_PICKAXE), "Type", tagTypeSetter, false,
                        new GUIDescriptionBuilder()
                                .action(GUIAction.LEFT_CLICK, "Next")
                                .build()));

        if(tag.getType() != null) {
            tagSetType.setState(tag.getType().name());
        }

        GuiStateElement tagSetRarity = new GuiStateElement('r',
                GUIUtil.createSelectionMenu(TagRarity.class, new ItemStack(Material.DIAMOND), "Rarity", tagRaritySetter, false,
                        new GUIDescriptionBuilder()
                                .action(GUIAction.LEFT_CLICK, "Next")
                                .build()));

        if(tag.getRarity() != null) {
            tagSetRarity.setState(tag.getRarity().name());
        }

        GuiStateElement tagToggleObtainable = new GuiStateElement('o',
                new GuiStateElement.State(
                        change -> {
                            tag.setObtainable(true);
                            update();
                        },
                        "obtainable",
                        new ItemStack(Material.WOOL, 1, Dye.LIME.data),
                        new GUIDescriptionBuilder().raw("Obtainable")
                                .pair("Enabled", "true")
                                .build()
                ),
                new GuiStateElement.State(
                        change -> {
                            tag.setObtainable(false);
                            update();
                        },
                        "unobtainable",
                        new ItemStack(Material.WOOL, 1, Dye.GRAY.data),
                        new GUIDescriptionBuilder().raw("Unobtainable")
                                .pair("Enabled", "false")
                                .build()
                )
        );

        tagToggleObtainable.setState(tag.isObtainable() ? "obtainable" : "unobtainable");

        GuiStateElement tagToggleVisibility = new GuiStateElement('v',
                new GuiStateElement.State(
                        change -> {
                            tag.setVisible(true);
                            update();
                        },
                        "visible",
                        new ItemStack(Material.WOOL, 1, Dye.LIME.data),
                        new GUIDescriptionBuilder().raw("Visible")
                                .pair("Enabled", "true")
                                .build()
                ),
                new GuiStateElement.State(
                        change -> {
                            tag.setVisible(false);
                            update();
                        },
                        "invisible",
                        new ItemStack(Material.WOOL, 1, Dye.GRAY.data),
                        new GUIDescriptionBuilder().raw("Invisible")
                                .pair("Enabled", "false")
                                .build()
                )
        );

        tagToggleVisibility.setState(tag.isVisible() ? "visible" : "invisible");

//        StaticGuiElement tagSetDescription = new StaticGuiElement('e', new ItemStack(Material.SIGN), click -> true,
//                new GUIDescriptionBuilder()
//                        .raw("Description")
//                        .pair("Description", GUIUtil.wrap(tag.getDescription()))
//                        .action(GUIAction.LEFT_CLICK, "Edit Description (Anvil)")
//                        .action(GUIAction.RIGHT_CLICK, "Edit Description (Chat)")
//                        .build());
//
//        tagSetDescription.setAction(click -> {
//            if(click.getType().isLeftClick()) {
//                new AnvilGUI.Builder().onComplete((completion) -> {
//                            tag.setDescription(completion.getText());
//                            tagSetDescription.setText(new GUIDescriptionBuilder()
//                                    .raw("Description")
//                                    .pair("Description", GUIUtil.wrap(completion.getText()))
//                                    .action(GUIAction.LEFT_CLICK, "Edit Description (Anvil)")
//                                    .action(GUIAction.RIGHT_CLICK, "Edit Description (Chat)")
//                                    .build());
//                            update();
//                            LCSound.SUCCESS.playLater(holder);
//
//                            return Collections.singletonList(AnvilGUI.ResponseAction.run(() -> {
//                                current.show(holder);
//                            }));
//                        })
//                        .onClose(close -> current.show(holder))
//                        .title("Tag Description")
//                        .itemLeft(ItemUtil.setItemText(new ItemStack(Material.PAPER), tag.getDescription()))
//                        .plugin(Linkcraft.getPlugin())
//                        .open(holder);
//            } else if(click.getType().isRightClick()) {
//                current.close();
//                chatManager.listenForGuiMessage(holder, new GuiMessage(this, (response) -> {
//                    tag.setDescription(response);
//                    tagSetDescription.setText(new GUIDescriptionBuilder()
//                            .raw("Description")
//                            .pair("Description", GUIUtil.wrap(response))
//                            .action(GUIAction.LEFT_CLICK, "Edit Description (Anvil)")
//                            .action(GUIAction.RIGHT_CLICK, "Edit Description (Chat)")
//                            .build());
//                    update();
//                    current.show(holder);
//                }));
//            }
//            return true;
//        });

        StaticGuiElement tagSetName = new StaticGuiElement('n', new ItemStack(Material.NAME_TAG), click -> true,
                new GUIDescriptionBuilder()
                        .raw("Name")
                        .pair("Name", tag.getName())
                        .action(GUIAction.LEFT_CLICK, "Edit Name (Anvil)")
                        .action(GUIAction.RIGHT_CLICK, "Edit Name (Chat)")
                        .build());

        tagSetName.setAction(click -> {
            if(edit) {
                MessageUtil.send(Message.TAGS_CANT_EDIT_NAME, holder);
                LCSound.ERROR.playLater(holder);
            }
            else if(click.getType().isLeftClick()) {
                new AnvilGUI.Builder().onComplete((completion) -> {
                            if(tagManager.tagExists(completion.getText())) {
                                MessageUtil.send(Message.TAGS_ALREADY_EXISTS, holder, completion.getText());
                                LCSound.ERROR.playLater(holder);
                                return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(completion.getText()));
                            }

                            tag.setName(completion.getText());
                            tagSetName.setText(new GUIDescriptionBuilder()
                                    .raw("Name")
                                    .pair("Name", completion.getText())
                                    .action(GUIAction.LEFT_CLICK, "Edit Name (Anvil)")
                                    .action(GUIAction.RIGHT_CLICK, "Edit Name (Chat)")
                                    .build());
                            update();
                            LCSound.SUCCESS.playLater(holder);

                            return Collections.singletonList(AnvilGUI.ResponseAction.run(() -> {
                                current.show(holder);
                            }));
                        })
                        .onClose(close -> current.show(holder))
                        .title("Tag Name")
                        .itemLeft(ItemUtil.setItemText(new ItemStack(Material.PAPER), tag.getName()))
                        .plugin(Linkcraft.getPlugin())
                        .open(holder);
            } else if(click.getType().isRightClick()) {
                current.close();
                chatManager.listenForGuiMessage(holder, new GuiMessage(this, (response) -> {
                    if(tagManager.tagExists(response)) {
                        MessageUtil.send(Message.TAGS_ALREADY_EXISTS, holder, response);
                        LCSound.ERROR.playLater(holder);
                        current.show(holder);
                        return;
                    }

                    tag.setName(response);
                    tagSetName.setText(new GUIDescriptionBuilder()
                            .raw("Name")
                            .pair("Name", GUIUtil.wrap(response))
                            .action(GUIAction.LEFT_CLICK, "Edit Name (Anvil)")
                            .action(GUIAction.RIGHT_CLICK, "Edit Name (Chat)")
                            .build());
                    update();
                    current.show(holder);
                }));
            }
            return true;
        });

        StaticGuiElement tagCreate = new StaticGuiElement('q', new ItemStack(Material.WOOL, 1, Dye.LIME.data), click -> {
            try {
                String id = tagManager.createTag(tag);
                MessageUtil.send(Message.TAGS_ADD_SUCCESS, holder, tag.getName(), id, tag.getDisplay());
                LCSound.COMPLETE.playLater(holder);
                current.close();
            } catch (CommandException e) {
                LCSound.ERROR.playLater(holder);
                MessageUtil.send(e.getMessage(), holder);
            }
           return true;
        }, new GUIDescriptionBuilder()
                .raw(ChatColor.GREEN + "Create Tag")
                .build());

        StaticGuiElement bottomItem = edit ? returnToParent : tagCreate;

        current.addElements(tagSetDisplay, tagSetType, tagSetRarity, tagToggleObtainable, tagToggleVisibility, tagSetName, bottomItem);
    }
}
