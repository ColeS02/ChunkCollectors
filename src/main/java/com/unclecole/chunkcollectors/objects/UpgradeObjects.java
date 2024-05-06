package com.unclecole.chunkcollectors.objects;

public class UpgradeObjects {

    private int sellMulti;
    private int capacityUpgrade;
    private int chunkUpgrade;

    public UpgradeObjects() {
        sellMulti = 1;
        capacityUpgrade = 1;
        chunkUpgrade = 1;
    }

    public UpgradeObjects(int sellMulti, int capacityUpgrade, int chunkUpgrade) {
        this.sellMulti = sellMulti;
        this.capacityUpgrade = capacityUpgrade;
        this.chunkUpgrade = chunkUpgrade;
    }

    public int getSellMulti() {
        return sellMulti;
    }

    public int getCapacityUpgrade() {
        return capacityUpgrade;
    }

    public int getChunkUpgrade() {
        return chunkUpgrade;
    }

    public void setSellMulti(int sellMulti) {
        this.sellMulti = sellMulti;
    }

    public void setCapacityUpgrade(int capacityUpgrade) {
        this.capacityUpgrade = capacityUpgrade;
    }

    public void setChunkUpgrade(int chunkUpgrade) {
        this.chunkUpgrade = chunkUpgrade;
    }

    public void upgradeSellMulti() {
        this.sellMulti = sellMulti+1;
    }

    public void upgradeCapacity() {
        this.capacityUpgrade = capacityUpgrade + 1;
    }

    public void upgradeChunk() {
        this.chunkUpgrade = chunkUpgrade + 1;
    }
}
