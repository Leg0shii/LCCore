package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.models.Tag;
import de.legoshi.lccore.menu.GuiMessage;
import de.legoshi.lccore.player.PunishmentType;
import de.legoshi.lccore.player.chat.ChatChannel;
import de.legoshi.lccore.player.display.*;
import de.legoshi.lccore.util.ColorHelper;
import de.legoshi.lccore.util.LCSound;
import de.legoshi.lccore.util.PlayerDisplayBuilder;
import de.legoshi.lccore.util.Utils;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.util.*;
import java.util.function.Function;

public class ChatManager {
    @Inject private LuckPermsManager luckPermsManager;
    @Inject private PunishmentManager punishmentManager;
    @Inject private PlayerManager playerManager;
    @Inject private VisibilityManager visibilityManager;
    @Inject private PartyManager partyManager;
    @Inject private Injector injector;

    // Leaving current channel cached since it'd be more of an inconvenience if it got destroyed on leave
    private final Map<String, ChatChannel> currentChannels = new HashMap<>();
    // Ditto for toggle chat
    private final HashSet<String> toggleChats = new HashSet<>();
    private final Map<String, String> lastMessaged = new HashMap<>();
    private final Map<String, String> directChannels = new HashMap<>();
    private final Map<String, Function<Player, String>> papiMap = new HashMap<>();
    private final Map<String, List<String>> ignores = new HashMap<>();
    private final Map<String, GuiMessage> captureGuiMessages = new HashMap<>();


    public void initPapiMap() {
        papiMap.put("rank", this::rank);
        papiMap.put("maze", this::maze);
        papiMap.put("name", Player::getName);
        papiMap.put("nickname", this::nick);
        papiMap.put("ping", player -> String.valueOf(playerManager.getPing(player)));
        papiMap.put("jumps", player -> String.valueOf(player.getStatistic(Statistic.JUMP)));
        papiMap.put("tab", this::tabRank);
        papiMap.put("star", this::star);
        papiMap.put("staff", this::staff);
    }

    public String getPlaceholder(String id, Player player) {
        Function<Player, String> func = papiMap.get(id);
        if(func != null) {
            return func.apply(player);
        }
        return null;
    }

    public void toggleToggleChat(Player player) {
        if(chatToggled(player)) {
            toggleChats.remove(player.getUniqueId().toString());
            MessageUtil.send(Message.CHAT_TOGGLED_ON, player);
        } else {
            toggleChats.add(player.getUniqueId().toString());
            MessageUtil.send(Message.CHAT_TOGGLED_OFF, player);
        }
    }

    private String rank(Player player) {
        return playerManager.getPlayer(player).getRank().getDisplay();
    }

    private String tabRank(Player player) {
        return playerManager.getPlayer(player).getRank().getTabDisplay() + " ";
    }

    private String nick(Player player) {
        return injector.getInstance(PlayerDisplayBuilder.class).setPlayer(player).nick().build();
    }

    private String star(Player player) {
        StarDTO starDTO = playerManager.getPlayer(player).getStar();
        return starDTO == null ? "" : starDTO.getDisplay();
    }

    private String staff(Player player) {
        StaffDTO staffDTO = playerManager.getPlayer(player).getStaff();
        return staffDTO == null ? "" : staffDTO.getDisplay() + " ";
    }

    private String maze(Player player) {
        String maze = playerManager.getPlayer(player).getMaze().getTabDisplay();
        return !maze.isEmpty() ? " " + maze : maze;
    }

    public void onJoin(Player player) {
        loadChannel(player);
        loadIgnores(player);
    }

    private void loadIgnores(Player player) {
        ignores.put(player.getUniqueId().toString(), playerManager.getIgnores(player));
    }

    public void loadChannel(Player player) {
        currentChannels.putIfAbsent(player.getUniqueId().toString(), ChatChannel.GLOBAL);
    }

    public void decacheDirectChannel(Player player) {
        directChannels.remove(player.getUniqueId().toString());
    }

    public void decacheLastMessaged(Player player) {
        lastMessaged.remove(player.getUniqueId().toString());
    }

    public void onLeave(Player player) {
        //decacheDirectChannel(player);
        decacheLastMessaged(player);
    }

    public void setLastMessaged(Player p1, Player p2) {
        lastMessaged.put(p1.getUniqueId().toString(), p2.getUniqueId().toString());
    }

