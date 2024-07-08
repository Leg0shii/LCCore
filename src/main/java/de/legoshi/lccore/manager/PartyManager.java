package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.player.PlayerPartyResult;
import de.legoshi.lccore.player.chat.PartyMember;
import de.legoshi.lccore.player.chat.PartyRole;
import de.legoshi.lccore.util.GUIUtil;
import de.legoshi.lccore.util.PlayerDisplayBuilder;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.util.*;

public class PartyManager {
    @Inject private Injector injector;
    @Inject private ChatManager chatManager;

    private final Map<String, String> playersToParty = new HashMap<>();
    private final Map<String, HashMap<String, PartyMember>> partyToPlayers = new HashMap<>();
    private final Map<String, HashSet<String>> invites = new HashMap<>();
    private final Map<String, BukkitTask> inactivityTimers = new HashMap<>();

    public void startInactivityTimer(Player player) {
        BukkitTask task = Linkcraft.asyncLater(() -> {
            removeDueToInactivity(player);
        }, 20L * (60L * 30L));

        inactivityTimers.put(player.getUniqueId().toString(), task);
    }

    public void cancelInactivityTimer(Player player) {
        BukkitTask task = inactivityTimers.get(player.getUniqueId().toString());
        if(task != null) {
            task.cancel();
        }
        inactivityTimers.remove(player.getUniqueId().toString());
    }

    public void inviteToParty(Player player, Player invitee) {
        if(isInParty(invitee)) {
            MessageUtil.send(Message.PARTY_ALREADY_IN, player, invitee.getName());
            MessageUtil.send(Message.PARTY_INVITE_ATTEMPT, invitee, player.getName());
            return;
        }

        if(!isInParty(player)) {
            createParty(player);
        }

        sendInvite(player, invitee);
    }

    public void sendInvite(Player inviter, Player invitee) {
        if(!canManageParty(inviter)) {
            MessageUtil.send(Message.PARTY_NO_INVITE_PERM, inviter);
            return;
        }

        if(hasInviteToParty(inviter, invitee)) {
            MessageUtil.send(Message.PARTY_PENDING_INVITE, inviter, invitee.getName());
            return;
        }

        if(chatManager.ignores(invitee, inviter)) {
            MessageUtil.send(Message.IGNORES_YOU, inviter);
            return;
        }

        String partyId = getPartyId(inviter);
        addToInviteList(partyId, invitee);

        Linkcraft.syncLater(() -> {
            if(isInInviteList(partyId, invitee)) {
                removeFromInviteList(partyId, invitee);
                broadcastToParty(partyId, MessageUtil.compose(Message.INVITE_EXPIRED_TO, true, chatManager.rankNickStar(invitee)));
                MessageUtil.send(Message.INVITE_EXPIRED_FROM, invitee, chatManager.rankNickStar(inviter));
                disbandIfEmpty(partyId);
            }
        }, 20L * 60L);


        broadcastToParty(partyId, MessageUtil.compose(Message.PARTY_INVITED, true, chatManager.rankNickStar(inviter), chatManager.rankNickStar(invitee)));


        // TODO: this is gross
        MessageUtil.send(Message.PARTY_INVITE, invitee, chatManager.rankNickStar(inviter));
        TextComponent comp = MessageUtil.createClickable(ChatColor.GOLD + "Click here to join!", "/party accept " + inviter.getName(), "/party accept " + inviter.getName());
        invitee.spigot().sendMessage(MessageUtil.combineBaseAndComponent(ChatColor.YELLOW + "You have " + ChatColor.RED + "60" + ChatColor.YELLOW + " seconds to accept. ", comp));
    }

    public void broadcastToParty(String partyId, String message) {
        MessageUtil.broadcast(getOnlinePartyMembers(partyId), message, false);
    }

