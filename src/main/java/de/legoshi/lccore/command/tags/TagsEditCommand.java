package de.legoshi.lccore.command.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.database.models.Tag;
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

@Command(names = {"edit"}, permission = "tags.edit", desc = "<id>")
public class TagsEditCommand implements CommandClass {

    @Inject private TagManager tagManager;
    @Inject private Injector injector;

    @Command(names = "")
    public void tagsEdit(CommandSender sender, @ReflectiveTabComplete(clazz = TagManager.class, method = "getTagNames") String id) {
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }
            Player player = (Player)sender;
            Tag tag = tagManager.getTag(id);

            if(tag == null) {
                MessageUtil.send(Message.TAGS_NO_TAG, player, id);
                return;
            }

            injector.getInstance(TagCreate.class).openGui(player, null, tag);
        });
    }
}
