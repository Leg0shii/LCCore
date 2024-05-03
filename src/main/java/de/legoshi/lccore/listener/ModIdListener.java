package de.legoshi.lccore.listener;

import de.legoshi.lccore.util.message.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModIdListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        // The response message is structured as follows:
        // message[0]: Message type. In the case of ModList message[0] == 2
        // message[1]: Number of mods the client has installed. Ex: message[1] == 12 (number of mods)
        // message[2...]: Array of mod information alternating between modId and mod version strings

        if(isFMLChannel(channel) && isModListMessage(message)) {
            List<String> playerModIds = new ArrayList<String>();
            // Optional mod count check
            int modCount = message[1];
            boolean skip = false;

            // Start at 2 since the first two indices are used for message type and number of mods
            for(int i = 2; i < message.length;) {
                // The first byte of a singular mod info contains the length of the information
                int modInfoLen = i + message[i] + 1;

                // Every other mod info segment contains version information for the mod. Since we only care about the modIds
                // We will skip every other mod info segment and update the position in the byte array.
                if(!skip) {
                    // Read the data past the initial byte (which contains the length of the mod info)
                    String data = new String(Arrays.copyOfRange(message, i + 1, modInfoLen));
                    playerModIds.add(data);

                    skip = true;
                } else {
                    skip = false;
                }
                // Update position to next mod
                i = modInfoLen;
            }

            // Do something with playerModIds/modCount... (i.e, kick player if list contains certain ids)
            // Note: You may want to switch the ArrayList to a HashSet, since I think a client cannot have 2 of the same
            // modIds, but I am not certain (HashSet is a more efficient data structure of course).

            printMods(player, playerModIds);
        }
    }

    private void printMods(Player player, List<String> playerModIds) {
        StringBuilder output = new StringBuilder();
        for(String mod : playerModIds) {
            output.append(mod).append(" ");
        }

        MessageUtil.log(player.getName() + "'s Forge Mods: " + output.toString().trim(), true);

        if(playerModIds.contains("bv")) {
            player.kickPlayer(ChatColor.RED + "Barrier mod is not permitted on LinkCraft. Please remove it before joining");
        }
    }

    private boolean isFMLChannel(String channel) {
        return channel.equals("FML|HS");
    }

    private boolean isModListMessage(byte[] message) {
        return message[0] == 2;
    }
}