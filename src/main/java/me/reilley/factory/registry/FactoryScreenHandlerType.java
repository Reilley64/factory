package me.reilley.factory.registry;

import me.reilley.factory.Factory;
import me.reilley.factory.block.ElectricFurnaceBlock;
import me.reilley.factory.screen.ElectricFurnaceBlockGuiDescription;
import me.reilley.factory.block.GeneratorBlock;
import me.reilley.factory.screen.GeneratorBlockGuiDescription;
import me.reilley.factory.block.MaceratorBlock;
import me.reilley.factory.screen.MaceratorBlockGuiDescription;
import me.reilley.factory.block.QuarryBlock;
import me.reilley.factory.screen.QuarryBlockGuiDescription;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class FactoryScreenHandlerType {
    public static final ScreenHandlerType<ElectricFurnaceBlockGuiDescription> ELECTRIC_FURNACE = register(ElectricFurnaceBlock.ID, (syncId, inventory) -> new ElectricFurnaceBlockGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY));
    public static final ScreenHandlerType<GeneratorBlockGuiDescription> GENERATOR = register(GeneratorBlock.ID, (syncId, inventory) -> new GeneratorBlockGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY));
    public static final ScreenHandlerType<MaceratorBlockGuiDescription> MACERATOR = register(MaceratorBlock.ID, (syncId, inventory) -> new MaceratorBlockGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY));
    public static final ScreenHandlerType<QuarryBlockGuiDescription> QUARRY = register(QuarryBlock.ID, (syncId, inventory) -> new QuarryBlockGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY));

    public static void initialize() {
    }

    public static <T extends ScreenHandler> ScreenHandlerType<T> register(String name, ScreenHandlerRegistry.SimpleClientHandlerFactory<T> factory) {
        return ScreenHandlerRegistry.registerSimple(new Identifier(Factory.MOD_ID, name), factory);
    }
}
