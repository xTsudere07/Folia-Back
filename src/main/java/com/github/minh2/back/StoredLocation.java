package com.github.minh2.back;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

public class StoredLocation {
    private final String world;
    private final double x, y, z;
    private final float yaw, pitch;

    public StoredLocation(Location location) {
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public Location toLocation() {
        World w = Bukkit.getWorld(world);
        return new Location(Objects.requireNonNull(w), x, y, z, yaw, pitch);
    }
}