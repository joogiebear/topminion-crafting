# Build Instructions

## Prerequisites

1. **Java 17** or higher
   - Check: `java -version`
   - Download: https://adoptium.net/

2. **Maven** 3.6+
   - Check: `mvn -version`
   - Download: https://maven.apache.org/download.cgi

3. **TopMinion.jar** (v3.x)
   - Place in the `lib/` folder

## Quick Build

```bash
# 1. Place TopMinion.jar in lib/ folder
cp /path/to/TopMinion.jar lib/

# 2. Build the project
mvn clean package

# 3. Find the output
# The compiled JAR will be in: target/TopMinion-Crafting-1.0.0.jar
```

## Detailed Steps

### 1. Setup Dependencies

```bash
# Navigate to the project directory
cd TopMinion-Crafting

# Create lib directory if it doesn't exist
mkdir -p lib

# Copy TopMinion.jar to lib folder
cp /path/to/your/TopMinion.jar lib/
```

### 2. Verify Maven Setup

```bash
# Test Maven installation
mvn -version

# Should output something like:
# Apache Maven 3.x.x
# Java version: 17.x.x
```

### 3. Build the Plugin

```bash
# Clean and build
mvn clean package

# Or just build
mvn package
```

### 4. Locate Output

The compiled plugin will be at:
```
target/TopMinion-Crafting-1.0.0.jar
```

## Installation

1. Copy `target/TopMinion-Crafting-1.0.0.jar` to your server's `plugins/` folder
2. Ensure TopMinion v3 is installed
3. Restart your server
4. Configure recipes in `plugins/TopMinion-Crafting/config.yml`
5. Reload with `/topminioncrafting reload`

## Troubleshooting

### "TopMinion API not found"

Make sure `TopMinion.jar` is in the `lib/` folder.

### "Java version error"

This plugin requires Java 17+. Update your Java version.

### Maven not found

Install Maven from https://maven.apache.org/install.html

### Build successful but plugin won't load

- Check server version (requires 1.19+)
- Ensure TopMinion v3 is installed and enabled
- Check server console for errors

## Development

### Import into IDE

#### IntelliJ IDEA
1. File → Open → Select `TopMinion-Crafting` folder
2. IntelliJ will auto-detect Maven project
3. Wait for dependencies to download

#### Eclipse
1. File → Import → Maven → Existing Maven Projects
2. Browse to `TopMinion-Crafting` folder
3. Click Finish

#### VS Code
1. Open folder in VS Code
2. Install "Java Extension Pack"
3. Maven will auto-configure

### Testing Changes

```bash
# Build and copy to test server
mvn clean package && cp target/TopMinion-Crafting-1.0.0.jar /path/to/test/server/plugins/

# Or use a build script (see below)
```

### Build Script Example

Create `build.sh`:
```bash
#!/bin/bash
mvn clean package
cp target/TopMinion-Crafting-1.0.0.jar ~/servers/test/plugins/
echo "Plugin built and copied!"
```

Make executable: `chmod +x build.sh`

Run: `./build.sh`

## Clean Build

```bash
# Remove all build artifacts
mvn clean

# Remove target directory manually
rm -rf target/
```

## Advanced

### Change Version

Edit `pom.xml`:
```xml
<version>1.0.0</version>  <!-- Change this -->
```

### Add Dependencies

Edit `pom.xml` under `<dependencies>`:
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>example-lib</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

### Shading Dependencies

The plugin uses Maven Shade Plugin to include dependencies. Configure in `pom.xml` under `<build><plugins>`.

## Support

If you encounter build issues:
1. Check Java version: `java -version` (must be 17+)
2. Check Maven version: `mvn -version`
3. Verify TopMinion.jar is in `lib/` folder
4. Run `mvn clean` and try again
5. Check console output for specific errors

---

Happy building! 🚀
