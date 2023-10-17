package com.unclecole.chunkcollectors;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.unclecole.chunkcollectors.database.ChunkCollectorData;
import com.unclecole.chunkcollectors.database.commands.CollectorCmd;
import com.unclecole.chunkcollectors.database.serializer.Persist;
import com.unclecole.chunkcollectors.listeners.CollectorListener;
import com.unclecole.chunkcollectors.listeners.MobListener;
import com.unclecole.chunkcollectors.objects.cubeObject;
import com.unclecole.chunkcollectors.tasks.ParticleTask;
import com.unclecole.chunkcollectors.utils.ConfigFile;
import com.unclecole.chunkcollectors.utils.ConfigUtils;
import com.unclecole.chunkcollectors.utils.LocationUtility;
import com.unclecole.chunkcollectors.utils.TL;
import dev.rosewood.rosestacker.api.RoseStackerAPI;
import lombok.Getter;
import net.brcdev.shopgui.ShopGuiPlugin;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class ChunkCollectors extends JavaPlugin {

    @Getter
    private RoseStackerAPI rsAPI;
    @Getter private SuperiorSkyblock superiorSkyblock;
    public static ChunkCollectors instance;
    @Getter private static Persist persist;
    @Getter LocationUtility locationUtility;
    @Getter ConfigUtils configUtils;
    private Economy economy;
    public ShopGuiPlugin shop;
    @Getter HashMap<UUID, cubeObject> viewBarrierList;

    @Override
    public void onEnable() {

        instance = this;

        persist = new Persist();
        locationUtility = new LocationUtility();
        saveDefaultConfig();
        TL.loadMessages(new ConfigFile("messages.yml", this));
        configUtils = new ConfigUtils(instance);
        viewBarrierList = new HashMap<>();
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new ParticleTask(), 5, 5);

        setupEconomy();
        shop = ShopGuiPlusApi.getPlugin();
        getCommand("collectors").setExecutor(new CollectorCmd());

        this.getServer().getPluginManager().registerEvents(new MobListener(), instance);
        this.getServer().getPluginManager().registerEvents(new CollectorListener(instance), instance);
        if (Bukkit.getPluginManager().isPluginEnabled("RoseStacker")) {
            this.rsAPI = RoseStackerAPI.getInstance();
        }
        this.superiorSkyblock = SuperiorSkyblockAPI.getSuperiorSkyblock();
        ChunkCollectorData.load();
        autoSaveTask();
    }

    @Override
    public void onDisable() {
        ChunkCollectorData.save();
    }

    public static ChunkCollectors getInstance() {
        return instance;
    }
    public ShopGuiPlugin getShop() { return shop; }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        } else {
            RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            } else {
                this.economy = (Economy)rsp.getProvider();
                return this.economy != null;
            }
        }
    }
    public Economy getEconomy() {
        return this.economy;
    }

    private void autoSaveTask() {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                ChunkCollectorData.save();
                getLogger().info("Saving all plugin data.");
            }
        }, 0L, 3000L);
    }
}
