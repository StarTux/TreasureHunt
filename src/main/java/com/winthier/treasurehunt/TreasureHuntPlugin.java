package com.winthier.treasurehunt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONValue;

@Getter
public class TreasureHuntPlugin extends JavaPlugin {
    TreasureConfig treasureConfig = null;
    private String tokenId, tokenTitle, tokenBoundPrefix;
    private final List<String> tokenLore = new ArrayList<String>();
    final PlayerListener playerListener = new PlayerListener(this);

    @Override public void onEnable() {
        getServer().getPluginManager().registerEvents(playerListener, this);
        getCommand("treasurehunt").setExecutor(new TreasureHuntCommand(this));
        getCommand("treasurehuntadmin").setExecutor(new TreasureHuntAdminCommand(this));
        saveDefaultConfig();
        load();
    }

    TreasureConfig getTreasureConfig() {
        if (treasureConfig == null) {
            treasureConfig = new TreasureConfig(this);
        }
        return treasureConfig;
    }

    static String format(String msg, Object... args) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        if (args.length != 0) msg = String.format(msg, args);
        return msg;
    }

    static void msg(CommandSender sender, String msg, Object... args) {
        sender.sendMessage(format(msg, args));
    }

    void giveToken(Player player, int amount) {
        ItemStack item = spawnToken(player, amount);
        player.getWorld().dropItem(player.getEyeLocation(), item).setPickupDelay(0);
    }

    void load() {
        treasureConfig = null;
        reloadConfig();
        tokenId = getConfig().getString("token.ID");
        tokenTitle = format(getConfig().getString("token.Title"));
        tokenBoundPrefix = format(getConfig().getString("token.BoundPrefix"));
        tokenLore.clear();
        for (String line : getConfig().getStringList("token.Description")) {
            tokenLore.add(format(line));
        }
    }

    
    String hideString(String string) {
        final String MAGIC_KEY = "" + ChatColor.RESET + ChatColor.BLACK + ChatColor.MAGIC;
        StringBuilder sb = new StringBuilder();
        sb.append(MAGIC_KEY);
        for (int i = 0; i < string.length(); ++i) {
            sb.append(ChatColor.COLOR_CHAR).append(string.charAt(i));
        }
        return sb.toString();
    }

    String makeHiddenTag(String id, Player player) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("owner", player.getUniqueId().toString());
        return hideString(JSONValue.toJSONString(map));
    }

    ItemStack spawnToken(Player player, int amount) {
        Material mat = Material.EGG;
        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(tokenTitle);
        List<String> lore = new ArrayList<String>(tokenLore);
        lore.add(tokenBoundPrefix + player.getName());
        lore.set(0, lore.get(0) + makeHiddenTag(tokenId, player));
        meta.setLore(lore);
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        item.setItemMeta(meta);
        return item;
    }
}
