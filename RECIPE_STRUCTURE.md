# Recipe Structure Guide

## New Individual Recipe File System

Each recipe is now stored in its own file for easy management!

## Folder Structure

```
plugins/TopMinion-Crafting/
├── config.yml                    # Main settings only
├── MinionRecipes/                # Individual minion recipe files
│   ├── miner.yml
│   ├── farmer.yml
│   ├── fisher.yml
│   ├── lumberjack.yml
│   └── slayer.yml
└── UpgradeRecipes/               # Individual upgrade recipe files
    ├── fuel.yml
    ├── compactor.yml
    ├── smelter.yml
    └── diamond_spread.yml
```

## Benefits

✓ **Easy to manage** - Each recipe is its own file
✓ **Copy & paste** - Just copy a file to create a new recipe
✓ **No conflicts** - Edit one recipe without affecting others
✓ **Easy to disable** - Set `enabled: false` or delete the file
✓ **Clean structure** - Matches TopMinion's file organization

## Adding a New Recipe

### For a New Minion

1. Go to `plugins/TopMinion-Crafting/MinionRecipes/`
2. Copy an existing file (e.g., `miner.yml`)
3. Rename it (e.g., `my_custom_miner.yml`)
4. Edit the file:
   - Change `minion_id` to match your TopMinion config
   - Update `shape` and `ingredients`
   - Optionally change `permission`
5. Run `/topminioncrafting reload`

### For a New Upgrade

1. Go to `plugins/TopMinion-Crafting/UpgradeRecipes/`
2. Copy an existing file (e.g., `fuel.yml`)
3. Rename it (e.g., `my_custom_upgrade.yml`)
4. Edit the file:
   - Change `upgrade_id` to match your TopMinion config
   - Update `shape` and `ingredients`
   - Optionally change `permission`
5. Run `/topminioncrafting reload`

## Recipe File Format

### Minion Recipe Example

```yaml
# Miner Minion Recipe

enabled: true

# Must match minion_id in TopMinion configs
minion_id: "MINER"

minion_level: 1

recipe_type: "SHAPED"  # or "SHAPELESS"

# For SHAPED recipes
shape:
  - "CCC"
  - "CPC"
  - "CCC"

ingredients:
  C: COBBLESTONE
  P: DIAMOND_PICKAXE

# Optional - remove to use default permission
permission: "topminioncrafting.craft.minion"
```

### Upgrade Recipe Example

```yaml
# Fuel Upgrade Recipe

enabled: true

# Must match upgrade_id in TopMinion configs
upgrade_id: "fuel"

recipe_type: "SHAPED"

shape:
  - "CCC"
  - "CIC"
  - "CCC"

ingredients:
  C: COAL
  I: IRON_INGOT

# Optional - remove to use default permission
permission: "topminioncrafting.craft.upgrade"
```

### Shapeless Recipe Example

```yaml
# Example Shapeless Recipe

enabled: true

minion_id: "EXAMPLE"
minion_level: 1

recipe_type: "SHAPELESS"

# For SHAPELESS recipes, items can go anywhere
ingredients:
  - DIAMOND
  - EMERALD
  - GOLD_INGOT
```

## Disabling Recipes

### Option 1: Set enabled to false

Open the recipe file and change:
```yaml
enabled: false
```

Then run `/topminioncrafting reload`

### Option 2: Delete or rename the file

- Delete the `.yml` file completely
- Or rename it (e.g., `miner.yml.disabled`)

Then run `/topminioncrafting reload`

## Matching IDs with TopMinion

### Finding Minion IDs

1. Go to `plugins/TopMinion/MinionConfigs/[type]/`
2. Open the minion config file (e.g., `miner-config-example.yml`)
3. Look for the `minion_id:` line at the top
4. Copy that **exact** ID to your recipe file

Example:
```yaml
# In TopMinion/MinionConfigs/miner/miner-config-example.yml
minion_id: MINER

# Must match in TopMinion-Crafting/MinionRecipes/miner.yml
minion_id: "MINER"
```

### Finding Upgrade IDs

1. Go to `plugins/TopMinion/MinionUpgrades/[type]/`
2. Open the upgrade config file (e.g., `fuel-config-example.yml`)
3. Look for the `id:` line
4. Copy that **exact** ID to your recipe file

Example:
```yaml
# In TopMinion/MinionUpgrades/fuel/fuel-config-example.yml
id: fuel

# Must match in TopMinion-Crafting/UpgradeRecipes/fuel.yml
upgrade_id: "fuel"
```

## Tips

- **IDs are case-sensitive!** `MINER` ≠ `miner`
- Always run `/topminioncrafting reload` after changing files
- Enable `debug: true` in `config.yml` to see detailed loading messages
- File names don't matter - only the IDs inside do
- You can have multiple recipes for the same minion (different file names, same minion_id)
- SKIN upgrades cannot be crafted (automatically blocked)

## Permissions

Each recipe can have an optional `permission` field:

- **If specified**: Only players with that permission can craft
- **If omitted**: Uses default permissions:
  - Minions: `topminioncrafting.craft.minion` (everyone has this)
  - Upgrades: `topminioncrafting.craft.upgrade` (everyone has this)

Example VIP-only recipe:
```yaml
permission: "vip.craft.special"
```

## Need Help?

1. Check `config.yml` for tips
2. Open `GUIDE.html` in your browser for full documentation
3. Enable `debug: true` in config and check console
4. Make sure TopMinion v3 is installed and working

---

**Version**: 1.0.0
**Updated**: 2025-10-25
