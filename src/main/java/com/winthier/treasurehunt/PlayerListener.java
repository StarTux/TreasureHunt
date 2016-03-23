package com.winthier.treasurehunt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
@Getter
public class PlayerListener implements Listener {
    final TreasureHuntPlugin plugin;
    final Map<UUID, String> adminCreateMap = new HashMap<>();
    final Set<UUID> adminDebugSet = new HashSet<>();
    
    @EventHandler(ignoreCancelled=false, priority=EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK &&
            event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        // Player
        final Player player = event.getPlayer();
        if (!player.hasPermission("treasurehunt.player")) return;
        // Admin stuff
        if (player.hasPermission("treasurehunt.admin")) {
            String adminCreateName = adminCreateMap.remove(player.getUniqueId());
            if (adminCreateName != null) {
                Treasure treasure = new Treasure(adminCreateName, event.getClickedBlock());
                plugin.getTreasureConfig().addTreasure(treasure);
                plugin.getTreasureConfig().save();
                plugin.msg(player, "&e&lTreasureHunt&r Treasure '&a%s&r' created.", adminCreateName);
                return;
            }
        }
        // Check treasure
        final Treasure treasure = plugin.getTreasureConfig().treasureAt(event.getClickedBlock());
        if (treasure == null) return;
        // Debug stuff
        if (adminDebugSet.contains(player.getUniqueId())) {
            plugin.msg(player, "&e&lTreasureHunt&r This is treasure '&a%s&r'.", treasure.getName());
            return;
        }
        if (treasure.hasFound(player)) return;
        // Set found
        boolean isFirst = treasure.getFound().isEmpty();
        treasure.setFound(player);
        plugin.getTreasureConfig().save();
        // Give
        plugin.giveToken(player, 1);
        // Tell, announce
        plugin.msg(player, "You found the &a%s&r treasure!", treasure.getName());
        if (isFirst) {
            for (Player other: plugin.getServer().getOnlinePlayers()) {
                plugin.msg(other, "&a%s&r just found the &a%s&r treasure.", player.getName(), treasure.getName());
            }
        }
    }
}
