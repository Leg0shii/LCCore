package de.legoshi.lccore.command.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.LCPlayerDB;
import de.legoshi.lccore.database.models.PlayerTag;
import de.legoshi.lccore.database.models.Tag;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.TagManager;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.Arrays;

@Command(names = {"unlock"}, permission = "tags.unlock", desc = "<id> <player>")
public class TagsUnlockCommand implements CommandClass {

    @Inject private PlayerManager playerManager;
    @Inject private TagManager tagManager;
    @Inject private DBManager db;

    @Command(names = "")
    public void unlock(CommandSender sender, @ReflectiveTabComplete(clazz = TagManager.class, method = "getTagNames") String id,
                       @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String name) {
        Player toReceive = playerManager.playerByName(name);
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            if(toReceive == null) {
                MessageUtil.send(Message.IS_OFFLINE, sender, name);
                return;
            }

            Tag tag = tagManager.getTag(id);
            if(tag == null) {
                MessageUtil.send(Message.TAGS_NO_TAG, sender, id);
                return;
            }

            if(tagManager.hasTag(toReceive, tag.getId())) {
                MessageUtil.send(Message.TAGS_ALREADY_HAVE, toReceive, tag.getDisplay());
                MessageUtil.send(Message.TAGS_ALREADY_HAVE_OTHER, sender, toReceive.getName(), tag.getDisplay(), tag.getId());
                return;
            }

            LCPlayerDB lcPlayerDB = playerManager.getPlayerDB(toReceive);
            db.persist(new PlayerTag(tag, lcPlayerDB), lcPlayerDB, tag);
            MessageUtil.send(Message.TAGS_GAVE_TAG, sender, toReceive.getName(), tag.getDisplay(), tag.getId());
            MessageUtil.send(Message.TAGS_UNLOCKED_TAG, toReceive, tag.getDisplay());
        });
    }
}
