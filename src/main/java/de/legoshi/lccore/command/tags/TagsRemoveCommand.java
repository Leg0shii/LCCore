package de.legoshi.lccore.command.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.TagManager;
import de.legoshi.lccore.util.CommandException;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import team.unnamed.inject.Inject;

@Command(names = {"remove"}, permission = "tags.remove", desc = "<id> <player>")
public class TagsRemoveCommand implements CommandClass {

    @Inject private PlayerManager playerManager;
    @Inject private TagManager tagManager;

    @Command(names = "")
    public void remove(CommandSender sender, @ReflectiveTabComplete(clazz = TagManager.class, method = "getTagNames") String id,
                       @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String name) {
        OfflinePlayer toRemove = playerManager.playerByName(name);
        if(toRemove == null) {
            toRemove = Bukkit.getOfflinePlayer(name);
        }
        OfflinePlayer finalToRemove = toRemove;

        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {


            try {
                tagManager.removeTagFrom(finalToRemove.getUniqueId().toString(), id);
            } catch (CommandException e) {
                MessageUtil.send(e.getMessage(), sender);
            }
        });
    }
}
