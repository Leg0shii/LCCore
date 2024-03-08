package de.legoshi.lccore.command.flow;

import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.command.flow.annotated.annotation.TabComplete;
import de.legoshi.lccore.command.flow.annotated.part.ReflectiveTabCompleteModifier;
import de.legoshi.lccore.command.flow.annotated.part.TabCompleteModifier;
import me.fixeddev.commandflow.annotated.part.AbstractModule;
import team.unnamed.inject.Injector;

public class LinkcraftCommandPartModule extends AbstractModule {
    private final Injector injector;

    public LinkcraftCommandPartModule(Injector injector) {
        this.injector = injector;
    }

    @Override
    public void configure() {
        this.bindModifier(TabComplete.class, new TabCompleteModifier());
        this.bindModifier(ReflectiveTabComplete.class, new ReflectiveTabCompleteModifier(injector));
    }

}
