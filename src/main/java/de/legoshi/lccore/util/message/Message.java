package de.legoshi.lccore.util.message;


import lombok.Getter;

public enum Message {

    // System
    PREFIX("§3§l[§b§lLC§3§l]§7 ", MessageType.SYSTEM),
    CRITICAL_ERROR("§cCRITICAL ERROR - could not load message", MessageType.SYSTEM),
    NOT_A_PLAYER("§cYou must be a player to execute this command", MessageType.SYSTEM),
    NO_PERMISSION("§cInsufficient permission.", MessageType.SYSTEM),
    INVALID_DOUBLE("§cArgument must be a number!", MessageType.SYSTEM),
    INVALID_INTEGER("§cArgument must be an integer!", MessageType.SYSTEM),
    ON_DISABLE_MESSAGE("§b§lLinkcraft§r§b is restarting...", MessageType.SYSTEM),
    ILLEGAL_CHARACTER("§bYou cannot use ·!", MessageType.SYSTEM),
    RELOAD_COMPLETE("§aReload complete!", MessageType.SYSTEM),
    GLOBAL_IS_MUTED("§cGlobal chat is currently muted", MessageType.SYSTEM),
    GLOBAL_MUTE("§aGlobal chat is now {0}!", MessageType.SYSTEM),

    // Logging
    CONFIG_LOADING_ERR("Error loading {0}", MessageType.LOGGING),
    DATABASE_CONNECTED("Database connected.", MessageType.LOGGING),
    DATABASE_FAILED("CRITICAL ERROR - Failed to connect to database. Shutting down server...", MessageType.LOGGING),
    SERVICE_STARTED("Started {0} service.", MessageType.LOGGING),
    SERVICE_STOPPED("Stopped {0} service.", MessageType.LOGGING),
    SERVICE_ERROR("Error encountered while starting {0} service.", MessageType.LOGGING),
    SPAWN_INITIALIZED("Spawn initialized.", MessageType.LOGGING),
    SPAWN_INITIALIZED_ERR("Spawn could not be initialized.", MessageType.LOGGING),

    // Player
    COMMAND_LIST_PAGE_HEADER("§0§m---------§r§2[§3Category {0}§2]§0§m---------", MessageType.PLAYER),
    COMMAND_SYNTAX("/{0} {1} {2} {3} {4} {5} {6} {7} {8}", MessageType.PLAYER),
    COMMAND_LIST_PAGE_FOOTER("§0§m--------------------------------", MessageType.PLAYER),
    PLAYER_JOIN("§8§l§m--* * *--§r §b§l{0} §3Has joined §8§l§m--* * *--", MessageType.PLAYER),
    PLAYER_LEAVE("§8§l§m--* * *--§r §4§l{0} §cHas left §8§l§m--* * *--", MessageType.PLAYER),
    JOIN_CHANNEL("§aYou are now in the §6{0}§a channel", MessageType.PLAYER),
    OPEN_DIRECT_CHANNEL("§aOpened a chat conversation with §r{0}§a. You can use §b/chat a §ato leave", MessageType.PLAYER),
    PARTY_INVITED("{0}§r§e invited {1}§r§e to the party! They have §c60§e seconds to accept.", MessageType.PLAYER),
    PARTY_PLAYER_JOIN("{0}§r§e joined the party.", MessageType.PLAYER),
    PARTY_JOIN("§eYou have joined §r{0}§r§e's party!", MessageType.PLAYER),
    PARTY_PLAYER_LEFT("{0}§r§e has left the party.", MessageType.PLAYER),
    PARTY_INACTIVE("{0}§r§e has been removed from the party due to inactivity.", MessageType.PLAYER),
    PARTY_LEFT("§eYou left the party.", MessageType.PLAYER),
    PARTY_INVITE("{0}§r§e has invited you to join their party!", MessageType.PLAYER),
    PARTY_KICK("{0}§r§e removed §r{1}§r§e from the party.", MessageType.PLAYER),
    PARTY_KICKED("§eYou have been kicked from the party by {0}", MessageType.PLAYER),
    IGNORE_PLAYER("§aYou are now ignoring {0}", MessageType.PLAYER),
    UNIGNORE_PLAYER("§aYou are no longer ignoring {0}", MessageType.PLAYER),
    CHAT_TOGGLED_OFF("§aGlobal chat has been toggled off for you!", MessageType.PLAYER),
    CHAT_TOGGLED_ON("§aGlobal chat is no longer toggled off for you!", MessageType.PLAYER),
    SET_STAR("§aYour star has been set to: §r{0}", MessageType.PLAYER),
    UNSET_STAR("§aYour star has been unset!", MessageType.PLAYER),
    CAPTURE_MSG("§aYour next message in chat will be captured!", MessageType.PLAYER),

