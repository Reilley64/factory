package me.reilley.factory.registry;

import me.reilley.factory.Factory;
import me.reilley.factory.block.PowerConduitBlock;
import me.reilley.factory.block.entity.PowerConduitBlockEntity;
import me.reilley.factory.block.ElectricFurnaceBlock;
import me.reilley.factory.block.entity.ElectricFurnaceBlockEntity;
import me.reilley.factory.block.GeneratorBlock;
import me.reilley.factory.block.entity.GeneratorBlockEntity;
import me.reilley.factory.block.MaceratorBlock;
import me.reilley.factory.block.entity.MaceratorBlockEntity;
import me.reilley.factory.block.QuarryBlock;
import me.reilley.factory.block.entity.QuarryBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class FactoryBlockEntityType {
    public static final BlockEntityType<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE = register(ElectricFurnaceBlock.ID, ElectricFurnaceBlockEntity::new, FactoryBlock.ELECTRIC_FURNACE);
    public static final BlockEntityType<GeneratorBlockEntity> GENERATOR = register(GeneratorBlock.ID, GeneratorBlockEntity::new, FactoryBlock.GENERATOR);
    public static final BlockEntityType<MaceratorBlockEntity> MACERATOR = register(MaceratorBlock.ID, MaceratorBlockEntity::new, FactoryBlock.MACERATOR);
    public static final BlockEntityType<PowerConduitBlockEntity> POWER_CONDUIT = register(PowerConduitBlock.ID, PowerConduitBlockEntity::new, FactoryBlock.POWER_CONDUIT);
    public static final BlockEntityType<QuarryBlockEntity> QUARRY = register(QuarryBlock.ID, QuarryBlockEntity::new, FactoryBlock.QUARRY);

    public static void initialize() {
    }

    public static <T extends BlockEntity> BlockEntityType<T> register(String name, Supplier<T> supplier, Block block) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Factory.MOD_ID, name), BlockEntityType.Builder.create(supplier, block).build(null));
    }
}
