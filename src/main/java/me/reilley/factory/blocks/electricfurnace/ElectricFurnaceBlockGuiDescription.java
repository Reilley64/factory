package me.reilley.factory.blocks.electricfurnace;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import me.reilley.factory.Factory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;

public class ElectricFurnaceBlockGuiDescription extends SyncedGuiDescription {
    public ElectricFurnaceBlockGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(Factory.ELECTRIC_FURNACE_SCREEN_HANDLER_TYPE, syncId, playerInventory, getBlockInventory(context, 2), getBlockPropertyDelegate(context, 3));

        WGridPanel root = new WGridPanel();
        setRootPanel(root);

        WDynamicLabel label = new WDynamicLabel(() -> String.format("%s/%s", propertyDelegate.get(1), propertyDelegate.get(2)));
        root.add(label, 4, 1, 4, 1);

        root.add(WItemSlot.of(blockInventory, 0), 2, 1);
        root.add(WItemSlot.of(blockInventory, 1), 6, 1);

        root.add(this.createPlayerInventoryPanel(), 0, 3);

        root.validate(this);
    }
}