    // Player Error
    USAGE("§cUsage: /{0} {1}", MessageType.PLAYER_ERROR),
    CMD_COOLDOWN_GLOBAL("§cPlease wait {0} second(s) before your next command.", MessageType.PLAYER_ERROR),
    CMD_COOLDOWN("§cPlease wait {0} second(s) before you can use this command again.", MessageType.PLAYER_ERROR),
    SPAWN_NOT_SET("§cA spawn location has not been set!", MessageType.PLAYER_ERROR),
    SPAWN_SET_ERR("§cCould not update the global spawn!", MessageType.PLAYER_ERROR),
    INVENTORY_FULL("§cCannot {0}, inventory full.", MessageType.PLAYER_ERROR),
    MUST_BE_ON_GROUND("§cYou must be on the ground to do this!", MessageType.PLAYER_ERROR),
    CAPTURE_TIMED_OUT("§cRan out of time, please try again", MessageType.PLAYER_ERROR),
    NEVER_JOINED("§c{0} has never joined the server.", MessageType.PLAYER_ERROR),
    OFFLINE_OR_NEVER("§c{0} is offline or has never joined.", MessageType.PLAYER_ERROR),
    PLAYER_NOT_EXIST("§cPlayer does not exist.", MessageType.PLAYER_ERROR),
    IS_OFFLINE("§c{0} is offline.", MessageType.PLAYER_ERROR),
    IS_OFFLINE_2("§cThat player recently went offline or no one has messaged you", MessageType.PLAYER_ERROR),
    INVALID_ARG("§cArgument {0} is invalid", MessageType.PLAYER_ERROR),
    IN_CHANNEL("§cYou are already in this channel!", MessageType.PLAYER_ERROR),
    PARTY_NO_INVITE_PERM("§cYou do not have permission to invite players to this party!", MessageType.PLAYER_ERROR),
    PARTY_ALREADY_IN("§c{0} is already in a party!", MessageType.PLAYER_ERROR),
    PARTY_INVITE_ATTEMPT("§c{0} tried to invite you to a party but you were already in one!", MessageType.PLAYER_ERROR),
    PARTY_PENDING_INVITE("§c{0} already has a pending invite from you!", MessageType.PLAYER_ERROR),
    PARTY_DOES_NOT_EXIST("§cThat party no longer exists!", MessageType.PLAYER_ERROR),
    PARTY_NOT_IN("§cYou are not in a party!", MessageType.PLAYER_ERROR),
    INVITE_EXPIRED_TO("§eThe party invite to {0}§r§e has expired.", MessageType.PLAYER_ERROR),
    INVITE_EXPIRED_FROM("§eThe party invite from {0}§r§e has expired.", MessageType.PLAYER_ERROR),
    PARTY_DISBANDED("§cThe party was disbanded due to all invites expiring and the party being empty.", MessageType.PLAYER_ERROR),
    ALREADY_IN_A_PARTY("§cYou are already in a party!", MessageType.PLAYER_ERROR),
    CHANGED_TO_ALL_CHANNEL("§cYou are not in a party and were moved to the ALL channel", MessageType.PLAYER_ERROR),
    PARTY_NO_KICK_PERM("§cYou do not have permission to kick players from this party!", MessageType.PLAYER_ERROR),
    PARTY_PLAYER_NOT_IN("§cThat player is not in your party!", MessageType.PLAYER_ERROR),
    IGNORE_SELF("§cYou cannot ignore yourself!", MessageType.PLAYER_ERROR),
    IGNORES_YOU("§cYou cannot message this player", MessageType.PLAYER_ERROR),
    INVALID_NICKNAME_FORMAT("§cInvalid nick, only a-z A-Z 0-9 and _'s are allowed!", MessageType.PLAYER_ERROR),
    INVALID_NICKNAME_LENGTH("§cInvalid nick, only up to 16 characters are allowed!", MessageType.PLAYER_ERROR),
    INVALID_NICKNAME_ALREADY_TAKEN("§cInvalid nick, someone who has played LC already has that name!", MessageType.PLAYER_ERROR),
    INVALID_NICKNAME_ALREADY_TAKEN_WARN("§cWARNING: someone already uses this nick. Using this nick may break things!", MessageType.PLAYER_ERROR),
    STAR_DOES_NOT_EXIST("§cThe star {0} does not exist!", MessageType.PLAYER_ERROR),

    // Practice
    PRAC_SUCCESS("§aYou are now in practice mode, do /unprac to stop.", MessageType.PRACTICE),
    PRAC_UNPRAC("§cYou have stopped practicing.", MessageType.PRACTICE),

