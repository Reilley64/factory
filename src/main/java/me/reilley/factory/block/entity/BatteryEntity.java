package me.reilley.factory.block.entity;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import me.reilley.factory.block.Battery;
import me.reilley.factory.energy.FactoryEnergy;
import me.reilley.factory.registry.FactoryBlockEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.Tickable;

public class BatteryEntity extends BlockEntity implements FactoryEnergy, PropertyDelegateHolder, Tickable {
    private final PropertyDelegate propertyDelegate;
    private double energy = 0;

    public BatteryEntity() {
        super(FactoryBlockEntityType.BATTERY);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                switch (index) {
                    case 0:
                        return (int) energy;

                    case 1:
                        return (int) getEnergyCapacity();
                }
                return -1;
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        energy = value;
                        break;

                    case 1:
                        break;
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
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
        return energy;
    }

    @Override
    public void setEnergy(double energy) {
        this.energy = energy;
    }

    @Override
    public double getEnergyCapacity() {
        return 40000;
    }

    @Override
    public double getMaxEnergyInput() {
        return 32;
    }

    @Override
    public double getMaxEnergyOutput() {
        return 32;
    }

    @Override
    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }

    @Override
    public void tick() {
        if (!this.world.isClient) {
            energyTick(this.world, this.pos);
            Battery.setIndicator(this.getEnergyCapacity(), this.getEnergy(), this.world, this.pos);
        }
    }
}
