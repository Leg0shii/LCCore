package de.legoshi.lcpractice.manager;

import de.legoshi.lcpractice.LCPractice;
import de.legoshi.lcpractice.command.PracticeCommand;
import de.legoshi.lcpractice.command.ReloadCommand;
import de.legoshi.lcpractice.command.TestCommand;
import de.legoshi.lcpractice.command.UnpracticeCommand;

public class CommandManager {
    private final LCPractice plugin;

    public CommandManager(LCPractice plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        plugin.getCommand("practice").setExecutor(new PracticeCommand(plugin));
        plugin.getCommand("unpractice").setExecutor(new UnpracticeCommand(plugin));
        plugin.getCommand("pkpracreload").setExecutor(new ReloadCommand(plugin));
        plugin.getCommand("test").setExecutor(new TestCommand());
    }
}