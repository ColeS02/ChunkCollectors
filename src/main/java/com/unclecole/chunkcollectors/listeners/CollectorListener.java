package com.unclecole.chunkcollectors.listeners;

import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.unclecole.chunkcollectors.ChunkCollectors;
import com.unclecole.chunkcollectors.database.ChunkCollectorData;
import com.unclecole.chunkcollectors.objects.CollectorObject;
import com.unclecole.chunkcollectors.objects.cubeObject;
import com.unclecole.chunkcollectors.utils.C;
import com.unclecole.chunkcollectors.utils.PlaceHolder;
import com.unclecole.chunkcollectors.utils.TL;
import de.tr7zw.nbtapi.NBTItem;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.itemutils.ItemBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;

public class CollectorListener implements Listener {

    private ChunkCollectors plugin;

    public CollectorListener(ChunkCollectors plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {


        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        Player player = event.getPlayer();

        if(event.getClickedBlock() == null) return;

        if(event.getClickedBlock().getType().equals(plugin.getConfigUtils().getCollector().getType())) {
            if(!ChunkCollectorData.chunkCollectorData.containsKey(plugin.getLocationUtility().parseToString(event.getClickedBlock().getChunk()))) return;
            if(ChunkCollectorData.chunkCollectorData.get(plugin.getLocationUtility().parseToString(event.getClickedBlock().getChunk())).getLocation().equals(plugin.getLocationUtility().parseToString(event.getClickedBlock().getLocation()))) {
                Location location = event.getClickedBlock().getLocation();
                    event.setCancelled(true);

                    if(plugin.getSuperiorSkyblock().getPlayers().getSuperiorPlayer(player.getUniqueId()) == null && !player.hasPermission("chunkcollectors.admin")) return;
                    SuperiorPlayer user = plugin.getSuperiorSkyblock().getPlayers().getSuperiorPlayer(player.getUniqueId());

                    if(plugin.getSuperiorSkyblock().getGrid().getIslandAt(location.getChunk()) == null) return;
                    if (plugin.getSuperiorSkyblock().getGrid().getIslandAt(location.getChunk()).isMember(user) || player.hasPermission("chunkcollectors.admin"))
                        mainGUI(player, location);
                    else
                        TL.NO_PERMISSION.send(player);
            }
        } else if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(plugin.getConfigUtils().getCollector().getType())) {
            NBTItem item = new NBTItem(event.getPlayer().getInventory().getItemInMainHand());

            if(item.hasKey("CollectorLevel")) {

                if(ChunkCollectorData.chunkCollectorData.containsKey(plugin.getLocationUtility().parseToString(event.getClickedBlock().getChunk()))) {
                    TL.ALREADY_COLLECTOR_IN_CHUNK.send(player);
                    event.setCancelled(true);
                    return;
                }

                Block placed = event.getClickedBlock().getRelative(event.getBlockFace());
                BlockState state = placed.getState();
                Block against = event.getClickedBlock();

                placed.setType(plugin.getConfigUtils().getCollector().getType());

                BlockPlaceEvent e = new BlockPlaceEvent(placed, state, against, plugin.getConfigUtils().getCollector(), player, true, EquipmentSlot.HAND);
                Bukkit.getPluginManager().callEvent(e);

                Location loc = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation();

                if(plugin.getSuperiorSkyblock().getGrid().getIslandAt(loc.getChunk()) == null) {
                    TL.MUST_BE_PLACED_ON_ISLAND.send(player);
                    e.setCancelled(true);
                    event.setCancelled(true);
                } else if(!plugin.getSuperiorSkyblock().getGrid().getIslandAt(loc.getChunk()).isMember(plugin.getSuperiorSkyblock().getPlayers().getSuperiorPlayer(player.getUniqueId()))) {
                    TL.MUST_BE_PLACED_ON_ISLAND.send(player);
                    e.setCancelled(true);
                    event.setCancelled(true);
                }

                if(player.getGameMode() != GameMode.CREATIVE && !e.isCancelled()) {
                    event.getPlayer().getInventory().getItemInMainHand().setAmount(event.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
                }

                if(e.isCancelled()) {
                    placed.setType(Material.AIR);
                    return;
                }


                /*String hologramName = randomWord();
                while(DHAPI.getHologram(hologramName) != null) {
                    hologramName = randomWord();
                }*/

                /*Hologram hologram = DHAPI.createHologram(hologramName, loc.clone().add(0.5, 2,0.5), false, plugin.getConfigUtils().getHologram());
                hologram.setDisplayRange(25);*/

                TL.PLACED_COLLECTOR.send(player);

                String key = item.getString("CollectorLevel");

                String[] data = key.split(":");

                //ChunkCollectorData.chunkCollectorData.put(plugin.getLocationUtility().parseToString(loc.getChunk()),new CollectorObject(loc, hologramName, Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2])));
                ChunkCollectorData.chunkCollectorData.put(plugin.getLocationUtility().parseToString(loc.getChunk()),new CollectorObject(loc, Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2])));
            }
        }

    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if(!event.getBlock().getType().equals(plugin.getConfigUtils().getCollector().getType())) return;

