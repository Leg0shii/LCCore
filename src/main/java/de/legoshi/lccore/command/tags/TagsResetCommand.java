package de.legoshi.lccore.command.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.TagManager;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Command(names = {"reset"}, permission = "tags.search", desc = "<player>")
public class TagsResetCommand implements CommandClass {

    @Inject private PlayerManager playerManager;
    @Inject private TagManager tagManager;

    @Command(names = "")
    public void reset(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String name) {
        Player toReset = playerManager.playerByName(name);
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            PlayerRecord record = playerManager.getPlayerRecord(toReset, name);
            String uuid = record.getUuid();
            if(uuid == null) {
                MessageUtil.send(Message.NEVER_JOINED, sender, name);
                return;
            }

            int count = tagManager.removeAllTagsFrom(uuid);

            if(count > 0) {
                MessageUtil.send(Message.TAGS_REMOVED_TAGS, sender, count, record.getName());
            } else {
                MessageUtil.send(Message.TAGS_NO_TAGS, sender, record.getName());
            }

        });
    }
}
