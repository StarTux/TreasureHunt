package com.winthier.treasurehunt;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class TreasureHuntCommand implements CommandExecutor {
    final TreasureHuntPlugin plugin;
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 0) return false;
        final Player player = sender instanceof Player ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage("Player expected");
            return true;
        }
        plugin.msg(player, "&a&lTreasure Hunt&r List");
        for (Treasure treasure: plugin.getTreasureConfig().getTreasures()) {
            if (treasure.hasFound(player)) {
                plugin.msg(player, " [&ax&r]&a %s", treasure.getName());
            } else {
                plugin.msg(player, " [&8?&r]&r %s", treasure.getName());
            }
        }
        return true;
    }
}
