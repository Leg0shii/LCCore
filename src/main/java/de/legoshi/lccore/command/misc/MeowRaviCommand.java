package de.legoshi.lccore.command.misc;

import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"meow"}, desc = "<player>")
public class MeowRaviCommand implements CommandClass {

    @Inject private ChatManager chatManager;

    private String[] messages = {
            ChatColor.WHITE + "" + ChatColor.BOLD + "You've got mail! " + "{0}" + ChatColor.WHITE + ChatColor.BOLD + " delivered a friendly " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "meow" + ChatColor.WHITE + ChatColor.BOLD + "! :3",
            ChatColor.WHITE + "" + ChatColor.BOLD + "Surprise! " + "{0}" + ChatColor.WHITE + ChatColor.BOLD + " whispered a charming " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "meow " + ChatColor.WHITE + ChatColor.BOLD + "in your ear. :3",
            "{0}" + ChatColor.WHITE + ChatColor.BOLD + " just couldn't resist sending you a virtual " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "meow " + ChatColor.WHITE + ChatColor.BOLD + "! :3",
            ChatColor.WHITE + "" + ChatColor.BOLD + "Guess what? " + "{0}" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " meowed " + ChatColor.WHITE + ChatColor.BOLD + "your way with a dash of adorableness! :3",
            "{0}" + ChatColor.WHITE + ChatColor.BOLD + " sent a sweet " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "meow " + ChatColor.WHITE + ChatColor.BOLD + "your way! :3",
            "{0}" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " meowed " + ChatColor.WHITE + ChatColor.BOLD + "at you! :3",
            "{0}" + ChatColor.WHITE + ChatColor.BOLD + " softly whispered a magical " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "meow " + ChatColor.WHITE + ChatColor.BOLD + "into your heart! :3",
            ChatColor.WHITE + "" + ChatColor.BOLD + "Surprise! " + "{0}" + ChatColor.WHITE + ChatColor.BOLD + " curled up and sent a cozy " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "meow " + ChatColor.WHITE + ChatColor.BOLD + "your way! :3",
            "{0}" + ChatColor.WHITE + ChatColor.BOLD + " gently sent you a warm " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "meow " + ChatColor.WHITE + ChatColor.BOLD + "! :3",
            ChatColor.WHITE + "" + ChatColor.BOLD + "You've got a " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "meow " + ChatColor.WHITE + ChatColor.BOLD + "delivery from " + "{0}" + ChatColor.WHITE + ChatColor.BOLD + "! Open with care! :3",
            ChatColor.WHITE + "" + ChatColor.BOLD + "Sending " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "meows " + ChatColor.WHITE + ChatColor.BOLD + "your way, courtesy of " + "{0}" + ChatColor.WHITE + ChatColor.BOLD + "! :3",
            ChatColor.WHITE + "" + ChatColor.BOLD + "From " + "{0}" + ChatColor.WHITE + ChatColor.BOLD + " with love: a gentle " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "meow " + ChatColor.WHITE + ChatColor.BOLD + "just for you! :3",
            ChatColor.WHITE + "" + ChatColor.BOLD + "A " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "meow " + ChatColor.WHITE + ChatColor.BOLD + "to remind you that you're pawsitively wonderful! Sent by " + "{0}" + ChatColor.WHITE + ChatColor.BOLD + "! :3",
            ChatColor.WHITE + "" + ChatColor.BOLD + "You've got mail! " + "{0}" + ChatColor.WHITE + ChatColor.BOLD + " carefully crafted a " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "meow " + ChatColor.WHITE + ChatColor.BOLD + "to brighten your day like a ray of sunshine! :3"
    };

    private String[] denied = {
            ChatColor.RED + "Yeah nice try, wannabe",
            ChatColor.RED + "Hold your horses, cowboy...",
            ChatColor.RED + "Not today man :/",
            ChatColor.RED + "Nope, not on the guest list",
            ChatColor.RED + "Ah ah ah! You didn't say the magic word!!!",
            ChatColor.RED + "Meow, meow, really? Someone's got their kitty ears on today it seems...",
            ChatColor.RED + "You've got to be kitten me right now",
            ChatColor.RED + "Would your parents be proud of you if they saw you doing this",
            ChatColor.RED + "Not on my watch bro",
            ChatColor.RED + "I don't think you should be here",
            ChatColor.RED + "Do you not have better things to do",
            ChatColor.RED + "Let's stick to human language maybe",
    };

    @Command(names = "")
    public void ravi(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPlayers", player = true) String name) {
        if (!(sender instanceof Player)) {
            MessageUtil.send(Message.NOT_A_PLAYER, sender);
            return;
        }

        if((!sender.getName().equalsIgnoreCase("raviolimood") && !sender.getName().equalsIgnoreCase("dkayee"))) {
            MessageUtil.send(denied[(int) (Math.random() * denied.length)], sender, false);
            return;
        }

        Player target = Bukkit.getPlayer(name);
        if (target == null) {
            MessageUtil.send(ChatColor.WHITE + "" + ChatColor.BOLD + name + " is either not online or isn't real :(", sender, false);
            return;
        }

        int random = (int) (Math.random() * messages.length);

        MessageUtil.send(MessageUtil.formatPlaceholder(messages[random], chatManager.rankNick((Player)sender)), target, false);
        MessageUtil.send(ChatColor.WHITE + "" + ChatColor.BOLD + "You successfully meowed at " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + target.getName() + ChatColor.WHITE + ChatColor.BOLD + "!", sender, false);
    }
}
