package me.reilley.factory.screen;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import io.netty.buffer.Unpooled;
import me.reilley.factory.Factory;
import me.reilley.factory.block.entity.PowerConduitBlockEntity;
import me.reilley.factory.registry.FactoryScreenHandlerType;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;

public class PowerConduitBlockGuiDescription extends SyncedGuiDescription {
    public PowerConduitBlockGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, BlockPos blockPos) {
        super(FactoryScreenHandlerType.POWER_CONDUIT, syncId, playerInventory, getBlockInventory(context, 1), getBlockPropertyDelegate(context));

        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(100, 0);

        WDynamicLabel mode = new WDynamicLabel(() -> ((PowerConduitBlockEntity) world.getBlockEntity(blockPos)).getMode().asString());
        root.add(mode, 0, 1);

        WButton changeMode = new WButton(new LiteralText("Change mode"));
        changeMode.setSize(20, 100);
        changeMode.setOnClick(() -> {
            PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
            packetByteBuf.writeBlockPos(blockPos);
            ClientSidePacketRegistry.INSTANCE.sendToServer(Factory.POWER_CONDUIT_MODE, packetByteBuf);
            ((PowerConduitBlockEntity) world.getBlockEntity(blockPos)).nextMode();
        });
        root.add(changeMode, 1, 2);

        root.validate(this);
    }
}
