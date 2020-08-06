package me.reilley.factory.client.screen;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import me.reilley.factory.screen.GeneratorBlockGuiDescription;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class GeneratorBlockScreen extends CottonInventoryScreen<GeneratorBlockGuiDescription> {
    public GeneratorBlockScreen(GeneratorBlockGuiDescription description, PlayerEntity player, Text title) {
        super(description, player, title);
    }
}
