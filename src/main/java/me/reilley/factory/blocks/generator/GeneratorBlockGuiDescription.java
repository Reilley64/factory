package me.reilley.factory.blocks.generator;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import me.reilley.factory.Factory;
import me.reilley.factory.FactoryClient;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;

public class GeneratorBlockGuiDescription extends SyncedGuiDescription {
    public GeneratorBlockGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(Factory.GENERATOR_SCREEN_HANDLER_TYPE, syncId, playerInventory, getBlockInventory(context, 1), getBlockPropertyDelegate(context, 3));

        WGridPanel root = new WGridPanel();
        setRootPanel(root);

        WDynamicLabel label = new WDynamicLabel(() -> String.valueOf(propertyDelegate.get(0)));
        root.add(label, 0, 1, 4, 1);

        WItemSlot wItemSlot = WItemSlot.of(blockInventory, 0);
        wItemSlot.setFilter(AbstractFurnaceBlockEntity::canUseAsFuel);
        root.add(wItemSlot, 4, 1);

        WBar wBar = new WBar(FactoryClient.FIRE_BAR_BG, FactoryClient.FIRE_BAR, 1, 2);
        root.add(wBar, 4, 2);

        root.add(this.createPlayerInventoryPanel(), 0, 3);

        root.validate(this);
    }
}
