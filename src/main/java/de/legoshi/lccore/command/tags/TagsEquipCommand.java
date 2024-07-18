package de.legoshi.lccore.command.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.database.models.Tag;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.TagManager;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.util.CommandException;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Command(names = {"equip"}, permission = "tags.equip", desc = "<id> [player]")
public class TagsEquipCommand implements CommandClass {

    @Inject private TagManager tagManager;
    @Inject private PlayerManager playerManager;

    @Command(names = "")
    public void tagEquip(CommandSender sender, @ReflectiveTabComplete(clazz = TagManager.class, player = true, method = "getOwnedTagNames") String id,
                         @OptArg @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String name) {
        Linkcraft.async(() -> {
            boolean isSelf = name == null;
            String toEquip = "";
            String playerName = "";

            Tag tag = tagManager.getTag(id);

            if(tag == null) {
                MessageUtil.send(Message.TAGS_NO_TAG, sender, id);
                return;
            }

            if(isSelf) {
                if (!(sender instanceof Player)) {
                    MessageUtil.send(Message.NOT_A_PLAYER, sender);
                    return;
                }

                Player player = (Player)sender;

                if(!player.hasPermission("linkcraft.tags.all") && !tagManager.hasTag(player, tag.getId())) {
                    MessageUtil.send(Message.TAGS_HASNT_UNLOCKED_SELF, player, tag.getDisplay());
                    return;
                }

                toEquip = player.getUniqueId().toString();
                playerName = player.getName();


            } else {
                if(!MessageUtil.hasPerm(sender, "linkcraft.tags.equip.other")) {
                    return;
                }
                Player toRemove = playerManager.playerByName(name);
                PlayerRecord record = playerManager.getPlayerRecord(toRemove, name);

                if(record == null) {
                    MessageUtil.send(Message.NEVER_JOINED, sender, name);
                    return;
                }

                toEquip = record.getUuid();
                playerName = record.getName();

                if(!tagManager.hasTag(toEquip, tag.getId())) {
                    MessageUtil.send(Message.TAGS_HASNT_UNLOCKED_OTHER, sender, playerName, tag.getDisplay());
                    return;
                }
            }

            try {
                tagManager.setTag(toEquip, tag.getId());
                if(isSelf) {
                    MessageUtil.send(Message.TAGS_SELECT, sender, tag.getDisplay());
                } else {
                    MessageUtil.send(Message.TAGS_SELECT_OTHER, sender, playerName, tag.getDisplay());
                }
            } catch (CommandException e) {
                Message messageToSend = isSelf ? Message.TAGS_HAS_TAG_SET : Message.TAGS_HAS_TAG_SET_OTHER;
                MessageUtil.send(messageToSend, sender, playerName);
            }

        });
    }
}
