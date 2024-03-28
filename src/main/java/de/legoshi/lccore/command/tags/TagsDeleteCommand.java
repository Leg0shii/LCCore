package de.legoshi.lccore.command.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.database.models.Tag;
import de.legoshi.lccore.manager.TagManager;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Command(names = {"delete"}, permission = "tags.delete", desc = "<id>")
public class TagsDeleteCommand implements CommandClass {

    @Inject private TagManager tagManager;

    @Command(names = "")
    public void tagDelete(CommandSender sender, @ReflectiveTabComplete(clazz = TagManager.class, method = "getTagNames") String id) {
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            Tag tag = tagManager.getTag(id);

            if(tag == null) {
                MessageUtil.send(Message.TAGS_NO_TAG, sender, id);
                return;
            }

            tagManager.deleteTag(tag);
            MessageUtil.send(Message.TAGS_DELETE_TAG, sender, tag.getDisplay(), tag.getName(), tag.getId());
        });
    }
}
