package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.LCPlayerDB;
import de.legoshi.lccore.database.models.Punishment;
import de.legoshi.lccore.player.PunishmentType;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.stream.Collectors;

public class PunishmentManager {

    @Inject private DBManager db;
    @Inject private PlayerManager playerManager;

    public List<Punishment> getCurrentPunishments(String player) {
        String hql = "SELECT p FROM Punishment p WHERE p.length > :now AND p.player=:player";
        EntityManager em = db.getEntityManager();
        TypedQuery<Punishment> query = db.getEntityManager().createQuery(hql, Punishment.class);
        query.setParameter("now", new Date());
        query.setParameter("player", new LCPlayerDB(player));
        List<Punishment> results = query.getResultList();
        em.close();

        return results == null ? new ArrayList<>() : results;
    }

    public Punishment addPunishment(String player, PunishmentType type, Date until, String reason) {
        List<Punishment> punishments = getCurrentPunishments(player);
        for(Punishment punish : punishments) {
            if(punish.getType().equals(type)) {
                return punish;
            }
        }

        Punishment punishment = new Punishment();
        LCPlayerDB lcPlayerDB = playerManager.getPlayerDB(player);
        punishment.setPlayer(lcPlayerDB);
        punishment.setLength(until);
        punishment.setType(type);
        punishment.setReason(reason);
        db.persist(punishment, lcPlayerDB);

        Linkcraft.sync(() -> {
            Player online = Bukkit.getPlayer(UUID.fromString(player));
            Linkcraft.async(() -> {
                if(online != null && online.isOnline()) {
                    playerManager.updatePlayer(online);
                }
            });
        });
        return null;
    }

    public boolean removePunishment(Player player, PunishmentType type) {
        return removePunishment(player.getUniqueId().toString(), type);
    }

    public boolean removePunishment(String player, PunishmentType type) {
        String hql = "DELETE FROM Punishment p WHERE p.player = :player AND p.type = :type";
        EntityManager em = db.getEntityManager();
        em.getTransaction().begin();
        Query query = em.createQuery(hql);
        query.setParameter("player", new LCPlayerDB(player));
        query.setParameter("type", type);
        int deletedCount = query.executeUpdate();
        em.getTransaction().commit();
        em.close();
        boolean removedPunishment = deletedCount > 0;

        if(removedPunishment) {
            Linkcraft.sync(() -> {
                Player online = Bukkit.getPlayer(UUID.fromString(player));
                Linkcraft.async(() -> {
                    if(online != null && online.isOnline()) {
                        playerManager.updatePlayer(online);
                    }
                });
            });
        }

        return removedPunishment;
    }

    public List<Punishment> getCurrentPunishments(Player player) {
        return getCurrentPunishments(player.getUniqueId().toString());
    }

    public boolean hasCurrentPunishmentOfType(String player, PunishmentType type) {
        List<Punishment> punishments = getCurrentPunishments(player);
        for(Punishment punishment : punishments) {
            if(punishment.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public void addPunishment(Player player, PunishmentType type, Date until, String reason) {
        addPunishment(player.getUniqueId().toString(), type, until, reason);
    }

    public boolean isPunished(Player player, PunishmentType type) {
        List<Punishment> punishments = playerManager.getPlayer(player).getPunishments().stream().filter(punishment -> punishment.getType().equals(type)).collect(Collectors.toList());
        if(punishments.isEmpty()) {
            return false;
        }

        if(punishments.size() > 1) {
            MessageUtil.log(Message.PUNISH_CRITICAL_ERROR, true, player.getName(), type.display);
        }

        Punishment punishment = punishments.get(0);
        if(punishment.getLength().before(new Date())) {
            MessageUtil.send(Message.PUNISH_FINISHED, player, type.display);
            playerManager.updatePlayer(player);
            return false;
        } else {
            return true;
        }
    }

    public Date getDateFromLength(String length) {
        if (length == null || length.isEmpty()) {
            return null;
        }

        int value;
        String unit;
        try {
            value = Integer.parseInt(length.replaceAll("[^\\d]", ""));
            unit = length.replaceAll("[\\d]", "").toLowerCase();
        } catch (NumberFormatException e) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        switch (unit) {
            case "s":
            case "sec":
            case "second":
                calendar.add(Calendar.SECOND, value);
                break;
            case "mi":
            case "min":
            case "minute":
                calendar.add(Calendar.MINUTE, value);
                break;
            case "h":
            case "hr":
            case "hour":
                calendar.add(Calendar.HOUR_OF_DAY, value);
                break;
            case "d":
            case "day":
                calendar.add(Calendar.DAY_OF_YEAR, value);
                break;
            case "w":
            case "week":
                calendar.add(Calendar.WEEK_OF_YEAR, value);
                break;
            case "m":
            case "mon":
            case "month":
                calendar.add(Calendar.MONTH, value);
                break;
            case "y":
            case "year":
                calendar.add(Calendar.YEAR, value);
                break;
            default:
                return null;
        }

        return calendar.getTime();
    }

}
