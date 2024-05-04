package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.*;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.player.display.LCPlayer;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.HeadUtil;
import de.legoshi.lccore.util.ItemUtil;
import de.tr7zw.changeme.nbtapi.NBTFile;
import de.tr7zw.changeme.nbtapi.NBTList;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerManager {
    @Inject private ChatManager chatManager;
    @Inject private VisibilityManager visibilityManager;
    @Inject private PunishmentManager punishmentManager;
    @Inject private DBManager db;

    private final HashMap<String, LCPlayer> players = new HashMap<>();
    private Set<String> clearEffectsOnTps = new HashSet<>();

    public void playerJoin(Player player) {
        initDbData(player);
        updateName(player);
        updateSkull(player);
        players.put(player.getUniqueId().toString(), loadPlayer(player));
        player.setDisplayName(chatManager.rankNickStar(player));
        visibilityManager.hideIfHiding(player);
        chatManager.onJoin(player);
        clearTitle(player);
        forgeHandshake(player);
    }

    public void forgeHandshake(Player player) {
        Linkcraft.asyncLater(() -> {
            // Handshake Reset (https://wiki.vg/Minecraft_Forge_Handshake#HandshakeReset)
            sendFMLPluginMessage(player, (byte)-2);
            // Modlist (https://wiki.vg/Minecraft_Forge_Handshake#ModList)
            sendFMLPluginMessage(player, (byte)0, (byte)2, (byte)0, (byte)0, (byte)0, (byte)0);
        }, 20L);
    }

    private void sendFMLPluginMessage(Player player, byte... data)
    {
        player.sendPluginMessage(Linkcraft.getPlugin(), "FML|HS", data);
    }

    public void updateDBName(String uuid, String name) {
        LCPlayerDB lcPlayerDB = getPlayerDB(uuid);
        if(lcPlayerDB != null) {
            lcPlayerDB.setName(name);
            db.update(lcPlayerDB);
        } else {
            lcPlayerDB = new LCPlayerDB(uuid, name);
            db.persist(lcPlayerDB);
        }
    }

    private void initDbData(Player player) {
        LCPlayerDB lcPlayerDB = getPlayerDB(player);
        if(lcPlayerDB == null) {
            db.persist(new LCPlayerDB(player.getUniqueId().toString(), player.getName()));
        }

        PlayerPreferences prefs = getPlayerPrefs(player);
        if(prefs == null) {
            db.persist(new PlayerPreferences(player));
        }
    }

    private void initDbData(String player) {
        LCPlayerDB lcPlayerDB = getPlayerDB(player);
        if(lcPlayerDB == null) {
            db.persist(new LCPlayerDB(player));
        }

        getPlayerPrefs(player);
    }

    public LCPlayerDB getPlayerDB(Player player) {
        return getPlayerDB(player.getUniqueId().toString());
    }

    public LCPlayerDB getPlayerDB(String player) {
        return db.find(player, LCPlayerDB.class);
    }

    public void playerLeave(Player player) {
        players.remove(player.getUniqueId().toString());
        visibilityManager.onLeave(player);
        chatManager.onLeave(player);
        saveConfigDataTemp(player);
    }

    public void updatePlayer(Player player) {
        players.put(player.getUniqueId().toString(), loadPlayer(player));
        player.setDisplayName(chatManager.rankNickStar(player));
    }

    public void updatePlayer(Player player, long delay) {
        Linkcraft.asyncLater(() -> {
            if(player.isOnline()) {
                updatePlayer(player);
            }
        }, delay);
    }

    private LCPlayer loadPlayer(Player player) {
        return loadPlayer(player.getUniqueId().toString());
    }

    public LCPlayer loadPlayer(String player) {
        LCPlayer lcPlayer = new LCPlayer();
        // TODO: set nickname somehow and save :skull:
        lcPlayer.setName(getName(player));
        lcPlayer.setNick(getNick(player));
        lcPlayer.setRank(chatManager.determineRank(player));
        lcPlayer.setBonus(chatManager.determineBonus(player));
        lcPlayer.setWolf(chatManager.determineWolf(player));
        lcPlayer.setMaze(chatManager.determineMaze(player));
        lcPlayer.setStar(chatManager.determineStar(player));
        lcPlayer.setStaff(chatManager.determineStaff(player));
        lcPlayer.setChatFormats(chatManager.determineChatFormats(player));
        lcPlayer.setChatColor(chatManager.determineChatColor(player));
        lcPlayer.setNameFormats(chatManager.determineNameFormat(player));
        lcPlayer.setManualChatColors(chatManager.determineManualChatColors(player));
        lcPlayer.setManualChatFormats(chatManager.determineManualChatFormats(player));
        lcPlayer.setPunishments(punishmentManager.getCurrentPunishments(player));
        return lcPlayer;
    }

    public PlayerPreferences getPlayerPrefs(Player player) {
        return getPlayerPrefs(player.getUniqueId().toString());
    }

    public PlayerPreferences getPlayerPrefs(String player) {
        PlayerPreferences playerPreferences = db.find(player, PlayerPreferences.class);
        if(playerPreferences == null) {
            db.persist(new PlayerPreferences(player));
            playerPreferences = db.find(player, PlayerPreferences.class);
        }
        return playerPreferences;
    }

    public boolean isStaff(Player player) {
        return getPlayer(player).getStaff() != null;
    }

    public void clearTitle(Player player) {
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatMessage(""));
        PacketPlayOutTitle subtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, new ChatMessage(""));
        PlayerConnection conn = ((CraftPlayer) player).getHandle().playerConnection;
        conn.sendPacket(title);
        conn.sendPacket(subtitle);
    }

    private void updateName(Player player) {
        updateNameDB(player);
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getPlugin(), Linkcraft.getPlugin().getPlayerdataFolder(), player.getUniqueId().toString() + ".yml");
        FileConfiguration config = playerData.getConfig();
        config.set("username", player.getName());
        playerData.saveConfig();
    }

    private void updateSkull(Player player) {
        PlayerSkull playerSkull = db.find(player.getUniqueId().toString(), PlayerSkull.class);
        if(playerSkull == null) {
            playerSkull = new PlayerSkull();
            playerSkull.setId(player.getUniqueId().toString());
            playerSkull.setSkull(ItemUtil.toBase64(HeadUtil.playerHead(player.getName())));
            db.persist(playerSkull);
        } else {
            playerSkull.setSkull(ItemUtil.toBase64(HeadUtil.playerHead(player.getName())));
            db.update(playerSkull);
        }
    }

    public void updateSkull(String uuid, ItemStack skull) {
        PlayerSkull playerSkull = db.find(uuid, PlayerSkull.class);
        if(playerSkull == null) {
            playerSkull = new PlayerSkull();
            playerSkull.setId(uuid);
            playerSkull.setSkull(ItemUtil.toBase64(skull));
            db.persist(playerSkull);
        } else {
            playerSkull.setSkull(ItemUtil.toBase64(skull));
            db.update(playerSkull);
        }
    }

    private void updateNameDB(Player player) {
        LCPlayerDB lcPlayerDB = getPlayerDB(player);
        if(!lcPlayerDB.getName().equals(player.getName())) {
            lcPlayerDB.setName(player.getName());
            db.update(lcPlayerDB);
        }
    }

    public void updateNick(Player player, String nick) {
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getPlugin(), Linkcraft.getPlugin().getPlayerdataFolder(), player.getUniqueId().toString() + ".yml");
        FileConfiguration config = playerData.getConfig();
        config.set("nick", nick);
        playerData.saveConfig();
        updatePlayer(player);
    }

    public List<String> getIgnores(Player player) {
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getPlugin(), Linkcraft.getPlugin().getPlayerdataFolder(), player.getUniqueId().toString() + ".yml");
        FileConfiguration config = playerData.getConfig();
        List<String> ignores = config.getStringList("ignores");

        return ignores != null ? ignores : new ArrayList<>();
    }

    public void updateIgnore(Player player, String toIgnore, boolean ignore) {
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getPlugin(), Linkcraft.getPlugin().getPlayerdataFolder(), player.getUniqueId().toString() + ".yml");
        FileConfiguration config = playerData.getConfig();
        List<String> ignores = getIgnores(player);

        if(!ignore) {
            ignores.remove(toIgnore);
        } else {
            ignores.add(toIgnore);
        }

        config.set("ignores", ignores);
        playerData.saveConfig();
    }

    private String getName(String uuid) {
        return nameByUUID(uuid);
    }

    private String getNick(String uuid) {
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getPlugin(), Linkcraft.getPlugin().getPlayerdataFolder(), uuid + ".yml");
        FileConfiguration config = playerData.getConfig();
        String nick = config.getString("nick");
        return nick != null ? nick : "";
    }


    private void saveConfigDataTemp(Player player) {
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getPlugin(), Linkcraft.getPlugin().getPlayerdataFolder(), player.getUniqueId() + ".yml");
        FileConfiguration playerDataConfig = playerData.getConfig();
        //playerDataConfig.set("lastlocation", Utils.getStringFromLocation(player.getLocation()));
        playerDataConfig.set("jumps", player.getStatistic(Statistic.JUMP));
        playerData.saveConfig();
    }

    public LCPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId().toString());
    }

    public LCPlayer getPlayer(String player) {
        return players.get(player);
    }

    public int getPing(Player player) {
        return ((CraftPlayer)player).getHandle().ping;
    }

    public Player playerByName(String name) {
        CraftServer server = (CraftServer)Bukkit.getServer();
        Player found = server.getPlayerExact(name);
        if (found != null) {
            return found;
        }

        String lowerName = name.toLowerCase();
        Player bestMatch = null;
        int delta = Integer.MAX_VALUE;

        for(Player player : Bukkit.getOnlinePlayers()) {
            String username = player.getName();
            LCPlayer lcPlayer = getPlayer(player);
            String nickname = lcPlayer != null ? lcPlayer.getNick() : "";

            int usernameDelta = Math.abs(username.length() - lowerName.length());
            int nicknameDelta = Math.abs(nickname.length() - lowerName.length());

            if (username.toLowerCase().startsWith(lowerName) && usernameDelta < delta) {
                bestMatch = player;
                delta = usernameDelta;
            }

            if (nickname.toLowerCase().startsWith(lowerName) && nicknameDelta < delta) {
                bestMatch = player;
                delta = nicknameDelta;
            }

            if (username.equalsIgnoreCase(name) || nickname.equalsIgnoreCase(name)) {
                return player;
            }
        }
        return bestMatch;
    }

    public String nameByUUID(String uuid) {
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        if(player != null) {
            return player.getName();
        }

        OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
        if(op != null) {
            return op.getName();
        }

        String hql = "SELECT p FROM LCPlayerDB p WHERE p.id=:id";
        EntityManager em = db.getEntityManager();
        TypedQuery<LCPlayerDB> query = em.createQuery(hql, LCPlayerDB.class);
        query.setParameter("id", uuid);
        try {
            LCPlayerDB lcPlayerDB = query.getSingleResult();
            if(lcPlayerDB != null) {
                return lcPlayerDB.getName();
            }
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }

        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getPlugin(), Linkcraft.getPlugin().getPlayerdataFolder(), uuid + ".yml");
        FileConfiguration config = playerData.getConfig();
        return config.getString("username");
    }

    public PlayerRecord getPlayerRecord(Player player, String name) {
        return player != null ? new PlayerRecord(player.getUniqueId().toString(), player.getName()) : uuidByName(name);
    }

    public PlayerRecord uuidByName(String name) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        if(player != null && player.hasPlayedBefore()) {
            return new PlayerRecord(player.getUniqueId().toString(), player.getName());
        }

        String hql = "SELECT p FROM LCPlayerDB p WHERE p.name=:name";
        EntityManager em = db.getEntityManager();
        TypedQuery<LCPlayerDB> query = em.createQuery(hql, LCPlayerDB.class);
        query.setParameter("name", name);
        try {
            LCPlayerDB lcPlayerDB = query.getSingleResult();
            if(lcPlayerDB != null) {
                return new PlayerRecord(lcPlayerDB.getId(), lcPlayerDB.getName());
            }
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }

        return null;
    }

    public void addClearEffectsOnTps(Player player) {
        clearEffectsOnTps.add(player.getUniqueId().toString());
    }

    public void removeClearEffectsOnTps(Player player) {
        clearEffectsOnTps.remove(player.getUniqueId().toString());
    }

    public boolean hasClearEffectsOnTp(Player player) {
        return clearEffectsOnTps.contains(player.getUniqueId().toString());
    }


    // Sadness ensues:
