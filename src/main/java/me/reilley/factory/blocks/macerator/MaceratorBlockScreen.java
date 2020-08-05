package me.reilley.factory.blocks.macerator;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class MaceratorBlockScreen extends CottonInventoryScreen<MaceratorBlockGuiDescription> {
    public MaceratorBlockScreen(MaceratorBlockGuiDescription description, PlayerEntity player, Text title) {
        super(description, player, title);
    }
}