    public void broadcastToParty(String partyId, String message, Player sender) {
        HashSet<Player> partyMembers = getOnlinePartyMembers(partyId);
        MessageUtil.broadcast(chatManager.getNonIgnoringPlayers(getOnlinePartyMembers(partyId), sender), message, false);

        StringBuilder sb = new StringBuilder();
        sb.append("[").append(sender.getName()).append("] ");
        sb.append("[");
        List<Player> orderedPartyMembers = new ArrayList<>(partyMembers);
        int partySize = orderedPartyMembers.size();
        for (int i = 0; i < partySize; i++) {
            Player p = orderedPartyMembers.get(i);
            sb.append(p.getName());
            if (i < partySize - 1) {
                sb.append(", ");
            }
        }

        sb.append("] ").append(message);
        MessageUtil.log(sb.toString(), false);
    }

    public boolean isInInviteList(String partyId, Player invitee) {
        HashSet<String> inviteList = invites.get(partyId);
        return inviteList.contains(invitee.getUniqueId().toString());
    }

    public void disbandIfEmpty(String partyId) {
        HashSet<PlayerPartyResult> members = getPartyMembers(partyId);
        if((members.isEmpty() || members.size() == 1) && inviteListIsEmpty(partyId)) {
            partyToPlayers.remove(partyId);
            for(PlayerPartyResult member : members) {
                playersToParty.remove(member.getId());
                if(member.isOnline()) {
                    MessageUtil.send(Message.PARTY_DISBANDED, member.getPlayer());
                }
            }
        }
    }

    public void promoteNewOwner(String partyId) {
        HashSet<PlayerPartyResult> members = getPartyMembers(partyId);

        if(members.stream().anyMatch(member -> getPartyMember(member.getId()).getRole().equals(PartyRole.OWNER))) {
            return;
        }

        PlayerPartyResult highestOnline = null;
        PlayerPartyResult result;
        int onlineWeight = 0;
        for(PlayerPartyResult member : members) {
            if(member.isOnline()) {
                PartyMember partyMember = getPartyMember(member.getId());
                PartyRole memberRole = partyMember.getRole();
                int memberWeight = memberRole.weight;
                if (memberWeight > onlineWeight) {
                    onlineWeight = memberWeight;
                    highestOnline = member;
                }
            }
        }

        if(highestOnline != null) {
           result = highestOnline;
        } else {
            PlayerPartyResult highestOffline = null;
            int offlineWeight = 0;
            for(PlayerPartyResult member : members) {
                if(member.isOnline()) {
                    PartyMember partyMember = getPartyMember(member.getId());
                    PartyRole memberRole = partyMember.getRole();
                    int memberWeight = memberRole.weight;
                    if (memberWeight > offlineWeight) {
                        offlineWeight = memberWeight;
                        highestOffline = member;
                    }
                }
            }
            result = highestOffline;
        }

        if(result != null) {
            changeRole(result.getId(), PartyRole.OWNER);
            broadcastToParty(getPartyId(result.getId()), MessageUtil.compose(Message.PARTY_TRANSFERRED_AUTO, true, chatManager.rankNickStar(result.getId())));
        }
    }

    public boolean inviteListIsEmpty(String partyId) {
        HashSet<String> inviteList = invites.get(partyId);
        return inviteList == null || inviteList.isEmpty();
    }

    public void removeFromInviteList(String partyId, Player invitee) {
        HashSet<String> inviteList = invites.get(partyId);
        if(inviteList != null) {
            inviteList.remove(invitee.getUniqueId().toString());
            invites.put(partyId, inviteList);
        }
    }

    public void addToInviteList(String partyId, Player invitee) {
        HashSet<String> inviteList = invites.get(partyId);
        if(inviteList == null) {
            inviteList = new HashSet<>();
        }

        inviteList.add(invitee.getUniqueId().toString());
        invites.put(partyId, inviteList);
    }

