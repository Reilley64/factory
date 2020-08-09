package me.reilley.factory.screen;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
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
        super(FactoryScreenHandlerType.POWER_CONDUIT, syncId, playerInventory, getBlockInventory(context, 1), getBlockPropertyDelegate(context, 2));

        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(100, 0);

        WToggleButton extractButton = new WToggleButton(new LiteralText("Extract"));
        System.out.println(((PowerConduitBlockEntity) world.getBlockEntity(blockPos)).isExtract());
        extractButton.setToggle(((PowerConduitBlockEntity) world.getBlockEntity(blockPos)).isExtract());
        extractButton.setOnToggle((on) -> {
            PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
            packetByteBuf.writeBlockPos(blockPos);
            packetByteBuf.writeBoolean(on);
            ClientSidePacketRegistry.INSTANCE.sendToServer(Factory.POWER_CONDUIT_EXTRACT, packetByteBuf);
            ((PowerConduitBlockEntity) world.getBlockEntity(blockPos)).setExtract(on);
        });
        root.add(extractButton, 0, 1);

        WToggleButton insertButton = new WToggleButton(new LiteralText("Insert"));
        insertButton.setToggle(((PowerConduitBlockEntity) world.getBlockEntity(blockPos)).isInsert());
        insertButton.setOnToggle((on) -> {
            PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
            packetByteBuf.writeBlockPos(blockPos);
            packetByteBuf.writeBoolean(on);
            ClientSidePacketRegistry.INSTANCE.sendToServer(Factory.POWER_CONDUIT_INSERT, packetByteBuf);
            ((PowerConduitBlockEntity) world.getBlockEntity(blockPos)).setInsert(on);
        });
        root.add(insertButton, 0, 2);

        root.validate(this);
    }
}
