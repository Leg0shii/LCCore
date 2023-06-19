package de.legoshi.lccore.util;

import de.legoshi.lccore.Linkcraft;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MusicHelper {

    public static void playMusic(final Player p) {
        BukkitRunnable music1 = new BukkitRunnable() {
            public void run() {
                p.playNote(p.getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.F));
                p.playNote(p.getLocation(), Instrument.STICKS, Note.natural(0, Note.Tone.B));
                p.playNote(p.getLocation(), Instrument.BASS_GUITAR, Note.sharp(1, Note.Tone.F));
            }
        };
        BukkitRunnable music2 = new BukkitRunnable() {
            public void run() {
                p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.A));
                p.playNote(p.getLocation(), Instrument.BASS_DRUM, Note.natural(0, Note.Tone.C));
                p.playNote(p.getLocation(), Instrument.BASS_GUITAR, Note.natural(1, Note.Tone.A));
            }
        };
        BukkitRunnable music3 = new BukkitRunnable() {
            public void run() {
                p.playNote(p.getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.C));
                p.playNote(p.getLocation(), Instrument.BASS_DRUM, Note.natural(0, Note.Tone.C));
                p.playNote(p.getLocation(), Instrument.STICKS, Note.natural(0, Note.Tone.B));
                p.playNote(p.getLocation(), Instrument.BASS_GUITAR, Note.sharp(1, Note.Tone.C));
            }
        };
        BukkitRunnable music4 = new BukkitRunnable() {
            public void run() {
                p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.D));
                p.playNote(p.getLocation(), Instrument.SNARE_DRUM, Note.natural(0, Note.Tone.C));
                p.playNote(p.getLocation(), Instrument.STICKS, Note.natural(0, Note.Tone.B));
                p.playNote(p.getLocation(), Instrument.BASS_GUITAR, Note.natural(1, Note.Tone.D));
            }
        };
        p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(0, Note.Tone.D));
        p.playNote(p.getLocation(), Instrument.BASS_DRUM, Note.natural(0, Note.Tone.C));
        p.playNote(p.getLocation(), Instrument.BASS_GUITAR, Note.natural(0, Note.Tone.D));
        int delay = 2;
        music1.runTaskLater(Linkcraft.getInstance(), delay);
        music2.runTaskLater(Linkcraft.getInstance(), (delay * 2));
        music3.runTaskLater(Linkcraft.getInstance(), (delay * 3));
        music4.runTaskLater(Linkcraft.getInstance(), (delay * 4));
    }

}
