package de.legoshi.lcpractice.command;

import com.darkender.SecondPrefix.SecondPrefix;
import de.legoshi.lcpractice.Linkcraft;
import de.legoshi.lcpractice.util.Utils;
import me.clip.deluxetags.DeluxeTag;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminMemeCommand implements CommandExecutor {

    private final Linkcraft plugin;

    public AdminMemeCommand(Linkcraft plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String eggsmessage = this.plugin.getConfig().getString("eggsmessage");
        String failmessage = this.plugin.getConfig().getString("failmessage");
        String ggmessage = this.plugin.getConfig().getString("ggmessage");
        String glmessage = this.plugin.getConfig().getString("glmessage");
        String hammessage = this.plugin.getConfig().getString("hammessage");
        String ripmessage = this.plugin.getConfig().getString("ripmessage");
        if (cmd.getName().equalsIgnoreCase("eggsall")) {
            if (!(sender instanceof Player)) return true;
            if (!sender.hasPermission("lc.admin")) {
                sender.sendMessage(Utils.chat("&cInsufficient permission"));
                return true;
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.hasPermission("sv.use")) {
                    Bukkit.broadcastMessage(Utils.chat(DeluxeTag.getPlayerDisplayTag(p) + " " + SecondPrefix.getPrefix(p) + p.getDisplayName() + " &7" + eggsmessage));
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("failall")) {
            if (!(sender instanceof Player)) return true;
            if (!sender.hasPermission("lc.admin")) {
                sender.sendMessage(Utils.chat("&cInsufficient permission"));
                return true;
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.hasPermission("sv.use")) {
                    Bukkit.broadcastMessage(Utils.chat(DeluxeTag.getPlayerDisplayTag(p) + " " + SecondPrefix.getPrefix(p) + p.getDisplayName() + " &7" + failmessage));
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("sayall")) {
            if (!(sender instanceof Player)) return true;
            if (!sender.hasPermission("lc.admin")) {
                sender.sendMessage(Utils.chat("&cInsufficient permission"));
                return true;
            }
            if (args.length == 0) {
                Player p = (Player) sender;
                p.sendMessage(Utils.chat("&cInvalid syntax!"));
            } else {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.hasPermission("sv.use")) {
                        String str = String.join(" ", (CharSequence[]) args);
                        p.chat(Utils.chat(str));
                    }
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("forcesay")) {
            if (args.length == 0 && !(sender instanceof Player)) {
                sender.sendMessage("Invalid Syntax");
                return true;
            }
            if (!sender.hasPermission("lc.admin")) {
                sender.sendMessage(Utils.chat("&cInsufficient Permission"));
            } else {
                if (args.length == 0) {
                    Player p = (Player) sender;
                    p.sendMessage(Utils.chat("&cInvalid Syntax"));
                    return true;
                }
                if (args.length == 1) {
                    Player p = (Player) sender;
                    p.sendMessage(Utils.chat("&cPlease include a message"));
                } else if (args.length > 1) {
                    Player p = Bukkit.getServer().getPlayer(args[0]);
                    if (p == null) {
                        sender.sendMessage(Utils.chat("&cPlayer not found!"));
                        return true;
                    }
                    String[] msg = String.join(" ", (CharSequence[]) args).split(" ", 2);
                    p.chat(Utils.chat(msg[1]));
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("ggall")) {
            if (!(sender instanceof Player)) return true;
            if (!sender.hasPermission("lc.admin")) {
                sender.sendMessage(Utils.chat("&cInsufficient permission"));
                return true;
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.hasPermission("sv.use")) {
                    Bukkit.broadcastMessage(Utils.chat(DeluxeTag.getPlayerDisplayTag(p) + " " + SecondPrefix.getPrefix(p) + p.getDisplayName() + " &7" + ggmessage + " &8"));
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("glall")) {
            if (!(sender instanceof Player)) return true;
            if (!sender.hasPermission("lc.admin")) {
                sender.sendMessage(Utils.chat("&cInsufficient permission"));
                return true;
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.hasPermission("sv.use")) {
                    Bukkit.broadcastMessage(Utils.chat(DeluxeTag.getPlayerDisplayTag(p) + " " + SecondPrefix.getPrefix(p) + p.getDisplayName() + " &7" + glmessage + " &e"));
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("hamall")) {
            if (!(sender instanceof Player)) return true;
            if (!sender.hasPermission("lc.admin")) {
                sender.sendMessage(Utils.chat("&cInsufficient permission"));
                return true;
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.hasPermission("sv.use")) {
                    Bukkit.broadcastMessage(Utils.chat(DeluxeTag.getPlayerDisplayTag(p) + " " + SecondPrefix.getPrefix(p) + p.getDisplayName() + " &7" + hammessage));
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("ripall")) {
            if (!(sender instanceof Player)) return true;
            if (!sender.hasPermission("lc.admins")) {
                sender.sendMessage(Utils.chat("&cInsufficient permission"));
                return true;
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.hasPermission("sv.use")) {
                    Bukkit.broadcastMessage(Utils.chat(DeluxeTag.getPlayerDisplayTag(p) + " " + SecondPrefix.getPrefix(p) + p.getDisplayName() + " &7" + ripmessage + " &8"));
                }
            }
        }
        return true;
    }
}