package de.legoshi.lccore.command.flow.annotated.part;

import de.legoshi.lccore.command.flow.TabCompleteModifierPart;
import de.legoshi.lccore.command.flow.annotated.annotation.TabComplete;
import me.fixeddev.commandflow.annotated.part.PartModifier;
import me.fixeddev.commandflow.part.CommandPart;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public class TabCompleteModifier implements PartModifier {
    public TabCompleteModifier() {

    }

    @Override
    public CommandPart modify(CommandPart original, List<? extends Annotation> modifiers) {
        TabComplete tabComplete = this.getModifier(modifiers, TabComplete.class);
        return new TabCompleteModifierPart(original, Arrays.asList(tabComplete.suggestions()));
    }
}