    public void revokeInvite(Player inviter, Player invitee) {
        String partyId = getPartyId(inviter);
        HashSet<String> inviteList = invites.get(partyId);
        if(inviteList != null) {
            inviteList.remove(invitee.getUniqueId().toString());
            invites.put(partyId, inviteList);
        }
    }

    public void removeFromParty(Player player) {
        String uuid = player.getUniqueId().toString();
        String partyId = getPartyId(player);
        if(partyId == null) {
            MessageUtil.send(Message.PARTY_NOT_IN, player);
            return;
        }


        HashMap<String, PartyMember> party = partyToPlayers.get(partyId);
        playersToParty.remove(uuid);

        if(party == null) {
            return;
        }

        party.remove(uuid);
        partyToPlayers.put(partyId, party);
        broadcastToParty(partyId, MessageUtil.compose(Message.PARTY_PLAYER_LEFT, true, chatManager.rankNickStar(player)));
        MessageUtil.send(Message.PARTY_LEFT, player);
        disbandIfEmpty(partyId);
        promoteNewOwner(partyId);
    }

    public HashMap<String, PartyMember> getParty(String partyId) {
        return partyToPlayers.get(partyId);
    }

    public void updateParty(String partyId, HashMap<String, PartyMember> party) {
        partyToPlayers.put(partyId, party);
    }

    public void removeDueToInactivity(Player player) {
        String uuid = player.getUniqueId().toString();
        String partyId = getPartyId(player);
        if(partyId == null) {
            return;
        }

        playersToParty.remove(uuid);
        HashMap<String, PartyMember> party = partyToPlayers.get(partyId);
        if(party == null) {
            return;
        }

        party.remove(uuid);
        partyToPlayers.put(partyId, party);
        broadcastToParty(partyId, MessageUtil.compose(Message.PARTY_INACTIVE, true, chatManager.rankNickStar(player)));
        disbandIfEmpty(partyId);
    }

    public void kickFromParty(Player kicker, String kickeeID) {
        String partyId = getPartyId(kicker);

        if(partyId == null) {
            MessageUtil.send(Message.PARTY_NOT_IN, kicker);
            return;
        }

        if(!isInParty(kickeeID, partyId)) {
            MessageUtil.send(Message.PARTY_PLAYER_NOT_IN, kicker);
            return;
        }

        if(!isHigherRank(kicker, kickeeID)) {
            MessageUtil.send(Message.PARTY_NO_KICK_PERM, kicker);
            return;
        }

        HashMap<String, PartyMember> party = partyToPlayers.get(partyId);
        if(party == null) {
            return;
        }

        Player kickee = Bukkit.getPlayer(UUID.fromString(kickeeID));

        party.remove(kickeeID);
        partyToPlayers.put(partyId, party);
        playersToParty.remove(kickeeID);

        if(kickee != null) {
            MessageUtil.send(Message.PARTY_KICKED, kickee, chatManager.rankNickStar(kicker));
        }
        broadcastToParty(partyId, MessageUtil.compose(Message.PARTY_KICK, true, chatManager.rankNickStar(kicker), chatManager.rankNickStar(kickeeID)));
        disbandIfEmpty(partyId);
    }

    public void acceptInvite(Player invitee, Player inviter) {
        String partyId = getPartyId(inviter);
        if(!hasInviteToParty(inviter, invitee)) {
            MessageUtil.send(Message.PARTY_DOES_NOT_EXIST, invitee);
            return;
        }

        if(isInParty(invitee)) {
            MessageUtil.send(Message.ALREADY_IN_A_PARTY, invitee);
            return;
        }

        revokeInvite(inviter, invitee);
        broadcastToParty(partyId, MessageUtil.compose(Message.PARTY_PLAYER_JOIN, true, chatManager.rankNickStar(invitee)));
        addPlayerToParty(partyId, invitee);
        MessageUtil.send(Message.PARTY_JOIN, invitee, chatManager.rankNickStar(inviter));
    }

