package me.reilley.factory.blocks.quarry;

import me.reilley.factory.Factory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class QuarryBlockEntity extends LootableContainerBlockEntity implements Tickable {
    public static DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private DefaultedList<ItemStack> inventory;
    protected int viewerCount;
    private int delay = 20;
    private BlockPos targetBlock;

    public QuarryBlockEntity() {
        super(Factory.QUARRY_ENTITY_TYPE);
        this.inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(tag)) {
            Inventories.fromTag(tag, this.inventory);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (!this.serializeLootTable(tag)) {
            Inventories.toTag(tag, this.inventory);
        }
        return tag;
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("factory.quarry");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    @Override
    public int size() {
        return 27;
    }

    @Override
    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1) {
            this.viewerCount = data;
            return true;
        } else {
            return super.onSyncedBlockEvent(type, data);
        }
    }

    @Override
    public void onOpen(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (this.viewerCount < 0) {
                this.viewerCount = 0;
            }

            ++this.viewerCount;
            this.onInvOpenOrClose();
        }
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.viewerCount;
            this.onInvOpenOrClose();
        }

    }

    protected void onInvOpenOrClose() {
        Block block = this.getCachedState().getBlock();
        if (block instanceof QuarryBlock) {
            this.world.addSyncedBlockEvent(this.pos, block, 1, this.viewerCount);
            this.world.updateNeighborsAlways(this.pos, block);
        }
    }

    public void tick(){
        --this.delay;
        if (this.delay <= 0) {
            this.removeBlock();
            delay = 20;
        }
    }

    private void removeBlock() {
        System.out.println(this.world.getBlockState(this.getPos()).get(FACING));
        if (targetBlock != null) System.out.println(targetBlock.toString());
        if (targetBlock == null) {
            switch(this.world.getBlockState(this.getPos()).get(FACING)) {
                case NORTH:
                    targetBlock = this.getPos().add(0, -1, 1);
                    break;

                case EAST:
                    targetBlock = this.getPos().add(-1, -1, 0);
                    break;

                case SOUTH:
                    targetBlock = this.getPos().add(0, -1, -1);
                    break;

                case WEST:
                    targetBlock = this.getPos().add(1, -1, 0);
                    break;
            }
        } else {
            switch(this.world.getBlockState(this.getPos()).get(FACING)) {
                case NORTH:
                    targetBlock = targetBlock.add(0, 0, 1);
                    break;

                case EAST:
                    targetBlock = targetBlock.add(-1, 0, 0);
                    break;

                case SOUTH:
                    targetBlock = targetBlock.add(0, 0, -1);
                    break;

                case WEST:
                    targetBlock = targetBlock.add(1, 0, 0);
                    break;
            }
        }

        assert this.world != null;
        if (!this.world.isClient) {
            this.world.removeBlock(targetBlock, false);
        }
    }
}
