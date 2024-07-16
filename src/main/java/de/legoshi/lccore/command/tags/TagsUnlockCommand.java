package de.legoshi.lccore.command.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.LCPlayerDB;
import de.legoshi.lccore.database.models.PlayerTag;
import de.legoshi.lccore.database.models.Tag;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.TagManager;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.tag.TagType;
import de.legoshi.lccore.util.LCSound;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Command(names = {"unlock"}, permission = "tags.unlock", desc = "<id> <player>")
public class TagsUnlockCommand implements CommandClass {

    @Inject private PlayerManager playerManager;
    @Inject private TagManager tagManager;
    @Inject private DBManager db;

    @Command(names = "")
    public void unlock(CommandSender sender, @ReflectiveTabComplete(clazz = TagManager.class, method = "getTagNames") String id,
                       @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String name, @OptArg Boolean pro) {
        Player toReceive = playerManager.playerByName(name);
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            PlayerRecord record = playerManager.getPlayerRecord(toReceive, name);

            if(record == null) {
                MessageUtil.send(Message.NEVER_JOINED, sender, name);
                return;
            }

            String uuid = record.getUuid();
            String playerName = record.getName();




            Tag tag = tagManager.getTag(id);
            if(tag == null) {
                MessageUtil.send(Message.TAGS_NO_TAG, sender, id);
                return;
            }


            boolean isVictor = tag.getType().equals(TagType.VICTOR);

            if(tagManager.hasTag(uuid, tag.getId())) {
                if(toReceive != null) {
                    MessageUtil.send(Message.TAGS_ALREADY_HAVE, toReceive, tag.getDisplay());
                }
                MessageUtil.send(Message.TAGS_ALREADY_HAVE_OTHER, sender, playerName, tag.getDisplay(), tag.getId());
                return;
            }

            if(pro != null && pro && toReceive != null) {
                if(toReceive.hasPermission("linkcraft.checkpoint")) {
                    MessageUtil.send(Message.TAGS_MUST_BE_PRO_MODE, toReceive, tag.getDisplay());
                    return;
                }
            }

            LCPlayerDB lcPlayerDB = playerManager.getPlayerDB(uuid);
            db.persist(new PlayerTag(tag, lcPlayerDB), lcPlayerDB, tag);
            MessageUtil.send(Message.TAGS_GAVE_TAG, sender, playerName, tag.getDisplay(), tag.getId());
            if(toReceive != null) {
                MessageUtil.send(Message.TAGS_UNLOCKED_TAG, toReceive, tag.getDisplay());
            }

            if(toReceive != null && !isVictor) {
                switch (tag.getRarity()) {
                    case COMMON:
                        LCSound.COMMON.playLater(toReceive);
                        break;
                    case UNCOMMON:
                        LCSound.UNCOMMON.playLater(toReceive);
                        break;
                    case RARE:
                        LCSound.RARE.playLater(toReceive);
                        break;
                    case EPIC:
                        LCSound.EPIC.playLater(toReceive);
                        break;
                    case LEGENDARY:
                        LCSound.LEGENDARY.playLater(toReceive);
                        break;
                }
            }
        });
    }
}
