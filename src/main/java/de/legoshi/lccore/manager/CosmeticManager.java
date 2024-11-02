package de.legoshi.lccore.manager;

import de.legoshi.lccore.cosmetics.ColorCosmetic;
import de.legoshi.lccore.cosmetics.Cosmetic;
import de.legoshi.lccore.cosmetics.CosmeticType;
import de.legoshi.lccore.cosmetics.cosmetics.boots.*;
import de.legoshi.lccore.cosmetics.cosmetics.chestplate.LeatherChestplate;
import de.legoshi.lccore.cosmetics.cosmetics.chestplate.RainbowChestplate;
import de.legoshi.lccore.cosmetics.cosmetics.helmet.LeatherHelmet;
import de.legoshi.lccore.cosmetics.cosmetics.leggings.LeatherLeggings;
import de.legoshi.lccore.cosmetics.cosmetics.leggings.RainbowLeggings;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.Cosmetics;
import de.legoshi.lccore.database.models.PlayerCosmetic;
import de.legoshi.lccore.util.Dye;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.io.Console;
import java.util.*;

public class CosmeticManager {
    @Inject private PlayerCosmetic playerCosmetic;
    private static final HashMap<UUID, Set<String>> playerDataMap = new HashMap<>();

    @Inject private DBManager dbManager;

