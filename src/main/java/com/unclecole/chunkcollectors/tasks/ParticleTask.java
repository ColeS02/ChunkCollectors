package com.unclecole.chunkcollectors.tasks;

import com.unclecole.chunkcollectors.ChunkCollectors;
import com.unclecole.chunkcollectors.database.ChunkCollectorData;
import com.unclecole.chunkcollectors.objects.cubeObject;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import xyz.xenondevs.particle.ParticleEffect;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ParticleTask implements Runnable{

    @Override
    public void run() {

        if(ChunkCollectors.getInstance().getViewBarrierList().isEmpty()) return;

        for (Map.Entry<UUID, cubeObject> map : ChunkCollectors.getInstance().getViewBarrierList().entrySet()) {
            UUID uuid = map.getKey();
            cubeObject s = map.getValue();

            if (Bukkit.getPlayer(uuid) == null) {
                ChunkCollectors.getInstance().getViewBarrierList().remove(uuid);
                continue;
            }
            if (!Bukkit.getPlayer(uuid).isOnline()) {
                ChunkCollectors.getInstance().getViewBarrierList().remove(uuid);
                continue;
            }
            if (Bukkit.getPlayer(uuid).getLocation().getWorld() != s.getCorner1().getWorld()) {
                ChunkCollectors.getInstance().getViewBarrierList().remove(uuid);
            }

            s.getHollowCube().forEach(location -> {
                ParticleEffect.REDSTONE.display(location, Color.RED, Bukkit.getPlayer(uuid));
            });
        }
    }
}
