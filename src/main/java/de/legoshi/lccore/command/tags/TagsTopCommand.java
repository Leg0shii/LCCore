package de.legoshi.lccore.command.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.TagManager;
import de.legoshi.lccore.tag.TagTopDTO;
import de.legoshi.lccore.tag.TagTopEntry;
import de.legoshi.lccore.tag.TagType;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.text.NumberFormat;
import java.util.Locale;

@Register
@Command(names = {"tagstop"}, permission = "tagstop", desc = "[type/page] [page]")
public class TagsTopCommand implements CommandClass {

    @Inject private TagManager tagManager;

    @Command(names = "")
    public void tagsTop(CommandSender sender,
                        @OptArg @ReflectiveTabComplete(clazz = TagManager.class, method = "getTagTypesAndAll") String pageOrType,
                        @OptArg String page) {
        Linkcraft.async(() -> {
            int pageNum = 1;
            TagType type = null;
            boolean allTags = false;
            Player p = null;

            if(sender instanceof Player) {
                p = (Player)sender;
            }

            if(pageOrType != null && (!isPageNumber(pageOrType) && !isValidType(pageOrType))) {
                MessageUtil.send(Message.TAGS_INVALID_TYPE, sender);
                return;
            }

            if(isValidType(pageOrType) && isPageNumber(page)) {
                pageNum = Integer.parseInt(page);
                if(pageOrType.equalsIgnoreCase("all")) {
                    allTags = true;
                } else {
                    type = TagType.valueOf(pageOrType.toUpperCase());
                }
            }
            else if(isPageNumber(pageOrType)) {
                pageNum = Integer.parseInt(pageOrType);
            }
            else if(isValidType(pageOrType)) {
                if(pageOrType.equalsIgnoreCase("all")) {
                    allTags = true;
                } else {
                    type = TagType.valueOf(pageOrType.toUpperCase());
                }
            }

            if(pageNum < 1) {
                MessageUtil.send(Message.TAGS_INVALID_PAGE, sender);
                return;
            }
            TagTopDTO lbData = tagManager.getTagTopData(type, pageNum - 1, 10, allTags);

            if(pageNum > lbData.getTotalPages()) {
                MessageUtil.send(Message.TAGS_INVALID_PAGE, sender);
                return;
            }

            NumberFormat nFormat = NumberFormat.getInstance(Locale.CANADA);
            String pageFormatted = nFormat.format(lbData.getCurrentPage());
            String totalPagesFormatted = nFormat.format(lbData.getTotalPages());
            String serverTotalFormatted = nFormat.format(lbData.getServerTagCount());
            String typeString = type == null ? "All" : type.name;
            typeString += " Tags";

            if(allTags) {
                typeString += " (Unobtainable Incl)";
            }

            String header = "§8--- " + "§d§l" + typeString + " §8-- §ePage §6" + pageFormatted + "§8/§6" + totalPagesFormatted + " §8---\n";
            StringBuilder result = new StringBuilder(header);

            if(pageNum == 1) {
                result.append("§dServer Total: §b").append(serverTotalFormatted).append("\n");
            }

            for(TagTopEntry entry : lbData.getEntries()) {
                String displayName = p != null && p.getName().equalsIgnoreCase(entry.getName()) ? "§e" + entry.getName() : "§7" + entry.getName();
                result.append("§a#").append(nFormat.format(entry.getPosition())).append(" §8» ").append(displayName).append("§7: ").append("§b").append(nFormat.format(entry.getTagCount())).append("\n");
            }

            sender.sendMessage(result.toString().trim());
        });
    }

    private boolean isPageNumber(String pageOrType) {
        if(pageOrType == null) {
            return false;
        }

        try {
            Integer.parseInt(pageOrType);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidType(String pageOrType) {
        if(pageOrType == null) {
            return false;
        }
        try {
            if(pageOrType.equalsIgnoreCase("all")) {
                return true;
            }

            TagType.valueOf(pageOrType.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
