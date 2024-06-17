package com.teamgames.main;

import com.teamgames.main.command.ClaimCommand;
import com.teamgames.main.command.TeamGamesCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class TeamGamesInitializer extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("TeamGames Plugin Enabled!");
        this.getCommand("claim").setExecutor(new ClaimCommand(this));
        this.getCommand("teamgames").setExecutor(new TeamGamesCommand(this));
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        getLogger().info("TeamGames Plugin Disabled!");
    }

}
