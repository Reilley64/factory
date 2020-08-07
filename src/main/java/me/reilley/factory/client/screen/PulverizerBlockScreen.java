package me.reilley.factory.client.screen;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import me.reilley.factory.screen.PulverizerBlockGuiDescription;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class PulverizerBlockScreen extends CottonInventoryScreen<PulverizerBlockGuiDescription> {
    public PulverizerBlockScreen(PulverizerBlockGuiDescription description, PlayerEntity player, Text title) {
        super(description, player, title);
    }
}
