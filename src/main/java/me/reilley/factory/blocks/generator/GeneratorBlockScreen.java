package me.reilley.factory.blocks.generator;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class GeneratorBlockScreen extends CottonInventoryScreen<GeneratorBlockGuiDescription> {
    public GeneratorBlockScreen(GeneratorBlockGuiDescription description, PlayerEntity player, Text title) {
        super(description, player, title);
    }
}
