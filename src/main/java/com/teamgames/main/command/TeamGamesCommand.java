package com.teamgames.main.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class TeamGamesCommand implements CommandExecutor {

    private JavaPlugin plugin;

    public TeamGamesCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("teamgames")) {
            if (args.length > 1 && args[0].equalsIgnoreCase("secret")) {
                return handleSecretCommand(sender, args[1]);
            } else {
                sender.sendMessage("Usage: /teamgames secret <key>");
            }
        }
        return true;
    }

    private boolean handleSecretCommand(CommandSender sender, String secretKey) {
        // Set the secret key in the plugin's configuration
        plugin.getConfig().set("store-secret-key", secretKey);
        plugin.saveConfig();  // Save the config to disk
        sender.sendMessage("Store secret key set successfully.");
        return true;
    }
}
