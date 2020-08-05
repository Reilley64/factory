package me.reilley.factory.registry;

import me.reilley.factory.Factory;
import me.reilley.factory.blocks.conduits.power.PowerConduitBlock;
import me.reilley.factory.blocks.conduits.power.PowerConduitBlockEntity;
import me.reilley.factory.blocks.electricfurnace.ElectricFurnaceBlock;
import me.reilley.factory.blocks.electricfurnace.ElectricFurnaceBlockEntity;
import me.reilley.factory.blocks.generator.GeneratorBlock;
import me.reilley.factory.blocks.generator.GeneratorBlockEntity;
import me.reilley.factory.blocks.macerator.MaceratorBlock;
import me.reilley.factory.blocks.macerator.MaceratorBlockEntity;
import me.reilley.factory.blocks.quarry.QuarryBlock;
import me.reilley.factory.blocks.quarry.QuarryBlockEntity;
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
