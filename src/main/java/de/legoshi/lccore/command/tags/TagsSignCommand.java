package de.legoshi.lccore.command.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.database.models.Tag;
import de.legoshi.lccore.manager.SVSManager;
import de.legoshi.lccore.manager.TagManager;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Command(names = {"sign"}, permission = "tags.sign", desc = "<id>")
public class TagsSignCommand implements CommandClass {

    @Inject private TagManager tagManager;
    @Inject private SVSManager svsManager;

    @Command(names = "")
    public void sign(CommandSender sender, @ReflectiveTabComplete(clazz = TagManager.class, method = "getTagNames") String id) {
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

            Block block = player.getTargetBlock((Set<Material>) null, 5);
            Location bLoc = block.getLocation();
            Set<Material> commonMats = new HashSet<>(Arrays.asList(
                    Material.WALL_SIGN,
                    Material.SIGN_POST,
                    Material.STONE_BUTTON,
                    Material.WOOD_BUTTON,
                    Material.GOLD_PLATE,
                    Material.IRON_PLATE,
                    Material.WOOD_PLATE,
                    Material.STONE_PLATE
            ));

            if(!commonMats.contains(block.getType())) {
                MessageUtil.send(Message.TAGS_UNCOMMON_TAG_BLOCK, player, block.getType().name(), bLoc.getBlockX(), bLoc.getBlockY(), bLoc.getBlockZ());
            }

            svsManager.createServerSign(bLoc, "tags unlock " + tag.getId() + " <player>");
            MessageUtil.send(Message.TAGS_SIGN_CREATED, player, tag.getDisplay());
        });
    }
}
