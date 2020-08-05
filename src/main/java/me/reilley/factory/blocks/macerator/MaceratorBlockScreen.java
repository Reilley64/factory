package me.reilley.factory.blocks.macerator;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import me.reilley.factory.blocks.electricfurnace.ElectricFurnaceBlockGuiDescription;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class MaceratorBlockScreen extends CottonInventoryScreen<ElectricFurnaceBlockGuiDescription> {
    public MaceratorBlockScreen(ElectricFurnaceBlockGuiDescription description, PlayerEntity player, Text title) {
        super(description, player, title);
    }
}
