package de.legoshi.lccore.manager;

import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.LCPlayerDB;
import de.legoshi.lccore.database.models.PlayerPreferences;
import de.legoshi.lccore.database.models.PlayerTag;
import de.legoshi.lccore.database.models.Tag;
import de.legoshi.lccore.menu.tags.TagHolder;
import de.legoshi.lccore.player.display.LCPlayer;
import de.legoshi.lccore.tag.TagDTO;
import de.legoshi.lccore.tag.TagRarity;
import de.legoshi.lccore.tag.TagType;
import de.legoshi.lccore.util.CommandException;
import de.legoshi.lccore.util.CommonUtil;
import de.legoshi.lccore.util.message.Message;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
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

        return db.persist(tag);
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

//    public Tag tagById(String id) {
//        try {
//            return tagById(Integer.parseInt(id));
//        } catch (NumberFormatException e) {
//            return null;
//        }
//    }

    public void setTag(Player player, String tagId) throws CommandException {
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
        query.setParameter("player", uuid);
        query.setParameter("tag", new Tag(tagId));
        query.setMaxResults(1);
        return db.hasResult(query);
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
        TypedQuery<Object[]> query = db.getEntityManager().createQuery(hql, Object[].class);

        for (Object[] result : query.getResultList()) {
            counts.put((TagType)result[0], (int)((long)result[1]));
        }

        em.close();
        return counts;
    }

    public HashMap<TagType, Integer> tagCountPlayer(Player player) {
        String hql = "SELECT t.type, COUNT(t) FROM PlayerTag p JOIN p.tag t WHERE p.player = :player " +
//                "AND t.obtainable = true " + // this made it look weird
                "GROUP BY t.type";

        HashMap<TagType, Integer> counts = new HashMap<>();
        for(TagType type : TagType.values()) {
            counts.put(type, 0);
        }
        EntityManager em = db.getEntityManager();
        TypedQuery<Object[]> query = db.getEntityManager().createQuery(hql, Object[].class);
        query.setParameter("player", playerManager.getPlayerDB(player));

        for (Object[] result : query.getResultList()) {
            counts.put((TagType)result[0], (int)((long)result[1]));
        }

        em.close();
        return counts;
    }

    public HashMap<Integer, Integer> getTagCountsCache() {
        String hql = "SELECT t.id, COUNT(pt.tag) FROM Tag t " +
                "LEFT JOIN PlayerTag pt ON pt.tag = t.id " +
                "GROUP BY t.id";

        EntityManager em = db.getEntityManager();
        TypedQuery<Object[]> query = em.createQuery(hql, Object[].class);
        List<Object[]> results = query.getResultList();

        HashMap<Integer, Integer> tagCounts = new HashMap<>();
        for (Object[] result : results) {
            Integer tagId = (Integer) result[0];
            Long count = (Long) result[1];
            tagCounts.put(tagId, count.intValue());
        }

        return tagCounts;
    }

    public List<TagDTO> getTags(Player player, TagType type, TagHolder.TagOwnershipFilter ownershipFilter, TagRarity rarityFilter, TagHolder.TagOrder tagOrder, int page, int pageVolume, boolean staff) {

        String hql = "SELECT t, pt.unlocked, COUNT(pt2) FROM Tag t " +
                "LEFT JOIN PlayerTag pt ON pt.tag = t.id AND pt.player = :player " +
                "LEFT JOIN PlayerTag pt2 ON pt2.tag = t.id " +
                "WHERE (:ignoreType is null OR t.type = :type) " +
                "AND (:ignoreRarity is null OR t.rarity = :rarity) " +
                "{} " +
                "AND (t.visible = true OR pt.unlocked IS NOT NULL OR :staff IS NOT NULL) " +
                "GROUP BY t.id, pt.unlocked ";

        switch (ownershipFilter) {
            case COLLECTED:
                hql = CommonUtil.format(hql, "{}", "AND pt.unlocked IS NOT NULL");
                break;
            case UNCOLLECTED:
                hql = CommonUtil.format(hql, "{}", "AND pt.unlocked IS NULL");
                break;
            default:
                hql = CommonUtil.format(hql, "{}", "");
        }

        switch(tagOrder) {
            case OWNERSHIP:
                hql += "ORDER BY pt.unlocked DESC, FIELD(t.rarity, " + CommonUtil.hqlOrderByEnum(TagRarity.class) + "), COUNT(pt2) DESC";
                break;
            case RARITY:
                hql += "ORDER BY FIELD(t.rarity, " + CommonUtil.hqlOrderByEnum(TagRarity.class) + "), COUNT(pt2) ASC, pt.unlocked DESC";
                break;
            case OWNER_COUNT:
                hql += "ORDER BY COUNT(pt2) DESC, FIELD(t.rarity, " + CommonUtil.hqlOrderByEnum(TagRarity.class) + "), pt.unlocked DESC";
        }

        EntityManager em = db.getEntityManager();
        TypedQuery<Object[]> query = em.createQuery(hql, Object[].class);
        query.setFirstResult(page * pageVolume);
        query.setMaxResults(pageVolume);
        query.setParameter("player", new LCPlayerDB(player.getUniqueId().toString()));
        query.setParameter("type", type);
        query.setParameter("rarity", rarityFilter);

        // Hack to get "All" menu working without changing query string
        if(type == null)
            query.setParameter("ignoreType", null);
        else
            query.setParameter("ignoreType", 1);

        // Same thing but for rarity filter
        if(rarityFilter == null)
            query.setParameter("ignoreRarity", null);
        else
            query.setParameter("ignoreRarity", 1);

        // Shows 'NONE' and 'CUSTOM' regardless of ownership
        if(staff) {
            query.setParameter("staff", 1);
        } else {
            query.setParameter("staff", null);
        }

        List<TagDTO> tags = new ArrayList<>();

        for(Object[] o : query.getResultList()) {
            tags.add(new TagDTO((Tag)o[0], (Date)o[1], (long)o[2]));
        }

        em.close();

        return tags;
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
}
