package de.legoshi.lccore.command.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.database.models.Tag;
import de.legoshi.lccore.manager.TagManager;
import de.legoshi.lccore.util.CommandException;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Command(names = {"equip"}, permission = "tags.equip", desc = "<id>")
public class TagsEquipCommand implements CommandClass {

    @Inject private TagManager tagManager;

    @Command(names = "")
    public void tagEquip(CommandSender sender, @ReflectiveTabComplete(clazz = TagManager.class, player = true, method = "getOwnedTagNames") String id) {
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


            if(!player.hasPermission("linkcraft.tags.all") && !tagManager.hasTag(player, tag.getId())) {
                MessageUtil.send(Message.TAGS_HASNT_UNLOCKED_SELF, player, tag.getDisplay());
                return;
            }

            try {
                tagManager.setTag(player, tag.getId());
                MessageUtil.send(Message.TAGS_SELECT, player, tag.getDisplay());
            } catch(CommandException e) {
                MessageUtil.send(Message.TAGS_HAS_TAG_SET, player);
            }

        });
    }
}
