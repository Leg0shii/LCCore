package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.NightVisionCommand;
import de.legoshi.lccore.command.PexCommand;
import de.legoshi.lccore.command.PingtpCommand;
import de.legoshi.lccore.command.debug.GiveUnlocksCommand;
import de.legoshi.lccore.command.hide.Hide;
import de.legoshi.lccore.command.hide.HideAll;
import de.legoshi.lccore.command.hide.Show;
import de.legoshi.lccore.command.hide.ShowAll;
import de.legoshi.lccore.command.saves.CheckSavesCommand;
import de.legoshi.lccore.command.saves.ResetSavesCommand;
import de.legoshi.lccore.command.saves.SaveCommand;
import de.legoshi.lccore.command.saves.SavesCommand;
import de.legoshi.lccore.command.staff.LockdownCommand;
import de.legoshi.lccore.command.staff.SetLockdownMessageCommand;
import team.unnamed.inject.Injector;

// TODO: refactor old commands to use command flow
public class OldCommandManager {

    private final Linkcraft plugin;
    private final Injector injector;

    public OldCommandManager(Linkcraft plugin, Injector injector) {
        this.plugin = plugin;
        this.injector = injector;
    }

    public void registerCommands() {
        plugin.getCommand("checksaves").setExecutor(new CheckSavesCommand());
        plugin.getCommand("saves").setExecutor(new SavesCommand());
        plugin.getCommand("save").setExecutor(new SaveCommand(plugin));
        plugin.getCommand("resetsaves").setExecutor(new ResetSavesCommand(plugin));
        plugin.getCommand("pingtp").setExecutor(new PingtpCommand());
        plugin.getCommand("pex").setExecutor(new PexCommand());
        plugin.getCommand("hide").setExecutor(injector.getInstance(Hide.class));
        plugin.getCommand("hideall").setExecutor(injector.getInstance(HideAll.class));
        plugin.getCommand("show").setExecutor(injector.getInstance(Show.class));
        plugin.getCommand("showall").setExecutor(injector.getInstance(ShowAll.class));
        plugin.getCommand("ld").setExecutor(injector.getInstance(LockdownCommand.class));
        plugin.getCommand("ldmsg").setExecutor(injector.getInstance(SetLockdownMessageCommand.class));
        plugin.getCommand("nv").setExecutor(new NightVisionCommand());
        plugin.getCommand("giveunlocks").setExecutor(new GiveUnlocksCommand());
    }

}