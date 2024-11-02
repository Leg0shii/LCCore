package de.legoshi.lccore.listener;

import de.legoshi.lccore.achievements.Achievement;
import de.legoshi.lccore.manager.AchievementManager;
import de.legoshi.lccore.util.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import team.unnamed.inject.Inject;

public class AchievementListeners implements Listener {



    @Inject public AchievementManager achievementManager;

    public void playerSendMessage(Player player, String achievementString) {
        Achievement achievement = achievementManager.getAchievementById(achievementString);
        switch (achievement.getDifficulty()) {
            case EASY:
            player.sendMessage(Message.PREFIX.message + String.valueOf(ChatColor.GREEN) + ChatColor.BOLD
                    + "You completed the Achievement: " + ChatColor.DARK_GREEN + ChatColor.BOLD + achievement.getName());
            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1.0f, 1.0f);
            break;

        case MEDIUM:
            player.sendMessage(Message.PREFIX.message + String.valueOf(ChatColor.GOLD) + ChatColor.BOLD
                    + "You completed the Achievement: " + ChatColor.YELLOW + ChatColor.BOLD + achievement.getName());
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
            break;

        case HARD:
            player.sendMessage(Message.PREFIX.message + String.valueOf(ChatColor.RED) + ChatColor.BOLD
                    + "You completed the Achievement: " + ChatColor.DARK_RED + ChatColor.BOLD + achievement.getName());
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
            break;
        case EXTREME:
            player.sendMessage(Message.PREFIX.message + String.valueOf(ChatColor.DARK_PURPLE) + ChatColor.BOLD
                    + "You completed the Achievement: " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + achievement.getName());
            player.playSound(player.getLocation(), Sound.FIREWORK_TWINKLE2, 1.0f, 1.0f);
            break;
            case DIVINE:
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                player1.sendMessage(Message.PREFIX.message + String.valueOf(ChatColor.DARK_RED) + ChatColor.MAGIC + "k" + ChatColor.RESET + " " + ChatColor.RED + ChatColor.BOLD +
                        player.getName()+ ChatColor.DARK_RED + ChatColor.BOLD + " completed the Achievement: " + ChatColor.RED + ChatColor.BOLD + achievement.getName() + " " + ChatColor.DARK_RED + ChatColor.MAGIC + "k" );
            }
            player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1.0f, 1.0f);
            break;
        }
    }

    // executed at PartyManager.invite();
    public void createPartyAchievement(Player player) {
        String achievement = "Party101";
        if (!achievementManager.hasAchievement(player, achievement)) {
            achievementManager.giveAchievement(player, achievement);
            playerSendMessage(player, achievement);
        }
    }

    /*
     executed on killing
     @EventHandler(priority = EventPriority.LOWEST)
     public void killPlayerAchievement(EntityDeathEvent event) {
         if (!(event.getEntity() instanceof Player)) {
             return;
         }
         Player player = event.getEntity().getKiller();
         if (player != null) {
             String achievement = "Killer";
             achievementManager.giveAchievement(player, achievement);
             playerSendMessage(player, achievement);
         }
     }
    */

}

