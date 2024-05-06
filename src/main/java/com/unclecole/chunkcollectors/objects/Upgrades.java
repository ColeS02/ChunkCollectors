package com.unclecole.chunkcollectors.objects;

public class Upgrades<T> {

    private T upgrade;
    private long cost;

    public Upgrades(T upgrade, long cost) {
        this.upgrade = upgrade;
        this.cost = cost;
    }

    public long getCost() {
        return cost;
    }

    public T getUpgrade() {
        return upgrade;
    }

}
