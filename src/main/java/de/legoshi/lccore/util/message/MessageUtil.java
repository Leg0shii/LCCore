package de.legoshi.lccore.util.message;

import de.legoshi.lccore.manager.ConfigManager;
import de.legoshi.lccore.util.CommonUtil;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.logging.Level;

public interface MessageUtil {

    static String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    static String compose(Object message, boolean prefix, Object... objects) {
        TextComponent.Builder builder = newBuilder(prefix);
        String send = processMessage(message);
        builder.append(objects.length > 0 ? formatPlaceholder(send, objects) : send);
        String m = toLegacy(builder.build());
        for (int i = objects.length; i < 10; i++) m = m.replace("{"+i+"} ", "").replace("{"+i+"}", "");
        return colorize(m);
    }

    static void log(Object message, Level level, boolean prefix, Object... objects) {
        TextComponent.Builder builder = newBuilder(prefix);
        String send = processMessage(message);
        builder.append(objects.length > 0 ? formatPlaceholder(send, objects) : send);
        String m = toLegacy(builder.build());
        for (int i = objects.length; i < 10; i++) m = m.replace("{"+i+"} ", "").replace("{"+i+"}", "");
        Bukkit.getLogger().log(level, ChatColor.stripColor(colorize(m)));
    }

    static void log(Object message, boolean prefix, Object... objects) {
        log(message, Level.INFO, true, objects);
    }

    static void log(Object message, boolean prefix) {
        log(message, Level.INFO, prefix);
    }

    static void send(Object message, CommandSender sender, Object... objects) {
        sender.sendMessage(compose(message, true, objects));
    }

    static void sendIfPlayer(Object message, CommandSender sender, Object... objects) {
        if(sender instanceof Player) {
            send(message, sender, objects);
        }
    }

    static void send(Object message, CommandSender sender, boolean prefix) {
        sender.sendMessage(compose(message, prefix));
    }

    // TODO: Move this to different util class
    static boolean hasPerm(CommandSender sender, String permission) {
        boolean result = permission.isEmpty() || sender.hasPermission(permission);

        if(!result)
            send(Message.NO_PERMISSION, sender);

        return result;
    }

    static void broadcast(Object message, boolean prefix, Object... objects) {
        broadcast(Bukkit.getOnlinePlayers(), message, prefix, objects);
    }

    static void broadcast(Collection<? extends Player> players, Object message, boolean prefix, Object... objects) {
        String result = compose(message, prefix, objects);
        players.forEach((p) -> p.sendMessage(result));
    }

    static void broadcastExcept(Collection<? extends Player> players, Player toRemove, Object message, boolean prefix, Object... objects) {
        broadcast(CommonUtil.removeOne(players, toRemove), message, prefix, objects);
    }

    static void broadcastExcept(Player toRemove, Object message, boolean prefix, Object... objects) {
        broadcast(CommonUtil.removeOne(Bukkit.getOnlinePlayers(), toRemove), message, prefix, objects);
    }

    static void broadcast(Object message) {
        String result = processMessage(message);
        Bukkit.getOnlinePlayers().forEach((p) -> p.sendMessage(result));
    }

    static String getMessageTranslated(Message message) {
        String result = ConfigManager.messages.get(message);

        if(result == null) {
            result = message.getMessage();
        }

        return result;
    }

    static String toLegacy(Component textComponent) {
        return LegacyComponentSerializer.INSTANCE.serialize(textComponent);
    }

    static TextComponent.Builder newBuilder(boolean prefix) {
        TextComponent.Builder builder = TextComponent.builder();
        if (prefix) {
            builder.append(processPrefix());
        }
        return builder;
    }

    static String processPrefix() {
        return getMessageTranslated(Message.PREFIX);
    }



    static String processMessage(Object send) {
        if (send == null) {
            return MessageUtil.getMessageTranslated(Message.CRITICAL_ERROR);
        }

        if (send instanceof Component) {
            return toLegacy((Component) send);
        }

        String message;
        if (send instanceof String) {
            message = (String) send;
        } else if (send instanceof Message) {
            message = getMessageTranslated((Message) send);
        } else {
            message = String.valueOf(send);
        }

        return message;
    }

    static String formatPlaceholder(String message, Object... objects) {
        return format(message, "{%s}", objects);
    }

    static String format(String message, String format, Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            String placeholder = String.format(format, i);
            String o = String.valueOf(objects[i]);
            message = message.replace(placeholder, o);
        }

        return message;
    }

    static String formatHelp(String message, String ... args) {
        return message += "\n §3>§7 " + compose(Message.COMMAND_SYNTAX, false, (Object[])args);
    }

    static net.md_5.bungee.api.chat.TextComponent createClickable(String msg, String hover, String action) {
        net.md_5.bungee.api.chat.TextComponent message = new net.md_5.bungee.api.chat.TextComponent(msg);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, action));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        return message;
    }

    static net.md_5.bungee.api.chat.TextComponent combineBaseAndComponent(String base, net.md_5.bungee.api.chat.TextComponent component) {
        net.md_5.bungee.api.chat.TextComponent baseComponent = new net.md_5.bungee.api.chat.TextComponent(base);
        baseComponent.addExtra(component);
        return baseComponent;
    }
}

