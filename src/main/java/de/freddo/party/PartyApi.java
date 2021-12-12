package de.freddo.party;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PartyApi {

    private Party plugin;

    public PartyApi(Party plugin) {
        this.plugin = plugin;
    }

    public boolean IsInParty(Player player) {
        return plugin.partyData.containsKey(player.getUniqueId());
    }

    public void createParty(Player player) {
        plugin.partyData.put(player.getUniqueId(), new PartyManager());
    }

    public void disbandParty(Player player) {
        HashMap<UUID, Enums.Roles> members = plugin.partyData.get(player.getUniqueId()).getMemberList();
        while (members.entrySet().iterator().hasNext()) {
            UUID uuid = members.entrySet().iterator().next().getKey();
            plugin.partyData.remove(uuid);
        }
        plugin.partyData.remove(player.getUniqueId());
    }

    public void addMember(Player player, Player target) {
        plugin.partyData.get(player.getUniqueId()).getMemberList().put(target.getUniqueId(), Enums.Roles.Member);
        updateParty(player);
    }

    public void addMember(Player player, Player target, Enums.Roles roles) {
        plugin.partyData.get(player.getUniqueId()).getMemberList().put(target.getUniqueId(), roles);
        updateParty(player);
    }

    public void remMember(Player player, Player target) {
        plugin.partyData.get(player.getUniqueId()).getMemberList().remove(target.getUniqueId());
        plugin.partyData.remove(target.getUniqueId());
        updateParty(player);
    }

    public void sendPartyMessage(Player player, String string) {
        HashMap<UUID, Enums.Roles> members = plugin.partyData.get(player.getUniqueId()).getMemberList();
        while (members.entrySet().iterator().hasNext()) {
            UUID uuid = members.entrySet().iterator().next().getKey();
            Player loopPlayer = plugin.getServer().getPlayer(uuid);
            if (loopPlayer != null && loopPlayer.isOnline()) {
                loopPlayer.sendMessage(string);
            }
        }
    }

    public void remMember(Player player) {
        HashMap<UUID, Enums.Roles> members = plugin.partyData.get(player.getUniqueId()).getMemberList();
        while (members.entrySet().iterator().hasNext()) {
            UUID uuid = members.entrySet().iterator().next().getKey();
            plugin.partyData.get(uuid).getMemberList().remove(player.getUniqueId());
        }
        plugin.partyData.remove(player.getUniqueId());
    }

    public int sizeMembers(Player player) {
        return plugin.partyData.get(player.getUniqueId()).getMemberList().size();
    }

    public void addInvite(Player player, Player target) {
        plugin.partyData.get(player.getUniqueId()).getInviteList().add(target.getUniqueId());
        updateParty(player);
    }

    public void remInvite(Player player, Player target) {
        plugin.partyData.get(player.getUniqueId()).getMemberList().remove(target.getUniqueId());
        updateParty(player);
    }

    public boolean IsInInvite(Player player, Player target) {
        return plugin.partyData.get(player.getUniqueId()).getInviteList().contains(target.getUniqueId());
    }

    public void acceptInvite(Player player, Player target) {
        plugin.partyData.get(target.getUniqueId()).getMemberList().put(player.getUniqueId(), Enums.Roles.Member);
        plugin.partyData.get(target.getUniqueId()).getInviteList().remove(player.getUniqueId());
        plugin.partyData.put(player.getUniqueId(), new PartyManager());
        plugin.inviteTemp.remove(player.getUniqueId());
        updateParty(target);
    }

    public Enums.Roles getRole(Player player, Player target) {
        return plugin.partyData.get(player.getUniqueId()).getMemberList().get(target.getUniqueId());
    }

    public Enums.Roles getRole(Player player) {
        return plugin.partyData.get(player.getUniqueId()).getMemberList().get(player.getUniqueId());
    }

    public void setRole(Player player, Player target, Enums.Roles roles) {
        plugin.partyData.get(player.getUniqueId()).getMemberList().put(target.getUniqueId(), roles);
        updateParty(player);
    }

    public void setRole(Player player, Enums.Roles roles) {
        plugin.partyData.get(player.getUniqueId()).getMemberList().put(player.getUniqueId(), roles);
        updateParty(player);
    }

    private void updateParty(Player player) {
        PartyManager playerParty = plugin.partyData.get(player.getUniqueId());
        HashMap<UUID, Enums.Roles> members = playerParty.getMemberList();
        while (members.entrySet().iterator().hasNext()) {
            UUID uuid = members.entrySet().iterator().next().getKey();
            plugin.partyData.put(uuid, playerParty);
            //Update new infos to each Members like new Members, Invite.
        }

    }


}
