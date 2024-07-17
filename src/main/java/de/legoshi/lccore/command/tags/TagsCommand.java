package de.legoshi.lccore.command.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.menu.tags.TagMenu;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.SubCommandClasses;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

@Register
@Command(names = {"tags"}, permission = "tags", desc = "")
@SubCommandClasses({
    TagsCreateCommand.class,
    TagsEditCommand.class,
    TagsDeleteCommand.class,
    TagsUnlockCommand.class,
    TagsSignCommand.class,
    TagsRemoveCommand.class,
    TagsEquipCommand.class,
    TagsSearchCommand.class,
    TagsResetCommand.class
})
public class TagsCommand implements CommandClass {

    @Inject private Injector injector;

    @Command(names = "")
    public void tags(CommandSender sender) {
        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }

            Player player = (Player)sender;
            injector.getInstance(TagMenu.class).openGui(player, null);
        });
    }
}
