package com.winthier.treasurehunt;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class TreasureHuntAdminCommand implements CommandExecutor {
    final TreasureHuntPlugin plugin;
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player = sender instanceof Player ? (Player)sender : null;
        if (args.length == 0) return false;
        String firstArg = args[0].toLowerCase();
        if ("reload".equals(firstArg) && args.length == 1) {
            plugin.load();
            plugin.msg(sender, "TreasureHunt config reloaded");
        } else if ("token".equals(firstArg) && (args.length == 1 || args.length == 2)) {
            if (player == null) {
                sender.sendMessage("Player expected");
                return true;
            }
            if (args.length == 2) {
                String targetName = args[1];
                Player target = plugin.getServer().getPlayer(targetName);
                if (target == null) {
                    plugin.msg(player, "&cPlayer not found: %s", targetName);
                    return true;
                }
                final int amount = 1;
                plugin.giveToken(target, amount);
                plugin.msg(player, "&e&lTreasureHunt&r Gave %d tokens to %s.", amount, target.getName());
            } else {
                final int amount = 16;
                plugin.giveToken(player, amount);
                plugin.msg(player, "&e&lTreasureHunt&r %d tokens spawned in.", amount);
            }
        } else if ("list".equals(firstArg) && args.length == 1) {
            int count = 0;
            plugin.msg(sender, "TreasureHunt treasure list");
            for (Treasure treasure: plugin.getTreasureConfig().getTreasures()) {
                plugin.msg(sender, "- %s (%s,%d,%d,%d) (found: %d)", treasure.name, treasure.world, treasure.x, treasure.y, treasure.z, treasure.getFound().size());
                count += 1;
            }
            plugin.msg(sender, "Total: %d", count);
        } else if ("create".equals(firstArg) && args.length > 1) {
            if (player == null) {
                sender.sendMessage("Player expected");
                return true;
            }
            StringBuilder sb = new StringBuilder(args[1]);
            for (int i = 2; i < args.length; ++i) sb.append(" ").append(args[i]);
            plugin.getPlayerListener().adminCreateMap.put(player.getUniqueId(), sb.toString());
            plugin.msg(player, "&e&lTreasureHunt&r Click a block to create treasure '&a%s&r' or type '&a/TreasureHuntAdmin Cancel&r'", sb.toString());
        } else if ("debug".equals(firstArg) && args.length == 1) {
            if (player == null) {
                sender.sendMessage("Player expected");
                return true;
            }
            if (plugin.getPlayerListener().adminDebugSet.contains(player.getUniqueId())) {
                plugin.getPlayerListener().adminDebugSet.remove(player.getUniqueId());
                plugin.msg(player, "&e&lTreasureHunt&r Debug mode disabled.");
            } else {
                plugin.getPlayerListener().adminDebugSet.add(player.getUniqueId());
                plugin.msg(player, "&e&lTreasureHunt&r Debug mode enabled.");
            }
        } else if ("cancel".equals(firstArg) && args.length == 1) {
            if (player == null) {
                sender.sendMessage("Player expected");
                return true;
            }
            plugin.getPlayerListener().adminCreateMap.remove(player.getUniqueId());
            plugin.getPlayerListener().adminDebugSet.remove(player.getUniqueId());
            plugin.msg(player, "&e&lTreasureHunt&r Admin actions cancelled.");
        } else {
            return false;
        }
        return true;
    }
}
