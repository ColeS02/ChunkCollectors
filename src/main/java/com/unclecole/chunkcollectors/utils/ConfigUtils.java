package com.unclecole.chunkcollectors.utils;

import com.unclecole.chunkcollectors.ChunkCollectors;
import com.unclecole.chunkcollectors.objects.Upgrades;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.checkerframework.checker.units.qual.A;
import redempt.redlib.itemutils.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class ConfigUtils {

    private ChunkCollectors plugin;

    //Collector Menu
    @Getter private int collectorMenuSize, upgradeItemSlot, dropsItemSlot, barrierItemSlot;
    @Getter private String collectorMenuTitle;
    @Getter private ItemBuilder upgradeItem;
    @Getter private ItemBuilder dropsItem;
    @Getter private ItemBuilder barrierItem;
    @Getter private ItemBuilder backButton;

    //Upgrade Menu
    @Getter private String sellMultiTitle, ChunkSizeTitle, CapacityTitle, upgradeMenuTitle;
    @Getter private Material sellMultiMaterial, ChunkSizeMaterial, CapacityMaterial;
    @Getter private List<String> sellMultiLore, ChunkSizeLore, CapacityLore;
    @Getter private int sellMultiSlot, ChunkSizeSlot, CapacitySlot, upgradeMenuSize;

    //Drops Menu
    @Getter private String dropMenuTitle, genericDropTitle;
    @Getter private int dropMenuSize;
    @Getter private List<String> genericDropLore;

    //Upgrades
    @Getter private ArrayList<Upgrades<Double>> sellMultiUpgrades;
    @Getter private ArrayList<Upgrades<Long>> capacityUpgrades;
    @Getter private ArrayList<Upgrades<Integer>> chunkUpgrades;

    //COLLECTOR
    @Getter private ItemBuilder collector;
    @Getter private ArrayList<String> hologram;
    @Getter private ArrayList<String> allowedItems;

    public ConfigUtils(ChunkCollectors chunkCollectors) {
        this.plugin = chunkCollectors;
        load();
    }

    public void load() {
        sellMultiUpgrades = new ArrayList<>();
        capacityUpgrades = new ArrayList<>();
        chunkUpgrades = new ArrayList<>();
        allowedItems = new ArrayList<>();
        hologram = new ArrayList<>();
        loadCollectorMenu();
        loadUpgradeMenu();
        loadDropsMenu();
        loadUpgrades();
        loadCollector();
    }

    public void loadDropsMenu() {
        dropMenuTitle = plugin.getConfig().getString("CollectorMenu.DropsMenu.Title");
        genericDropTitle = plugin.getConfig().getString("CollectorMenu.DropsMenu.GenericTitle");

        dropMenuSize = plugin.getConfig().getInt("CollectorMenu.DropsMenu.Size");

        genericDropLore = plugin.getConfig().getStringList("CollectorMenu.DropsMenu.GenericLore");
    }

    public void loadUpgradeMenu() {
        upgradeMenuTitle = plugin.getConfig().getString("CollectorMenu.UpgradeMenu.Title");
        upgradeMenuSize = plugin.getConfig().getInt("CollectorMenu.UpgradeMenu.Size");

        sellMultiTitle = plugin.getConfig().getString("CollectorMenu.UpgradeMenu.SellMultiItem.Title");
        sellMultiMaterial = Material.getMaterial(plugin.getConfig().getString("CollectorMenu.UpgradeMenu.SellMultiItem.Material"));
        sellMultiLore = plugin.getConfig().getStringList("CollectorMenu.UpgradeMenu.SellMultiItem.Lore");
        sellMultiSlot = plugin.getConfig().getInt("CollectorMenu.UpgradeMenu.SellMultiItem.Slot");

        ChunkSizeTitle = plugin.getConfig().getString("CollectorMenu.UpgradeMenu.ChunkSizeItem.Title");
        ChunkSizeMaterial = Material.getMaterial(plugin.getConfig().getString("CollectorMenu.UpgradeMenu.ChunkSizeItem.Material"));
        ChunkSizeLore = plugin.getConfig().getStringList("CollectorMenu.UpgradeMenu.ChunkSizeItem.Lore");
        ChunkSizeSlot = plugin.getConfig().getInt("CollectorMenu.UpgradeMenu.ChunkSizeItem.Slot");

        CapacityTitle = plugin.getConfig().getString("CollectorMenu.UpgradeMenu.CapacitySizeItem.Title");
        CapacityMaterial = Material.getMaterial(plugin.getConfig().getString("CollectorMenu.UpgradeMenu.CapacitySizeItem.Material"));
        CapacityLore = plugin.getConfig().getStringList("CollectorMenu.UpgradeMenu.CapacitySizeItem.Lore");
        CapacitySlot = plugin.getConfig().getInt("CollectorMenu.UpgradeMenu.CapacitySizeItem.Slot");
    }

    public void loadCollectorMenu() {
        collectorMenuSize = plugin.getConfig().getInt("CollectorMenu.Size");
        collectorMenuTitle = plugin.getConfig().getString("CollectorMenu.Title");

        upgradeItem = new ItemBuilder(Material.getMaterial(plugin.getConfig().getString("CollectorMenu.Upgrades.Material")), 1);
        upgradeItem.setName(C.color(plugin.getConfig().getString("CollectorMenu.Upgrades.Title")));
        plugin.getConfig().getStringList("CollectorMenu.Upgrades.Lore").forEach(string -> {
            upgradeItem.addLore(C.color(string));
        });
        upgradeItemSlot = plugin.getConfig().getInt("CollectorMenu.Upgrades.Slot");

        dropsItem = new ItemBuilder(Material.getMaterial(plugin.getConfig().getString("CollectorMenu.Drops.Material")), 1);
        dropsItem.setName(C.color(plugin.getConfig().getString("CollectorMenu.Drops.Title")));
        plugin.getConfig().getStringList("CollectorMenu.Drops.Lore").forEach(string -> {
            dropsItem.addLore(C.color(string));
        });
        dropsItemSlot = plugin.getConfig().getInt("CollectorMenu.Drops.Slot");

        barrierItem = new ItemBuilder(Material.getMaterial(plugin.getConfig().getString("CollectorMenu.Barrier.Material")), 1);
        barrierItem.setName(C.color(plugin.getConfig().getString("CollectorMenu.Barrier.Title")));
        plugin.getConfig().getStringList("CollectorMenu.Barrier.Lore").forEach(string -> {
            barrierItem.addLore(C.color(string));
        });
        barrierItemSlot = plugin.getConfig().getInt("CollectorMenu.Barrier.Slot");

        backButton = new ItemBuilder(Material.getMaterial(plugin.getConfig().getString("BackButton.Material")), 1);
        backButton.setName(C.color(plugin.getConfig().getString("BackButton.Title")));
        plugin.getConfig().getStringList("BackButton.Lore").forEach(string -> {
            backButton.addLore(C.color(string));
        });
    }

    public void loadUpgrades() {

        ConfigurationSection multi = plugin.getConfig().getConfigurationSection("Upgrades.SellMulti");

        for(String key : multi.getKeys(false)) {
            sellMultiUpgrades.add(new Upgrades<Double>(plugin.getConfig().getDouble("Upgrades.SellMulti." + key + ".Multi")
            , plugin.getConfig().getLong("Upgrades.SellMulti." + key + ".Cost")));
        }

        ConfigurationSection capacity = plugin.getConfig().getConfigurationSection("Upgrades.Capacity");

        for(String key : capacity.getKeys(false)) {
            capacityUpgrades.add(new Upgrades<Long>(plugin.getConfig().getLong("Upgrades.Capacity." + key + ".Capacity")
                    , plugin.getConfig().getLong("Upgrades.Capacity." + key + ".Cost")));
        }

        ConfigurationSection chunk = plugin.getConfig().getConfigurationSection("Upgrades.Chunks");

        for(String key : chunk.getKeys(false)) {
            chunkUpgrades.add(new Upgrades<Integer>(plugin.getConfig().getInt("Upgrades.Chunks." + key + ".Radius")
                    , plugin.getConfig().getLong("Upgrades.Chunks." + key + ".Cost")));
        }
    }

    public void loadCollector() {
        collector = new ItemBuilder(Material.getMaterial(plugin.getConfig().getString("Collector.Material")));
        collector.setName(C.color(plugin.getConfig().getString("Collector.Title")));
        plugin.getConfig().getStringList("Collector.Lore").forEach(string -> {
            collector.addLore(C.color(string));
        });
        plugin.getConfig().getStringList("Collector.Hologram").forEach(string -> {
            hologram.add(C.color(string));
        });

        NBTItem item = new NBTItem(collector);

        item.setString("CollectorLevel", "0:0:0");
        collector.setItemMeta(item.getItem().getItemMeta());

        allowedItems = (ArrayList<String>) plugin.getConfig().getStringList("AllowedDrops");
    }


}
