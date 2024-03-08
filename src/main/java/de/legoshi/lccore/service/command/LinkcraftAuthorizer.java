package de.legoshi.lccore.service.command;

import lombok.NoArgsConstructor;
import me.fixeddev.commandflow.Authorizer;
import me.fixeddev.commandflow.Namespace;
import org.bukkit.command.CommandSender;

@NoArgsConstructor
public class LinkcraftAuthorizer implements Authorizer {
    public boolean isAuthorized(Namespace namespace, String permission) {
        if(permission.isEmpty()) {
            return true;
        } else {
            CommandSender sender = (CommandSender)namespace.getObject(CommandSender.class, "SENDER");
            return sender.hasPermission("linkcraft." + permission);
        }
    }
}
