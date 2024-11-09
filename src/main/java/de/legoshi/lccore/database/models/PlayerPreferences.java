package de.legoshi.lccore.database.models;

import de.legoshi.lccore.database.Identifiable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Cacheable
@Entity
@Table(name = "lc_player_preferences")
public class PlayerPreferences implements Identifiable<String> {
    @Id
    @Column(length = 36)
    private String id;

    @OneToOne
    @JoinColumn(name = "tag_id")
    private Tag tag = null;

    private String star = null;

    @Column(name = "chat_color")
    private String chatColor = null;

    @Column(name = "maze_display")
    private boolean mazeDisplay = true;

    @Column(name = "wolf_display")
    private boolean wolfDisplay = true;

    @Column(name = "bonus_display")
    private boolean bonusDisplay = true;

    @Column(name = "magic_chat_format")
    private boolean magicChatFormat = false;

    @Column(name = "bold_chat_format")
    private boolean boldChatFormat = false;

    @Column(name = "strike_chat_format")
    private boolean strikeChatFormat = false;

    @Column(name = "underline_chat_format")
    private boolean underlineChatFormat = false;

    @Column(name = "italic_chat_format")
    private boolean italicChatFormat = false;

    @Column(name = "bold_name_format")
    private boolean boldNameFormat = false;

    @Column(name = "strike_name_format")
    private boolean strikeNameFormat = false;

    @Column(name = "underline_name_format")
    private boolean underlineNameFormat = false;

    @Column(name = "italic_name_format")
    private boolean italicNameFormat = false;

    @Column(name = "practice_hotbar_position")
    private Integer practiceHotbarPosition = null;

    @Column(name = "old_practice")
    private boolean oldPractice = false;

    public PlayerPreferences(Player player) {
        this.id = player.getUniqueId().toString();
    }

    public PlayerPreferences(String player) {
        this.id = player;
    }

    public String getChatFormats() {
        StringBuilder sb = new StringBuilder();

        if(isMagicChatFormat()) {
            sb.append(ChatColor.MAGIC);
        }

        if(isBoldChatFormat()) {
            sb.append(ChatColor.BOLD);
        }

        if(isStrikeChatFormat()) {
            sb.append(ChatColor.STRIKETHROUGH);
        }

        if(isUnderlineChatFormat()) {
            sb.append(ChatColor.UNDERLINE);
        }

        if(isItalicChatFormat()) {
            sb.append(ChatColor.ITALIC);
        }

        return sb.toString();
    }


    public String getNameFormats() {
        StringBuilder sb = new StringBuilder();

        if(isBoldNameFormat()) {
            sb.append(ChatColor.BOLD);
        }

        if(isStrikeNameFormat()) {
            sb.append(ChatColor.STRIKETHROUGH);
        }

        if(isUnderlineNameFormat()) {
            sb.append(ChatColor.UNDERLINE);
        }

        if(isItalicNameFormat()) {
            sb.append(ChatColor.ITALIC);
        }

        return sb.toString();
    }
}
