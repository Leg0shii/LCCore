package de.legoshi.lccore.service;

import de.legoshi.lccore.command.flow.LinkcraftCommandPartModule;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.CommandManager;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilder;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilderImpl;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.builder.AnnotatedCommandBuilderImpl;
import me.fixeddev.commandflow.annotated.part.PartInjector;
import me.fixeddev.commandflow.annotated.part.SimplePartInjector;
import me.fixeddev.commandflow.annotated.part.defaults.DefaultsModule;
import me.fixeddev.commandflow.bukkit.factory.BukkitModule;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.reflections.Reflections;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommandService implements Service {

    @Inject private CommandManager commandManager;
    @Inject private Injector injector;

    private static final String[] commandsToUnregister =
                    {
                            "pl", "nick", "realname", "ac", "helpop",
                            "msg", "message", "r", "reply", "t", "tell",
                            "w", "whisper", "ignore", "togglechat", "p",
                            "tags", "mute", "unmute"
                    };

    @Override
    public void start() {
        unregister();
        register();
        MessageUtil.log(Message.SERVICE_STARTED, true, "command");
    }

    private void register() {
        PartInjector partInjector = new SimplePartInjector();
        partInjector.install(new BukkitModule());
        partInjector.install(new DefaultsModule());
        partInjector.install(new LinkcraftCommandPartModule(injector));
        AnnotatedCommandTreeBuilder treeBuilder = new AnnotatedCommandTreeBuilderImpl(
                new AnnotatedCommandBuilderImpl(partInjector),
                (clazz, parent) -> injector.getInstance(clazz)
        );

        Reflections reflections = new Reflections("de.legoshi.lccore.command");
        Set<Class<?>> commands = reflections.getTypesAnnotatedWith(Register.class);
        for(Class<?> command : commands) {
            commandManager.registerCommands(treeBuilder.fromClass((CommandClass)injector.getInstance(command)));
        }
    }

    private void unregister() {
        Map<String, Command> registered = getRegisteredCommandMap();
        for(String command : commandsToUnregister) {
            registered.remove(command);
        }
    }

    private Map<String, Command> getRegisteredCommandMap() {
        SimpleCommandMap map = getSimpleCommandMap();
        if(map != null) {
            try {
                Field knownCommandsField = map.getClass().getDeclaredField("knownCommands");
                knownCommandsField.setAccessible(true);
                return (Map<String, Command>)knownCommandsField.get(map);
            } catch (Exception ignored) {}
        }
        return new HashMap<>();
    }

    private SimpleCommandMap getSimpleCommandMap() {
        try {
            Field commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMap.setAccessible(true);
            return (SimpleCommandMap)commandMap.get(Bukkit.getServer());
        } catch (Exception ignored) {}
        return null;
    }
}
