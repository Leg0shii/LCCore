package de.legoshi.lcpractice.util;

import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

// PARTLY REFACTORED
public class TitleAPI {
  public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String message) {
    sendTitle(player, fadeIn, stay, fadeOut, message, null);
  }
  
  public static void sendSubtitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String message) {
    sendTitle(player, fadeIn, stay, fadeOut, null, message);
  }
  
  public static void sendFullTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
    sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
  }
  
  public static void sendPacket(Player player, Object packet) {
    try {
      Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
      Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
      playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, packet);
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public static Class<?> getNMSClass(String name) {
    String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    try {
      return Class.forName("net.minecraft.server." + version + "." + name);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    } 
  }
  
  public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
    try {
      if (title != null) {
        title = ChatColor.translateAlternateColorCodes('&', title);
        title = title.replaceAll("%player%", player.getDisplayName());
        Object e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
        Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
        Constructor<?> subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
        Object titlePacket = subtitleConstructor.newInstance(e, chatTitle, fadeIn, stay, fadeOut);
        sendPacket(player, titlePacket);
        e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
        chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
        subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));
        titlePacket = subtitleConstructor.newInstance(e, chatTitle);
        sendPacket(player, titlePacket);
      } 
      if (subtitle != null) {
        subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
        subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
        Object e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
        Object chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + title + "\"}");
        Constructor<?> subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
        Object subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
        sendPacket(player, subtitlePacket);
        e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
        chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + subtitle + "\"}");
        subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
        subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
        sendPacket(player, subtitlePacket);
      } 
    } catch (Exception ex) {
      ex.printStackTrace();
    } 
  }
  
  public static void clearTitle(Player player) {
    sendTitle(player, 0, 0, 0, "", "");
  }
  
  public static void sendActionBar(Player player, String message) {
    PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
    (((CraftPlayer)player).getHandle()).playerConnection.sendPacket(packet);
  }
}