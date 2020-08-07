package me.reilley.factory.registry;

import me.reilley.factory.Factory;
import me.reilley.factory.block.PowerConduitBlock;
import me.reilley.factory.block.ElectricFurnaceBlock;
import me.reilley.factory.block.FrameBlock;
import me.reilley.factory.block.GeneratorBlock;
import me.reilley.factory.block.PulverizerBlock;
import me.reilley.factory.block.QuarryBlock;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FactoryBlock {
    public static final Block ELECTRIC_FURNACE = register(ElectricFurnaceBlock.ID, new ElectricFurnaceBlock());
    public static final Block FRAME = register(FrameBlock.ID, new FrameBlock());
    public static final Block GENERATOR = register(GeneratorBlock.ID, new GeneratorBlock());
    public static final Block POWER_CONDUIT = register(PowerConduitBlock.ID, new PowerConduitBlock());
    public static final Block PULVERIZER = register(PulverizerBlock.ID, new PulverizerBlock());
    public static final Block QUARRY = register(QuarryBlock.ID, new QuarryBlock());

    public static void initialize() {
    }

    public static <T extends Block> T register(String name, T block) {
        return Registry.register(Registry.BLOCK, new Identifier(Factory.MOD_ID, name), block);
    }
}
