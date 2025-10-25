package com.topminion.crafting.managers;

import com.topminion.crafting.TopMinionCrafting;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.io.File;
import java.util.*;

/**
 * Manages all crafting recipes for minions and upgrades
 * Loads from individual recipe files in MinionRecipes/ and UpgradeRecipes/ folders
 */
public class RecipeManager {

    private final TopMinionCrafting plugin;
    private final Map<String, NamespacedKey> minionRecipeKeys;
    private final Map<String, NamespacedKey> upgradeRecipeKeys;
    private final Map<String, MinionRecipeData> minionRecipeData;
    private final Map<String, UpgradeRecipeData> upgradeRecipeData;

    public RecipeManager(TopMinionCrafting plugin) {
        this.plugin = plugin;
        this.minionRecipeKeys = new HashMap<>();
        this.upgradeRecipeKeys = new HashMap<>();
        this.minionRecipeData = new HashMap<>();
        this.upgradeRecipeData = new HashMap<>();
    }

    /**
     * Load all recipes from individual files
     */
    public void loadRecipes() {
        loadMinionRecipesFromFiles();
        loadUpgradeRecipesFromFiles();
    }

    /**
     * Load minion recipes from individual files in MinionRecipes/ folder
     */
    private void loadMinionRecipesFromFiles() {
        File minionRecipesFolder = new File(plugin.getDataFolder(), "MinionRecipes");

        // Create folder if it doesn't exist
        if (!minionRecipesFolder.exists()) {
            minionRecipesFolder.mkdirs();
            plugin.getLogger().info("Created MinionRecipes folder");
        }

        // Get all .yml files in the folder
        File[] recipeFiles = minionRecipesFolder.listFiles((dir, name) -> name.endsWith(".yml"));

        if (recipeFiles == null || recipeFiles.length == 0) {
            plugin.getLogger().warning("No minion recipe files found in MinionRecipes/ folder!");
            return;
        }

        int loaded = 0;
        for (File recipeFile : recipeFiles) {
            try {
                String fileName = recipeFile.getName().replace(".yml", "");
                FileConfiguration config = YamlConfiguration.loadConfiguration(recipeFile);

                if (!config.getBoolean("enabled", true)) {
                    plugin.debug("Skipping disabled minion recipe: " + fileName);
                    continue;
                }

                String minionId = config.getString("minion_id");
                int level = config.getInt("minion_level", 1);
                String recipeType = config.getString("recipe_type", "SHAPED");
                String permission = config.getString("permission", "topminioncrafting.craft.minion");

                if (minionId == null) {
                    plugin.getLogger().warning("Minion recipe " + fileName + " is missing minion_id!");
                    continue;
                }

                // Create placeholder item (actual item will be given in event listener)
                ItemStack result = new ItemStack(Material.PLAYER_HEAD);
                NamespacedKey recipeKey = new NamespacedKey(plugin, "minion_" + fileName);

                if (recipeType.equalsIgnoreCase("SHAPED")) {
                    ShapedRecipe shapedRecipe = new ShapedRecipe(recipeKey, result);

                    // Set shape
                    List<String> shape = config.getStringList("shape");
                    if (shape.size() != 3) {
                        plugin.getLogger().warning("Invalid shape in minion recipe " + fileName + " - must have 3 rows!");
                        continue;
                    }
                    shapedRecipe.shape(shape.toArray(new String[0]));

                    // Set ingredients
                    if (config.contains("ingredients")) {
                        for (String ingredientKey : config.getConfigurationSection("ingredients").getKeys(false)) {
                            String materialName = config.getString("ingredients." + ingredientKey);
                            Material material = Material.matchMaterial(materialName);
                            if (material != null) {
                                shapedRecipe.setIngredient(ingredientKey.charAt(0), material);
                            } else {
                                plugin.getLogger().warning("Invalid material in minion recipe " + fileName + ": " + materialName);
                            }
                        }
                    }

                    Bukkit.addRecipe(shapedRecipe);
                    minionRecipeKeys.put(fileName, recipeKey);
                    minionRecipeData.put(fileName, new MinionRecipeData(minionId, level, permission));
                    loaded++;

                    plugin.debug("Loaded SHAPED minion recipe: " + fileName + " for minion: " + minionId);

                } else if (recipeType.equalsIgnoreCase("SHAPELESS")) {
                    ShapelessRecipe shapelessRecipe = new ShapelessRecipe(recipeKey, result);

                    // Add ingredients
                    List<String> ingredients = config.getStringList("ingredients");
                    for (String materialName : ingredients) {
                        Material material = Material.matchMaterial(materialName);
                        if (material != null) {
                            shapelessRecipe.addIngredient(material);
                        } else {
                            plugin.getLogger().warning("Invalid material in minion recipe " + fileName + ": " + materialName);
                        }
                    }

                    Bukkit.addRecipe(shapelessRecipe);
                    minionRecipeKeys.put(fileName, recipeKey);
                    minionRecipeData.put(fileName, new MinionRecipeData(minionId, level, permission));
                    loaded++;

                    plugin.debug("Loaded SHAPELESS minion recipe: " + fileName + " for minion: " + minionId);
                }

            } catch (Exception e) {
                plugin.getLogger().severe("Error loading minion recipe from file: " + recipeFile.getName());
                e.printStackTrace();
            }
        }

        plugin.getLogger().info("Loaded " + loaded + " minion recipe(s) from " + recipeFiles.length + " file(s)");
    }

