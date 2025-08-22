package com.github.minh2.back;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class Back extends JavaPlugin {

    private final HashMap<UUID, StoredLocation> deathLocations = new HashMap<>();
    private ConfigManager configManager;
    private boolean isFolia;

    @Override
    public void onEnable() {
        // Kiểm tra xem có phải Folia không
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.ScheduledTask");
            isFolia = true;
            getLogger().info("Detected Folia server, using Folia scheduling.");
        } catch (ClassNotFoundException e) {
            isFolia = false;
            getLogger().info("Detected Bukkit/Paper server, using Bukkit scheduling.");
        }

        // Khởi tạo ConfigManager
        this.configManager = new ConfigManager(this);

        // Đăng ký commands và events
        BackCommand backCommand = new BackCommand(this);
        getCommand("back").setExecutor(backCommand);

        // Đăng ký death listener
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);

        getLogger().info("Back plugin đã được kích hoạt!");
    }

    @Override
    public void onDisable() {
        // Hủy tất cả các task đang chạy
        if (getCommand("back").getExecutor() instanceof BackCommand backCommand) {
            backCommand.getActiveTasks().values().forEach(task -> {
                try {
                    task.cancel();
                } catch (Exception e) {
                    // Ignore cancellation errors on shutdown
                }
            });
            backCommand.getActiveTasks().clear();
        }

        getLogger().info("Back plugin đã được tắt!");
    }

    public HashMap<UUID, StoredLocation> getDeathLocations() {
        return deathLocations;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public boolean isFolia() {
        return isFolia;
    }
}