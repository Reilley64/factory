package me.reilley.factory.client.screen;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import me.reilley.factory.screen.MaceratorBlockGuiDescription;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class MaceratorBlockScreen extends CottonInventoryScreen<MaceratorBlockGuiDescription> {
    public MaceratorBlockScreen(MaceratorBlockGuiDescription description, PlayerEntity player, Text title) {
        super(description, player, title);
    }
}