    /**
     * Load upgrade recipes from individual files in UpgradeRecipes/ folder
     */
    private void loadUpgradeRecipesFromFiles() {
        File upgradeRecipesFolder = new File(plugin.getDataFolder(), "UpgradeRecipes");

        // Create folder if it doesn't exist
        if (!upgradeRecipesFolder.exists()) {
            upgradeRecipesFolder.mkdirs();
            plugin.getLogger().info("Created UpgradeRecipes folder");
        }

        // Get all .yml files in the folder
        File[] recipeFiles = upgradeRecipesFolder.listFiles((dir, name) -> name.endsWith(".yml"));

        if (recipeFiles == null || recipeFiles.length == 0) {
            plugin.getLogger().warning("No upgrade recipe files found in UpgradeRecipes/ folder!");
            return;
        }

        int loaded = 0;
        for (File recipeFile : recipeFiles) {
            try {
                String fileName = recipeFile.getName().replace(".yml", "");
                FileConfiguration config = YamlConfiguration.loadConfiguration(recipeFile);

                if (!config.getBoolean("enabled", true)) {
                    plugin.debug("Skipping disabled upgrade recipe: " + fileName);
                    continue;
                }

                String upgradeId = config.getString("upgrade_id");
                String recipeType = config.getString("recipe_type", "SHAPED");
                String permission = config.getString("permission", "topminioncrafting.craft.upgrade");

                if (upgradeId == null) {
                    plugin.getLogger().warning("Upgrade recipe " + fileName + " is missing upgrade_id!");
                    continue;
                }

                // Create placeholder item (actual item will be given in event listener)
                ItemStack result = new ItemStack(Material.PAPER);
                NamespacedKey recipeKey = new NamespacedKey(plugin, "upgrade_" + fileName);

                if (recipeType.equalsIgnoreCase("SHAPED")) {
                    ShapedRecipe shapedRecipe = new ShapedRecipe(recipeKey, result);

                    // Set shape
                    List<String> shape = config.getStringList("shape");
                    if (shape.size() != 3) {
                        plugin.getLogger().warning("Invalid shape in upgrade recipe " + fileName + " - must have 3 rows!");
                        continue;
                    }
                    shapedRecipe.shape(shape.toArray(new String[0]));

                    // Set ingredients
                    if (config.contains("ingredients")) {
                        for (String ingredientKey : config.getConfigurationSection("ingredients").getKeys(false)) {
                            String materialName = config.getString("ingredients." + ingredientKey);
                            Material material = Material.matchMaterial(materialName);
                            if (material != null) {
                                shapedRecipe.setIngredient(ingredientKey.charAt(0), material);
                            } else {
                                plugin.getLogger().warning("Invalid material in upgrade recipe " + fileName + ": " + materialName);
                            }
                        }
                    }

                    Bukkit.addRecipe(shapedRecipe);
                    upgradeRecipeKeys.put(fileName, recipeKey);
                    upgradeRecipeData.put(fileName, new UpgradeRecipeData(upgradeId, permission));
                    loaded++;

                    plugin.debug("Loaded SHAPED upgrade recipe: " + fileName + " for upgrade: " + upgradeId);

                } else if (recipeType.equalsIgnoreCase("SHAPELESS")) {
                    ShapelessRecipe shapelessRecipe = new ShapelessRecipe(recipeKey, result);

                    // Add ingredients
                    List<String> ingredients = config.getStringList("ingredients");
                    for (String materialName : ingredients) {
                        Material material = Material.matchMaterial(materialName);
                        if (material != null) {
                            shapelessRecipe.addIngredient(material);
                        } else {
                            plugin.getLogger().warning("Invalid material in upgrade recipe " + fileName + ": " + materialName);
                        }
                    }

                    Bukkit.addRecipe(shapelessRecipe);
                    upgradeRecipeKeys.put(fileName, recipeKey);
                    upgradeRecipeData.put(fileName, new UpgradeRecipeData(upgradeId, permission));
                    loaded++;

                    plugin.debug("Loaded SHAPELESS upgrade recipe: " + fileName + " for upgrade: " + upgradeId);
                }

            } catch (Exception e) {
                plugin.getLogger().severe("Error loading upgrade recipe from file: " + recipeFile.getName());
                e.printStackTrace();
            }
        }

        plugin.getLogger().info("Loaded " + loaded + " upgrade recipe(s) from " + recipeFiles.length + " file(s)");
    }

