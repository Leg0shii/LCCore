package de.legoshi.lcpractice.manager;

import de.legoshi.lcpractice.Linkcraft;
import de.legoshi.lcpractice.command.*;
import de.legoshi.lcpractice.command.practice.PracticeCommand;
import de.legoshi.lcpractice.command.practice.UnpracticeCommand;

// REFACTORED
public class CommandManager {

    private final Linkcraft plugin;

    public CommandManager(Linkcraft plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        plugin.getCommand("practice").setExecutor(new PracticeCommand(plugin));
        plugin.getCommand("unpractice").setExecutor(new UnpracticeCommand(plugin));
        plugin.getCommand("ggall").setExecutor(new AdminMemeCommand(plugin));
        plugin.getCommand("glall").setExecutor(new AdminMemeCommand(plugin));
        plugin.getCommand("ripall").setExecutor(new AdminMemeCommand(plugin));
        plugin.getCommand("failall").setExecutor(new AdminMemeCommand(plugin));
        plugin.getCommand("eggsall").setExecutor(new AdminMemeCommand(plugin));
        plugin.getCommand("hamall").setExecutor(new AdminMemeCommand(plugin));
        plugin.getCommand("forcesay").setExecutor(new AdminMemeCommand(plugin));
        plugin.getCommand("sayall").setExecutor(new AdminMemeCommand(plugin));
        plugin.getCommand("gg").setExecutor(new MemeCommand(plugin));
        plugin.getCommand("gl").setExecutor(new MemeCommand(plugin));
        plugin.getCommand("rip").setExecutor(new MemeCommand(plugin));
        plugin.getCommand("fail").setExecutor(new MemeCommand(plugin));
        plugin.getCommand("eggs").setExecutor(new MemeCommand(plugin));
        plugin.getCommand("ham").setExecutor(new MemeCommand(plugin));
        plugin.getCommand("chungus").setExecutor(new MemeCommand(plugin));
        plugin.getCommand("checksaves").setExecutor(new CheckSavesCommand());
        plugin.getCommand("chatcolor").setExecutor(new ChatColorCommand());
        plugin.getCommand("chatformat").setExecutor(new ChatFormatCommand());
        plugin.getCommand("togglenotify").setExecutor(new ToggleNotifyCommand());
        plugin.getCommand("stats").setExecutor(new StatsCommand());
        plugin.getCommand("saves").setExecutor(new SavesCommand());
        plugin.getCommand("save").setExecutor(new SaveCommand(plugin));
        plugin.getCommand("resetsaves").setExecutor(new ResetSavesCommand(plugin));
        plugin.getCommand("psreload").setExecutor(new ReloadCommand(plugin));
        plugin.getCommand("pingtp").setExecutor(new PingtpCommand());
        plugin.getCommand("pex").setExecutor(new PexCommand());
    }

}