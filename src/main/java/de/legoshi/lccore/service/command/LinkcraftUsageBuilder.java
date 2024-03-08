package de.legoshi.lccore.service.command;

import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import lombok.RequiredArgsConstructor;
import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.command.Command;
import me.fixeddev.commandflow.usage.UsageBuilder;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;

@RequiredArgsConstructor
public class LinkcraftUsageBuilder implements UsageBuilder {
    @Override
    public Component getUsage(CommandContext commandContext) {
        // TODO: Change this logic as required
        Command command = commandContext.getCommand();
        StringBuilder name = new StringBuilder();

        for (int i = 0; i < commandContext.getExecutionPath().size() - 1; i++) {
            Command path = commandContext.getExecutionPath().get(i);
            name.append(path.getName()).append(" ");
        }

        name.append(command.getName());

        String desc = MessageUtil.toLegacy(command.getDescription());

        return TextComponent.of(MessageUtil.compose(Message.USAGE, true, name.toString(), desc));
    }
}