    public PlayerCosmetic findPlayerCosmeticById(Player player) {
        String playerId = player.getUniqueId().toString();
        EntityManager em = dbManager.getEntityManager();
        EntityTransaction et = em.getTransaction();
        PlayerCosmetic playerCosmetic = null;

        try {
            et.begin();
            playerCosmetic = em.find(PlayerCosmetic.class, playerId);
            et.commit();
        } catch (Exception e) {
            et.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        return playerCosmetic;
    }


    public void addPlayerCosmetic(Player player, String helmet, String chestplate, String leggings, String boots, String helmetColor, String chestplateColor, String leggingsColor, String bootsColor) {
        PlayerCosmetic playerCosmetic = new PlayerCosmetic(player.getUniqueId().toString(), helmet, chestplate, leggings, boots, helmetColor, chestplateColor, leggingsColor, bootsColor);
        dbManager.persist(playerCosmetic);
    }

    public void loadPlayerCosmetics(Player player) {
        if (!playerExists(player)) { addPlayerCosmetic(player, "None", "None", "None", "None", "None", "None", "None", "None"); }
        PlayerCosmetic cosmetics = findPlayerCosmeticById(player);
        assert cosmetics != null;

        Cosmetic helmet = getCosmeticFromString(cosmetics.getHelmet());
        Cosmetic chestplate = getCosmeticFromString(cosmetics.getChestplate());
        Cosmetic leggings = getCosmeticFromString(cosmetics.getLeggings());
        Cosmetic boots = getCosmeticFromString(cosmetics.getBoots());

        if (helmet != null) {
            if (helmet instanceof ColorCosmetic) {
                ((ColorCosmetic) helmet).equipArmor(player, getColor(player, CosmeticType.HELMET));
            } else { helmet.startAnimation(player); } }
        else { player.getInventory().setHelmet(null); }

        if (chestplate != null) {
            if (chestplate instanceof ColorCosmetic) {
                ((ColorCosmetic) chestplate).equipArmor(player, getColor(player, CosmeticType.CHESTPLATE));
            } else { chestplate.startAnimation(player); } }
        else { player.getInventory().setChestplate(null); }

        if (leggings != null) {
            if (leggings instanceof ColorCosmetic) {
                ((ColorCosmetic) leggings).equipArmor(player, getColor(player, CosmeticType.LEGGINGS));
            } else { leggings.startAnimation(player); }
        } else { player.getInventory().setLeggings(null); }

        if (boots != null) {
            if (boots instanceof ColorCosmetic) {
                ((ColorCosmetic) boots).equipArmor(player, getColor(player, CosmeticType.BOOTS));
            } else { boots.startAnimation(player); }
        } else { player.getInventory().setBoots(null); }

        playerDataMap.put(player.getUniqueId(), getPlayerDataEntries(player));
    }

    public Cosmetic getCosmeticFromString(String cosmeticString) {

        switch (cosmeticString) {
            case "LeatherBoots":
                return new LeatherBoots();
            case "RainbowBoots":
                return new RainbowBoots();
            case "LeatherLeggings":
                return new LeatherLeggings();
            case "RainbowLeggings":
                return new RainbowLeggings();
            case "LeatherChestplate":
                return new LeatherChestplate();
            case "RainbowChestplate":
                return new RainbowChestplate();
            case "LeatherHelmet":
                return new LeatherHelmet();
            default:
                return null;
        }
    }


    public void updatePlayerCosmetic(Player player, CosmeticType cosmeticType, String cosmetic) {
        PlayerCosmetic playerCosmetic = findPlayerCosmeticById(player);

        if (playerCosmetic != null) {

            switch (cosmeticType) {
                case HELMET:
                    playerCosmetic.setHelmet(cosmetic);
                    break;
                case CHESTPLATE:
                    playerCosmetic.setChestplate(cosmetic);
                    break;
                case LEGGINGS:
                    playerCosmetic.setLeggings(cosmetic);
                    break;
                case BOOTS:
                    playerCosmetic.setBoots(cosmetic);
                    break;
            }

            dbManager.update(playerCosmetic);
            loadPlayerCosmetics(player);
        } else {
            System.out.println("No cosmetics found for player with ID: " + player.getUniqueId().toString());
        }
    }

    public boolean playerExists(Player player) {
        String playerId = player.getUniqueId().toString();
        return dbManager.find(playerId, PlayerCosmetic.class) != null;
    }

    public String getCosmeticStringByType(Player player, CosmeticType cosmeticType) {
        switch (cosmeticType) {
            case HELMET: return findPlayerCosmeticById(player).getHelmet();
            case CHESTPLATE: return findPlayerCosmeticById(player).getChestplate();
            case LEGGINGS: return findPlayerCosmeticById(player).getLeggings();
            case BOOTS: return findPlayerCosmeticById(player).getBoots();
        }
        return null;
    }

    public Cosmetic getCosmeticByType(Player player, CosmeticType cosmeticType) {
        switch (cosmeticType) {
            case HELMET: return getCosmeticFromString(findPlayerCosmeticById(player).getHelmet());
            case CHESTPLATE: return getCosmeticFromString(findPlayerCosmeticById(player).getChestplate());
            case LEGGINGS: return getCosmeticFromString(findPlayerCosmeticById(player).getLeggings());
            case BOOTS: return getCosmeticFromString(findPlayerCosmeticById(player).getBoots());
        }
        return null;
    }

    public Set<String> getPlayerDataEntries(Player player) {
        UUID playerUUID = player.getUniqueId();
        String hql = "SELECT pd.dataEntry FROM Cosmetics pd WHERE pd.playerUUID = :playerUUID";
        EntityManager em = dbManager.getEntityManager();

        try {
            TypedQuery<String> query = em.createQuery(hql, String.class);
            query.setParameter("playerUUID", playerUUID.toString());

            // Get the result list
            List<String> resultList = query.getResultList();

            // Convert the List to a Set to ensure uniqueness
            return new HashSet<>(resultList);

        } finally {
            em.close(); // Ensure that EntityManager is closed in a finally block
        }
    }



    public HashMap<UUID, List<String>> getAllPlayerData() {
        String hql = "SELECT pd FROM Cosmetics pd";
        EntityManager em = dbManager.getEntityManager();

        TypedQuery<Cosmetics> query = em.createQuery(hql, Cosmetics.class);
        List<Cosmetics> allDataEntries = query.getResultList();
        em.close();

        HashMap<UUID, List<String>> playerDataMap = new HashMap<>();

        for (Cosmetics dataEntry : allDataEntries) {
            UUID playerUUID = UUID.fromString(dataEntry.getPlayerUUID());
            playerDataMap.computeIfAbsent(playerUUID, k -> new ArrayList<>()).add(dataEntry.getDataEntry());
        }

        return playerDataMap;
    }

    public void giveCosmetic(Player player, String dataEntry) {
        // Retrieve or create the player's set of entries
        UUID playerUUID = player.getUniqueId();
        Set<String> playerEntries = playerDataMap.computeIfAbsent(playerUUID, k -> new HashSet<>());

        // Only add the entry if it is unique
        if (playerEntries.add(dataEntry)) {
            saveDataEntryToDB(playerUUID, dataEntry);
        }
    }


    private void saveDataEntryToDB(UUID playerUUID, String dataEntry) {
        Cosmetics playerData = new Cosmetics(playerUUID.toString(), dataEntry);

        EntityManager em = dbManager.getEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();
            em.persist(playerData);
            et.commit();
        } catch (Exception e) {
            et.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public boolean hasCosmetic(Player player, String dataEntry) {
        UUID playerUUID = player.getUniqueId();

        return playerDataMap.containsKey(playerUUID) &&
                playerDataMap.get(playerUUID).stream().anyMatch(entry -> entry.equalsIgnoreCase(dataEntry));
    }


    public void setColor(Player player, CosmeticType cosmeticType, Color color) {
        setColor(player, cosmeticType, getStringFromColor(color));
    }

    public void setColor(Player player, CosmeticType cosmeticType, String color) {
        PlayerCosmetic cosmetic = findPlayerCosmeticById(player);

        switch (cosmeticType) {
            case HELMET: cosmetic.setHelmetColor(color);
            break;
            case CHESTPLATE: cosmetic.setChestplateColor(color);
            break;
            case LEGGINGS: cosmetic.setLeggingsColor(color);
            break;
            case BOOTS: cosmetic.setBootsColor(color);
            break;
        }
        dbManager.update(cosmetic);
        loadPlayerCosmetics(player);
    }

    public Color getColor(Player player, CosmeticType cosmeticType) {
        switch (cosmeticType) {
            case HELMET: return getColorFromString(findPlayerCosmeticById(player).getHelmetColor());
            case CHESTPLATE: return getColorFromString(findPlayerCosmeticById(player).getChestplateColor());
            case LEGGINGS: return getColorFromString(findPlayerCosmeticById(player).getLeggingsColor());
            case BOOTS: return getColorFromString(findPlayerCosmeticById(player).getBootsColor());
        }
        return null;
    }

    // for some reason Dye and Color shorts are switched??
    public String getColorStringFromDye(Dye dye) {
        switch (dye) {
            case WHITE: return "Black";
            case ORANGE: return "Red";
            case MAGENTA: return "Green";
            case LIGHT_BLUE: return "Brown";
            case YELLOW: return "Blue";
            case LIME: return "Purple";
            case PINK: return "Cyan";
            case GRAY: return "Light_Gray";
            case LIGHT_GRAY: return "Gray";
            case CYAN: return "Pink";
            case PURPLE: return "Lime";
            case BLUE: return "Yellow";
            case BROWN: return "Aqua";
            case GREEN: return "Magenta";
            case RED: return "Orange";
            case BLACK: return "White";
            default: return null;
        }
    }

    public Color getColorFromString(String color) {
        switch (color.toLowerCase()) {
            case "white": return Color.WHITE;
            case "light_gray": return Color.SILVER;
            case "gray": return Color.GRAY;
            case "black": return Color.BLACK;
            case "red": return Color.RED;
            case "dark_red": return Color.MAROON;
            case "yellow": return Color.YELLOW;
            case "olive": return Color.OLIVE;
            case "lime": return Color.LIME;
            case "green": return Color.GREEN;
            case "aqua": return Color.AQUA;
            case "cyan": return Color.TEAL;
            case "blue": return Color.BLUE;
            case "dark_blue": return Color.NAVY;
            case "purple": return Color.fromBGR(219, 13, 78);
            case "magenta": return Color.FUCHSIA;
            case "orange": return Color.ORANGE;
            case "pink": return Color.fromBGR(212, 169, 255);
            case "brown": return Color.fromBGR(19, 69, 139);
            default:
                return null;
        }
    }

    public String getStringFromDye(Dye dye) {
        return dye.toString().toLowerCase();
    }

    public String getStringFromColor(Color color) {
        if (color.equals(Color.WHITE)) return "white";
        if (color.equals(Color.SILVER)) return "light_gray";
        if (color.equals(Color.GRAY)) return "gray";
        if (color.equals(Color.BLACK)) return "black";
        if (color.equals(Color.RED)) return "red";
        if (color.equals(Color.MAROON)) return "dark_red";
        if (color.equals(Color.YELLOW)) return "yellow";
        if (color.equals(Color.OLIVE)) return "olive";
        if (color.equals(Color.LIME)) return "lime";
        if (color.equals(Color.GREEN)) return "green";
        if (color.equals(Color.AQUA)) return "aqua";
        if (color.equals(Color.TEAL)) return "cyan";
        if (color.equals(Color.BLUE)) return "blue";
        if (color.equals(Color.NAVY)) return "dark_blue";
        if (color.equals(Color.FUCHSIA)) return "purple";
        if (color.equals(Color.ORANGE)) return "orange";
        if (color.equals(Color.fromBGR(219, 13, 78))) return "magenta";  // Custom magenta
        if (color.equals(Color.fromBGR(212, 169, 255))) return "pink";    // Custom pink
        if (color.equals(Color.fromBGR(42, 42, 165))) return "brown";     // Custom brown

        return null;
    }

    public Dye getDyeFromString(String color) {
        switch (color.toLowerCase()) {
            case "white": return Dye.WHITE;
            case "orange": return Dye.ORANGE;
            case "magenta": return Dye.MAGENTA;
            case "light_blue": return Dye.LIGHT_BLUE;
            case "yellow": return Dye.YELLOW;
            case "lime": return Dye.LIME;
            case "pink": return Dye.PINK;
            case "gray": return Dye.GRAY;
            case "light_gray": return Dye.LIGHT_GRAY;
            case "cyan": return Dye.CYAN;
            case "purple": return Dye.PURPLE;
            case "blue": return Dye.BLUE;
            case "brown": return Dye.BROWN;
            case "green": return Dye.GREEN;
            case "red": return Dye.RED;
            case "black": return Dye.BLACK;
            default:
                return null; // Return null if the color string is not recognized
        }
    }

    public ChatColor getChatColorFromDye(Dye dye) {
        switch (dye) {
            case WHITE: return ChatColor.BLACK;
            case ORANGE: return ChatColor.RED;
            case MAGENTA: return ChatColor.DARK_GREEN;
            case LIGHT_BLUE:
            case RED:
                return ChatColor.GOLD;
            case YELLOW: return ChatColor.DARK_BLUE;
            case LIME: return ChatColor.DARK_PURPLE;
            case PINK: return ChatColor.DARK_AQUA;
            case GRAY: return ChatColor.GRAY;
            case LIGHT_GRAY: return ChatColor.DARK_GRAY;
            case CYAN:
            case GREEN:
                return ChatColor.LIGHT_PURPLE;
            case PURPLE: return ChatColor.GREEN;
            case BLUE: return ChatColor.YELLOW;
            case BROWN: return ChatColor.AQUA;
            case BLACK: return ChatColor.WHITE;
            default:
                return ChatColor.RESET; // Default to RESET if no match is found
        }
    }



}