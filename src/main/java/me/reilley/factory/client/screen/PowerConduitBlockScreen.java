package me.reilley.factory.client.screen;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import me.reilley.factory.screen.ElectricFurnaceBlockGuiDescription;
import me.reilley.factory.screen.PowerConduitBlockGuiDescription;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class PowerConduitBlockScreen extends CottonInventoryScreen<PowerConduitBlockGuiDescription> {
    public PowerConduitBlockScreen(PowerConduitBlockGuiDescription description, PlayerEntity player, Text title) {
        super(description, player, title);
    }
}
