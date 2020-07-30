package me.reilley.factory.misc;

public interface FactoryEnergy {
    int getEnergy();

    void setEnergy(int value);

    default int extract(int amount) {
        int minAmount = Math.min(amount, getEnergy());
        setEnergy(getEnergy() - minAmount);
        return minAmount;
    }

    default void insert(int amount) {
        setEnergy(getEnergy() + amount);
    }
}