    public void addPlayerToParty(String partyId, Player player) {
        String uuid = player.getUniqueId().toString();
        playersToParty.put(player.getUniqueId().toString(), partyId);
        HashMap<String, PartyMember> party = partyToPlayers.get(partyId);

        if(party == null) {
            party = new HashMap<>();
        }

        party.put(uuid, new PartyMember(uuid, PartyRole.GUEST));
        partyToPlayers.put(partyId, party);
    }

    public boolean hasInviteToParty(Player inviter, Player invitee) {
        HashSet<String> inviteList = invites.get(getPartyId(inviter));
        return inviteList != null && inviteList.contains(invitee.getUniqueId().toString());
    }

    public void createParty(Player player) {
        String uuid = player.getUniqueId().toString();
        String partyId = generatePartyId();
        HashMap<String, PartyMember> partyMembers = new HashMap<>();
        partyMembers.put(uuid, new PartyMember(uuid, PartyRole.OWNER));
        playersToParty.put(uuid, partyId);
        partyToPlayers.put(partyId, partyMembers);
    }

    public String generatePartyId() {
        return UUID.randomUUID().toString();
    }

    public boolean isPartyCreator(Player player) {
        PartyMember member = getPartyMember(player);
        if(member == null) {
            return false;
        }

        return member.getRole().equals(PartyRole.OWNER);
    }

    public boolean isHigherRank(Player player, String member) {
        PartyMember playerRole = getPartyMember(player);
        PartyMember memberRole = getPartyMember(member);

        return playerRole != null && memberRole != null && playerRole.getRole().weight > memberRole.getRole().weight;
    }

    public boolean canManageParty(Player player) {
        PartyMember member = getPartyMember(player);
        if(member == null) {
            return false;
        }

        return member.getRole().equals(PartyRole.MODERATOR) || member.getRole().equals(PartyRole.OWNER);
    }

    public PartyMember getPartyMember(Player player) {
        String partyId = getPartyId(player);
        if(partyId == null) {
            return null;
        }

        HashMap<String, PartyMember> party = partyToPlayers.get(partyId);

        if(party == null) {
            return null;
        }

        return party.get(player.getUniqueId().toString());
    }

    public PartyMember getPartyMember(String player) {
        String partyId = getPartyId(player);
        if(partyId == null) {
            return null;
        }

        HashMap<String, PartyMember> party = partyToPlayers.get(partyId);

        if(party == null) {
            return null;
        }

        return party.get(player);
    }

    public void changeRole(String member, PartyRole role) {
        if(isInParty(member)) {
            HashMap<String, PartyMember> party = getParty(getPartyId(member));
            if(party.get(member) != null) {
                party.put(member, new PartyMember(member, role));
            }
        }
    }

    public void promote(Player player, String member) {
        if(!isInParty(player)) {
            MessageUtil.send(Message.PARTY_NOT_IN, player);
            return;
        }

        if(!isInSameParty(player, member)) {
            MessageUtil.send(Message.PARTY_PLAYER_NOT_IN, player);
            return;
        }

        PartyMember promoter = getPartyMember(player);
        PartyMember promotee = getPartyMember(member);
        PartyRole promoterRole = promoter.getRole();
        PartyRole promoteeRole = promotee.getRole();
        if(promoterRole.equals(PartyRole.OWNER) && promoteeRole.equals(PartyRole.MODERATOR)) {
            changeRole(player.getUniqueId().toString(), PartyRole.MODERATOR);
            changeRole(member, PartyRole.OWNER);
            broadcastToParty(getPartyId(player), MessageUtil.compose(Message.PARTY_TRANSFERRED, true, chatManager.rankNickStar(player), chatManager.rankNickStar(member)));
        } else if(promoterRole.equals(PartyRole.OWNER) && promoteeRole.equals(PartyRole.GUEST)) {
            changeRole(member, PartyRole.MODERATOR);
            broadcastToParty(getPartyId(player), MessageUtil.compose(Message.PARTY_PROMOTED, true, chatManager.rankNickStar(player), chatManager.rankNickStar(member), GUIUtil.capitalize(PartyRole.MODERATOR.name())));
        } else {
            MessageUtil.send(Message.PARTY_CAN_NOT_PROMOTE, player, GUIUtil.capitalize(promoterRole.name()), GUIUtil.capitalize(promoteeRole.name()));
        }
    }

