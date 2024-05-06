package com.unclecole.chunkcollectors.objects;

import com.unclecole.chunkcollectors.ChunkCollectors;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class CollectorObject {

    private String location;
    private String collectorName;
    private UpgradeObjects upgrades;
    private HashMap<String, Integer> drops;
    private Long experience;

    public CollectorObject(Location location, String collectorName, int sellMulti, int capacityUpgrade, int chunkUpgrade) {
        this.collectorName = collectorName;
        this.location = ChunkCollectors.getInstance().getLocationUtility().parseToString(location);
        this.upgrades = new UpgradeObjects(sellMulti, capacityUpgrade, chunkUpgrade);
        drops = new HashMap<>();
    }

    public CollectorObject(Location location, int sellMulti, int capacityUpgrade, int chunkUpgrade) {
        this.location = ChunkCollectors.getInstance().getLocationUtility().parseToString(location);
        this.upgrades = new UpgradeObjects(sellMulti, capacityUpgrade, chunkUpgrade);
        drops = new HashMap<>();
    }

    public CollectorObject(Location location) {
        this.location = ChunkCollectors.getInstance().getLocationUtility().parseToString(location);
        this.upgrades = new UpgradeObjects();
        drops = new HashMap<>();
    }

    public HashMap<String, Integer> getDrops() {
        return drops;
    }
    public UpgradeObjects getUpgrades() { return upgrades; }
    public String getLocation() { return location; }
    public String getCollectorName() { return collectorName; }
    public Chunk getChunk() { return ChunkCollectors.getInstance().getLocationUtility().parseToChunk(location); }
    public Long getExperience() { return experience; }


}
