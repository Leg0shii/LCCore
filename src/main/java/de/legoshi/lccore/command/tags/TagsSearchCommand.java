package de.legoshi.lccore.command.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.TagManager;
import de.legoshi.lccore.menu.tags.TagHolder;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

@Command(names = {"search"}, permission = "tags.search", desc = "<query>")
public class TagsSearchCommand implements CommandClass {

    @Inject private TagManager tagManager;
    @Inject private Injector injector;

    @Command(names = "")
    public void search(CommandSender sender, String search) {
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }
            Player player = (Player)sender;
            int total = tagManager.tagCountPlayer(player).values().stream().mapToInt(Integer::intValue).sum();

            injector.getInstance(TagHolder.class).openGui(player, null, null, total, tagManager.getTagMenuData(player), search);
        });
    }
}
