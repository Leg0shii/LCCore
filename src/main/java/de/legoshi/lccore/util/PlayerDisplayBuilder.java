package de.legoshi.lccore.util;

import de.legoshi.lccore.database.models.PlayerPreferences;
import de.legoshi.lccore.database.models.Tag;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.player.chat.ChatChannel;
import de.legoshi.lccore.player.display.*;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.ArrayList;
import java.util.List;

public class PlayerDisplayBuilder {
    @Inject private PlayerManager playerManager;
    @Inject private ChatManager chatManager;

    private Player player = null;
    private String playerId = null;
    private LCPlayer lcPlayer;
    private PlayerPreferences playerPreferences;

    @Accessors(chain = true) @Setter private String tag = null;
    @Accessors(chain = true) @Setter private String nickname = null;
    @Accessors(chain = true) @Setter private RankDTO rankDTO = null;
    @Accessors(chain = true) @Setter private WolfDTO wolfDTO = null;
    @Accessors(chain = true) @Setter private BonusDTO bonusDTO = null;
    @Accessors(chain = true) @Setter private StarDTO starDTO = null;
    @Accessors(chain = true) @Setter private MazeDTO mazeDTO = null;
    @Accessors(chain = true) @Setter private String baseChatColour = null;
    @Accessors(chain = true) @Setter private String chatModifiers = null;
    @Accessors(chain = true) @Setter private String nameModifiers = null;


    private ArrayList<DisplayComponent> components = new ArrayList<>();

    public PlayerDisplayBuilder setPlayer(Player player) {
        this.player = player;
        this.lcPlayer = playerManager.getPlayer(player);
        this.playerPreferences = playerManager.getPlayerPrefs(player);
        init();
        return this;
    }

    public PlayerDisplayBuilder setPlayer(String playerId) {
        this.playerId = playerId;
        this.lcPlayer = playerManager.loadPlayer(playerId);
        this.playerPreferences = playerManager.getPlayerPrefs(player);
        init();
        return this;
    }

    public void init() {
        Tag tag = player != null ? playerManager.getPlayerPrefs(player.getUniqueId().toString()).getTag() : playerManager.getPlayerPrefs(playerId).getTag();
        this.tag = tag != null ? tag.getDisplay() : "";
        this.rankDTO = lcPlayer.getRank();
        this.bonusDTO = lcPlayer.getBonus();
        this.starDTO = lcPlayer.getStar();
        this.wolfDTO = lcPlayer.getWolf();
        this.mazeDTO = lcPlayer.getMaze();
        this.baseChatColour = lcPlayer.getChatColor() != null ? ChatColor.getByChar(lcPlayer.getChatColor().getCode()) + "" : "";
        this.nickname = lcPlayer.getNick();
        loadChatFormats(lcPlayer);
        loadNameModifiers(lcPlayer);
    }

    private void loadChatFormats(LCPlayer lcPlayer) {
        StringBuilder sb = new StringBuilder();
        for(ColorHelper.ChatFormat format : lcPlayer.getChatFormats()) {
            sb.append(ChatColor.getByChar(format.getCode()));
        }
        this.chatModifiers = sb.toString();
    }

    private void loadNameModifiers(LCPlayer lcPlayer) {
        StringBuilder sb = new StringBuilder();
        for(ColorHelper.ChatFormat format : lcPlayer.getNameFormats()) {
            sb.append(ChatColor.getByChar(format.getCode()));
        }
        this.nameModifiers = sb.toString();
    }

    private String getNickOrDefault() {
        return !nickname.isEmpty() ? nickname : lcPlayer.getName();
    }

    private ChatColor getRankColour() { return ChatColor.getByChar(rankDTO.getColorCode()); }

    public PlayerDisplayBuilder name() {
        String name = getRankColour() + "" + nameModifiers + lcPlayer.getName(); //chatManager.carryOver(lcPlayer.getName(), playerUnlockables, nameModifiers);
        components.add(new DisplayComponent(name));
        return this;
    }

