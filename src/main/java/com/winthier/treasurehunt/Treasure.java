package com.winthier.treasurehunt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@Getter
class Treasure {
    final String name;
    final String world;
    final int x, y, z;
    final Set<UUID> found = new HashSet<>();

    Treasure(String name, Block block) {
        this(name, block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
    }

    static Treasure load(String name, ConfigurationSection config) {
        String world = config.getString("World");
        List<Integer> pos = config.getIntegerList("Position");
        Treasure result = new Treasure(name, world, pos.get(0), pos.get(1), pos.get(2));
        for (String found: config.getStringList("Found")) {
            UUID uuid = UUID.fromString(found);
            result.found.add(uuid);
        }
        return result;
    }

    ConfigurationSection save(ConfigurationSection config) {
        ConfigurationSection section = config.createSection(name);
        section.set("World", world);
        section.set("Position", Arrays.asList(x, y, z));
        List<String> foundList = new ArrayList<>();
        for (UUID uuid: found) foundList.add(uuid.toString());
        section.set("Found", foundList);
        return section;
    }

    boolean isAt(Block block) {
        if (!world.equals(block.getWorld().getName())) return false;
        return x == block.getX() &&
            z == block.getZ() &&
            y == block.getY();
    }

    boolean hasFound(Player player) {
        return found.contains(player.getUniqueId());
    }

    void setFound(Player player) {
        found.add(player.getUniqueId());
    }
}