    public Player getLastMessaged(Player player) {
        String lastMessagedUUID = lastMessaged.get(player.getUniqueId().toString());
        if(lastMessagedUUID == null) {
            return null;
        }

        return Bukkit.getPlayer(UUID.fromString(lastMessagedUUID));
    }

    public ChatChannel getChannel(Player player) {
        return currentChannels.get(player.getUniqueId().toString());
    }

    public boolean isInChannel(Player player, ChatChannel channel) {
        return getChannel(player) != null && getChannel(player).equals(channel);
    }

    public void setChannelSilent(Player player, ChatChannel channel) {
        currentChannels.put(player.getUniqueId().toString(), channel);
    }

    public void ignore(Player player, Player toIgnore) {
        if(ignores(player, toIgnore)) {
            ignores.get(player.getUniqueId().toString()).remove(toIgnore.getUniqueId().toString());
            playerManager.updateIgnore(player, toIgnore, false);
            MessageUtil.send(Message.UNIGNORE_PLAYER, player, rankNickStar(toIgnore));
        } else {
            ignores.get(player.getUniqueId().toString()).add(toIgnore.getUniqueId().toString());
            playerManager.updateIgnore(player, toIgnore, true);
            MessageUtil.send(Message.IGNORE_PLAYER, player, rankNickStar(toIgnore));
        }
    }


    public void setChannel(Player player, ChatChannel channel) {
        if(isInChannel(player, channel)) {
            MessageUtil.send(Message.IN_CHANNEL, player);
            return;
        }
        // Check if they have a party?
        MessageUtil.send(Message.JOIN_CHANNEL, player, channel.display);
        setChannelSilent(player, channel);
    }

    public void setDirectChannel(Player sender, Player recipient) {
        directChannels.put(sender.getUniqueId().toString(), recipient.getUniqueId().toString());
    }

    public void openDirectChannel(Player sender, Player recipient) {
        setChannelSilent(sender, ChatChannel.DIRECT);
        setDirectChannel(sender, recipient);
        MessageUtil.send(Message.OPEN_DIRECT_CHANNEL, sender, injector.getInstance(PlayerDisplayBuilder.class).setPlayer(recipient).rank().nick().star().build());
    }

    public String rankNickStar(Player player) {
        return injector.getInstance(PlayerDisplayBuilder.class).setPlayer(player).rank().nick().star().build();
    }

    public String rankNick(Player player) {
        return injector.getInstance(PlayerDisplayBuilder.class).setPlayer(player).rank().nick().build();
    }

    public String rankNickStar(String player) {
        return injector.getInstance(PlayerDisplayBuilder.class).setPlayer(player).rank().nick().star().build();
    }

    public String nickPreview(Player player) {
        return injector.getInstance(PlayerDisplayBuilder.class).setPlayer(player).nick().build();
    }

    public void openDirectChannelWithLastMessaged(Player sender) {
        Player lastMessaged = getLastMessaged(sender);
        if(lastMessaged == null) {
            // Move channels if in dm?
            MessageUtil.send(Message.IS_OFFLINE_2, sender);
            return;
        }
        openDirectChannel(sender, lastMessaged);
    }

    public void sendMessageToCurrentChannel(Player player, String message) {
        if(isNextMessageGuiMessage(player)) {
            sendMessageToGui(player, message);
            return;
        }

        if(punishmentManager.isPunished(player, PunishmentType.FULL_MUTE)) {
            MessageUtil.send(Message.PUNISH_YOU_ARE_FULL_MUTED, player);
            return;
        }

        switch (getChannel(player)) {
            case GLOBAL:
                if(punishmentManager.isPunished(player, PunishmentType.MUTE)) {
                    MessageUtil.send(Message.PUNISH_YOU_ARE_MUTED, player);
                    return;
                }
                sendGlobalMessage(player, message);
                break;
            case PARTY:
                sendPartyMessage(player, message);
                break;
            case STAFF:
                sendStaffMessage(player, message);
                break;
            case DIRECT:
                sendDirectToChannel(player, message);
                break;
        }
    }

