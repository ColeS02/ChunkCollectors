package com.unclecole.chunkcollectors.listeners;

import com.unclecole.chunkcollectors.ChunkCollectors;
import com.unclecole.chunkcollectors.database.ChunkCollectorData;
import dev.rosewood.rosestacker.event.EntityStackMultipleDeathEvent;
import dev.rosewood.rosestacker.event.PreDropStackedItemsEvent;
import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class MobListener implements Listener {

    @EventHandler
    public void onStackedDeath(EntityStackMultipleDeathEvent event) {
        String chunk = ChunkCollectors.getInstance().getLocationUtility().parseToString(event.getStack().getLocation().getChunk());
        ChunkCollectorData.chunkCollectorData.forEach((s, collectorObject) -> {
            int radius = ChunkCollectors.getInstance().getConfigUtils().getChunkUpgrades().get(collectorObject.getUpgrades().getChunkUpgrade()).getUpgrade();

            if (compare(radius, chunk, s)) {
                event.getEntityDrops().forEach((livingEntity, entityDrops) -> {

                    entityDrops.getDrops().forEach(itemStack -> {
                        if(!ChunkCollectors.getInstance().getConfigUtils().getAllowedItems().contains(itemStack.getType().name())) return;

                        int amount = 0;
                        if (collectorObject.getDrops().containsKey(itemStack.getType().name())) {
                            amount = collectorObject.getDrops().get(itemStack.getType().name());
                        }
                        if (amount + itemStack.getAmount() > ChunkCollectors.getInstance().getConfigUtils().getCapacityUpgrades().get(collectorObject.getUpgrades().getCapacityUpgrade()).getUpgrade()) {
                            collectorObject.getDrops().put(itemStack.getType().name(), Math.toIntExact(ChunkCollectors.getInstance().getConfigUtils().getCapacityUpgrades().get(collectorObject.getUpgrades().getCapacityUpgrade()).getUpgrade()));
                        } else
                            collectorObject.getDrops().put(itemStack.getType().name(), amount + itemStack.getAmount());

                        itemStack.setAmount(0);
                    });
                });
            }
        });
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        String chunk = ChunkCollectors.getInstance().getLocationUtility().parseToString(event.getEntity().getLocation().getChunk());
        ChunkCollectorData.chunkCollectorData.forEach((s, collectorObject) -> {
            int radius = ChunkCollectors.getInstance().getConfigUtils().getChunkUpgrades().get(collectorObject.getUpgrades().getChunkUpgrade()).getUpgrade();

            if (compare(radius, chunk, s)) {
                event.getDrops().forEach(itemStack -> {

                    if(!ChunkCollectors.getInstance().getConfigUtils().getAllowedItems().contains(itemStack.getType().name())) return;

                    int amount = 0;
                    if (collectorObject.getDrops().containsKey(itemStack.getType().name())) {
                        amount = collectorObject.getDrops().get(itemStack.getType().name());
                    }
                    if (amount + itemStack.getAmount() > ChunkCollectors.getInstance().getConfigUtils().getCapacityUpgrades().get(collectorObject.getUpgrades().getCapacityUpgrade()).getUpgrade()) {
                        collectorObject.getDrops().put(itemStack.getType().name(), Math.toIntExact(ChunkCollectors.getInstance().getConfigUtils().getCapacityUpgrades().get(collectorObject.getUpgrades().getCapacityUpgrade()).getUpgrade()));
                    } else
                        collectorObject.getDrops().put(itemStack.getType().name(), amount + itemStack.getAmount());

                    itemStack.setAmount(0);
                });
            }
        });
    }

    public boolean compare(int radius, String chunk1, String chunk2) {
        String[] chunkData1 = chunk1.split(":");
        String[] chunkData2 = chunk2.split(":");

        int x1 = Integer.parseInt(chunkData1[0]);
        int x2 = Integer.parseInt(chunkData2[0]);
        int z1 = Integer.parseInt(chunkData1[1]);
        int z2 = Integer.parseInt(chunkData2[1]);
        String world1 = chunkData1[2];
        String world2 = chunkData2[2];

        if(world1.equals(world2)) {
            return (x1 + radius >= x2 && x2 >= x1 - radius) && (z1 + radius >= z2 && z2 >= z1 - radius);
        } return false;
    }
}
