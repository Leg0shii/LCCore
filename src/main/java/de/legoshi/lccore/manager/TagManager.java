package de.legoshi.lccore.manager;

import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.LCPlayerDB;
import de.legoshi.lccore.database.models.PlayerPreferences;
import de.legoshi.lccore.database.models.PlayerTag;
import de.legoshi.lccore.database.models.Tag;
import de.legoshi.lccore.tag.*;
import de.legoshi.lccore.util.CommandException;
import de.legoshi.lccore.util.ItemUtil;
import de.legoshi.lccore.util.message.Message;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TagManager {

    @Inject private DBManager db;
    @Inject private PlayerManager playerManager;

    public boolean isValidRarity(String rarity) {
        return getRarity(rarity) != null;
    }

    public TagRarity getRarity(String rarity) {
        try {
            return TagRarity.valueOf(rarity.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean tagExists(String tagName) {
        return tagByName(tagName) != null;
    }

    public Tag getTag(String tagNameOrId) {
        Tag tag = tagById(tagNameOrId);

        if(tag == null) {
            tag = tagByName(tagNameOrId);
        }

        return tag;
    }

    public void deleteTag(Tag tag) {
        unsetPlayerPrefsTagForTag(tag);
        db.delete(tag);
    }

    public void unsetTag(Player player) {
        PlayerPreferences prefs = playerManager.getPlayerPrefs(player);
        prefs.setTag(null);
        db.update(prefs);
    }

    public void unsetIfCurrent(PlayerPreferences prefs, String tag) {
        if(prefs != null) {
            Tag tagObj = prefs.getTag();
            if(tagObj != null && tagObj.getId().equals(tag)) {
                prefs.setTag(null);
                db.update(prefs);
            }
        }
    }

    public boolean hasTagSelected(Player player) {
        return playerManager.getPlayerPrefs(player).getTag() != null;
    }

    public void unsetPlayerPrefsTagForTag(Tag tag) {
        EntityManager em = db.getEntityManager();
        em.getTransaction().begin();
        Query query = em.createQuery("UPDATE PlayerPreferences p SET p.tag = null WHERE p.tag = :tag");
        query.setParameter("tag", tag);
        query.executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    public Tag tagByName(String tagName) {
        EntityManager em = db.getEntityManager();
        try {
            Query query = em.createQuery("SELECT t FROM Tag t WHERE t.name = :tagName");
            query.setParameter("tagName", tagName);
            return (Tag) query.getSingleResult();
        } catch (NoResultException e) {
            return null; // Tag doesn't exist
        } finally {
            em.close();
        }
    }

    public String createTag(Tag tag) throws CommandException {
        if(tag.getName().isEmpty()) {
            throw new CommandException(Message.TAGS_MUST_HAVE_NAME);
        }

        if(tag.getDisplay().isEmpty()) {
            throw new CommandException(Message.TAGS_MUST_HAVE_DISPLAY);
        }

        if(tag.getRarity() == null) {
            throw new CommandException(Message.TAGS_INVALID_RARITY);
        }

        if(tag.getType() == null) {
            throw new CommandException(Message.TAGS_INVALID_TYPE);
        }

        if(tagExists(tag.getName())) {
            throw new CommandException(Message.TAGS_ALREADY_EXISTS, tag.getName());
        }

        tag.setOrder(getNextTagOrder());

        return db.persist(tag);
    }

    public int getNextTagOrder() {
        String hql = "SELECT t.order FROM Tag t ORDER BY t.order DESC";

        EntityManager em = db.getEntityManager();
        TypedQuery<Integer> query = em.createQuery(hql, Integer.class);
        query.setMaxResults(1);
        try {
            Integer result = query.getSingleResult();
            return result != null ? result + 1 : 1;
        } catch (NoResultException e) {
            return 1;
        } finally {
            em.close();
        }
    }

    public boolean isValidType(String type) {
        return getType(type) != null;
    }

    public TagType getType(String type) {
        try {
            return TagType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void setTag(Player player, String tagId) throws CommandException {
        setTag(player.getUniqueId().toString(), tagId);
    }

    public void setTag(String player, String tagId) throws CommandException {
        PlayerPreferences prefs = playerManager.getPlayerPrefs(player);
        if(prefs.getTag() != null && prefs.getTag().getId().equals(tagId)) {
            throw new CommandException(Message.TAGS_HAS_TAG_SET);
        }

        prefs.setTag(db.find(tagId, Tag.class));
        db.update(prefs);
    }


    public boolean hasTag(Player player, String tagId) {
        return hasTag(player.getUniqueId().toString(), tagId);
    }

    public boolean hasTag(String uuid, String tagId) {
        EntityManager em = db.getEntityManager();
        String hql = "FROM PlayerTag p WHERE p.player = :player AND p.tag = :tag";
        TypedQuery<PlayerTag> query = em.createQuery(hql, PlayerTag.class);
        query.setParameter("player", new LCPlayerDB(uuid));
        query.setParameter("tag", new Tag(tagId));
        query.setMaxResults(1);
        boolean result = db.hasResult(query);
        em.close();
        return result;
    }

    public Tag tagById(String id) {
        return db.find(id, Tag.class);
    }

    public HashMap<TagType, Integer> tagCounts() {
        String hql = "SELECT type, COUNT(t) FROM Tag t WHERE t.obtainable = true GROUP BY type";

        HashMap<TagType, Integer> counts = new HashMap<>();
        for(TagType type : TagType.values()) {
            counts.put(type, 0);
        }

        EntityManager em = db.getEntityManager();
        TypedQuery<Object[]> query = em.createQuery(hql, Object[].class);

        for (Object[] result : query.getResultList()) {
            counts.put((TagType)result[0], (int)((long)result[1]));
        }

        em.close();
        return counts;
    }

    public HashMap<TagRarity, Integer> tagRarityCounts() {
        String hql = "SELECT rarity, COUNT(t) FROM Tag t WHERE t.obtainable = true GROUP BY rarity";

        HashMap<TagRarity, Integer> counts = new HashMap<>();
        for(TagRarity rarity : TagRarity.values()) {
            counts.put(rarity, 0);
        }

        EntityManager em = db.getEntityManager();
        TypedQuery<Object[]> query = em.createQuery(hql, Object[].class);

        for (Object[] result : query.getResultList()) {
            counts.put((TagRarity)result[0], (int)((long)result[1]));
        }

        em.close();
        return counts;
    }

    public HashMap<TagType, Integer> tagCountPlayer(Player player) {
        String hql = "SELECT t.type, COUNT(t) FROM PlayerTag p JOIN p.tag t WHERE p.player = :player " +
                "GROUP BY t.type";

        HashMap<TagType, Integer> counts = new HashMap<>();
        for(TagType type : TagType.values()) {
            counts.put(type, 0);
        }
        EntityManager em = db.getEntityManager();
        TypedQuery<Object[]> query = em.createQuery(hql, Object[].class);
        query.setParameter("player", playerManager.getPlayerDB(player));

        for (Object[] result : query.getResultList()) {
            counts.put((TagType)result[0], (int)((long)result[1]));
        }

        em.close();
        return counts;
    }

    public HashMap<TagRarity, Integer> tagRarityCountPlayer(Player player) {
        String hql = "SELECT t.rarity, COUNT(t) FROM PlayerTag p JOIN p.tag t WHERE p.player = :player " +
                "GROUP BY t.rarity";

        HashMap<TagRarity, Integer> counts = new HashMap<>();
        for(TagRarity rarity : TagRarity.values()) {
            counts.put(rarity, 0);
        }
        EntityManager em = db.getEntityManager();
        TypedQuery<Object[]> query = em.createQuery(hql, Object[].class);
        query.setParameter("player", playerManager.getPlayerDB(player));

        for (Object[] result : query.getResultList()) {
            counts.put((TagRarity)result[0], (int)((long)result[1]));
        }

        em.close();
        return counts;
    }

    public HashMap<TagRarity, Integer> tagRarityCountPlayerUnobtainable(Player player) {
        String hql = "SELECT t.rarity, COUNT(t) FROM PlayerTag p JOIN p.tag t WHERE p.player = :player " +
                "AND t.obtainable = false " +
                "GROUP BY t.rarity";

        HashMap<TagRarity, Integer> counts = new HashMap<>();
        for(TagRarity rarity : TagRarity.values()) {
            counts.put(rarity, 0);
        }

        EntityManager em = db.getEntityManager();
        TypedQuery<Object[]> query = em.createQuery(hql, Object[].class);
        query.setParameter("player", playerManager.getPlayerDB(player));

        for (Object[] result : query.getResultList()) {
            counts.put((TagRarity)result[0], (int)((long)result[1]));
        }

        em.close();
        return counts;
    }

    public HashMap<TagType, Integer> tagCountPlayerUnobtainable(Player player) {
        String hql = "SELECT t.type, COUNT(t) FROM PlayerTag p JOIN p.tag t WHERE p.player = :player " +
                "AND t.obtainable = false " +
                "GROUP BY t.type";

        HashMap<TagType, Integer> counts = new HashMap<>();
        for(TagType type : TagType.values()) {
            counts.put(type, 0);
        }

        EntityManager em = db.getEntityManager();
        TypedQuery<Object[]> query = em.createQuery(hql, Object[].class);
        query.setParameter("player", playerManager.getPlayerDB(player));

        for (Object[] result : query.getResultList()) {
            counts.put((TagType)result[0], (int)((long)result[1]));
        }

        em.close();
        return counts;
    }

    public HashMap<String, Long> getOwnerCounts() {
        String hql = "SELECT t.name, COUNT(pt.tag) FROM Tag t " +
                "LEFT JOIN PlayerTag pt ON pt.tag = t.name " +
                "GROUP BY t.name";

        EntityManager em = db.getEntityManager();
        TypedQuery<Object[]> query = em.createQuery(hql, Object[].class);
        List<Object[]> results = query.getResultList();

        HashMap<String, Long> tagCounts = new HashMap<>();
        for (Object[] result : results) {
            String tagName = (String) result[0];
            Long count = (Long) result[1];
            if(count == null) {
                count = 0L;
            }
            tagCounts.put(tagName, count);
        }
        em.close();

        return tagCounts;
    }

    public List<TagDTO> getOwnedTags(Player player, HashMap<String, Long> ownerCounts) {
        String hql = "SELECT t, pt.unlocked FROM Tag t " +
                "LEFT JOIN PlayerTag pt ON pt.tag = t.name " +
                "WHERE pt.player = :player";

        List<TagDTO> ownedTags = new ArrayList<>();
        EntityManager em = db.getEntityManager();
        TypedQuery<Object[]> query = em.createQuery(hql, Object[].class);
        query.setParameter("player", new LCPlayerDB(player));

        for(Object[] o : query.getResultList()) {
            Tag tag = (Tag)o[0];
            ownedTags.add(new TagDTO((Tag)o[0], (Date)o[1], ownerCounts.get(tag.getName())));
        }

        em.close();
        return ownedTags;
    }

    public List<TagDTO> getUnownedTags(Player player, HashMap<String, Long> ownerCounts) {
        String hql = "SELECT t FROM Tag t " +
                "LEFT JOIN PlayerTag pt " +
                "ON pt.tag = t.name AND pt.player = :player " +
                "WHERE pt.tag IS NULL";

        EntityManager em = db.getEntityManager();
        TypedQuery<Tag> query = em.createQuery(hql, Tag.class);
        query.setParameter("player", new LCPlayerDB(player));

        List<TagDTO> unownedTags = new ArrayList<>();
        for(Tag t : query.getResultList()) {
            unownedTags.add(new TagDTO(t, null, ownerCounts.get(t.getName())));
        }


        em.close();
        return unownedTags;
    }

    public List<Tag> getOwnedTags(String player) {
        String hql = "SELECT t FROM Tag t " +
                "LEFT JOIN PlayerTag pt ON pt.tag = t.name " +
                "WHERE pt.player = :player";

        List<Tag> ownedTags;
        EntityManager em = db.getEntityManager();
        TypedQuery<Tag> query = em.createQuery(hql, Tag.class);
        query.setParameter("player", new LCPlayerDB(player));
        ownedTags = query.getResultList();
        em.close();
        return ownedTags;
    }

    public List<Tag> getOwnedTags(Player player) {
        return getOwnedTags(player.getUniqueId().toString());
    }

    public TagMenuData getTagMenuData(Player player) {
        HashMap<String, Long> ownerCounts = getOwnerCounts();
        return new TagMenuData(getOwnedTags(player, ownerCounts), getUnownedTags(player, ownerCounts));
    }

    public List<TagOwnedDTO> getTagLBData(String tagId) {
        String hql = "SELECT pt.player.id, pt.player.name, ps.skull, pt.unlocked FROM PlayerTag pt " +
                "LEFT JOIN PlayerSkull ps ON ps.id=pt.player.id " +
                "WHERE pt.tag = :tag " +
                "ORDER BY pt.unlocked";

        EntityManager em = db.getEntityManager();
        TypedQuery<Object[]> query = em.createQuery(hql, Object[].class);
        query.setParameter("tag", new Tag(tagId));

        List<TagOwnedDTO> results = new ArrayList<>();
        List<Object[]> queryResult = query.getResultList();
        for(Object[] ownedTag : queryResult) {
            String skullBase64 = (String)ownedTag[2];
            ItemStack skull = skullBase64 != null ? ItemUtil.fromBase64(skullBase64) : null;
            results.add(new TagOwnedDTO((String)ownedTag[0], (String)ownedTag[1], skull, (Date)ownedTag[3]));
        }

        em.close();
        return results;
    }

    public void removeTagFrom(String uuid, String tag) throws CommandException {
        if(!tagExists(tag)) {
            throw new CommandException(Message.TAGS_NO_TAG, tag);
        }
        if(!hasTag(uuid, tag)) {
            throw new CommandException(Message.TAGS_HASNT_UNLOCKED, tag);
        }

        PlayerPreferences prefs = playerManager.getPlayerPrefs(uuid);
        unsetIfCurrent(prefs, tag);

        EntityManager em = db.getEntityManager();
        em.getTransaction().begin();
        Query query = em.createQuery("DELETE FROM PlayerTag pt WHERE pt.tag = :tag AND pt.player = :player");
        query.setParameter("tag", new Tag(tag));
        query.setParameter("player", new LCPlayerDB(uuid));
        query.executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    public TagTopResultDTO getTagTopEntries(TagType type, int page, int volume, boolean all) {
        String sql = "SELECT ROW_NUMBER() OVER(ORDER BY COUNT(pt.tag_id) DESC), p.name, COUNT(pt.tag_id) " +
                "FROM lc_players p " +
                "LEFT JOIN lc_player_tags pt ON p.id = pt.player_id " +
                "LEFT JOIN lc_tags t ON pt.tag_id = t.name " +
                "WHERE (? IS NULL OR t.type = ?) " +
                "AND (? IS NULL OR t.obtainable = TRUE) " +
                "GROUP BY p.id, p.name " +
                "HAVING COUNT(pt.tag_id) > 0 " +
                "ORDER BY COUNT(pt.tag_id) DESC ";

        String typeName = type == null ? "" : type.name;
        EntityManager em = db.getEntityManager();
        Query query = em.createNativeQuery(sql);
        query.setParameter(2, typeName);

        if(type == null) {
            query.setParameter(1, null);
        } else {
            query.setParameter(1, 1);
        }

        if(all) {
            query.setParameter(3, null);
        } else {
            query.setParameter(3, 1);
        }

        TagTopResultDTO result = new TagTopResultDTO();
        List<Object[]> temp = (List<Object[]>)query.getResultList();
        result.setTotalPages((int)Math.ceil(temp.size() / 10.0));
        int endIndex = (page + 1) * volume;
        int startIndex = Math.min(page * volume, temp.size());
        endIndex = Math.min(endIndex, temp.size());
        List<Object[]> sublist = temp.subList(startIndex, endIndex);




        List<TagTopEntry> entries = new ArrayList<>();


        for(Object[] e : sublist) {
            entries.add(new TagTopEntry(((BigInteger)e[0]).intValue(), (String)e[1], ((BigInteger)e[2]).intValue()));
        }

        result.setEntries(entries);

        em.close();

        return result;
    }

    public Integer serverTagCount(TagType type, boolean all) {
        String hql = "SELECT COUNT(pt) FROM PlayerTag pt " +
                "LEFT JOIN Tag t ON pt.tag = t.name " +
                "WHERE (:ignoreType is null or t.type = :type) " +
                "AND (:ignoreUnobtainable is null or t.obtainable = true)";

        EntityManager em = db.getEntityManager();
        TypedQuery<Long> query = em.createQuery(hql, Long.class);
        query.setParameter("type", type);

        if(type == null) {
            query.setParameter("ignoreType", null);
        } else {
            query.setParameter("ignoreType", 1);
        }

        if(all) {
            query.setParameter("ignoreUnobtainable", null);
        } else {
            query.setParameter("ignoreUnobtainable", 1);
        }
        try {
            return query.getSingleResult().intValue();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public TagTopDTO getTagTopData(TagType type, int page, int volume, boolean all) {
        TagTopResultDTO tagTopResults = getTagTopEntries(type, page, volume, all);
        List<TagTopEntry> entries = tagTopResults.getEntries();
        int currentPage = page + 1;
        int totalPageCount = tagTopResults.getTotalPages();
        int serverCount = serverTagCount(type, all);
        return new TagTopDTO(entries, currentPage, totalPageCount, serverCount);
    }

    @SuppressWarnings("unused")
    public List<String> getTagTypes() {
        List<String> types = new ArrayList<>();
        for(TagType type : TagType.values()) {
            types.add(type.name());
        }

        return types;
    }

    @SuppressWarnings("unused")
    public List<String> getTagTypesAndAll() {
        List<String> types = new ArrayList<>();
        for(TagType type : TagType.values()) {
            types.add(type.name().toLowerCase());
        }
        types.add("all");

        return types;
    }


    @SuppressWarnings("unused")
    public List<String> getTagRarities() {
        List<String> rarities = new ArrayList<>();
        for(TagRarity rarity : TagRarity.values()) {
            rarities.add(rarity.name());
        }

        return rarities;
    }

    @SuppressWarnings("unused")
    public List<String> getTagNames() {
        List<String> tagNames = new ArrayList<>();
        for(Tag tag : db.findAll(Tag.class)) {
            tagNames.add(tag.getName());
        }
        return tagNames;
    }

    @SuppressWarnings("unused")
    public List<String> getOwnedTagNames(Player player) {
        List<String> tagNames = new ArrayList<>();
        for(Tag tag : getOwnedTags(player)) {
            tagNames.add(tag.getName());
        }
        return tagNames;
    }


}
