package com.github.minh2.back;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final Back plugin;
    private FileConfiguration config;

    public ConfigManager(Back plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public void reloadConfig() {
        loadConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    // Getter methods for configuration values
    public int getTeleportDelay() {
        return config.getInt("teleport-delay", 5);
    }

    // Message getters
    public Component getNoDeathLocationMessage() {
        String message = config.getString("messages.no-death-location", "⚠ Bạn chưa chết lần nào để quay lại đâu!");
        return Component.text("⚠ ", NamedTextColor.RED)
                .append(Component.text(message.replace("⚠ ", ""), NamedTextColor.GRAY));
    }

    public Component getAlreadyTeleportingMessage() {
        String message = config.getString("messages.already-teleporting", "⚠ Bạn đang trong quá trình dịch chuyển rồi!");
        return Component.text("⚠ ", NamedTextColor.RED)
                .append(Component.text(message.replace("⚠ ", ""), NamedTextColor.GRAY));
    }

    public Component getCancelledByDamageMessage() {
        String message = config.getString("messages.cancelled-by-damage", "✖ Bạn bị tấn công, hủy dịch chuyển.");
        return Component.text(message, NamedTextColor.RED);
    }

    public Component getCancelledByMovementMessage() {
        String message = config.getString("messages.cancelled-by-movement", "✖ Bạn đã di chuyển, hủy dịch chuyển.");
        return Component.text(message, NamedTextColor.RED);
    }

    public Component getCountdownActionBar(int time) {
        String message = config.getString("messages.countdown-actionbar", "☠ Dịch chuyển sau {time} giây...");
        message = message.replace("{time}", String.valueOf(time));
        
        return Component.text("☠ ", TextColor.color(0xFFAA00))
                .append(Component.text(message.replace("☠ ", "").replace("{time}", String.valueOf(time)), NamedTextColor.GRAY));
    }

    public Component getTeleportSuccessMessage() {
        String message = config.getString("messages.teleport-success", "☠ Đã quay lại vị trí vừa tử nạn!");
        return Component.text("[", NamedTextColor.GRAY)
                .append(Component.text("☠", NamedTextColor.RED))
                .append(Component.text("] ", NamedTextColor.GRAY))
                .append(Component.text(message.replace("☠ ", ""), NamedTextColor.WHITE));
    }

    public Component getConfigReloadedMessage() {
        String message = config.getString("messages.config-reloaded", "✅ Đã tải lại cấu hình plugin Back!");
        return Component.text(message, NamedTextColor.GREEN);
    }

    public Component getNoPermissionMessage() {
        String message = config.getString("messages.no-permission", "❌ Bạn không có quyền sử dụng lệnh này!");
        return Component.text(message, NamedTextColor.RED);
    }

    public Component getPlayerOnlyMessage() {
        String message = config.getString("messages.player-only", "Lệnh này chỉ có thể được sử dụng bởi người chơi.");
        return Component.text(message, NamedTextColor.RED);
    }

    // Sound getters
    public Sound getNoDeathLocationSound() {
        String soundName = config.getString("sounds.no-death-location", "ENTITY_VILLAGER_NO");
        try {
            return Sound.valueOf(soundName);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid sound: " + soundName + ", using default ENTITY_VILLAGER_NO");
            return Sound.ENTITY_VILLAGER_NO;
        }
    }

    public Sound getTeleportSuccessSound() {
        String soundName = config.getString("sounds.teleport-success", "BLOCK_NOTE_BLOCK_PLING");
        try {
            return Sound.valueOf(soundName);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid sound: " + soundName + ", using default BLOCK_NOTE_BLOCK_PLING");
            return Sound.BLOCK_NOTE_BLOCK_PLING;
        }
    }

    public Sound getConfigReloadedSound() {
        String soundName = config.getString("sounds.config-reloaded", "BLOCK_NOTE_BLOCK_CHIME");
        try {
            return Sound.valueOf(soundName);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid sound: " + soundName + ", using default BLOCK_NOTE_BLOCK_CHIME");
            return Sound.BLOCK_NOTE_BLOCK_CHIME;
        }
    }

    // Permission getters
    public String getBackUsePermission() {
        return config.getString("permissions.back-use", "back.use");
    }

    public String getBackReloadPermission() {
        return config.getString("permissions.back-reload", "back.reload");
    }
}