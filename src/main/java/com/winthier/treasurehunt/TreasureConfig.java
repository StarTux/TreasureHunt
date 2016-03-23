package com.winthier.treasurehunt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

@Getter
public class TreasureConfig {
    final TreasureHuntPlugin plugin;
    final List<Treasure> treasures = new ArrayList<>();

    TreasureConfig(TreasureHuntPlugin plugin) {
        this.plugin = plugin;
        File file = new File(plugin.getDataFolder(), "treasures.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key: config.getKeys(false)) {
            Treasure treasure = Treasure.load(key, config.getConfigurationSection(key));
            treasures.add(treasure);
        }
    }

    void addTreasure(Treasure treasure) {
        treasures.add(treasure);
    }

    void save() {
        YamlConfiguration config = new YamlConfiguration();
        for (Treasure treasure: treasures) {
            treasure.save(config);
        }
        File file = new File(plugin.getDataFolder(), "treasures.yml");
        try {
            config.save(file);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    Treasure treasureAt(Block block) {
        for (Treasure treasure: treasures) {
            if (treasure.isAt(block)) return treasure;
        }
        return null;
    }
}
