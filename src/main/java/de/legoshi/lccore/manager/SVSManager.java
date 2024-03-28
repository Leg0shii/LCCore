package de.legoshi.lccore.manager;


import de.czymm.serversigns.ServerSignsPlugin;
import de.czymm.serversigns.parsing.CommandType;
import de.czymm.serversigns.parsing.command.ServerSignCommand;
import de.czymm.serversigns.signs.ServerSign;
import de.legoshi.lccore.util.svs.SignClickType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.HashMap;
import java.util.Map;

public class SVSManager {
    public void createServerSign(Location location, String cmd) {
        createServerSign(location, cmd, SignClickType.RIGHT);
    }

    public void createServerSign(Location location, String cmd, SignClickType clickType) {
        createServerSign(location, cmd, clickType, CommandType.SERVER_COMMAND);
    }

    public void createServerSign(Location location, String cmd, SignClickType clickType, CommandType commandType) {
        ServerSign old = getSVS().serverSignsManager.getServerSignByLocation(location);
        if(old != null) {
            getSVS().serverSignsManager.remove(old);
        }

        ServerSignCommand command = new ServerSignCommand(commandType, cmd);
        command.setInteractValue(clickType.value);

        ServerSign serverSign = new ServerSign(location, command);
        saveServerSign(serverSign);
    }

    private ServerSignsPlugin getSVS() {
        return (ServerSignsPlugin) Bukkit.getPluginManager().getPlugin("ServerSigns");
    }

    private void saveServerSign(ServerSign serverSign) {
        getSVS().serverSignsManager.save(serverSign);
    }


    private void setSignText(Block sign, HashMap<Integer, String> templateMap) {
        Sign signState = (Sign)sign.getState();
        for(Map.Entry<Integer, String> entry : templateMap.entrySet()) {
            signState.setLine(entry.getKey(), entry.getValue());
        }

        signState.update();
    }



}
