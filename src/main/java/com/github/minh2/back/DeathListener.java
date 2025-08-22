package com.github.minh2.back;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {
    
    private final Back plugin;

    public DeathListener(Back plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getPlayer();
        Location deathLocation = victim.getLocation();
        
        // Lưu death location
        plugin.getDeathLocations().put(victim.getUniqueId(), new StoredLocation(deathLocation));
        
        // Gửi thông báo death location
        victim.sendMessage(getDeathLocationMessage(deathLocation));
        
        // Gửi thông báo hướng dẫn sử dụng /back
        victim.sendMessage(getBackInstructionMessage());
    }
    
    private Component getDeathLocationMessage(Location location) {
        String message = plugin.getConfigManager().getConfig().getString(
            "messages.death-location", 
            "Bạn đã chết tại {x} {y} {z}"
        );
        
        // Replace placeholders
        message = message.replace("{x}", String.valueOf(location.getBlockX()))
                        .replace("{y}", String.valueOf(location.getBlockY()))
                        .replace("{z}", String.valueOf(location.getBlockZ()));
        
        return Component.text("[", NamedTextColor.GRAY)
                .append(Component.text("☠", NamedTextColor.RED))
                .append(Component.text("] ", NamedTextColor.GRAY))
                .append(Component.text(message, NamedTextColor.GRAY));
    }
    
    private Component getBackInstructionMessage() {
        String message = plugin.getConfigManager().getConfig().getString(
            "messages.back-instruction", 
            "Dùng lệnh /back để quay lại !"
        );
        
        return Component.text("➜ ", TextColor.color(0xFFAA00))
                .append(Component.text(message, NamedTextColor.GRAY));
    }
}