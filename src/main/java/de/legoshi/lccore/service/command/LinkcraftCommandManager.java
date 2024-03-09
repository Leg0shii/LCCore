package de.legoshi.lccore.service.command;

import de.legoshi.lccore.command.flow.ReflectiveTabCompleteModifierPart;
import de.legoshi.lccore.command.flow.TabCompleteModifierPart;
import me.fixeddev.commandflow.*;
import me.fixeddev.commandflow.command.Command;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.part.defaults.OptionalPart;
import me.fixeddev.commandflow.part.defaults.SequentialCommandPart;
import me.fixeddev.commandflow.part.defaults.SubCommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import me.fixeddev.commandflow.stack.SimpleArgumentStack;
import org.bukkit.command.CommandSender;

import java.util.*;

public class LinkcraftCommandManager extends SimpleCommandManager {
    public LinkcraftCommandManager() {
        super();
    }

    @Override
    public List<String> getSuggestions(Namespace accessor, List<String> arguments) {
        if (arguments != null && !arguments.isEmpty()) {
            Optional<Command> optionalCommand = this.getCommand((String)arguments.get(0));
            if (!optionalCommand.isPresent()) {
                return Collections.emptyList();
            } else {
                arguments.remove(0);
                Command command = (Command)optionalCommand.get();
                if (!this.getAuthorizer().isAuthorized(accessor, command.getPermission())) {
                    return Collections.emptyList();
                } else {
                    CommandContext commandContext = new SimpleCommandContext(accessor, arguments);
                    commandContext.setCommand(command, command.getName());
                    accessor.setObject(CommandManager.class, "commandManager", this);

                    CommandSender sender = accessor.getObject(CommandSender.class, "SENDER");

                    ArgumentStack stack = new SimpleArgumentStack(arguments);
                    CommandPart part = command.getPart();
                    List<String> suggestions;
                    if(part instanceof SequentialCommandPart) {
                        SequentialCommandPart seqPart = (SequentialCommandPart)part;
                        suggestions = getSequentialSuggestions(seqPart, commandContext, stack, sender);
                    } else {
                        suggestions = part.getSuggestions(commandContext, stack);
                    }

                    return suggestions == null ? Collections.emptyList() : suggestions;
                }
            }
        } else {
            return Collections.emptyList();
        }
    }

    public List<String> getSequentialSuggestions(SequentialCommandPart seqPart, CommandContext context, ArgumentStack stack, CommandSender player) {
        Iterator var3 = seqPart.getParts().iterator();

        List suggestions;
        do {
            if (!var3.hasNext()) {
                return Collections.emptyList();
            }

            CommandPart part = (CommandPart)var3.next();
            if(part instanceof SubCommandPart) {
                suggestions = getSubCommandSuggestions((SubCommandPart)part, context, stack, player);
            } else if(part instanceof ReflectiveTabCompleteModifierPart) {
                suggestions = ((ReflectiveTabCompleteModifierPart) part).getSuggestions(context, stack, player);
            } else if(part instanceof OptionalPart) {
                OptionalPart op = (OptionalPart)part;
                CommandPart subpart = op.getPart();
                if(subpart instanceof ReflectiveTabCompleteModifierPart) {
                    suggestions = ((ReflectiveTabCompleteModifierPart) subpart).getSuggestions(context, stack, player);
                } else {
                    suggestions = null;
                }
            }
            else {
                suggestions = part.getSuggestions(context, stack);
            }

        } while(suggestions == null || stack.hasNext());

        return suggestions;
    }

    public List<String> getSubCommandSuggestions(SubCommandPart subPart, CommandContext context, ArgumentStack stack, CommandSender player) {
        String next = stack.hasNext() ? stack.next() : null;
        if (next == null) {
            return Collections.emptyList();
        } else {

            Command command = (Command)subPart.getSubCommandMap().get(next);
            List<String> suggestions = new ArrayList();
            CommandManager manager = (CommandManager)context.getObject(CommandManager.class, "commandManager");
            Authorizer authorizer = manager.getAuthorizer();
            Map<Command, Boolean> testedCommands = new HashMap();
            subPart.getSubCommandMap().forEach((name, subCommand) -> {
                if (name.startsWith(next) && (Boolean)testedCommands.computeIfAbsent(subCommand, (c) -> {
                    return authorizer.isAuthorized(context, subCommand.getPermission());
                })) {
                    suggestions.add(name);
                }

            });

            if(stack.hasNext() && command != null) {
                CommandPart part = command.getPart();
                if(part instanceof SequentialCommandPart) {
                    return getSequentialSuggestions((SequentialCommandPart)part, context, stack, player);
                } else {
                    return command.getPart().getSuggestions(context, stack);
                }
            } else {
                return suggestions;
            }
        }
    }
}
