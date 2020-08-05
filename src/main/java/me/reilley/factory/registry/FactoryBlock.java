package me.reilley.factory.registry;

import me.reilley.factory.Factory;
import me.reilley.factory.blocks.conduits.power.PowerConduitBlock;
import me.reilley.factory.blocks.electricfurnace.ElectricFurnaceBlock;
import me.reilley.factory.blocks.frame.FrameBlock;
import me.reilley.factory.blocks.generator.GeneratorBlock;
import me.reilley.factory.blocks.macerator.MaceratorBlock;
import me.reilley.factory.blocks.quarry.QuarryBlock;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FactoryBlock {
    public static final Block ELECTRIC_FURNACE = register(ElectricFurnaceBlock.ID, new ElectricFurnaceBlock());
    public static final Block FRAME = register(FrameBlock.ID, new FrameBlock());
    public static final Block GENERATOR = register(GeneratorBlock.ID, new GeneratorBlock());
    public static final Block MACERATOR = register(MaceratorBlock.ID, new MaceratorBlock());
    public static final Block POWER_CONDUIT = register(PowerConduitBlock.ID, new PowerConduitBlock());
    public static final Block QUARRY = register(QuarryBlock.ID, new QuarryBlock());

    public static void initialize() {
    }

    public static <T extends Block> T register(String name, T block) {
        return Registry.register(Registry.BLOCK, new Identifier(Factory.MOD_ID, name), block);
    }
}
