package de.legoshi.lccore.command;

import com.darkender.SecondPrefix.SecondPrefix;
import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.Utils;
import me.clip.deluxetags.DeluxeTag;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MemeCommand implements CommandExecutor {

    private final Linkcraft plugin;

    public MemeCommand(Linkcraft plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String eggsmessage = this.plugin.getConfig().getString("eggsmessage");
        String failmessage = this.plugin.getConfig().getString("failmessage");
        String ggmessage = this.plugin.getConfig().getString("ggmessage");
        String glmessage = this.plugin.getConfig().getString("glmessage");
        String hammessage = this.plugin.getConfig().getString("hammessage");
        String ripmessage = this.plugin.getConfig().getString("ripmessage");
        if (cmd.getName().equalsIgnoreCase("eggs")) {
            if (!(sender instanceof Player)) return true;

            if (args.length == 0) {
                Player p = (Player) sender;
                Bukkit.broadcastMessage(Utils.chat(DeluxeTag.getPlayerDisplayTag(p) + " " + SecondPrefix.getPrefix(p) + p.getDisplayName() + " &7" + eggsmessage));
                return true;
            }

            if (args.length == 1) {
                if (!sender.hasPermission("lc.others.eggs")) {
                    sender.sendMessage(Utils.chat("&cInsufficient permissions"));
                    return true;
                }

                Player p = Bukkit.getServer().getPlayer(args[0]);
                if (p == null) {
                    sender.sendMessage(Utils.chat("&cNo player online with that name"));
                    return true;
                }

                Bukkit.broadcastMessage(Utils.chat(DeluxeTag.getPlayerDisplayTag(p) + " " + SecondPrefix.getPrefix(p) + p.getDisplayName() + " &7" + eggsmessage));
                return true;
            }

            if (!sender.hasPermission("lc.others.eggs")) {
                sender.sendMessage(Utils.chat("&cInsufficient permissions"));
                return true;
            }

            sender.sendMessage(Utils.chat("&cInvalid syntax: Please list &4&lone &r&cplayer."));
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("fail")) {
            if (!(sender instanceof Player)) return true;
            if (args.length == 0) {
                Player p = (Player) sender;
                Bukkit.broadcastMessage(Utils.chat(DeluxeTag.getPlayerDisplayTag(p) + " " + SecondPrefix.getPrefix(p) + p.getDisplayName() + " &7" + failmessage));
                return true;
            }
            if (args.length == 1) {
                if (!sender.hasPermission("lc.others.fail")) {
                    sender.sendMessage(Utils.chat("&cInsufficient permissions"));
                    return true;
                }
                Player p = Bukkit.getServer().getPlayer(args[0]);
                if (p == null) {
                    sender.sendMessage(Utils.chat("&cNo player online with that name"));
                    return true;
                }
                Bukkit.broadcastMessage(Utils.chat(DeluxeTag.getPlayerDisplayTag(p) + " " + SecondPrefix.getPrefix(p) + p.getDisplayName() + " &7" + failmessage));
                return true;
            }
            if (!sender.hasPermission("lc.others.fail")) {
                sender.sendMessage(Utils.chat("&cInsufficient permissions"));
                return true;
            }
            sender.sendMessage(Utils.chat("&cInvalid syntax: Please list &4&lone &r&cplayer."));
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("gg")) {
            if (!(sender instanceof Player)) return true;
            if (args.length == 0) {
                Player p = (Player) sender;
                Bukkit.broadcastMessage(Utils.chat(DeluxeTag.getPlayerDisplayTag(p) + " " + SecondPrefix.getPrefix(p) + p.getDisplayName() + " &7" + ggmessage + " &8"));
                return true;
            } if (args.length == 1) {
                if (!sender.hasPermission("lc.others.gg")) {
                    sender.sendMessage(Utils.chat("&cInsufficient permissions"));
                    return true;
                }
                Player p = Bukkit.getServer().getPlayer(args[0]);
                if (p == null) {
                    sender.sendMessage(Utils.chat("&cNo player online with that name"));
                    return true;
                }
                Bukkit.broadcastMessage(Utils.chat(DeluxeTag.getPlayerDisplayTag(p) + " " + SecondPrefix.getPrefix(p) + p.getDisplayName() + " &7" + ggmessage + " &8"));
                return true;
            } if (!sender.hasPermission("lc.others.gg")) {
                sender.sendMessage(Utils.chat("&cInsufficient permissions"));
                return true;
            }
            sender.sendMessage(Utils.chat("&cInvalid syntax: Please list &4&lone &r&cplayer."));
            return true;
        } if (cmd.getName().equalsIgnoreCase("gl")) {
            if (!(sender instanceof Player)) return true;
            if (args.length == 0) {
                Player p = (Player) sender;
                Bukkit.broadcastMessage(Utils.chat(DeluxeTag.getPlayerDisplayTag(p) + " " + SecondPrefix.getPrefix(p) + p.getDisplayName() + " &7" + glmessage + " &e"));
                return true;
            } if (args.length == 1) {
                if (!sender.hasPermission("lc.others.gl")) {
                    sender.sendMessage(Utils.chat("&cInsufficient permissions"));
                    return true;
                }
                Player p = Bukkit.getServer().getPlayer(args[0]);
                if (p == null) {
                    sender.sendMessage(Utils.chat("&cNo player online with that name"));
                    return true;
                }
                Bukkit.broadcastMessage(Utils.chat(DeluxeTag.getPlayerDisplayTag(p) + " " + SecondPrefix.getPrefix(p) + p.getDisplayName() + " &7" + glmessage + " &e"));
                return true;
            } if (!sender.hasPermission("lc.others.gl")) {
                sender.sendMessage(Utils.chat("&cInsufficient permissions"));
                return true;
            }
            sender.sendMessage(Utils.chat("&cInvalid syntax: Please list &4&lone &r&cplayer."));
            return true;
        } if (cmd.getName().equalsIgnoreCase("ham")) {
            if (!(sender instanceof Player))
                return true;
            if (args.length == 0) {
                Player p = (Player) sender;
                Bukkit.broadcastMessage(Utils.chat(DeluxeTag.getPlayerDisplayTag(p) + " " + SecondPrefix.getPrefix(p) + p.getDisplayName() + " &7" + hammessage));
                return true;
            }
            if (args.length == 1) {
                if (!sender.hasPermission("lc.others.ham")) {
                    sender.sendMessage(Utils.chat("&cInsufficient permissions"));
                    return true;
                }
                Player p = Bukkit.getServer().getPlayer(args[0]);
                if (p == null) {
                    sender.sendMessage(Utils.chat("&cNo player online with that name"));
                    return true;
                }
                Bukkit.broadcastMessage(Utils.chat(DeluxeTag.getPlayerDisplayTag(p) + " " + SecondPrefix.getPrefix(p) + p.getDisplayName() + " &7" + hammessage));
                return true;
            }
            if (!sender.hasPermission("lc.others.ham")) {
                sender.sendMessage(Utils.chat("&cInsufficient permissions"));
                return true;
            }
            sender.sendMessage(Utils.chat("&cInvalid syntax: Please list &4&lone &r&cplayer."));
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("rip")) {
            if (!(sender instanceof Player))
                return true;
            if (args.length == 0) {
                Player p = (Player) sender;
                Bukkit.broadcastMessage(Utils.chat(DeluxeTag.getPlayerDisplayTag(p) + " " + SecondPrefix.getPrefix(p) + p.getDisplayName() + " &7" + ripmessage + " &8"));
                return true;
            } if (args.length == 1) {
                if (!sender.hasPermission("lc.others.rip")) {
                    sender.sendMessage(Utils.chat("&cInsufficient permissions"));
                    return true;
                }
                Player p = Bukkit.getServer().getPlayer(args[0]);
                if (p == null) {
                    sender.sendMessage(Utils.chat("&cNo player online with that name"));
                    return true;
                }
                Bukkit.broadcastMessage(Utils.chat(DeluxeTag.getPlayerDisplayTag(p) + " " + SecondPrefix.getPrefix(p) + p.getDisplayName() + " &7" + ripmessage + " &8"));
                return true;
            } if (!sender.hasPermission("lc.others.rip")) {
                sender.sendMessage(Utils.chat("&cInsufficient permissions"));
                return true;
            }
            sender.sendMessage(Utils.chat("&cInvalid syntax: Please list &4&lone &r&cplayer."));
            return true;
        } return true;
    }
}