package me.reilley.factory.blocks.conduits.power;

import me.reilley.factory.Factory;
import me.reilley.factory.misc.FactoryEnergy;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;

public class PowerConduitBlockEntity extends BlockEntity implements FactoryEnergy, Tickable {
    private double energy = 0;
    private int counter = 0;

    public PowerConduitBlockEntity() {
        super(Factory.POWER_CONDUIT_ENTITY_TYPE);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.energy = tag.getShort("Energy");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putShort("Energy", (short) this.energy);
        return tag;
    }

    @Override
    public double getEnergy() {
        return this.energy;
    }

    @Override
    public void setEnergy(double energy) {
        this.energy = energy;
    }

    @Override
    public double getEnergyCapacity() {
        return 4;
    }

    @Override
    public double getMaxEnergyInput() {
        return 1;
    }

    @Override
    public double getMaxEnergyOutput() {
        return 1;
    }

    @Override
    public void tick() {
        if (!this.world.isClient) {
            ArrayList<PowerConduitBlockEntity> cables = new ArrayList<>();

            for (Direction side : Direction.values()) {
                BlockEntity blockEntity = getWorld().getBlockEntity(this.pos.offset(side));
                if (blockEntity instanceof FactoryEnergy) {
                    if (blockEntity instanceof  PowerConduitBlockEntity) {
                        cables.add((PowerConduitBlockEntity) blockEntity);
                    } else {
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

            if (!cables.isEmpty()) {
                cables.add(this);
                double energyTotal = cables.stream().mapToDouble(PowerConduitBlockEntity::getEnergy).sum();
                double energyPer = energyTotal / cables.size();
                cables.forEach(cableBlockEntity -> cableBlockEntity.setEnergy(energyPer));
            }
        }
    }
}
