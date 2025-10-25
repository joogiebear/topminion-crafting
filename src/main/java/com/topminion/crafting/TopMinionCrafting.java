package com.topminion.crafting;

import com.topminion.crafting.listeners.CraftListener;
import com.topminion.crafting.managers.RecipeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class TopMinionCrafting extends JavaPlugin {

    private static TopMinionCrafting instance;
    private RecipeManager recipeManager;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config
        saveDefaultConfig();

        // Copy default recipe files if they don't exist
        copyDefaultRecipes();

        // Initialize managers
        recipeManager = new RecipeManager(this);

        // Register listeners
        Bukkit.getPluginManager().registerEvents(new CraftListener(this), this);

        // Load recipes
        recipeManager.loadRecipes();

        getLogger().info("TopMinion-Crafting has been enabled!");
        getLogger().info("Loaded " + recipeManager.getMinionRecipeCount() + " minion recipes");
        getLogger().info("Loaded " + recipeManager.getUpgradeRecipeCount() + " upgrade recipes");
    }

    /**
     * Copy default recipe files from resources to plugin data folder
     */
    private void copyDefaultRecipes() {
        // Minion recipes
        String[] minionRecipes = {"miner.yml", "farmer.yml", "fisher.yml", "lumberjack.yml", "slayer.yml"};
        for (String recipe : minionRecipes) {
            saveResource("MinionRecipes/" + recipe, false);
        }

        // Upgrade recipes
        String[] upgradeRecipes = {"fuel.yml", "compactor.yml", "smelter.yml", "diamond_spread.yml"};
        for (String recipe : upgradeRecipes) {
            saveResource("UpgradeRecipes/" + recipe, false);
        }

        debug("Default recipe files copied (if they didn't exist)");
    }

    @Override
    public void onDisable() {
        // Unregister all custom recipes
        if (recipeManager != null) {
            recipeManager.unregisterRecipes();
        }

        getLogger().info("TopMinion-Crafting has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("topminioncrafting")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("topminioncrafting.admin")) {
                    sender.sendMessage(colorize(getConfig().getString("settings.messages.no_permission")));
                    return true;
                }

                // Reload config
                reloadConfig();

                // Unregister old recipes
                recipeManager.unregisterRecipes();

                // Reload recipes
                recipeManager.loadRecipes();

                sender.sendMessage(colorize(getConfig().getString("settings.messages.reload_success")));
                return true;
            }
        }
        return false;
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    public static TopMinionCrafting getInstance() {
        return instance;
    }

    public String colorize(String message) {
        if (message == null) return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void debug(String message) {
        if (getConfig().getBoolean("settings.debug", false)) {
            getLogger().info("[DEBUG] " + message);
        }
    }
}
