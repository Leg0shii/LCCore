package de.legoshi.lccore.command.debug;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.EssentialsManager;
import de.legoshi.lccore.manager.MapManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.menu.maps.LCMap;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.command.CommandSender;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"test8"}, permission = "test8", desc = "<map>")
public class Test8Command implements CommandClass {

    @Inject private MapManager mapManager;
    @Inject private EssentialsManager essentialsManager;


    @Command(names = "")
    public void test8(CommandSender sender,  @ReflectiveTabComplete(clazz = EssentialsManager.class, method = "getWarpsFor", player = true) String map) {
        Linkcraft.async(() -> {
            LCMap lcMap = mapManager.getMap(map);
            if(lcMap == null) {
                MessageUtil.send(Message.MAP_NOT_EXISTS, sender, map);
                return;
            }

            long res = mapManager.getMapCompletionCount(lcMap);
            MessageUtil.send("Total completion count of " + lcMap.getName() + " is " + res, sender);
        });
    }
}
