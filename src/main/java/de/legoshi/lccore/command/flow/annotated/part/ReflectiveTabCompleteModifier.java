package de.legoshi.lccore.command.flow.annotated.part;


import de.legoshi.lccore.command.flow.ReflectiveTabCompleteModifierPart;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import me.fixeddev.commandflow.annotated.part.PartModifier;
import me.fixeddev.commandflow.part.CommandPart;
import org.bukkit.entity.Player;
import team.unnamed.inject.Injector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class ReflectiveTabCompleteModifier implements PartModifier {
    private final Injector injector;
    public ReflectiveTabCompleteModifier(Injector injector) {
        this.injector = injector;
    }

    @Override
    public CommandPart modify(CommandPart original, List<? extends Annotation> modifiers) {
        ReflectiveTabComplete reflectiveTabComplete = this.getModifier(modifiers, ReflectiveTabComplete.class);
        Method method = null;
        try {
            if(reflectiveTabComplete.player()) {
                method = reflectiveTabComplete.clazz().getMethod(reflectiveTabComplete.method(), Player.class);
            } else {
                method = reflectiveTabComplete.clazz().getMethod(reflectiveTabComplete.method());
            }
        } catch (NoSuchMethodException ignored) {}
        return new ReflectiveTabCompleteModifierPart(original, method, reflectiveTabComplete.clazz(), injector, reflectiveTabComplete.player());
    }
}
