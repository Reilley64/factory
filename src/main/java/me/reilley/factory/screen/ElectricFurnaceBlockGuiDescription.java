package me.reilley.factory.screen;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import me.reilley.factory.FactoryClient;
import me.reilley.factory.registry.FactoryScreenHandlerType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;

public class ElectricFurnaceBlockGuiDescription extends SyncedGuiDescription {
    public ElectricFurnaceBlockGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(FactoryScreenHandlerType.ELECTRIC_FURNACE, syncId, playerInventory, getBlockInventory(context, 2), getBlockPropertyDelegate(context, 4));

        WGridPanel root = new WGridPanel();
        setRootPanel(root);

        root.add(WItemSlot.of(blockInventory, 0), 3, 1);
        WItemSlot outputSlot = WItemSlot.of(blockInventory, 1);
        outputSlot.setFilter((itemStack) -> false);
        root.add(outputSlot, 6, 1);

        WBar energyBar = new WBar(FactoryClient.ENERGY_BAR_BG, FactoryClient.ENERGY_BAR, 0, 1, WBar.Direction.UP);
        energyBar.withTooltip("tooltip.factory.power_bar");
        root.add(energyBar, 0, 1);

        WBar progressBar = new WBar(FactoryClient.PROGRESS_BAR_BG, FactoryClient.PROGRESS_BAR, 2, 3, WBar.Direction.RIGHT);
        root.add(progressBar, 4, 1, 2, 1);

        root.add(this.createPlayerInventoryPanel(), 0, 2);

        root.validate(this);
    }
}
