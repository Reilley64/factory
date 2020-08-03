package me.reilley.factory.blocks.electricfurnace;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class ElectricFurnaceBlockScreen extends CottonInventoryScreen<ElectricFurnaceBlockGuiDescription> {
    public ElectricFurnaceBlockScreen(ElectricFurnaceBlockGuiDescription description, PlayerEntity player, Text title) {
        super(description, player, title);
    }
}
