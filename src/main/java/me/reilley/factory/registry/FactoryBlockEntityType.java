package me.reilley.factory.registry;

import me.reilley.factory.Factory;
import me.reilley.factory.block.*;
import me.reilley.factory.block.entity.*;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class FactoryBlockEntityType {
    public static final BlockEntityType<BatteryEntity> BATTERY = register(Battery.ID, BatteryEntity::new, FactoryBlock.BATTERY);
    public static final BlockEntityType<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE = register(ElectricFurnaceBlock.ID, ElectricFurnaceBlockEntity::new, FactoryBlock.ELECTRIC_FURNACE);
    public static final BlockEntityType<GeneratorBlockEntity> GENERATOR = register(GeneratorBlock.ID, GeneratorBlockEntity::new, FactoryBlock.GENERATOR);
    public static final BlockEntityType<PowerConduitBlockEntity> POWER_CONDUIT = register(PowerConduitBlock.ID, PowerConduitBlockEntity::new, FactoryBlock.POWER_CONDUIT);
    public static final BlockEntityType<PulverizerBlockEntity> PULVERIZER = register(PulverizerBlock.ID, PulverizerBlockEntity::new, FactoryBlock.PULVERIZER);
    public static final BlockEntityType<QuarryBlockEntity> QUARRY = register(QuarryBlock.ID, QuarryBlockEntity::new, FactoryBlock.QUARRY);

    public static void initialize() {
    }

    public static <T extends BlockEntity> BlockEntityType<T> register(String name, Supplier<T> supplier, Block block) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Factory.MOD_ID, name), BlockEntityType.Builder.create(supplier, block).build(null));
    }
}
