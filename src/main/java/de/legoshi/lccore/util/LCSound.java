package de.legoshi.lccore.util;

import de.legoshi.lccore.Linkcraft;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public enum LCSound {
    DELETE(Sound.ITEM_BREAK, 1 ,1),
    ERROR(Sound.VILLAGER_NO, 1 , 1),
    SUCCESS(Sound.ORB_PICKUP, 1, 1),
    COMPLETE(Sound.LEVEL_UP, 1, 1),
    COMMON(Sound.SHOOT_ARROW, 1, 0.5F),
    UNCOMMON(Sound.ORB_PICKUP, 1, 1),
    RARE(Sound.LEVEL_UP, 1, 1),
    EPIC(Sound.WITHER_SPAWN, 1, 1),
    LEGENDARY(Sound.PORTAL_TRAVEL, 1, 1),

    DUMMY(null, 0, 0);


    public final Sound sound;
    public final float volume;
    public final float pitch;

    LCSound(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void play(Player player) {
        player.playSound(player.getEyeLocation(), this.sound, volume, pitch);
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