    public boolean isHigherRank(PartyMember p, PartyMember p2) {
        return p.getRole().weight > p2.getRole().weight;
    }

    public boolean canPromote(PartyMember p, PartyMember p2) {
        return p.getRole().weight > p2.getRole().weight + 1;
    }

    public boolean isInParty(Player player) {
        return getPartyId(player) != null;
    }

    public boolean isInParty(String player) {
        return getPartyId(player) != null;
    }

    public boolean isInSameParty(Player member, String toCheck) {
        return getPartyId(member).equals(getPartyId(toCheck));
    }

    public boolean isInParty(String playerId, String partyId) {
        String playerPartyId = getPartyId(playerId);
        return playerPartyId != null && playerPartyId.equals(partyId);
    }

    public String getPartyId(Player player) {
        return playersToParty.get(player.getUniqueId().toString());
    }

    public String getPartyId(String uuid) { return playersToParty.get(uuid); }

    public HashSet<PlayerPartyResult> getPartyMembers(Player player) {
        return getPartyMembers(getPartyId(player));
    }


    public HashSet<Player> getOnlinePartyMembers(String partyId) {
        HashMap<String, PartyMember> partyMembers = partyToPlayers.get(partyId);
        HashSet<Player> partyPlayers = new HashSet<>();
        if(partyMembers == null) {
            return partyPlayers;
        }

        for(PartyMember member : partyMembers.values()) {
            Player player = Bukkit.getPlayer(UUID.fromString(member.getPlayer()));
            if(player != null && player.isOnline()) {
                partyPlayers.add(player);
            }
        }

        return partyPlayers;
    }

    public HashSet<PlayerPartyResult> getPartyMembers(String partyId) {
        HashMap<String, PartyMember> partyMembers = partyToPlayers.get(partyId);
        HashSet<PlayerPartyResult> partyPlayers = new HashSet<>();
        if(partyMembers == null) {
            return partyPlayers;
        }

        for(PartyMember member : partyMembers.values()) {
            Player player = Bukkit.getPlayer(UUID.fromString(member.getPlayer()));
            if(player != null && player.isOnline()) {
                partyPlayers.add(new PlayerPartyResult(player));
            } else {
                partyPlayers.add(new PlayerPartyResult(member.getPlayer()));
            }
        }

        return partyPlayers;
    }

    public void printPartyMemberListing(Player player) {
        String partyId = getPartyId(player);
        if(partyId == null) {
            MessageUtil.send(Message.PARTY_NOT_IN, player);
            return;
        }
        printPartyMemberListing(getPartyId(player), player);
    }

    public void printPartyMemberListing(String partyId, Player player) {
        HashMap<String, PartyMember> partyMembers = partyToPlayers.get(partyId);
        PlayerDisplayBuilder playerDisplayBuilder = injector.getInstance(PlayerDisplayBuilder.class);

        for(PartyMember member : partyMembers.values()) {
            Player p = Bukkit.getPlayer(UUID.fromString(member.getPlayer()));
            if(p != null && p.isOnline()) {
                playerDisplayBuilder.setPlayer(p).raw(member.getRole().prefix).nick().raw(ChatColor.GREEN + "●");
            } else {
                playerDisplayBuilder.setPlayer(member.getPlayer()).raw(member.getRole().prefix).nick().raw(ChatColor.RED + "●");
            }
        }
        MessageUtil.send(playerDisplayBuilder.build(), player, true);
    }
}
