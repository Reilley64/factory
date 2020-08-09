package me.reilley.factory.registry;

import me.reilley.factory.Factory;
import me.reilley.factory.block.*;
import me.reilley.factory.screen.*;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class FactoryScreenHandlerType {
    public static final ScreenHandlerType<ElectricFurnaceBlockGuiDescription> ELECTRIC_FURNACE = register(ElectricFurnaceBlock.ID, (syncId, inventory) -> new ElectricFurnaceBlockGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY));
    public static final ScreenHandlerType<GeneratorBlockGuiDescription> GENERATOR = register(GeneratorBlock.ID, (syncId, inventory) -> new GeneratorBlockGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY));
    public static final ScreenHandlerType<PowerConduitBlockGuiDescription> POWER_CONDUIT = ScreenHandlerRegistry.registerExtended(new Identifier(Factory.MOD_ID, PowerConduitBlock.ID), (syncId, inventory, buf) -> new PowerConduitBlockGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY, buf.readBlockPos()));
    public static final ScreenHandlerType<PulverizerBlockGuiDescription> PULVERIZER = register(PulverizerBlock.ID, (syncId, inventory) -> new PulverizerBlockGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY));
    public static final ScreenHandlerType<QuarryBlockGuiDescription> QUARRY = register(QuarryBlock.ID, (syncId, inventory) -> new QuarryBlockGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY));

    public static void initialize() {
    }

    public static <T extends ScreenHandler> ScreenHandlerType<T> register(String name, ScreenHandlerRegistry.SimpleClientHandlerFactory<T> factory) {
        return ScreenHandlerRegistry.registerSimple(new Identifier(Factory.MOD_ID, name), factory);
    }
}
