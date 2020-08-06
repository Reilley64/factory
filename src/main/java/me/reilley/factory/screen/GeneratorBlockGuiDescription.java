package me.reilley.factory.screen;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import me.reilley.factory.FactoryClient;
import me.reilley.factory.registry.FactoryScreenHandlerType;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;

public class GeneratorBlockGuiDescription extends SyncedGuiDescription {
    public GeneratorBlockGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(FactoryScreenHandlerType.GENERATOR, syncId, playerInventory, getBlockInventory(context, 1), getBlockPropertyDelegate(context, 4));

        WGridPanel root = new WGridPanel();
        setRootPanel(root);

        WItemSlot wItemSlot = WItemSlot.of(blockInventory, 0);
        wItemSlot.setFilter(AbstractFurnaceBlockEntity::canUseAsFuel);
        root.add(wItemSlot, 4, 1);

        WBar energyBar = new WBar(FactoryClient.ENERGY_BAR_BG, FactoryClient.ENERGY_BAR, 0, 1, WBar.Direction.UP);
        energyBar.withTooltip("tooltip.factory.power_bar");
        root.add(energyBar, 0, 1);

        WBar wBar = new WBar(FactoryClient.FIRE_BAR_BG, FactoryClient.FIRE_BAR, 2, 3, WBar.Direction.UP);
        root.add(wBar, 4, 2);

        root.add(this.createPlayerInventoryPanel(), 0, 3);

        root.validate(this);
    }
}
