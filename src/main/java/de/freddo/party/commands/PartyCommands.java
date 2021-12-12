package de.freddo.party.commands;

import de.freddo.party.Enums;
import de.freddo.party.Party;
import de.freddo.party.PartyApi;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PartyCommands implements CommandExecutor, TabCompleter {

    private Party plugin;
    private PartyApi api;

    public PartyCommands(Party plugin) {
        this.plugin = plugin;
        this.api = plugin.api;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Common, get into game, console life.");
        }
        Player player = (Player) sender;
        if (args.length == 0 || args[0].equals("help")) {
            if (api.IsInParty(player)) {
                plugin.getServer().dispatchCommand(player, "party info");
                return false;
            }
            //help menu
            //create
            //invite
            //accept
            //leave
            //disband
            //chat
            //info
            return false;
        }
        if (args[0].equals("create")) {
            if (api.IsInParty(player)) {
                player.sendMessage("You are in a party.");
                return false;
            }
            api.createParty(player);
            api.setRole(player, Enums.Roles.Leader);
            player.sendMessage("Successfully created a party!");
            return false;
        }
        if (args[0].equals("invite")) {
            if (!api.IsInParty(player)) {
                player.sendMessage("Youre not in party");
                return false;
            }
            if (api.getRole(player).equals(Enums.Roles.Member)) {
                player.sendMessage("You can't invite, you need be officer or Leader.");
                return false;
            }
            if (args.length != 2) {
                player.sendMessage("Wrong commands?");
                return false;
            }
            Player target = plugin.getServer().getPlayer(args[1]);
            if (target == null || target.isOnline()) {
                player.sendMessage("Player is not online");
                return false;
            }
            if (api.IsInParty(target)) {
                player.sendMessage("Target is in party");
                return false;
            }
            if (api.IsInInvite(player, target)) {
                player.sendMessage("You already invited him.");
                return false;
            }
            player.sendMessage("you sent a invite");
            target.sendMessage("Player invited you to join party, ignore to decline. else accept/party accept");
            api.addInvite(player, target);
            plugin.inviteTemp.get(target.getUniqueId()).add(player.getUniqueId());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (plugin.inviteTemp.containsKey(target.getUniqueId())) {
                    api.remInvite(player,target);
                    if (plugin.inviteTemp.get(target.getUniqueId()).size() == 1) {
                        plugin.inviteTemp.remove(target.getUniqueId());
                    } else {
                        plugin.inviteTemp.get(target.getUniqueId()).remove(player.getUniqueId());
                    }
                    player.sendMessage("Target doesn't want to be in Party.");
                    target.sendMessage("You decline the invite of "+player.getName());
                }
            },60*20L);
            return false;
        }
        if (args[0].equals("accept")) {
            if (!plugin.inviteTemp.containsKey(player.getUniqueId()) || plugin.inviteTemp.get(player.getUniqueId()).size() == 0) {
                player.sendMessage("no one invited you :c");
                return false;
            }
            UUID uuid = null;
            if (plugin.inviteTemp.get(player.getUniqueId()).size() == 1) {
                uuid = plugin.inviteTemp.get(player.getUniqueId()).get(0);
            }
            if (uuid == null && args.length != 2) {
                player.sendMessage("You got multiple invite, so enter the target name please.");
                return false;
            } else if (uuid == null) {
                Player target = plugin.getServer().getPlayer(args[1]);
                if (target == null || !target.isOnline()) {
                    player.sendMessage("The player need be online.");
                    return false;
                }
                uuid = target.getUniqueId();
            }
            Player target = plugin.getServer().getPlayer(uuid);
            if (target == null) {
                player.sendMessage("Somethings fucked up. KEK.");
                return false;
            }
            api.acceptInvite(player, target);
        }
        if (args[0].equals("leave")) {
            if (!api.IsInParty(player)) {
                player.sendMessage("youre not in party anyways. huh.");
                return false;
            }
            if (api.getRole(player).equals(Enums.Roles.Leader)) {
                if (api.sizeMembers(player) != 1) {
                    player.sendMessage("Youre leader, and there is still some Members inside. use /party disband");
                    return false;
                }
            }
            api.sendPartyMessage(player, player.getName()+" has lefted the Party.");
            api.remMember(player);
            return false;
        }
        if (args[0].equals("disband")) {
            if (!api.IsInParty(player)) {
                player.sendMessage("Youre not in party anyways. huh.");
                return false;
            }
            if (!api.getRole(player).equals(Enums.Roles.Leader)) {
                player.sendMessage("If you want leave, then /party leave!");
                return false;
            }
            api.sendPartyMessage(player, player.getName()+" disbanded the Party.");
            api.disbandParty(player);
            return false;
        }
        if (args[0].equals("chat")) {
            if (!api.IsInParty(player)) {
                player.sendMessage("Youre not in party ugh.");
                return false;
            }
            api.sendPartyMessage(player, StringUtils.join(args, " ", 2, args.length));
            return false;
        }
        if (args[0].equals("info")) {
            HashMap<UUID, Enums.Roles> members = plugin.partyData.get(player.getUniqueId()).getMemberList();
            ArrayList<UUID> invites = plugin.partyData.get(player.getUniqueId()).getInviteList();
            ArrayList<Player> invitedConvert = new ArrayList<>();
            while (invites.iterator().hasNext()) {
                invitedConvert.add(plugin.getServer().getPlayer(invites.iterator().next()));
            }
            StringBuilder sb = new StringBuilder();
            while (members.entrySet().iterator().hasNext()) {
                sb.append(members.entrySet().iterator().next().getValue().toString()).append(" ").append(plugin.getServer().getPlayer(members.entrySet().iterator().next().getKey())).append(",");
            }
            player.sendMessage("In Party:");
            player.sendMessage(sb.toString());
            player.sendMessage("In invite: "+StringUtils.join(invitedConvert, ", "));

        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
