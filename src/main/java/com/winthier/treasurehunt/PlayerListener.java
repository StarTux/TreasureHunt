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
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

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
        if (plugin.paused) return;
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
            plugin.getLogger().info(player.getName() + " found treasure '" + treasure.getName() + "'");
            for (Player other: plugin.getServer().getOnlinePlayers()) {
                if (other.hasPermission("treasurehunt.player")) {
                    plugin.msg(other, "&a&lTreasureHunt&r %s just found the &a%s&r treasure.", player.getName(), treasure.getName());
                }
            }
        }
    }

    boolean holdsToken(Player player, EquipmentSlot slot) {
        if (slot == null) return false;
        switch (slot) {
        case HAND: return plugin.isToken(player.getInventory().getItemInMainHand());
        case OFF_HAND: return plugin.isToken(player.getInventory().getItemInOffHand());
        default: return false;
        }
    }

    @EventHandler(ignoreCancelled=false, priority=EventPriority.LOW)
    public void onPlayerInteractToken(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK &&
            event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (holdsToken(event.getPlayer(), event.getHand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=false, priority=EventPriority.LOW)
    public void onPlayerInteractEntityToken(PlayerInteractEntityEvent event) {
        if (holdsToken(event.getPlayer(), event.getHand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=false, priority=EventPriority.LOW)
    public void onPlayerInteractAtEntityToken(PlayerInteractAtEntityEvent event) {
        if (holdsToken(event.getPlayer(), event.getHand())) {
            event.setCancelled(true);
        }
    }
}
