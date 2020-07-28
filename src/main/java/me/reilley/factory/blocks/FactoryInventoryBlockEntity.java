package me.reilley.factory.blocks;

import me.reilley.factory.blocks.quarry.QuarryBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

import java.util.List;

public abstract class FactoryInventoryBlockEntity extends BlockEntity implements SidedInventory {
    protected DefaultedList<ItemStack> inventory;
    private int viewerCount;

    public FactoryInventoryBlockEntity(BlockEntityType<?> type, int size) {
        super(type);
        this.inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[inventory.size()];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack(this.inventory, slot, amount);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }
        return itemStack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        this.markDirty();
    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    private void onInvOpenOrClose() {
        Block block = this.getCachedState().getBlock();
        if (block instanceof QuarryBlock) {
            this.world.addSyncedBlockEvent(this.pos, block, 1, this.viewerCount);
            this.world.updateNeighborsAlways(this.pos, block);
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

    public boolean canItemStacksBeAddedToInventory(DefaultedList<ItemStack> inventory, List<ItemStack> itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            boolean canInsert = false;

            for (ItemStack stack : inventory) {
                if (stack.isItemEqual(itemStack) && stack.getCount() + itemStack.getCount() <= stack.getMaxCount()) {
                    canInsert = true;
                    break;
                }
            }

            for (ItemStack stack : inventory) {
                if (stack.isEmpty()) {
                    canInsert = true;
                    break;
                }
            }

            if (canInsert) continue;
            return false;
        }

        return true;
    }

    public void addItemStackToInventory(DefaultedList<ItemStack> inventory, ItemStack itemStack) {
        for (ItemStack stack : inventory) {
            if (stack.isItemEqual(itemStack) && stack.getCount() + itemStack.getCount() <= stack.getMaxCount()) {
                stack.setCount(stack.getCount() + itemStack.getCount());
                return;
            }
        }

        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).isEmpty()) {
                inventory.set(i, itemStack);
                return;
            }
        }
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }
}
