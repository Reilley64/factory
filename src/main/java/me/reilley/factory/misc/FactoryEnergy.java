package me.reilley.factory.misc;

public interface FactoryEnergy {
    double getEnergy();

    void setEnergy(double energy);

    double getEnergyCapacity();

    double getMaxEnergyInput();

    double getMaxEnergyOutput();

    default double extractEnergy(double energy) {
        double minAmount = Math.min(energy, getEnergy());
        setEnergy(getEnergy() - minAmount);
        return minAmount;
    }

    default void insertEnergy(double energy) {
        setEnergy(getEnergy() + energy);
    }
}
