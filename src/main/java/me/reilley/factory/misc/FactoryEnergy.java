package me.reilley.factory.misc;

public interface FactoryEnergy {
    int getEnergy();

    void setEnergy(int value);

    int getMaxPowerInput();

    int getMaxPowerOutput();

    default int extractPower(int amount) {
        int minAmount = Math.min(amount, getEnergy());
        setEnergy(getEnergy() - minAmount);
        return minAmount;
    }

    default void insertPower(int amount) {
        setEnergy(getEnergy() + amount);
    }

    default boolean canInsertPower() {
        return false;
    }

    default boolean canExtractPower() {
        return false;
    }
}
