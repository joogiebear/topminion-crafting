package com.topminion.crafting.listeners;

import com.topminion.crafting.TopMinionCrafting;
import com.topminion.crafting.managers.RecipeManager;
import com.topminion.crafting.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

/**
 * Listens for craft events to replace placeholder items with actual TopMinion items
 */
public class CraftListener implements Listener {

    private final TopMinionCrafting plugin;
    private final ItemUtil itemUtil;

    public CraftListener(TopMinionCrafting plugin) {
        this.plugin = plugin;
        this.itemUtil = new ItemUtil(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Recipe recipe = event.getRecipe();

        // Get the recipe's NamespacedKey
        NamespacedKey recipeKey = null;
        try {
            // Try to get the key using reflection for compatibility across versions
            java.lang.reflect.Method getKeyMethod = recipe.getClass().getMethod("getKey");
            recipeKey = (NamespacedKey) getKeyMethod.invoke(recipe);
        } catch (Exception e) {
            plugin.debug("Could not get recipe key: " + e.getMessage());
            return;
        }

        if (recipeKey == null) return;

        RecipeManager recipeManager = plugin.getRecipeManager();

        // Check if it's a minion recipe
        if (recipeManager.isMinionRecipe(recipeKey)) {
            handleMinionCraft(event, player, recipeKey);
        }
        // Check if it's an upgrade recipe
        else if (recipeManager.isUpgradeRecipe(recipeKey)) {
            handleUpgradeCraft(event, player, recipeKey);
        }
    }

    /**
     * Handle crafting of a minion
     */
    private void handleMinionCraft(CraftItemEvent event, Player player, NamespacedKey recipeKey) {
        RecipeManager.MinionRecipeData data = plugin.getRecipeManager().getMinionRecipeData(recipeKey);
        if (data == null) {
            plugin.getLogger().warning("Minion recipe data not found for key: " + recipeKey);
            return;
        }

        // Check permission
        if (!player.hasPermission(data.getPermission())) {
            event.setCancelled(true);
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("settings.messages.no_permission")));
            return;
        }

        // Get the actual minion item from TopMinion API
        ItemStack minionItem = itemUtil.getMinionItem(player, data.getMinionId(), data.getLevel());

        if (minionItem == null) {
            event.setCancelled(true);
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("settings.prefix") + " &cError: Could not create minion item!"));
            plugin.getLogger().warning("Failed to create minion item for: " + data.getMinionId());
            return;
        }

        // Cancel the event and give the correct item
        event.setCancelled(true);

        // Schedule for next tick to avoid inventory issues
        Bukkit.getScheduler().runTask(plugin, () -> {
            // Remove ingredients from crafting matrix
            removeIngredients(event);

            // Give the minion item to the player
            player.getInventory().addItem(minionItem);

            // Send success message
            String message = plugin.getConfig().getString("settings.messages.minion_crafted", "&aYou crafted a minion!");
            message = message.replace("{minion}", data.getMinionId());
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("settings.prefix") + " " + message));

            plugin.debug("Player " + player.getName() + " crafted minion: " + data.getMinionId());
        });
    }

    /**
     * Handle crafting of an upgrade
     */
    private void handleUpgradeCraft(CraftItemEvent event, Player player, NamespacedKey recipeKey) {
        RecipeManager.UpgradeRecipeData data = plugin.getRecipeManager().getUpgradeRecipeData(recipeKey);
        if (data == null) {
            plugin.getLogger().warning("Upgrade recipe data not found for key: " + recipeKey);
            return;
        }

        // Check permission
        if (!player.hasPermission(data.getPermission())) {
            event.setCancelled(true);
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("settings.messages.no_permission")));
            return;
        }

        // Get the actual upgrade item from TopMinion API
        ItemStack upgradeItem = itemUtil.getUpgradeItem(data.getUpgradeId());

        if (upgradeItem == null) {
            event.setCancelled(true);
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("settings.prefix") + " &cError: Could not create upgrade item!"));
            plugin.getLogger().warning("Failed to create upgrade item for: " + data.getUpgradeId());
            return;
        }

        // Cancel the event and give the correct item
        event.setCancelled(true);

        // Schedule for next tick to avoid inventory issues
        Bukkit.getScheduler().runTask(plugin, () -> {
            // Remove ingredients from crafting matrix
            removeIngredients(event);

            // Give the upgrade item to the player
            player.getInventory().addItem(upgradeItem);

            // Send success message
            String message = plugin.getConfig().getString("settings.messages.upgrade_crafted", "&aYou crafted an upgrade!");
            message = message.replace("{upgrade}", data.getUpgradeId());
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("settings.prefix") + " " + message));

            plugin.debug("Player " + player.getName() + " crafted upgrade: " + data.getUpgradeId());
        });
    }

    /**
     * Remove ingredients from the crafting matrix
     */
    private void removeIngredients(CraftItemEvent event) {
        org.bukkit.inventory.CraftingInventory craftingInventory = event.getInventory();
        ItemStack[] matrix = craftingInventory.getMatrix();

        for (int i = 0; i < matrix.length; i++) {
            ItemStack item = matrix[i];
            if (item != null && !item.getType().isAir()) {
                item.setAmount(item.getAmount() - 1);
                matrix[i] = item;
            }
        }

        craftingInventory.setMatrix(matrix);
    }
}
