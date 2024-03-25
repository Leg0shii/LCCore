package de.legoshi.lccore.util;

import de.legoshi.lccore.Linkcraft;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public enum LCSound {
    DELETE(Sound.ITEM_BREAK),
    ERROR(Sound.VILLAGER_NO),
    SUCCESS(Sound.ORB_PICKUP),
    COMPLETE(Sound.LEVEL_UP);


    public final Sound sound;
    LCSound(Sound sound) {
        this.sound = sound;
    }

    public void play(Player player) {
        player.playSound(player.getEyeLocation(), this.sound, 1, 1);
    }

    // This is useful if the snapshot of the player object is outdated, for example 'holder' in the GUI, since their location may have changed
    public void playLater(Player player) {
        Linkcraft.sync(() -> {
            Player p = Bukkit.getPlayer(player.getName());
            if(p.isOnline()) {
                play(p);
            }
        });
    }
}
