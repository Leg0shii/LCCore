package de.legoshi.lccore.command.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.database.models.Tag;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.TagManager;
import de.legoshi.lccore.util.GUIUtil;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.Utils;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.stack.ArgumentStack;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"tagtp"}, permission = "tagtp", desc = "<player> <tag> x y z [message]")
public class TagTeleportCommand implements CommandClass {

    @Inject private PlayerManager playerManager;
    @Inject private TagManager tagManager;

    @Command(names = "")
    public void tagTp(CommandSender sender,
                      @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String name,
                      @ReflectiveTabComplete(clazz = TagManager.class, method = "getTagNames") String tagName,
                      double x, double y, double z,
                      ArgumentStack as) {
        Player toTp = playerManager.playerByName(name);
        Linkcraft.async(() -> {
            if(toTp == null) {
                MessageUtil.send(Message.IS_OFFLINE, sender, name);
                return;
            }

            Tag tag = tagManager.getTag(tagName);
            if(tag == null) {
                MessageUtil.send(Message.TAGS_NO_TAG, sender, tagName);
                return;
            }

            if(tagManager.hasTag(toTp, tagName)) {
                Linkcraft.sync(() -> toTp.teleport(new Location(toTp.getWorld(), x, y, z)));
            } else {
                String msg = Utils.joinArguments(as);
                if(!msg.isEmpty()) {
                    MessageUtil.send(GUIUtil.colorize(msg), toTp, false);
                }
            }
        });
    }
}
