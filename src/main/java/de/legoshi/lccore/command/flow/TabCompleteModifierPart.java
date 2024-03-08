package de.legoshi.lccore.command.flow;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabCompleteModifierPart implements CommandPart {
    private final CommandPart part;
    private final List<String> suggestions;

    public TabCompleteModifierPart(CommandPart part, List<String> suggestions) {
        this.part = part;
        this.suggestions = suggestions;
    }

    public String getName() {
        return this.part.getName() + "-suggestions";
    }

    public void parse(CommandContext context, ArgumentStack stack, @Nullable CommandPart caller) throws ArgumentParseException {
        this.part.parse(context, stack, caller);
    }

    public @Nullable List<String> getSuggestions(CommandContext commandContext, ArgumentStack stack) {
        if (!stack.hasNext()) {
            return Collections.emptyList();
        } else {
            List<String> suggestions = new ArrayList<>();
            String prefix = stack.next().toLowerCase();

            for (String suggestion : this.suggestions) {
                if (suggestion.toLowerCase().startsWith(prefix)) {
                    suggestions.add(suggestion);
                }
            }

            return suggestions;
        }
    }
}
