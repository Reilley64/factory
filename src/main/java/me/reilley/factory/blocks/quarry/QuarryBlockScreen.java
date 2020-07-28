package me.reilley.factory.blocks.quarry;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class QuarryBlockScreen extends CottonInventoryScreen<QuarryBlockGuiDescription> {
    public QuarryBlockScreen(QuarryBlockGuiDescription description, PlayerEntity player, Text title) {
        super(description, player, title);
    }
}
