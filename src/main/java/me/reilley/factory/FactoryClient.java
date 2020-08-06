package me.reilley.factory;

import me.reilley.factory.screen.ElectricFurnaceBlockGuiDescription;
import me.reilley.factory.client.screen.ElectricFurnaceBlockScreen;
import me.reilley.factory.screen.GeneratorBlockGuiDescription;
import me.reilley.factory.client.screen.GeneratorBlockScreen;
import me.reilley.factory.screen.MaceratorBlockGuiDescription;
import me.reilley.factory.client.screen.MaceratorBlockScreen;
import me.reilley.factory.screen.QuarryBlockGuiDescription;
import me.reilley.factory.client.screen.QuarryBlockScreen;
import me.reilley.factory.registry.FactoryBlock;
import me.reilley.factory.registry.FactoryScreenHandlerType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class FactoryClient implements ClientModInitializer {
    public static final Identifier ENERGY_BAR = new Identifier(Factory.MOD_ID, "textures/gui/energy_bar.png");
    public static final Identifier ENERGY_BAR_BG = new Identifier(Factory.MOD_ID, "textures/gui/energy_bar_bg.png");

    public static final Identifier FIRE_BAR = new Identifier(Factory.MOD_ID, "textures/gui/fire_bar.png");
    public static final Identifier FIRE_BAR_BG = new Identifier(Factory.MOD_ID, "textures/gui/fire_bar_bg.png");

    public static final Identifier PROGRESS_BAR = new Identifier(Factory.MOD_ID, "textures/gui/progress_bar.png");
    public static final Identifier PROGRESS_BAR_BG = new Identifier(Factory.MOD_ID, "textures/gui/progress_bar_bg.png");

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(FactoryBlock.FRAME, RenderLayer.getCutout());
        ScreenRegistry.<QuarryBlockGuiDescription, QuarryBlockScreen>register(FactoryScreenHandlerType.QUARRY, (gui, inventory, title) -> new QuarryBlockScreen(gui, inventory.player, title));
        ScreenRegistry.<GeneratorBlockGuiDescription, GeneratorBlockScreen>register(FactoryScreenHandlerType.GENERATOR, (gui, inventory, title) -> new GeneratorBlockScreen(gui, inventory.player, title));
        ScreenRegistry.<ElectricFurnaceBlockGuiDescription, ElectricFurnaceBlockScreen>register(FactoryScreenHandlerType.ELECTRIC_FURNACE, (gui, inventory, title) -> new ElectricFurnaceBlockScreen(gui, inventory.player, title));
        ScreenRegistry.<MaceratorBlockGuiDescription, MaceratorBlockScreen>register(FactoryScreenHandlerType.MACERATOR, (gui, inventory, title) -> new MaceratorBlockScreen(gui, inventory.player, title));
    }
}