    public PlayerDisplayBuilder nameRaw() {
        components.add(new DisplayComponent(lcPlayer.getName()));
        return this;
    }

    public PlayerDisplayBuilder nameColoured(ChatColor color) {
        components.add(new DisplayComponent(color + lcPlayer.getName()));
        return this;
    }

    public PlayerDisplayBuilder nick() {
        String nick = getRankColour() + "" + nameModifiers + getNickOrDefault();
        components.add(new DisplayComponent(nick));
        return this;
    }

    public PlayerDisplayBuilder tag() {
        if(tag == null || tag.isEmpty()) {
            return this;
        }
        components.add(new DisplayComponent(tag));
        return this;
    }

    public PlayerDisplayBuilder rank() {
        if(rankDTO == null) {
            return this;
        }

        components.add(new DisplayComponent(rankDTO.getDisplay()));
        return this;
    }

    public PlayerDisplayBuilder bonus() {
        if(!playerPreferences.isBonusDisplay()) {
            return this;
        }

        if(bonusDTO == null) {
            return this;
        }

        components.add(new DisplayComponent(bonusDTO.getDisplay()));
        return this;
    }

    public PlayerDisplayBuilder maze() {
        if(mazeDTO == null) {
            return this;
        }

        components.add(new DisplayComponent(mazeDTO.getTabDisplay()));
        return this;
    }

    public PlayerDisplayBuilder star() {
        if(starDTO == null) {
            return this;
        }

        components.add(new DisplayComponent(starDTO.getDisplay()));
        return this;
    }

    public PlayerDisplayBuilder wolf() {
        if(!playerPreferences.isWolfDisplay()) {
            return this;
        }

        if(wolfDTO == null) {
            return this;
        }

        components.add(new DisplayComponent(wolfDTO.getDisplay()));
        return this;
    }

    public PlayerDisplayBuilder arrow(ChatColor color) {
        components.add(new DisplayComponent(color + "»"));
        return this;
    }

    public PlayerDisplayBuilder arrow() {
        return arrow(ChatColor.GRAY);
    }

    public PlayerDisplayBuilder arrowSingle(ChatColor color) {
        components.add(new DisplayComponent(color + ">"));
        return this;
    }

    public PlayerDisplayBuilder arrowSingle() {
        return arrowSingle(ChatColor.DARK_GRAY);
    }


    public PlayerDisplayBuilder channel(ChatChannel channel) {
        components.add(new DisplayComponent(channel.prefix));
        return arrowSingle();
    }

    public PlayerDisplayBuilder message(String text) {
        components.add(new DisplayComponent(baseChatColour + chatModifiers + chatManager.colourIfHasColour(text, getManualColors(), chatModifiers)));
        return this;
    }

    public PlayerDisplayBuilder message() {
        return message("msg");
    }

    public PlayerDisplayBuilder messageRaw(String text) {
        components.add(new DisplayComponent(text));
        return this;
    }

    public PlayerDisplayBuilder from() {
        components.add(new DisplayComponent(ChatColor.GOLD + "-> me"));
        return this;
    }

    public PlayerDisplayBuilder to() {
        components.add(new DisplayComponent(ChatColor.GOLD + "me ->"));
        return this;
    }

    public PlayerDisplayBuilder toFromArrow() {
        components.add(new DisplayComponent(ChatColor.GOLD + "->"));
        return this;
    }

    public PlayerDisplayBuilder raw(String text) {
        components.add(new DisplayComponent(text));
        return this;
    }

    public String build() {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < components.size(); i++) {
            sb.append(components.get(i).getDisplay()).append(ChatColor.RESET);

            if(i < components.size() - 1) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    public PlayerDisplayBuilder messageColoured(String text, ChatColor color) {
        components.add(new DisplayComponent(color + text));
        return this;
    }

    private List<ChatColor> getManualColors() {
        List<ChatColor> colors = new ArrayList<>();
        colors.addAll(lcPlayer.getManualChatColors());
        colors.addAll(lcPlayer.getManualChatFormats());
        return colors;
    }
}
