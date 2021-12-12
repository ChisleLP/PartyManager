package de.freddo.party;

import de.freddo.party.commands.PartyCommands;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class Party extends JavaPlugin {

    public HashMap<UUID, PartyManager> partyData;
    public HashMap<UUID, ArrayList<UUID>> inviteTemp;

    public PartyApi api;


    @Override
    public void onEnable() {

        api = new PartyApi(this);

        partyData = new HashMap<>();
        inviteTemp = new HashMap<>();

        getCommand("party").setExecutor(new PartyCommands(this));
        getCommand("party").setTabCompleter(new PartyCommands(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