    // Practice Errors
    PRAC_NOT_ALLOWED("§cYou cannot practice right now.", MessageType.PRACTICE_ERROR),
    PRAC_IN_AIR("§cYou must be on a block to use the practice system!", MessageType.PRACTICE_ERROR),
    PRAC_NOT_IN_PRAC("§cYou are not in practice mode.", MessageType.PRACTICE_ERROR),

    // Fun
    MESSAGE_GG("§6§lG§e§lG", MessageType.FUN),
    MESSAGE_GL("§2§lG§a§lL ☺", MessageType.FUN),
    MESSAGE_RIP("§0§lR§8§lI§f§lP ☹", MessageType.FUN),
    MESSAGE_HAM("§d§lha §cm", MessageType.FUN),
    MESSAGE_EGGS("§6§lE§e§lG§6§lg§e§lS", MessageType.FUN),
    MESSAGE_FAIL("§f§lFAIL", MessageType.FUN),
    FUN_RAGEQUIT("{0} {1}§r {2}§r§4§lhas ragequit!", MessageType.FUN),

    // Hide
    HIDE_ALL("§fYou are now §bhiding §fall players.", MessageType.HIDE),
    HIDE_ONE("§fYou are now §bhiding {0}. ", MessageType.HIDE),
    SHOW_ALL("§fYou are now §bshowing §fall players.", MessageType.HIDE),
    SHOW_ONE("§fYou are now §bshowing {0}. ", MessageType.HIDE),

    // Save
    SAVES_DELETE_SAVE("§aDeleted {0}'s save", MessageType.SAVE),

    // Save Errors
    SAVES_CANT_SAVE("§cYou cannot save right now, try unpracticing!", MessageType.SAVE_ERROR),

    // Checkpoint
    GET_CHECKPOINT("§aYou got the checkpoint!", MessageType.CHECKPOINT),

    // Checkpoint Errors
    MUST_BE_SIGN("§cYou must look at a sign to create a checkpoint!", MessageType.CHECKPOINT_ERROR),
    WRONG_CHECKPOINT_TYPE("§cPlease choose a valid checkpoint type!", MessageType.CHECKPOINT_ERROR),
    NO_CHECKPOINT_CAP("§cYou can not use checkpoints right now!", MessageType.CHECKPOINT_ERROR),
    NONEXISTENT_TEMPLATE("§cThat template does not exist!", MessageType.CHECKPOINT_ERROR),
    NO_WORLD("§cInvalid world on checkpoint, please notify an admin!", MessageType.CHECKPOINT_ERROR),
    NO_CHECKPOINT("§cYou do not have a current checkpoint!", MessageType.CHECKPOINT_ERROR),

    // Nickname
    NICK_SET_TO("§aYour nickname is now §r{0}", MessageType.NICKNAME),
    NICK_OFF("§aYou no longer have a nickname.", MessageType.NICKNAME),
    NICK_CHANGE_OTHER("§aNickname changed.", MessageType.NICKNAME),
    NICK_REALNAME("{0} §6is§r {1}", MessageType.NICKNAME),

    // Tag
    TAGS_DELETE_TAG("§aSuccessfully deleted tag: §r{0} §r({1}/{2})", MessageType.TAGS),
    TAGS_ADD_SUCCESS("§aSuccessfully added tag §r({0})§a: §r{2}", MessageType.TAGS),
    TAGS_SELECT("§aYour tag has been set to: §r{0}", MessageType.TAGS),
    TAGS_SELECT_OTHER("§a{0}'s tag has been set to: §r{1}", MessageType.TAGS),
    TAGS_UNLOCKED_TAG("{0}§r §7unlocked, check out §6/tags§r!", MessageType.TAGS),
    TAGS_GAVE_TAG("§aSuccessfully gave {0} tag: §r{1} §r({2})", MessageType.TAGS),
    TAGS_SIGN_CREATED("§aTag sign for §r{0}§a created!", MessageType.TAGS),
    TAGS_REMOVE_TAG("§aSuccessfully removed {0}'s tag: §r{1}", MessageType.TAGS),
    TAGS_UNSET_TAG("§aYour tag has been unset", MessageType.TAGS),

