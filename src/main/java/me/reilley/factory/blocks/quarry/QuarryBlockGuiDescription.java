package me.reilley.factory.blocks.quarry;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import me.reilley.factory.Factory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QuarryBlockGuiDescription extends SyncedGuiDescription {
    public QuarryBlockGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(Factory.QUARRY_SCREEN_HANDLER_TYPE, syncId, playerInventory, getBlockInventory(context, 27), getBlockPropertyDelegate(context));

        WGridPanel root = new WGridPanel();
        setRootPanel(root);

        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < blockInventory.size(); i++) slots.add(i);
        Iterator<Integer> iterator = slots.iterator();
        for (int y = 0; y < blockInventory.size() / 9; y++) {
            for (int x = 0; x < 9; x++) {
                WItemSlot itemSlot = WItemSlot.of(blockInventory, iterator.next());
                root.add(itemSlot, x, y + 1);
            }
        }

        root.add(this.createPlayerInventoryPanel(), 0, 4);

        root.validate(this);
    }
}
