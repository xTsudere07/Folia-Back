package com.github.minh2.back;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import java.util.HashMap;
import java.util.UUID;

public class BackCommand implements CommandExecutor, Listener {

    private final Back plugin;
    private final HashMap<UUID, TeleportTask> activeTasks = new HashMap<>();

    public BackCommand(Back plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public HashMap<UUID, TeleportTask> getActiveTasks() {
        return activeTasks;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Handle reload subcommand
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission(plugin.getConfigManager().getBackReloadPermission())) {
                sender.sendMessage(plugin.getConfigManager().getNoPermissionMessage());
                return true;
            }
            
            plugin.getConfigManager().reloadConfig();
            sender.sendMessage(plugin.getConfigManager().getConfigReloadedMessage());
            
            if (sender instanceof Player player) {
                player.playSound(player.getLocation(), plugin.getConfigManager().getConfigReloadedSound(), 1, 1);
            }
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getPlayerOnlyMessage());
            return true;
        }

        if (!player.hasPermission(plugin.getConfigManager().getBackUsePermission())) {
            player.sendMessage(plugin.getConfigManager().getNoPermissionMessage());
            return true;
        }

        UUID playerUUID = player.getUniqueId();

        if (!plugin.getDeathLocations().containsKey(playerUUID)) {
            player.sendMessage(plugin.getConfigManager().getNoDeathLocationMessage());
            player.playSound(player.getLocation(), plugin.getConfigManager().getNoDeathLocationSound(), 1, 1);
            return true;
        }

        if (activeTasks.containsKey(playerUUID)) {
            player.sendMessage(plugin.getConfigManager().getAlreadyTeleportingMessage());
            return true;
        }

        TeleportTask task = new TeleportTask(player);
        activeTasks.put(playerUUID, task);

        if (plugin.isFolia()) {
            // Sử dụng Folia EntityScheduler với Runnable thay vì lambda
            task.scheduleOnFolia();
        } else {
            task.runTaskTimer(plugin, 0L, 20L);
        }

        return true;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            UUID uuid = player.getUniqueId();
            if (activeTasks.containsKey(uuid)) {
                activeTasks.get(uuid).cancelTeleport(plugin.getConfigManager().getCancelledByDamageMessage());
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (event.hasChangedPosition() && activeTasks.containsKey(uuid)) {
            activeTasks.get(uuid).cancelTeleport(plugin.getConfigManager().getCancelledByMovementMessage());
        }
    }

    public class TeleportTask extends BukkitRunnable {
        private final Player player;
        private final Location startLocation;
        private int countdown;
        private ScheduledTask foliaTask;

        public TeleportTask(Player player) {
            this.player = player;
            this.startLocation = player.getLocation();
            this.countdown = plugin.getConfigManager().getTeleportDelay();
        }

        public void scheduleOnFolia() {
            foliaTask = player.getScheduler().runAtFixedRate(plugin, (ScheduledTask task) -> {
                this.run();
            }, null, 1L, 20L);
        }

        @Override
        public void run() {
            if (countdown > 0) {
                player.sendActionBar(plugin.getConfigManager().getCountdownActionBar(countdown));
                countdown--;
            } else {
                this.cancel();

                StoredLocation deathLocation = plugin.getDeathLocations().get(player.getUniqueId());
                if (deathLocation != null) {
                    Location targetLocation = deathLocation.toLocation();
                    
                    if (plugin.isFolia()) {
                        // Sử dụng teleportAsync cho Folia để tránh cross-region issues
                        player.teleportAsync(targetLocation).thenRun(() -> {
                            player.sendMessage(plugin.getConfigManager().getTeleportSuccessMessage());
                            player.playSound(player.getLocation(), plugin.getConfigManager().getTeleportSuccessSound(), 1, 1);
                        });
                    } else {
                        player.teleport(targetLocation);
                        player.sendMessage(plugin.getConfigManager().getTeleportSuccessMessage());
                        player.playSound(player.getLocation(), plugin.getConfigManager().getTeleportSuccessSound(), 1, 1);
                    }
                    
                    plugin.getDeathLocations().remove(player.getUniqueId());
                }

                activeTasks.remove(player.getUniqueId());
            }
        }

        public void cancelTeleport(Component message) {
            this.cancel();
            player.sendActionBar(message);
            activeTasks.remove(player.getUniqueId());
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            if (plugin.isFolia() && foliaTask != null) {
                foliaTask.cancel();
            } else {
                super.cancel();
            }
            activeTasks.remove(player.getUniqueId());
        }
    }
}