    /**
     * Unregister all recipes
     */
    public void unregisterRecipes() {
        // Unregister minion recipes
        for (NamespacedKey key : minionRecipeKeys.values()) {
            Bukkit.removeRecipe(key);
        }
        minionRecipeKeys.clear();
        minionRecipeData.clear();

        // Unregister upgrade recipes
        for (NamespacedKey key : upgradeRecipeKeys.values()) {
            Bukkit.removeRecipe(key);
        }
        upgradeRecipeKeys.clear();
        upgradeRecipeData.clear();

        plugin.debug("Unregistered all recipes");
    }

    /**
     * Check if a recipe key is a registered minion recipe
     */
    public boolean isMinionRecipe(NamespacedKey key) {
        return minionRecipeKeys.containsValue(key);
    }

    /**
     * Check if a recipe key is a registered upgrade recipe
     */
    public boolean isUpgradeRecipe(NamespacedKey key) {
        return upgradeRecipeKeys.containsValue(key);
    }

    /**
     * Get minion recipe data by NamespacedKey
     */
    public MinionRecipeData getMinionRecipeData(NamespacedKey key) {
        for (Map.Entry<String, NamespacedKey> entry : minionRecipeKeys.entrySet()) {
            if (entry.getValue().equals(key)) {
                return minionRecipeData.get(entry.getKey());
            }
        }
        return null;
    }

    /**
     * Get upgrade recipe data by NamespacedKey
     */
    public UpgradeRecipeData getUpgradeRecipeData(NamespacedKey key) {
        for (Map.Entry<String, NamespacedKey> entry : upgradeRecipeKeys.entrySet()) {
            if (entry.getValue().equals(key)) {
                return upgradeRecipeData.get(entry.getKey());
            }
        }
        return null;
    }

    public int getMinionRecipeCount() {
        return minionRecipeKeys.size();
    }

    public int getUpgradeRecipeCount() {
        return upgradeRecipeKeys.size();
    }

    /**
     * Data class for minion recipe information
     */
    public static class MinionRecipeData {
        private final String minionId;
        private final int level;
        private final String permission;

        public MinionRecipeData(String minionId, int level, String permission) {
            this.minionId = minionId;
            this.level = level;
            this.permission = permission;
        }

        public String getMinionId() {
            return minionId;
        }

        public int getLevel() {
            return level;
        }

        public String getPermission() {
            return permission;
        }
    }

    /**
     * Data class for upgrade recipe information
     */
    public static class UpgradeRecipeData {
        private final String upgradeId;
        private final String permission;

        public UpgradeRecipeData(String upgradeId, String permission) {
            this.upgradeId = upgradeId;
            this.permission = permission;
        }

        public String getUpgradeId() {
            return upgradeId;
        }

        public String getPermission() {
            return permission;
        }
    }
}