    // Tag Errors
    TAGS_INVALID_RARITY("§cInvalid tag rarity!", MessageType.TAGS_ERROR),
    TAGS_INVALID_TYPE("§cInvalid tag type!", MessageType.TAGS_ERROR),
    TAGS_INVALID_PAGE("§cInvalid page number!", MessageType.TAGS_ERROR),
    TAGS_REMOVE_ERROR("§cError when deleting tag.", MessageType.TAGS_ERROR),
    TAGS_HASNT_UNLOCKED("§cPlayer does not have a tag with the id {0}", MessageType.TAGS_ERROR),
    TAGS_HASNT_UNLOCKED_SELF("§cYou do not own the tag: §r{0}", MessageType.TAGS_ERROR),
    TAGS_HASNT_UNLOCKED_OTHER("§c{0} has not unlocked the tag: §r{1}", MessageType.TAGS_ERROR),
    TAGS_HAS_TAG("§c{0} already has the tag with id {1}.", MessageType.TAGS_ERROR),
    TAGS_ALREADY_HAVE("§cYou already have the tag: §r{0}", MessageType.TAGS_ERROR),
    TAGS_ALREADY_HAVE_OTHER("§c{0} already has the tag: §r{1} §r({2})", MessageType.TAGS_ERROR),
    TAGS_HAS_TAG_SET("§cYou already have that tag selected!", MessageType.TAGS_ERROR),
    TAGS_HAS_TAG_SET_OTHER("§c{0} already has that tag selected!", MessageType.TAGS_ERROR),
    TAGS_NOT_UNLOCKED("§cYou have not unlocked §r{0}", MessageType.TAGS_ERROR),
    TAGS_REMOVE_TAG_ERROR("§cCouldn't remove {0}'s tag: §r{1}", MessageType.TAGS_ERROR),
    TAGS_SELECT_OTHER_ERROR("§c{0}'s tag couldn't be set to: §r{1}", MessageType.TAGS_ERROR),
    TAGS_UNSET_TAG_ERROR("§cYour tag couldn't be unset", MessageType.TAGS_ERROR),
    TAGS_UNSET_NONE("§cYou have no tag equipped!", MessageType.TAGS_ERROR),
    TAGS_ADD_ERROR("§cCouldn't add tag.", MessageType.TAGS_ERROR),
    TAGS_NO_TAG("§cThe tag with id or name {0} doesn't exist.", MessageType.TAGS_ERROR),
    TAGS_MUST_BE_SIGN("§cYou must look at a sign to create a tag sign!", MessageType.TAGS_ERROR),
    TAGS_CANT_BE_IN_PRAC("§cYou cant unlock tags in practice mode!", MessageType.TAGS_ERROR),
    TAGS_ALREADY_EXISTS("§cThere is already a tag with the name: {0}", MessageType.TAGS_ERROR),
    TAGS_MUST_HAVE_NAME("§cThe tag must have a name set!", MessageType.TAGS_ERROR),
    TAGS_MUST_HAVE_DISPLAY("§cThe tag must have a display set!", MessageType.TAGS_ERROR),
    TAGS_CANT_EDIT_NAME("§cHi staff. To unfortunately to edit the name, tags need to be deleted and remade (DB primary key). I was too lazy to automate this niche feature. sorry -dk", MessageType.TAGS_ERROR),
    TAGS_UNCOMMON_TAG_BLOCK("§cYou just made ({0}, {1}, {2}, {3}) a tag sign, did you mean to do that?", MessageType.TAGS_ERROR),

    // Punishment
    PUNISH_FINISHED("§aYou no longer have a {0}!", MessageType.PUNISHMENT),
    PUNISH_REMOVE_OTHER("§6{0} §ais no longer &b{1}!", MessageType.PUNISHMENT),
    PUNISH_REMOVED("§aYou have been {0}!", MessageType.PUNISHMENT),
    PUNISH_UNTIL_SILENT("§f[Silent] §6{0} &fhas been &c{1} &funtil &b{2} &ffor &c{3}!", MessageType.PUNISHMENT),
    PUNISH_UNTIL("§6{0} &fhas been &c{1} &funtil &b{2} &ffor &c{3}!", MessageType.PUNISHMENT),
    PUNISH_YOU_ARE_MUTED("§cYou are muted!", MessageType.PUNISHMENT),
    PUNISH_YOU_ARE_FULL_MUTED("§cYou are full muted!", MessageType.PUNISHMENT),

    // Punishment Errors
    PUNISH_CRITICAL_ERROR("§cCRITICAL ERROR: {0} has more than one currently active {1}???", MessageType.PUNISHMENT_ERROR),
    PUNISH_WAS_NOT_PUNISHED("§c{0} was not {1}!", MessageType.PUNISHMENT_ERROR),
    PUNISH_WRONG_LENGTH("§cThe length '{0}' is incorrectly formatted! (ex: 30d/1y/...)!", MessageType.PUNISHMENT_ERROR),
    PUNISH_ALREADY_PUNISHED("§c{0} is already {1} until {2}!", MessageType.PUNISHMENT_ERROR),


    FILLER("", MessageType.SYSTEM);

    @Getter public final String message;
    public final MessageType type;

    Message(String message, MessageType type) {
        this.message = message;
        this.type = type;
    }

    public String lower() { return name().toLowerCase(); }
}
