package com.topminion.crafting.utils;

import com.topminion.crafting.TopMinionCrafting;
import com.sarry20.topminion.api.TopMinionProvider;
import com.sarry20.topminion.api.manager.ConfigMinionManager;
import com.sarry20.topminion.api.manager.ConfigUpgradeManager;
import com.sarry20.topminion.api.manager.MinionManager;
import com.sarry20.topminion.api.minion.Minion;
import com.sarry20.topminion.api.minion.config.ConfigMinion;
import com.sarry20.topminion.api.minion.upgrade.UpgradeType;
import com.sarry20.topminion.api.minion.upgrade.config.ConfigUpgrade;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Utility class for interacting with TopMinion API to get minion and upgrade items
 */
public class ItemUtil {

    private final TopMinionCrafting plugin;

    public ItemUtil(TopMinionCrafting plugin) {
        this.plugin = plugin;
    }

    /**
     * Get a minion item from TopMinion API
     *
     * NOTE: Current TopMinion v3 dev build has limited API
     * This method uses reflection as a workaround until full API is released
     *
     * @param player The player who will own the minion
     * @param minionId The ConfigMinion ID
     * @param level The minion level (usually 1 for crafting)
     * @return ItemStack representing the minion, or null if not found
     */
    public ItemStack getMinionItem(Player player, String minionId, int level) {
        try {
            // Try to get TopMinion plugin instance
            org.bukkit.plugin.Plugin topMinionPlugin = org.bukkit.Bukkit.getPluginManager().getPlugin("TopMinion");
            if (topMinionPlugin == null) {
                plugin.getLogger().severe("TopMinion plugin not found!");
                return null;
            }

            // Use reflection to access ConfigMinionManager
            java.lang.reflect.Field configManagerField = topMinionPlugin.getClass().getDeclaredField("configMinionManager");
            configManagerField.setAccessible(true);
            ConfigMinionManager configManager = (ConfigMinionManager) configManagerField.get(topMinionPlugin);

            if (configManager == null) {
                plugin.getLogger().warning("Could not access ConfigMinionManager");
                return null;
            }

            // Get the config minion
            ConfigMinion configMinion = configManager.getConfigMinion(minionId);

            if (configMinion == null) {
                plugin.getLogger().warning("ConfigMinion not found for ID: " + minionId);
                return null;
            }

            // Get the display item
            ItemStack item = configMinion.getDisplayItem();

            if (item == null) {
                plugin.getLogger().warning("Display item is null for minion: " + minionId);
                return null;
            }

            plugin.debug("Created minion item for: " + minionId + " level: " + level);
            return item.clone();

        } catch (Exception e) {
            plugin.getLogger().severe("Error getting minion item for " + minionId + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get an upgrade item from TopMinion API
     *
     * NOTE: Current TopMinion v3 dev build has limited API
     * This method uses reflection as a workaround until full API is released
     *
     * @param upgradeId The ConfigUpgrade ID
     * @return ItemStack representing the upgrade, or null if not found
     */
    public ItemStack getUpgradeItem(String upgradeId) {
        try {
            // Try to get TopMinion plugin instance
            org.bukkit.plugin.Plugin topMinionPlugin = org.bukkit.Bukkit.getPluginManager().getPlugin("TopMinion");
            if (topMinionPlugin == null) {
                plugin.getLogger().severe("TopMinion plugin not found!");
                return null;
            }

            // Use reflection to access ConfigUpgradeManager
            java.lang.reflect.Field upgradeManagerField = topMinionPlugin.getClass().getDeclaredField("configUpgradeManager");
            upgradeManagerField.setAccessible(true);
            ConfigUpgradeManager upgradeManager = (ConfigUpgradeManager) upgradeManagerField.get(topMinionPlugin);

            if (upgradeManager == null) {
                plugin.getLogger().warning("Could not access ConfigUpgradeManager");
                return null;
            }

            // Get the config upgrade
            ConfigUpgrade configUpgrade = upgradeManager.getConfigUpgrade(upgradeId);

            if (configUpgrade == null) {
                plugin.getLogger().warning("ConfigUpgrade not found for ID: " + upgradeId);
                return null;
            }

            // Check if it's a SKIN type (not craftable)
            if (configUpgrade.getType() == UpgradeType.SKIN) {
                plugin.getLogger().warning("Cannot craft SKIN type upgrades: " + upgradeId);
                return null;
            }

            // Get the display item
            ItemStack item = configUpgrade.getDisplayItem();

            if (item == null) {
                plugin.getLogger().warning("Display item is null for upgrade: " + upgradeId);
                return null;
            }

            plugin.debug("Created upgrade item for: " + upgradeId + " type: " + configUpgrade.getType());
            return item.clone();

        } catch (Exception e) {
            plugin.getLogger().severe("Error getting upgrade item for " + upgradeId + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Check if TopMinion API is available
     *
     * @return true if API is accessible
     */
    public boolean isTopMinionAvailable() {
        try {
            TopMinionProvider.get();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
