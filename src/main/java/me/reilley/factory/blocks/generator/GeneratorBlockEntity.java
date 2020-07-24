package me.reilley.factory.blocks.generator;

import me.reilley.factory.Factory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;

public class GeneratorBlockEntity extends BlockEntity implements Tickable {
    private int energy;

    public GeneratorBlockEntity() {
        super(Factory.GENERATOR_ENTITY_TYPE);
    }

    @Override
    public void tick() {
        energy += 20;
        System.out.println(energy);
    }
}