    public void sendPartyMessage(Player player, String message) {
        String partyId = partyManager.getPartyId(player);
        if(partyId == null && isInChannel(player, ChatChannel.PARTY)) {
            setChannelSilent(player, ChatChannel.GLOBAL);
            MessageUtil.send(Message.CHANGED_TO_ALL_CHANNEL, player);
            return;
        }

        if (partyId == null) {
            MessageUtil.send(Message.PARTY_NOT_IN, player);
            return;
        }

        partyManager.broadcastToParty(partyId, partyChat(player, message), player);
    }

    public void sendStaffMessage(Player player, String message) {
        for(Player staff : Bukkit.getOnlinePlayers()) {
            if(staff.hasPermission("linkcraft.staff") || staff.getUniqueId().toString().equals(player.getUniqueId().toString())) {
                staff.sendMessage(staffChat(player, message));
            }
        }
    }

    public boolean ignores(Player player, Player other) {
        List<String> ignoredPlayers = ignores.get(player.getUniqueId().toString());
        return ignoredPlayers != null && ignoredPlayers.contains(other.getUniqueId().toString());
    }

    public void sendDirectChat(Player sender, Player recipient, String message) {
        if(recipient == null || (!visibilityManager.canSee(sender, recipient) && !sender.hasPermission("linkcraft.message.vanished"))) {
            MessageUtil.send(Message.IS_OFFLINE_2, sender);
            if(isInChannel(sender, ChatChannel.DIRECT)) {
                setChannel(sender, ChatChannel.GLOBAL);
            }
            return;
        }

        if(!recipient.isOnline()) {
            MessageUtil.send(Message.IS_OFFLINE, sender, recipient.getName());
            if(isInChannel(sender, ChatChannel.DIRECT)) {
                setChannel(sender, ChatChannel.GLOBAL);
            }
            return;
        }

        if(ignores(recipient, sender)) {
            MessageUtil.send(Message.IGNORES_YOU, sender);
            return;
        }

        recipient.sendMessage(directChatFrom(sender, message));
        recipient.playSound(recipient.getEyeLocation(), Sound.CHICKEN_EGG_POP, 1.5f, 1f);
        setLastMessaged(recipient, sender);
        sender.sendMessage(directChatTo(recipient, message));
        setLastMessaged(sender, recipient);
        logDm(sender, recipient, message);
    }

    private void logDm(Player sender, Player receiver, String message) {
        PlayerDisplayBuilder builderSend = injector.getInstance(PlayerDisplayBuilder.class);
        PlayerDisplayBuilder builderReceive = injector.getInstance(PlayerDisplayBuilder.class);
        String dm = "[" + sender.getName() + "] " + builderSend.setPlayer(sender).rank().nick().star().toFromArrow().build() + " ";
        dm += "[" + receiver.getName() + "] " + builderReceive.setPlayer(receiver).rank().nick().star().arrow().message(message).build();
        MessageUtil.log(dm, false);
    }

    public String globalChat(Player player, String message) {
        PlayerDisplayBuilder builder = injector.getInstance(PlayerDisplayBuilder.class);
        return builder.setPlayer(player).tag().bonus().wolf().rank().nick().star().arrow().message(message).build();
    }

    public String partyChat(Player player, String message) {
        PlayerDisplayBuilder builder = injector.getInstance(PlayerDisplayBuilder.class);
        return builder.setPlayer(player).channel(ChatChannel.PARTY).rank().nick().arrow().messageColoured(message, ChatColor.WHITE).build();
    }

    public String staffChat(Player player, String message) {
        PlayerDisplayBuilder builder = injector.getInstance(PlayerDisplayBuilder.class);
        return builder.setPlayer(player).channel(ChatChannel.STAFF).nameColoured(ChatColor.LIGHT_PURPLE).arrow().messageColoured(message, ChatColor.GOLD).build();
    }

    public String directChatTo(Player recipient, String message) {
        PlayerDisplayBuilder builder = injector.getInstance(PlayerDisplayBuilder.class);
        return builder.setPlayer(recipient).to().rank().nick().star().arrow(ChatColor.YELLOW).messageColoured(message, ChatColor.YELLOW).build();
    }

    public String directChatFrom(Player sender, String message) {
        PlayerDisplayBuilder builder = injector.getInstance(PlayerDisplayBuilder.class);
        return builder.setPlayer(sender).rank().nick().star().from().arrow(ChatColor.YELLOW).messageColoured(message, ChatColor.YELLOW).build();
    }