        if(!ChunkCollectorData.chunkCollectorData.containsKey(plugin.getLocationUtility().parseToString(event.getBlock().getChunk()))) return;
        if(ChunkCollectorData.chunkCollectorData.get(plugin.getLocationUtility().parseToString(event.getBlock().getChunk())).getLocation().equals(plugin.getLocationUtility().parseToString(event.getBlock().getLocation()))) {

            CollectorObject collector = ChunkCollectorData.chunkCollectorData.get(plugin.getLocationUtility().parseToString(event.getBlock().getChunk()));

            event.setDropItems(false);

            ItemStack itemStack = plugin.getConfigUtils().getCollector().toItemStack();
            NBTItem item = new NBTItem(itemStack);
            item.setString("CollectorLevel",
                    collector.getUpgrades().getSellMulti() +
                            ":"
                            + collector.getUpgrades().getCapacityUpgrade() + ":" + collector.getUpgrades().getChunkUpgrade());

            item.applyNBT(itemStack);

            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), itemStack);
            //DHAPI.removeHologram(collector.getCollectorName());
            ChunkCollectorData.chunkCollectorData.remove(plugin.getLocationUtility().parseToString(event.getBlock().getChunk()));
        }
    }
    /*@EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {

        String chunk = ChunkCollectors.getInstance().getLocationUtility().parseToString(event.getChunk());

        if(ChunkCollectorData.chunkData.containsKey(chunk)) {
            String hologram = ChunkCollectorData.chunkData.get(chunk);

            DHAPI.createHologram(hologram, )
        }
    }*/

    public void mainGUI(Player player, Location location) {
        InventoryGUI gui = new InventoryGUI(plugin.getConfigUtils().getCollectorMenuSize(), C.color(plugin.getConfigUtils().getCollectorMenuTitle()));

        ItemStack dropsItem = plugin.getConfigUtils().getDropsItem();
        ItemStack upgradesItem = plugin.getConfigUtils().getUpgradeItem();
        ItemStack barrierItem = plugin.getConfigUtils().getBarrierItem();
        ItemStack experienceItem = new ItemBuilder(Material.REDSTONE);
        gui.fill(0, plugin.getConfigUtils().getCollectorMenuSize(), new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE));

        ItemButton dropsButton = ItemButton.create(dropsItem, clickEvent -> {
            dropsGUI(player, location);
        });

        ItemButton upgradesButton = ItemButton.create(upgradesItem, clickEvent -> {
            upgradesGUI(player, location);
        });

        ItemButton experienceButton = ItemButton.create(experienceItem, clickEvent -> {
           experienceGUI(player, location);
        });

        ItemButton barrier = ItemButton.create(barrierItem, clickEvent -> {
           Chunk chunk = location.getChunk();
           CollectorObject collector = ChunkCollectorData.chunkCollectorData.get(plugin.getLocationUtility().parseToString(location.getChunk()));

           int y = location.getBlockY();
           int minX = (chunk.getX()-collector.getUpgrades().getChunkUpgrade()) << 4;
           int minZ = (chunk.getZ()-collector.getUpgrades().getChunkUpgrade()) << 4;

           Location corner1 = new Location(location.getWorld(), minX, y, minZ);

           int maxX = ((chunk.getX()+collector.getUpgrades().getChunkUpgrade()) << 4)+16;
           int maxZ = ((chunk.getZ()+collector.getUpgrades().getChunkUpgrade()) << 4)+16;

           Location corner2 = new Location(location.getWorld(), maxX, y, maxZ);
           if(!ChunkCollectors.getInstance().getViewBarrierList().containsKey(clickEvent.getWhoClicked().getUniqueId())) {
               TL.ACTIVATED_COLLECTOR_RADIUS.send(player);
               ChunkCollectors.getInstance().getViewBarrierList().put(clickEvent.getWhoClicked().getUniqueId(), new cubeObject(corner1, corner2));
           } else {
               TL.DEACTIVATED_COLLECTOR_RADIUS.send(player);
               ChunkCollectors.getInstance().getViewBarrierList().remove(clickEvent.getWhoClicked().getUniqueId());
           }
        });

        gui.addButton(plugin.getConfigUtils().getDropsItemSlot(), dropsButton);
        gui.addButton(plugin.getConfigUtils().getUpgradeItemSlot(), upgradesButton);
        gui.addButton(plugin.getConfigUtils().getBarrierItemSlot(), barrier);
        //gui.addButton(2, experienceButton);
        gui.open(player);
    }

    public void dropsGUI(Player player, Location location) {
        InventoryGUI dropsMenu = new InventoryGUI(plugin.getConfigUtils().getUpgradeMenuSize(), C.color(plugin.getConfigUtils().getUpgradeMenuTitle()));

        dropsMenu.fill(0, plugin.getConfigUtils().getCollectorMenuSize(), new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE));

        dropsMenu.addButton(plugin.getConfigUtils().getUpgradeMenuSize()-1, new ItemButton(plugin.getConfigUtils().getBackButton()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                mainGUI(player,location);
            }
        });

        CollectorObject collector = ChunkCollectorData.chunkCollectorData.get(plugin.getLocationUtility().parseToString(location.getChunk()));

        Iterator<Map.Entry<String, Integer>> listDrops = collector.getDrops().entrySet().iterator();

        int i = 0;
        while(listDrops.hasNext()) {
            Map.Entry<String, Integer> map = listDrops.next();

            String drop = String.valueOf(map.getKey());
            int amount = map.getValue();

            ItemBuilder item = new ItemBuilder(Material.valueOf(drop));

            item.setName(C.color(plugin.getConfigUtils().getGenericDropTitle().replaceAll("%type%", item.getType().name()).replaceAll("%amount%", String.valueOf(amount))));

            double itemPrice = 0.0;
            if(plugin.getShop().getShopManager().findShopItemByItemStack(player, item.toItemStack(), false) != null) {
                itemPrice = plugin.getShop().getShopManager().findShopItemByItemStack(player, item.toItemStack(), false).getSellPriceForAmount(amount)*plugin.getConfigUtils().getSellMultiUpgrades().get(collector.getUpgrades().getSellMulti()).getUpgrade();
            }

            itemPrice = round(itemPrice, 2);
            double finalItemPrice1 = itemPrice;
            plugin.getConfigUtils().getGenericDropLore().forEach(string -> {
                item.addLore(C.color(string
                        .replace("%type%", item.getType().name())
                        .replace("%amount%", NumberFormat.getIntegerInstance().format(amount))
                        .replace("%price%", NumberFormat.getCurrencyInstance().format(finalItemPrice1))));
            });
            double finalItemPrice = itemPrice;
            dropsMenu.addButton(i, new ItemButton(item) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    map.setValue(map.getValue() - amount);

                    plugin.getEconomy().depositPlayer(player, finalItemPrice);
                    TL.SOLD_ITEM.send(player,
                            new PlaceHolder("%amount%", NumberFormat.getIntegerInstance().format(amount)),
                            new PlaceHolder("%price%", NumberFormat.getCurrencyInstance().format(finalItemPrice)),
                            new PlaceHolder("%item%", drop));
                    dropsGUI(player,location);
                }
            });
            i++;
        }
        dropsMenu.open(player);
    }

    public void upgradesGUI(Player player, Location location) {
        InventoryGUI upgradeMenu = new InventoryGUI(plugin.getConfigUtils().getUpgradeMenuSize(), C.color(plugin.getConfigUtils().getUpgradeMenuTitle()));

        upgradeMenu.fill(0, plugin.getConfigUtils().getCollectorMenuSize(), new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE));

        upgradeMenu.addButton(plugin.getConfigUtils().getUpgradeMenuSize()-1, new ItemButton(plugin.getConfigUtils().getBackButton()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                mainGUI(player,location);
            }
        });

        CollectorObject collector = ChunkCollectorData.chunkCollectorData.get(plugin.getLocationUtility().parseToString(location.getChunk()));

        ItemBuilder sellMultiItem = new ItemBuilder(plugin.getConfigUtils().getSellMultiMaterial());
        sellMultiItem.setName(C.color(plugin.getConfigUtils().getSellMultiTitle()
                .replaceAll("%level%", (collector.getUpgrades().getSellMulti()+1) + "")));
        plugin.getConfigUtils().getSellMultiLore().forEach(string -> {
            if(collector.getUpgrades().getSellMulti()+1 >= plugin.getConfigUtils().getSellMultiUpgrades().size()) {
                sellMultiItem.addLore(C.color(string
                        .replace("%level%", (collector.getUpgrades().getSellMulti()+1) + "")
                        .replace("%multi%", plugin.getConfigUtils().getSellMultiUpgrades().get(collector.getUpgrades().getSellMulti()).getUpgrade() + "")
                        .replace("%next_multi%",plugin.getConfigUtils().getSellMultiUpgrades().get(collector.getUpgrades().getSellMulti()).getUpgrade() + "")
                        .replace("%cost%", "MAX")));
            } else {
                sellMultiItem.addLore(C.color(string
                        .replace("%level%", (collector.getUpgrades().getSellMulti()+1) + "")
                        .replace("%multi%", plugin.getConfigUtils().getSellMultiUpgrades().get(collector.getUpgrades().getSellMulti()).getUpgrade() + "")
                        .replace("%next_multi%", plugin.getConfigUtils().getSellMultiUpgrades().get(collector.getUpgrades().getSellMulti()+1).getUpgrade() + "")
                        .replace("%cost%", NumberFormat.getCurrencyInstance().format(plugin.getConfigUtils().getSellMultiUpgrades().get(collector.getUpgrades().getSellMulti()+1).getCost()))));
            }
        });

        upgradeMenu.addButton(plugin.getConfigUtils().getSellMultiSlot(), new ItemButton(sellMultiItem) {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(collector.getUpgrades().getSellMulti()+1 >= plugin.getConfigUtils().getSellMultiUpgrades().size()) {
                    TL.ALREADY_MAX_LEVEL.send(player);
                    return;
                }
                if(plugin.getEconomy().getBalance(player) < plugin.getConfigUtils().getSellMultiUpgrades().get(collector.getUpgrades().getSellMulti()+1).getCost()) {
                    TL.INSUFFICENT_FUNDS.send(player, new PlaceHolder("%cost%", plugin.getConfigUtils().getSellMultiUpgrades().get(collector.getUpgrades().getSellMulti()+1).getCost()));
                    return;
                }

                collector.getUpgrades().upgradeSellMulti();
                plugin.getEconomy().withdrawPlayer(player, plugin.getConfigUtils().getSellMultiUpgrades().get(collector.getUpgrades().getSellMulti()).getCost());

                TL.UPGRADED.send(player,
                        new PlaceHolder("%type%", "Sell Multiplier"),
                        new PlaceHolder("%level%", (collector.getUpgrades().getSellMulti()+1)));

                upgradesGUI(player, location);
            }
        });

        ItemBuilder chunkSizeItem = new ItemBuilder(plugin.getConfigUtils().getChunkSizeMaterial());
        chunkSizeItem.setName(C.color(plugin.getConfigUtils().getChunkSizeTitle()));
        plugin.getConfigUtils().getChunkSizeLore().forEach(string -> {
            if(collector.getUpgrades().getChunkUpgrade()+1 >= plugin.getConfigUtils().getChunkUpgrades().size()) {
                chunkSizeItem.addLore(C.color(string)
                        .replace("%level%", (collector.getUpgrades().getChunkUpgrade()+1) + "")
                        .replace("%radius%", (plugin.getConfigUtils().getChunkUpgrades().get(collector.getUpgrades().getChunkUpgrade()).getUpgrade()+1) + "")
                        .replace("%next_radius%",(plugin.getConfigUtils().getChunkUpgrades().get(collector.getUpgrades().getChunkUpgrade()).getUpgrade()+1) + "")
                        .replace("%cost%", "MAX"));
            } else {
                chunkSizeItem.addLore(C.color(string)
                        .replace("%level%", (collector.getUpgrades().getChunkUpgrade()+1) + "")
                        .replace("%radius%", (plugin.getConfigUtils().getChunkUpgrades().get(collector.getUpgrades().getChunkUpgrade()).getUpgrade()+1) + "")
                        .replace("%next_radius%", (plugin.getConfigUtils().getChunkUpgrades().get(collector.getUpgrades().getChunkUpgrade()+1).getUpgrade()+1) + "")
                        .replace("%cost%", NumberFormat.getCurrencyInstance().format(plugin.getConfigUtils().getChunkUpgrades().get(collector.getUpgrades().getChunkUpgrade()+1).getCost())));
            }
        });
        upgradeMenu.addButton(plugin.getConfigUtils().getChunkSizeSlot(), new ItemButton(chunkSizeItem) {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(collector.getUpgrades().getChunkUpgrade()+1 >= plugin.getConfigUtils().getChunkUpgrades().size()) {
                    TL.ALREADY_MAX_LEVEL.send(player);
                    return;
                }
                if(plugin.getEconomy().getBalance(player) < plugin.getConfigUtils().getChunkUpgrades().get(collector.getUpgrades().getChunkUpgrade()+1).getCost()) {
                    TL.INSUFFICENT_FUNDS.send(player, new PlaceHolder("%cost%", plugin.getConfigUtils().getChunkUpgrades().get(collector.getUpgrades().getChunkUpgrade()+1).getCost()));

                    return;
                }

                collector.getUpgrades().upgradeChunk();
                plugin.getEconomy().withdrawPlayer(player, plugin.getConfigUtils().getChunkUpgrades().get(collector.getUpgrades().getChunkUpgrade()).getCost());
                if(ChunkCollectors.getInstance().getViewBarrierList().containsKey(player.getUniqueId())) {
                    Chunk chunk = location.getChunk();
                    int y = location.getBlockY();
                    int minX = (chunk.getX() - collector.getUpgrades().getChunkUpgrade()) << 4;
                    int minZ = (chunk.getZ() - collector.getUpgrades().getChunkUpgrade()) << 4;

                    Location corner1 = new Location(location.getWorld(), minX, y, minZ);

                    int maxX = ((chunk.getX() + collector.getUpgrades().getChunkUpgrade()) << 4) + 16;
                    int maxZ = ((chunk.getZ() + collector.getUpgrades().getChunkUpgrade()) << 4) + 16;

                    Location corner2 = new Location(location.getWorld(), maxX, y, maxZ);
                    ChunkCollectors.getInstance().getViewBarrierList().put(player.getUniqueId(), new cubeObject(corner1, corner2));
                }

                TL.UPGRADED.send(player,
                        new PlaceHolder("%type%", "Chunk Size"),
                        new PlaceHolder("%level%", (collector.getUpgrades().getChunkUpgrade()+1)));

                upgradesGUI(player, location);
            }
        });

        ItemBuilder capacitySizeItem = new ItemBuilder(plugin.getConfigUtils().getCapacityMaterial());
        capacitySizeItem.setName(C.color(plugin.getConfigUtils().getCapacityTitle().replaceAll("%level%", (collector.getUpgrades().getCapacityUpgrade()+1) + "")));
        plugin.getConfigUtils().getCapacityLore().forEach(string -> {
            if(collector.getUpgrades().getCapacityUpgrade()+1 >= plugin.getConfigUtils().getCapacityUpgrades().size()) {
                capacitySizeItem.addLore(C.color(string)
                        .replace("%level%", (collector.getUpgrades().getCapacityUpgrade()+1) + "")
                        .replace("%capacity%", plugin.getConfigUtils().getCapacityUpgrades().get(collector.getUpgrades().getCapacityUpgrade()).getUpgrade() + "")
                        .replace("%next_capacity%",plugin.getConfigUtils().getCapacityUpgrades().get(collector.getUpgrades().getCapacityUpgrade()).getUpgrade() + "")
                        .replace("%cost%", "MAX"));
            } else {
                capacitySizeItem.addLore(C.color(string)
                        .replace("%level%", (collector.getUpgrades().getCapacityUpgrade()+1) + "")
                        .replace("%capacity%", plugin.getConfigUtils().getCapacityUpgrades().get(collector.getUpgrades().getCapacityUpgrade()).getUpgrade() + "")
                        .replace("%next_capacity%", plugin.getConfigUtils().getCapacityUpgrades().get(collector.getUpgrades().getCapacityUpgrade()+1).getUpgrade() + "")
                        .replace("%cost%", NumberFormat.getCurrencyInstance().format(plugin.getConfigUtils().getCapacityUpgrades().get(collector.getUpgrades().getCapacityUpgrade()+1).getCost())));
            }
        });
        upgradeMenu.addButton(plugin.getConfigUtils().getCapacitySlot(), new ItemButton(capacitySizeItem) {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(collector.getUpgrades().getCapacityUpgrade()+1 >= plugin.getConfigUtils().getCapacityUpgrades().size()) {
                    TL.ALREADY_MAX_LEVEL.send(player);
                    return;
                }
                if(plugin.getEconomy().getBalance(player) < plugin.getConfigUtils().getCapacityUpgrades().get(collector.getUpgrades().getCapacityUpgrade()+1).getCost()) {
                    TL.INSUFFICENT_FUNDS.send(player, new PlaceHolder("%cost%", plugin.getConfigUtils().getCapacityUpgrades().get(collector.getUpgrades().getCapacityUpgrade()+1).getCost()));
                    return;
                }

                collector.getUpgrades().upgradeCapacity();
                plugin.getEconomy().withdrawPlayer(player, plugin.getConfigUtils().getCapacityUpgrades().get(collector.getUpgrades().getCapacityUpgrade()).getCost());

                TL.UPGRADED.send(player,
                        new PlaceHolder("%type%", "Capacity"),
                        new PlaceHolder("%level%", (collector.getUpgrades().getCapacityUpgrade()+1)));

                upgradesGUI(player, location);
            }
        });

        upgradeMenu.open(player);
    }

    public void experienceGUI(Player player, Location location) {
        InventoryGUI upgradeMenu = new InventoryGUI(plugin.getConfigUtils().getUpgradeMenuSize(), C.color(plugin.getConfigUtils().getUpgradeMenuTitle()));

        upgradeMenu.fill(0, plugin.getConfigUtils().getCollectorMenuSize(), new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE));

        upgradeMenu.addButton(plugin.getConfigUtils().getUpgradeMenuSize()-1, new ItemButton(plugin.getConfigUtils().getBackButton()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                mainGUI(player,location);
            }
        });

        CollectorObject collector = ChunkCollectorData.chunkCollectorData.get(plugin.getLocationUtility().parseToString(location.getChunk()));

        ItemBuilder sellMultiItem = new ItemBuilder(plugin.getConfigUtils().getSellMultiMaterial());
        sellMultiItem.setName(C.color(plugin.getConfigUtils().getSellMultiTitle()
                .replaceAll("%level%", (collector.getUpgrades().getSellMulti()+1) + "")));
        plugin.getConfigUtils().getSellMultiLore().forEach(string -> {
            if(collector.getUpgrades().getSellMulti()+1 >= plugin.getConfigUtils().getSellMultiUpgrades().size()) {
                sellMultiItem.addLore(C.color(string
                        .replace("%level%", (collector.getUpgrades().getSellMulti()+1) + "")
                        .replace("%multi%", plugin.getConfigUtils().getSellMultiUpgrades().get(collector.getUpgrades().getSellMulti()).getUpgrade() + "")
                        .replace("%next_multi%",plugin.getConfigUtils().getSellMultiUpgrades().get(collector.getUpgrades().getSellMulti()).getUpgrade() + "")
                        .replace("%cost%", "MAX")));
            } else {
                sellMultiItem.addLore(C.color(string
                        .replace("%level%", (collector.getUpgrades().getSellMulti()+1) + "")
                        .replace("%multi%", plugin.getConfigUtils().getSellMultiUpgrades().get(collector.getUpgrades().getSellMulti()).getUpgrade() + "")
                        .replace("%next_multi%", plugin.getConfigUtils().getSellMultiUpgrades().get(collector.getUpgrades().getSellMulti()+1).getUpgrade() + "")
                        .replace("%cost%", NumberFormat.getCurrencyInstance().format(plugin.getConfigUtils().getSellMultiUpgrades().get(collector.getUpgrades().getSellMulti()+1).getCost()))));
            }
        });

        upgradeMenu.addButton(plugin.getConfigUtils().getSellMultiSlot(), new ItemButton(sellMultiItem) {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(collector.getUpgrades().getSellMulti()+1 >= plugin.getConfigUtils().getSellMultiUpgrades().size()) {
                    TL.ALREADY_MAX_LEVEL.send(player);
                    return;
                }
                if(plugin.getEconomy().getBalance(player) < plugin.getConfigUtils().getSellMultiUpgrades().get(collector.getUpgrades().getSellMulti()+1).getCost()) {
                    TL.INSUFFICENT_FUNDS.send(player, new PlaceHolder("%cost%", plugin.getConfigUtils().getSellMultiUpgrades().get(collector.getUpgrades().getSellMulti()+1).getCost()));
                    return;
                }

                collector.getUpgrades().upgradeSellMulti();
                plugin.getEconomy().withdrawPlayer(player, plugin.getConfigUtils().getSellMultiUpgrades().get(collector.getUpgrades().getSellMulti()).getCost());

                TL.UPGRADED.send(player,
                        new PlaceHolder("%type%", "Sell Multiplier"),
                        new PlaceHolder("%level%", (collector.getUpgrades().getSellMulti()+1)));

                upgradesGUI(player, location);
            }
        });

        ItemBuilder chunkSizeItem = new ItemBuilder(plugin.getConfigUtils().getChunkSizeMaterial());
        chunkSizeItem.setName(C.color(plugin.getConfigUtils().getChunkSizeTitle()));
        plugin.getConfigUtils().getChunkSizeLore().forEach(string -> {
            if(collector.getUpgrades().getChunkUpgrade()+1 >= plugin.getConfigUtils().getChunkUpgrades().size()) {
                chunkSizeItem.addLore(C.color(string)
                        .replace("%level%", (collector.getUpgrades().getChunkUpgrade()+1) + "")
                        .replace("%radius%", (plugin.getConfigUtils().getChunkUpgrades().get(collector.getUpgrades().getChunkUpgrade()).getUpgrade()+1) + "")
                        .replace("%next_radius%",(plugin.getConfigUtils().getChunkUpgrades().get(collector.getUpgrades().getChunkUpgrade()).getUpgrade()+1) + "")
                        .replace("%cost%", "MAX"));
            } else {
                chunkSizeItem.addLore(C.color(string)
                        .replace("%level%", (collector.getUpgrades().getChunkUpgrade()+1) + "")
                        .replace("%radius%", (plugin.getConfigUtils().getChunkUpgrades().get(collector.getUpgrades().getChunkUpgrade()).getUpgrade()+1) + "")
                        .replace("%next_radius%", (plugin.getConfigUtils().getChunkUpgrades().get(collector.getUpgrades().getChunkUpgrade()+1).getUpgrade()+1) + "")
                        .replace("%cost%", NumberFormat.getCurrencyInstance().format(plugin.getConfigUtils().getChunkUpgrades().get(collector.getUpgrades().getChunkUpgrade()+1).getCost())));
            }
        });
        upgradeMenu.addButton(plugin.getConfigUtils().getChunkSizeSlot(), new ItemButton(chunkSizeItem) {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(collector.getUpgrades().getChunkUpgrade()+1 >= plugin.getConfigUtils().getChunkUpgrades().size()) {
                    TL.ALREADY_MAX_LEVEL.send(player);
                    return;
                }
                if(plugin.getEconomy().getBalance(player) < plugin.getConfigUtils().getChunkUpgrades().get(collector.getUpgrades().getChunkUpgrade()+1).getCost()) {
                    TL.INSUFFICENT_FUNDS.send(player, new PlaceHolder("%cost%", plugin.getConfigUtils().getChunkUpgrades().get(collector.getUpgrades().getChunkUpgrade()+1).getCost()));

                    return;
                }

                collector.getUpgrades().upgradeChunk();
                plugin.getEconomy().withdrawPlayer(player, plugin.getConfigUtils().getChunkUpgrades().get(collector.getUpgrades().getChunkUpgrade()).getCost());
                if(ChunkCollectors.getInstance().getViewBarrierList().containsKey(player.getUniqueId())) {
                    Chunk chunk = location.getChunk();
                    int y = location.getBlockY();
                    int minX = (chunk.getX() - collector.getUpgrades().getChunkUpgrade()) << 4;
                    int minZ = (chunk.getZ() - collector.getUpgrades().getChunkUpgrade()) << 4;

                    Location corner1 = new Location(location.getWorld(), minX, y, minZ);

                    int maxX = ((chunk.getX() + collector.getUpgrades().getChunkUpgrade()) << 4) + 16;
                    int maxZ = ((chunk.getZ() + collector.getUpgrades().getChunkUpgrade()) << 4) + 16;

                    Location corner2 = new Location(location.getWorld(), maxX, y, maxZ);
                    ChunkCollectors.getInstance().getViewBarrierList().put(player.getUniqueId(), new cubeObject(corner1, corner2));
                }

                TL.UPGRADED.send(player,
                        new PlaceHolder("%type%", "Chunk Size"),
                        new PlaceHolder("%level%", (collector.getUpgrades().getChunkUpgrade()+1)));

                upgradesGUI(player, location);
            }
        });

        ItemBuilder capacitySizeItem = new ItemBuilder(plugin.getConfigUtils().getCapacityMaterial());
        capacitySizeItem.setName(C.color(plugin.getConfigUtils().getCapacityTitle().replaceAll("%level%", (collector.getUpgrades().getCapacityUpgrade()+1) + "")));
        plugin.getConfigUtils().getCapacityLore().forEach(string -> {
            if(collector.getUpgrades().getCapacityUpgrade()+1 >= plugin.getConfigUtils().getCapacityUpgrades().size()) {
                capacitySizeItem.addLore(C.color(string)
                        .replace("%level%", (collector.getUpgrades().getCapacityUpgrade()+1) + "")
                        .replace("%capacity%", plugin.getConfigUtils().getCapacityUpgrades().get(collector.getUpgrades().getCapacityUpgrade()).getUpgrade() + "")
                        .replace("%next_capacity%",plugin.getConfigUtils().getCapacityUpgrades().get(collector.getUpgrades().getCapacityUpgrade()).getUpgrade() + "")
                        .replace("%cost%", "MAX"));
            } else {
                capacitySizeItem.addLore(C.color(string)
                        .replace("%level%", (collector.getUpgrades().getCapacityUpgrade()+1) + "")
                        .replace("%capacity%", plugin.getConfigUtils().getCapacityUpgrades().get(collector.getUpgrades().getCapacityUpgrade()).getUpgrade() + "")
                        .replace("%next_capacity%", plugin.getConfigUtils().getCapacityUpgrades().get(collector.getUpgrades().getCapacityUpgrade()+1).getUpgrade() + "")
                        .replace("%cost%", NumberFormat.getCurrencyInstance().format(plugin.getConfigUtils().getCapacityUpgrades().get(collector.getUpgrades().getCapacityUpgrade()+1).getCost())));
            }
        });
        upgradeMenu.addButton(plugin.getConfigUtils().getCapacitySlot(), new ItemButton(capacitySizeItem) {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(collector.getUpgrades().getCapacityUpgrade()+1 >= plugin.getConfigUtils().getCapacityUpgrades().size()) {
                    TL.ALREADY_MAX_LEVEL.send(player);
                    return;
                }
                if(plugin.getEconomy().getBalance(player) < plugin.getConfigUtils().getCapacityUpgrades().get(collector.getUpgrades().getCapacityUpgrade()+1).getCost()) {
                    TL.INSUFFICENT_FUNDS.send(player, new PlaceHolder("%cost%", plugin.getConfigUtils().getCapacityUpgrades().get(collector.getUpgrades().getCapacityUpgrade()+1).getCost()));
                    return;
                }

                collector.getUpgrades().upgradeCapacity();
                plugin.getEconomy().withdrawPlayer(player, plugin.getConfigUtils().getCapacityUpgrades().get(collector.getUpgrades().getCapacityUpgrade()).getCost());

                TL.UPGRADED.send(player,
                        new PlaceHolder("%type%", "Capacity"),
                        new PlaceHolder("%level%", (collector.getUpgrades().getCapacityUpgrade()+1)));

                upgradesGUI(player, location);
            }
        });

        upgradeMenu.open(player);
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public String randomWord() {
        Random random = new Random();
        StringBuilder word = new StringBuilder(10);

        for(int i = 0; i < 10; i++) {
            word.append(('a') + random.nextInt(26));
        }
        return word.toString();
    }

}
