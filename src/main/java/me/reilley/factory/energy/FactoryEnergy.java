package me.reilley.factory.energy;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

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

    default void energyTick(World world, BlockPos pos) {
        for (Direction side : Direction.values()) {
            BlockEntity blockEntity = world.getBlockEntity(pos.offset(side));
            if (blockEntity instanceof FactoryEnergy) {
                FactoryEnergy factoryEnergyBlockEntity = (FactoryEnergy) blockEntity;
                if (factoryEnergyBlockEntity.getMaxEnergyInput() > 0)
                    factoryEnergyBlockEntity.insertEnergy(extractEnergy(
                            Math.min(
                                    Math.min(getMaxEnergyOutput(), factoryEnergyBlockEntity.getMaxEnergyInput()),
                                    factoryEnergyBlockEntity.getEnergyCapacity() - factoryEnergyBlockEntity.getEnergy()
                            )
                    ));
            }
        }
    }
}
