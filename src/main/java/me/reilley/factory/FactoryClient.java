package me.reilley.factory;

import me.reilley.factory.blocks.generator.GeneratorBlockGuiDescription;
import me.reilley.factory.blocks.generator.GeneratorBlockScreen;
import me.reilley.factory.blocks.quarry.QuarryBlockGuiDescription;
import me.reilley.factory.blocks.quarry.QuarryBlockScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

public class FactoryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.<QuarryBlockGuiDescription, QuarryBlockScreen>register(Factory.QUARRY_SCREEN_HANDLER_TYPE, (gui, inventory, title) -> new QuarryBlockScreen(gui, inventory.player, title));
        ScreenRegistry.<GeneratorBlockGuiDescription, GeneratorBlockScreen>register(Factory.GENERATOR_SCREEN_HANDLER_TYPE, (gui, inventory, title) -> new GeneratorBlockScreen(gui, inventory.player, title));
    }
}
