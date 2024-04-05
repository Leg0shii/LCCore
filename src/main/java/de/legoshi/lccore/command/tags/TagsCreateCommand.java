package de.legoshi.lccore.command.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.TagManager;
import de.legoshi.lccore.menu.tags.TagCreate;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

@Command(names = {"create"}, permission = "tags.create", desc = "")
public class TagsCreateCommand implements CommandClass {

    @Inject private TagManager tagManager;
    @Inject private Injector injector;

    // TODO: add optional arguments to pass to the creation menu to set values automatically!
    @Command(names = "")
    public void tagsCreate(CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {

            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }
            Player player = (Player)sender;
            injector.getInstance(TagCreate.class).openGui(player, null);

        });
    }
}
