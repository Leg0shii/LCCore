package de.legoshi.lccore.command.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.Tag;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.TagManager;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.util.CommandException;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Command(names = {"remove"}, permission = "tags.remove", desc = "<id> <player>")
public class TagsRemoveCommand implements CommandClass {

    @Inject private PlayerManager playerManager;
    @Inject private TagManager tagManager;
    @Inject private DBManager db;

    @Command(names = "")
    public void remove(CommandSender sender, @ReflectiveTabComplete(clazz = TagManager.class, method = "getTagNames") String id,
                       @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String name) {
        Player toRemove = playerManager.playerByName(name);


        Linkcraft.async(() -> {
            PlayerRecord record = playerManager.getPlayerRecord(toRemove, name);
            String uuid = record.getUuid();
            if(uuid == null) {
                MessageUtil.send(Message.NEVER_JOINED, sender, name);
                return;
            }
            String playerName = record.getName();

            try {
                Tag tag = db.find(id, Tag.class);
                tagManager.removeTagFrom(uuid, id);
                if(tag != null) {
                    MessageUtil.send(Message.TAGS_REMOVE_TAG, sender, playerName, tag.getDisplay());
                }
            } catch (CommandException e) {
                MessageUtil.send(e.getMessage(), sender);
            }
        });
    }
}
