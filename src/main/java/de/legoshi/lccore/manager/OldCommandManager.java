package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.*;
import de.legoshi.lccore.command.checkpoint.*;
import de.legoshi.lccore.command.hide.Hide;
import de.legoshi.lccore.command.hide.HideAll;
import de.legoshi.lccore.command.hide.Show;
import de.legoshi.lccore.command.hide.ShowAll;
import de.legoshi.lccore.command.practice.PracticeCommand;
import de.legoshi.lccore.command.practice.UnpracticeCommand;
import team.unnamed.inject.Injector;

// REFACTORED
public class OldCommandManager {

    private final Linkcraft plugin;
    private final Injector injector;

    public OldCommandManager(Linkcraft plugin, Injector injector) {
        this.plugin = plugin;
        this.injector = injector;
    }

    public void registerCommands() {
        plugin.getCommand("practice").setExecutor(new PracticeCommand(plugin));
        plugin.getCommand("unpractice").setExecutor(new UnpracticeCommand(plugin));
        plugin.getCommand("checksaves").setExecutor(new CheckSavesCommand());
        plugin.getCommand("togglenotify").setExecutor(new ToggleNotifyCommand());
        plugin.getCommand("saves").setExecutor(new SavesCommand());
        plugin.getCommand("save").setExecutor(new SaveCommand(plugin));
        plugin.getCommand("resetsaves").setExecutor(new ResetSavesCommand(plugin));
        plugin.getCommand("pingtp").setExecutor(new PingtpCommand());
        plugin.getCommand("pex").setExecutor(new PexCommand());
        plugin.getCommand("hide").setExecutor(injector.getInstance(Hide.class));
        plugin.getCommand("hideall").setExecutor(injector.getInstance(HideAll.class));
        plugin.getCommand("show").setExecutor(injector.getInstance(Show.class));
        plugin.getCommand("showall").setExecutor(injector.getInstance(ShowAll.class));
        plugin.getCommand("checkpoint").setExecutor(new CheckpointCommand(plugin));
        plugin.getCommand("sendtomap").setExecutor(new SendToMapCommand(plugin.getPluginFolder().getAbsolutePath()));
        plugin.getCommand("sendtomapworld").setExecutor(new SendToMapWorldCommand(plugin.getPluginFolder().getAbsolutePath()));
        plugin.getCommand("checkpointdelete").setExecutor(new CheckpointDeleteCommand(plugin));
        plugin.getCommand("savecheckpoint").setExecutor(new SaveCheckpointCommand(plugin));
        plugin.getCommand("fakeup").setExecutor(new FakeupCommand());
        plugin.getCommand("ld").setExecutor(injector.getInstance(LockdownCommand.class));
        plugin.getCommand("ldmsg").setExecutor(injector.getInstance(SetLockdownMessageCommand.class));
        plugin.getCommand("nv").setExecutor(new NightVisionCommand());
        plugin.getCommand("otphere").setExecutor(new OfflineTpHereCommand());
        plugin.getCommand("otp").setExecutor(new OfflineTpCommand());
        plugin.getCommand("giveunlocks").setExecutor(new GiveUnlocksCommand());
    }

}