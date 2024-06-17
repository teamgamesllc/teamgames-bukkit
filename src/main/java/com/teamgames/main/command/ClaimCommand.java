package com.teamgames.main.command;

import com.teamgames.endpoints.store.Transaction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class ClaimCommand implements CommandExecutor {

    private JavaPlugin plugin;

    // Constructor that accepts the main plugin instance
    public ClaimCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("claim")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                handleClaim(player);
            } else {
                sender.sendMessage("This command can only be used by a player.");
            }
            return true;
        }
        return false;
    }

    private void handleClaim(Player player) {
        String playerName = player.getName();
        String apiKey = plugin.getConfig().getString("store-secret-key", "default-key");  // Fetch the API key each time a claim is handled

        // Asynchronously fetch the transactions
        Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                Transaction[] transactions = new Transaction().setApiKey(apiKey).setPlayerName(playerName).getTransactions();

                // Handle the transactions on the main server thread
                Bukkit.getServer().getScheduler().runTask(this.plugin, () -> {

                    // Check if transactions is null or empty first
                    if (transactions == null || transactions.length == 0) {
                        player.sendMessage("You currently don't have any items waiting. You must donate first!");
                        return;
                    }

                    // Then check for an error message in the first transaction
                    if (transactions.length == 1 && transactions[0].message != null) {
                        player.sendMessage(transactions[0].message);
                        return;
                    }

                    // Process each transaction and add items to the player's inventory
                    for (Transaction transaction : transactions) {
                        String productName = transaction.product_id_string;
                        int productAmount = transaction.product_amount;

                        String name = getString(transaction, productName);


                        // Create an ItemStack for the item
                        Material material = Material.matchMaterial(name.toUpperCase());

                        if (material != null) {
                            // Create an ItemStack for the item
                            ItemStack itemStack = new ItemStack(material, productAmount);

                            // Get the player's inventory
                            PlayerInventory inventory = player.getInventory();

                            // Add the item to the player's inventory
                            inventory.addItem(itemStack);
                        } else {
                            // If the material does not exist, send an error message
                            player.sendMessage("Item " + productName + " not found!");
                        }
                        // Simulate adding item to the inventory
                    }

                    player.sendMessage("Thank you for your support!");
                });
            } catch (Exception e) {
                Bukkit.getServer().getScheduler().runTask(this.plugin, () -> {
                    player.sendMessage("API Services are currently offline. Please check back shortly");
                });
                e.printStackTrace();
            }
        });
    }

    private static String getString(Transaction transaction, String productName) {
        String[] parts = transaction.product_id_string.split(":");
        String name = "";
        if (parts.length == 1) {
            // If there's no colon, the full name is just the product name without a namespace
            name = parts[0];
            System.out.println("1: " + name);
        } else if (parts.length > 1) {
            // If there's a colon, we assume the format "namespace:item_name"
            String namespace = parts[0];
            name = parts[1];
            System.out.println("2: " + name);
        } else {
            // If somehow the product_name is empty or split() fails, revert to the original product name
            name = productName;
            System.out.println("3: " + name);
        }
        return name;
    }
}