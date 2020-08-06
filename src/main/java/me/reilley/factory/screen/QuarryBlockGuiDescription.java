package me.reilley.factory.screen;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import me.reilley.factory.registry.FactoryScreenHandlerType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QuarryBlockGuiDescription extends SyncedGuiDescription {
    public QuarryBlockGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(FactoryScreenHandlerType.QUARRY, syncId, playerInventory, getBlockInventory(context, 27), getBlockPropertyDelegate(context));

        WGridPanel root = new WGridPanel();
        setRootPanel(root);

        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < blockInventory.size(); i++) slots.add(i);
        Iterator<Integer> iterator = slots.iterator();
        for (int y = 0; y < blockInventory.size() / 9; y++) {
            for (int x = 0; x < 9; x++) {
                root.add(WItemSlot.of(blockInventory, iterator.next()), x, y + 1);
            }
        }

        root.add(this.createPlayerInventoryPanel(), 0, 4);

        root.validate(this);
    }
}