    public String globalChatTagExample(Player player, String message, Tag tag) {
        PlayerDisplayBuilder builder = injector.getInstance(PlayerDisplayBuilder.class);
        return builder.setPlayer(player).setTag(tag.getDisplay()).tag().bonus().wolf().rank().nick().star().arrow().message(message).build();
    }

    public void sendGlobalMessage(Player player, String message) {
        String globalMessage = globalChat(player, message);
        MessageUtil.broadcast(getGlobalChatFromRecipients(player), globalMessage, false);
        MessageUtil.log("[" + player.getName() + "] " + globalMessage, false);
        Utils.sendDiscordChat(player, globalMessage);
    }

    public boolean chatToggled(Player player) {
        return toggleChats.contains(player.getUniqueId().toString());
    }

    public void sendDirectToChannel(Player sender, String message) {
        sendDirectChat(sender, getDirectChannelReceiver(sender), message);
    }

    public Player getDirectChannelReceiver(Player sender) {
        return Bukkit.getPlayer(UUID.fromString(directChannels.get(sender.getUniqueId().toString())));
    }

    public void sendDirectToLastMessaged(Player sender, String message) {
        sendDirectChat(sender, getLastMessaged(sender), message);
    }

    public List<Player> getNonIgnoringPlayers(Collection<? extends Player> players, Player player) {
        List<Player> list = new ArrayList<>();
        for(Player p : players) {
            if(!ignores(p, player)) {
                list.add(p);
            }
        }
        return list;
    }

    public List<Player> getGlobalChatFromRecipients(Player player) {
        List<Player> list = new ArrayList<>();
        for(Player p : getNonIgnoringPlayers(Bukkit.getOnlinePlayers(), player)) {
            if(!chatToggled(p)) {
                list.add(p);
            }
        }
        return list;
    }

