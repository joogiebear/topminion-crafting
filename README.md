# TopMinion-Crafting

An addon for **TopMinion v3** that adds crafting recipes for level 1 minions and upgrades.

## Features

- **Craft Level 1 Minions**: Create custom crafting recipes for all minion types (MINER, FARMER, FISHER, LUMBERJACK, SLAYER)
- **Craft Upgrades**: Create custom crafting recipes for upgrades (FUEL, UPGRADE, AUTO_SELL)
- **Excludes Skins**: SKIN type upgrades are not craftable (as intended)
- **Fully Configurable**: Define custom shaped and shapeless recipes in `config.yml`
- **Permission-Based**: Control who can craft minions and upgrades
- **API Integration**: Uses TopMinion API to get proper minion and upgrade items

## Requirements

- **Minecraft**: 1.19+
- **Java**: 17+
- **TopMinion**: v3.x (Required dependency)

## Installation

1. Download or compile the plugin JAR
2. Place `TopMinion-Crafting.jar` in your server's `plugins/` folder
3. Ensure **TopMinion** is installed and enabled
4. Restart or reload your server
5. Configure recipes in `plugins/TopMinion-Crafting/config.yml`

## Building from Source

### Prerequisites

- **Java 17** or higher
- **Maven** 3.6+
- **TopMinion.jar** (place in `lib/` folder)

### Build Steps

1. Clone or download this repository
2. Place `TopMinion.jar` in the `lib/` directory
3. Run Maven:
   ```bash
   mvn clean package
   ```
4. Find the compiled JAR in the `target/` folder

## Configuration

### Recipe Structure

#### Minion Recipes

```yaml
minion_recipes:
  recipe_name:
    enabled: true
    minion_id: "MINER"  # ConfigMinion ID from TopMinion
    minion_level: 1
    recipe_type: "SHAPED"  # or "SHAPELESS"
    shape:
      - "CCC"
      - "CPC"
      - "CCC"
    ingredients:
      C: COBBLESTONE
      P: DIAMOND_PICKAXE
    permission: "topminioncrafting.craft.minion"
```

#### Upgrade Recipes

```yaml
upgrade_recipes:
  recipe_name:
    enabled: true
    upgrade_id: "fuel"  # ConfigUpgrade ID from TopMinion
    recipe_type: "SHAPED"  # or "SHAPELESS"
    shape:
      - "CCC"
      - "CIC"
      - "CCC"
    ingredients:
      C: COAL
      I: IRON_INGOT
    permission: "topminioncrafting.craft.upgrade"
```

### Recipe Types

- **SHAPED**: Requires items in specific positions (like a vanilla crafting recipe)
- **SHAPELESS**: Items can be placed anywhere in the crafting grid

### Important Notes

1. **minion_id** must match the ConfigMinion ID in TopMinion's `MinionConfigs/` folder
2. **upgrade_id** must match the ConfigUpgrade ID in TopMinion's `MinionUpgrades/` folder
3. SKIN type upgrades cannot be crafted (automatically excluded)
4. Recipes use placeholder items (PLAYER_HEAD for minions, PAPER for upgrades) that are replaced with the actual TopMinion items when crafted

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/topminioncrafting reload` | Reload configuration and recipes | `topminioncrafting.admin` |
| `/tmc reload` | Alias for reload command | `topminioncrafting.admin` |

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `topminioncrafting.admin` | Access to admin commands | op |
| `topminioncrafting.craft.minion` | Ability to craft minions | true |
| `topminioncrafting.craft.upgrade` | Ability to craft upgrades | true |

## Example Recipes

The default `config.yml` includes example recipes for:

### Minions
- **Miner Minion**: 8 Cobblestone + 1 Diamond Pickaxe
- **Farmer Minion**: 8 Wheat + 1 Diamond Hoe
- **Fisher Minion**: 8 Cod + 1 Fishing Rod
- **Lumberjack Minion**: 8 Oak Logs + 1 Diamond Axe
- **Slayer Minion**: 8 Rotten Flesh + 1 Diamond Sword

### Upgrades
- **Coal Fuel**: 8 Coal + 1 Iron Ingot
- **Compactor**: 3 Iron Ingots + 1 Hopper + 1 Piston + 1 Redstone
- **Smelter**: 8 Iron Ingots + 1 Furnace
- **Diamond Spread**: 8 Diamonds + 1 Emerald

## Troubleshooting

### Recipes Not Working

1. Check that TopMinion is installed and enabled
2. Verify that the `minion_id` or `upgrade_id` matches exactly with TopMinion configs
3. Enable debug mode in config (`settings.debug: true`) and check console
4. Ensure materials in recipes are valid Bukkit materials

### Items Not Appearing

1. Make sure the player has the required permission
2. Check that the ConfigMinion/ConfigUpgrade exists in TopMinion
3. Verify TopMinion API is accessible (`/plugins` should show TopMinion as green)

### Recipe Conflicts

If recipes conflict with other plugins, you can:
- Change the ingredients in `config.yml`
- Disable specific recipes by setting `enabled: false`
- Use more unique crafting patterns

## API Usage

This plugin uses the TopMinion API:

```java
// Get API
TopMinionAPI api = TopMinionProvider.get();

// Get Managers
ConfigMinionManager minionManager = api.getConfigMinionManager();
ConfigUpgradeManager upgradeManager = api.getConfigUpgradeManager();

// Get Items
ConfigMinion configMinion = minionManager.getConfigMinion("MINER");
ItemStack minionItem = configMinion.getDisplayItem();

ConfigUpgrade configUpgrade = upgradeManager.getConfigUpgrade("fuel");
ItemStack upgradeItem = configUpgrade.getDisplayItem();
```

## Project Structure

```
TopMinion-Crafting/
├── src/main/java/com/topminion/crafting/
│   ├── TopMinionCrafting.java          # Main plugin class
│   ├── listeners/
│   │   └── CraftListener.java          # Handles craft events
│   ├── managers/
│   │   └── RecipeManager.java          # Manages recipes
│   └── utils/
│       └── ItemUtil.java               # TopMinion API utilities
├── src/main/resources/
│   ├── plugin.yml                      # Plugin metadata
│   └── config.yml                      # Recipe configuration
├── lib/                                # Dependencies (place TopMinion.jar here)
├── pom.xml                             # Maven build file
└── README.md                           # This file
```

## Support

For issues or questions:
1. Check the TopMinion API documentation
2. Verify your TopMinion config files
3. Enable debug mode and check console logs

## License

This is an addon for TopMinion. Make sure you have the rights to use TopMinion before using this addon.

## Credits

- **TopMinion**: Original plugin by dejvokep
- **TopMinion-Crafting**: Addon created for crafting functionality

---

**Version**: 1.0.0
**Last Updated**: 2025-10-25
