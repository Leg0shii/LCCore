package de.legoshi.lccore.command.flow;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReflectiveTabCompleteModifierPart implements CommandPart {
    private final CommandPart part;
    private final Method retriever;
    private final Class<?> commandClass;
    private final boolean hasPlayer;
    @Inject private Injector injector;

    public ReflectiveTabCompleteModifierPart(CommandPart part, Method retriever, Class<?> commandClass, Injector injector, boolean player) {
        this.part = part;
        this.retriever = retriever;
        this.commandClass = commandClass;
        this.injector = injector;
        this.hasPlayer = player;
    }

    public String getName() {
        return this.part.getName() + "-reflectivesuggestions";
    }

    public void parse(CommandContext context, ArgumentStack stack, @Nullable CommandPart caller) throws ArgumentParseException {
        this.part.parse(context, stack, caller);
    }

    public @Nullable List<String> getSuggestions(CommandContext commandContext, ArgumentStack stack, CommandSender sender) {

        Player tabCompleteSender = null;
        if(hasPlayer) {
            tabCompleteSender = (Player)sender;
        }

        if (!stack.hasNext()) {
            return Collections.emptyList();
        } else {
            List<String> suggestions = new ArrayList<>();
            List<String> potentialSuggestions = new ArrayList<>();

            String prefix = stack.next().toLowerCase();
            try {
                if(retriever != null && tabCompleteSender != null) {
                    potentialSuggestions = ((List<String>)retriever.invoke(injector.getInstance(commandClass), tabCompleteSender));
                } else if(retriever != null) {
                    potentialSuggestions = (List<String>)retriever.invoke(injector.getInstance(commandClass));
                }

            } catch (InvocationTargetException | IllegalAccessException ignored) {}
            for (String suggestion : potentialSuggestions) {
                if (suggestion.toLowerCase().startsWith(prefix)) {
                    suggestions.add(suggestion);
                }
            }

            return suggestions;
        }
    }
}