    public List<Player> getPlayersWithPerm(String permission) {
        List<Player> list = new ArrayList<>();
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.hasPermission(permission)) {
                list.add(p);
            }
        }
        return list;
    }



    public String colourIfHasColour(String message, List<ChatColor> owned, String chatMods) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < message.length(); i++) {
            char curr = message.charAt(i);
            if(curr == '&' && i + 1 < message.length()) {
                char next = message.charAt(i + 1);
                ChatColor color = ChatColor.getByChar(next);
                if(color != null && owned.contains(color)) {
                    sb.append('§').append(next).append(chatMods);
                    i++;
                } else {
                    sb.append(curr);
                }
            } else {
                sb.append(curr);
            }
        }
        return sb.toString();
    }

    public RankDTO determineRank(String player) {
        RankDTO highest = ConfigManager.ranksDisplay.get("I");
        for(RankDTO rank : ConfigManager.ranksDisplay.values()) {
            if(rank.getPosition() > highest.getPosition()) {
                if(luckPermsManager.hasGroup(player, rank.getKey())) {
                    highest = rank;
                }
            }
        }

        return highest;
    }

    public WolfDTO determineWolf(String player) {
        WolfDTO highest = ConfigManager.wolfDisplay.get("wolf_i");
        for(WolfDTO wolf : ConfigManager.wolfDisplay.values()) {
            if(wolf.getPosition() > highest.getPosition()) {
                if(luckPermsManager.hasGroup(player, wolf.getKey())) {
                    highest = wolf;
                }
            }
        }

        return highest;
    }

    public BonusDTO determineBonus(String player) {
        BonusDTO highest = ConfigManager.bonusDisplay.get("prefix.1");
        for(BonusDTO bonus : ConfigManager.bonusDisplay.values()) {
            if(bonus.getPosition() > highest.getPosition()) {
                if(luckPermsManager.hasPermission(player, bonus.getKey())) {
                    highest = bonus;
                }
            }
        }

        return highest;
    }

    public MazeDTO determineMaze(String player) {
        MazeDTO highest = ConfigManager.mazeDisplay.get("maze.i");
        for(MazeDTO maze : ConfigManager.mazeDisplay.values()) {
            if(maze.getPosition() > highest.getPosition()) {
                if(luckPermsManager.hasPermission(player, maze.getKey())) {
                    highest = maze;
                }
            }
        }

        return highest;
    }

    public StarDTO determineStar(String player) {
        String starName = ColorHelper.getStar(player);
        StarDTO determinedStar = null;
        if(starName == null) {
            StarDTO highest = null;
            for(StarDTO star : ConfigManager.starDisplay.values()) {
                if(highest == null || star.getPosition() > highest.getPosition()) {
                    if(luckPermsManager.hasPermission(player, star.getKey())) {
                        highest = star;
                    }
                }
            }
            if(highest != null) {
                determinedStar = highest;
                ColorHelper.setStar(player, highest.getKey());
            }
        } else {
            determinedStar = ConfigManager.starDisplay.get(starName);
        }


        return determinedStar;
    }

    public StaffDTO determineStaff(String player) {
        StaffDTO highest = null;
        for(StaffDTO staff : ConfigManager.staffDisplay.values()) {
            if(highest == null || staff.getPosition() > highest.getPosition()) {
                if(luckPermsManager.hasGroup(player, staff.getKey())) {
                    highest = staff;
                }
            }
        }
        return highest;
    }

    public List<ColorHelper.ChatFormat> determineChatFormats(String player) {
        return ColorHelper.getChatFormats(player);
    }

    public ColorHelper.ChatColor determineChatColor(String player) {
        return ColorHelper.getChatColor(player);
    }

    public List<ColorHelper.ChatFormat> determineNameFormat(String player) {
        return ColorHelper.getNameFormats(player);
    }

    public List<ChatColor> determineManualChatColors(String player) {
        if(luckPermsManager.hasPermission(player, "essentials.chat.color")) {
            return new ArrayList<>(Arrays.asList(
                    ChatColor.BLACK,
                    ChatColor.DARK_BLUE,
                    ChatColor.DARK_GREEN,
                    ChatColor.DARK_AQUA,
                    ChatColor.DARK_RED,
                    ChatColor.DARK_PURPLE,
                    ChatColor.GOLD,
                    ChatColor.GRAY,
                    ChatColor.DARK_GRAY,
                    ChatColor.BLUE,
                    ChatColor.GREEN,
                    ChatColor.AQUA,
                    ChatColor.RED,
                    ChatColor.LIGHT_PURPLE,
                    ChatColor.YELLOW,
                    ChatColor.WHITE
            ));
        }
        return new ArrayList<>();
    }

    public List<ChatColor> determineManualChatFormats(String player) {
        if(luckPermsManager.hasPermission(player, "essentials.chat.magic")) {
            return new ArrayList<>(Arrays.asList(
                    ChatColor.MAGIC,
                    ChatColor.BOLD,
                    ChatColor.STRIKETHROUGH,
                    ChatColor.UNDERLINE,
                    ChatColor.ITALIC,
                    ChatColor.RESET
            ));
        }
        return new ArrayList<>();
    }

    public String removeChatColour(String string) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < string.length(); i++) {
            char curr = string.charAt(i);
            if(curr == '&') {
                if(i + 1 < string.length()) {
                    i++;
                }
            } else {
                sb.append(curr);
            }
        }
        return sb.toString();
    }

    public boolean isNextMessageGuiMessage(Player player) {
        return captureGuiMessages.get(player.getUniqueId().toString()) != null;
    }

    public void listenForGuiMessage(Player player, GuiMessage message, int delay) {
        MessageUtil.send(Message.CAPTURE_MSG, player);
        captureGuiMessages.put(player.getUniqueId().toString(), message);
        removeGuiMessageListenerAfterDelay(player, delay);
    }

    public void listenForGuiMessage(Player player, GuiMessage message) {
        listenForGuiMessage(player, message, 60);
    }

    public void sendMessageToGui(Player player, String string) {
        GuiMessage gui = captureGuiMessages.get(player.getUniqueId().toString());
        if(gui != null) {
            LCSound.SUCCESS.playLater(player);
            gui.getGui().onReceiveGuiMessage();
            gui.getCallback().accept(string);
        }
        captureGuiMessages.remove(player.getUniqueId().toString());
    }

    public void removeGuiMessageListenerAfterDelay(Player player, int seconds) {
        Bukkit.getScheduler().runTaskLater(Linkcraft.getPlugin(), () -> {
            if(isNextMessageGuiMessage(player)) {
                captureGuiMessages.remove(player.getUniqueId().toString());
                if (player.isOnline()) {
                    MessageUtil.send(Message.CAPTURE_TIMED_OUT, player);
                }
            }
        }, seconds * 20L);
    }
}