//    public void spigotToNBTLocation(Location l, String uuid) throws IOException {
//        NBTFile file = new NBTFile(getPlayerDataFile(uuid));
//        NBTList<Double> position = file.getDoubleList("Pos");
//        position.set(0, l.getX());
//        position.set(1, l.getY());
//        position.set(2, l.getZ());
//        NBTList<Float> rotation = file.getFloatList("Rotation");
//        rotation.set(0, l.getYaw());
//        rotation.set(1, l.getPitch());
//        CraftWorld craftWorld = (CraftWorld)l.getWorld();
//        file.setInteger("Dimension", craftWorld.getHandle().dimension);
//        file.save();
//    }

    public Location NBTLocationToSpigotLocation(String uuid) throws IOException {
        NBTFile file = new NBTFile(getPlayerDataFile(uuid));
        NBTList<Double> position = file.getDoubleList("Pos");
        NBTList<Float> rotation = file.getFloatList("Rotation");
        int dim = file.getInteger("Dimension");

        World world = getSpigotWorldFromDimId(dim);

        if(world == null) {
            return null;
        }

        double x = position.get(0);
        double y = position.get(1);
        double z = position.get(2);
        float yaw = rotation.get(0);
        float pitch = rotation.get(1);

        return new Location(world, x, y, z, yaw, pitch);
    }

    public Location getOTPLocation(String uuid) throws IOException {
        OTPHereLocation otp = db.find(uuid, OTPHereLocation.class);
        if(otp != null) {
            return otp.getLocation().toSpigot();
        }

        return null;
    }

    public Location getOTPLocationNBT(String uuid) throws IOException {
        OTPHereLocation otp = db.find(uuid, OTPHereLocation.class);
        if(otp != null) {
            return otp.getLocation().toSpigot();
        }

        return NBTLocationToSpigotLocation(uuid);
    }

    public void saveOTPLocation(Location l, String uuid, Player teleportedBy) {
        deleteCurrentOTPLocation(uuid);

        LCLocation loc = new LCLocation(l);
        db.persist(loc);

        LCPlayerDB p = db.find(uuid, LCPlayerDB.class);
        OTPHereLocation otpHereLocation = new OTPHereLocation();
        otpHereLocation.setLocation(loc);
        otpHereLocation.setPlayer(p);
        otpHereLocation.setOtpBy(teleportedBy.getName());
        db.persist(otpHereLocation, p, loc);
    }

    public void deleteCurrentOTPLocation(String uuid) {
        OTPHereLocation l = db.find(uuid, OTPHereLocation.class);
        if(l != null) {
            LCLocation pos = l.getLocation();
            db.delete(l);
            db.delete(pos);
        }
    }

    public World getSpigotWorldFromDimId(int dim) {
        List<World> worlds = Linkcraft.getPlugin().getServer().getWorlds();

        World world = null;
        for(World w : worlds) {
            CraftWorld craftWorld = (CraftWorld)w;

            if(craftWorld.getHandle().dimension == dim) {
                world = w;
                break;
            }
        }

        return world;
    }

    public File getPlayerDataFile(String uuid) {
        return new File(Linkcraft.getPlugin().getServer().getWorlds().get(0).getWorldFolder().getAbsolutePath() + File.separator + "playerdata" + File.separator + uuid + ".dat");
    }

    @SuppressWarnings("unused")
    public List<String> getPlayers(Player p) {
        List<String> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach((player) -> {
            if(visibilityManager.canSee(p, player)) {
                players.add(player.getName());
            }
        });

        return players;
    }

    @SuppressWarnings("unused")
    public List<String> getPossibleNames(Player p) {
        List<String> names = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach((player) -> {
            if(visibilityManager.canSee(p, player)) {
                String nick = getPlayer(player).getNick();
                if (!nick.isEmpty()) {
                    names.add(chatManager.removeChatColour(nick));
                }
                names.add(player.getName());
            }
        });
        return names;
    }

    @SuppressWarnings("unused")
    public List<String> getNicks(Player p) {
        List<String> nicks = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach((player) -> {
            if(visibilityManager.canSee(p, player)) {
                String nick = getPlayer(player).getNick();
                if (!nick.isEmpty()) {
                    nicks.add(chatManager.removeChatColour(nick));
                }
            }
        });

        return nicks;
    }

    @SuppressWarnings("unused")
    public List<String> getNicksLower(Player p) {
        List<String> nicks = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach((player) -> {
            if(visibilityManager.canSee(p, player)) {
                String nick = getPlayer(player).getNick();
                if (!nick.isEmpty()) {
                    nicks.add(chatManager.removeChatColour(nick).toLowerCase());
                }
            }
        });

        return nicks;
    }

    @SuppressWarnings("unused")
    public List<String> getStars() {
        return new ArrayList<>(ConfigManager.starDisplay.keySet());
    }
}
