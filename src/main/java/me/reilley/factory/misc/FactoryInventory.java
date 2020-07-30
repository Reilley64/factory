package me.reilley.factory.misc;

import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

import java.util.List;

public interface FactoryInventory extends SidedInventory {
    DefaultedList<ItemStack> getInventory();

    default int[] getAvailableSlots(Direction side) {
        return new int[getInventory().size()];
    }

    default boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    default boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    default int size() {
        return getInventory().size();
    }

    default boolean isEmpty() {
        return getInventory().stream().allMatch(ItemStack::isEmpty);
    }

    default ItemStack getStack(int slot) {
        return getInventory().get(slot);
    }

    default ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack(getInventory(), slot, amount);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }
        return itemStack;
    }

    default ItemStack removeStack(int slot) {
        return Inventories.removeStack(getInventory(), slot);
    }

    default void setStack(int slot, ItemStack stack) {
        getInventory().set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        this.markDirty();
    }

    default int getMaxCountPerStack() {
        return 64;
    }

    default boolean canItemStacksBeAddedToInventory(List<ItemStack> itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            boolean canInsert = false;

            for (ItemStack stack : getInventory()) {
                if (stack.isItemEqual(itemStack) && stack.getCount() + itemStack.getCount() <= stack.getMaxCount()) {
                    canInsert = true;
                    break;
                }
            }

            for (ItemStack stack : getInventory()) {
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

    default void addItemStackToInventory(ItemStack itemStack) {
        for (ItemStack stack : getInventory()) {
            if (stack.isItemEqual(itemStack) && stack.getCount() + itemStack.getCount() <= stack.getMaxCount()) {
                stack.setCount(stack.getCount() + itemStack.getCount());
                return;
            }
        }

        for (int i = 0; i < getInventory().size(); i++) {
            if (getInventory().get(i).isEmpty()) {
                getInventory().set(i, itemStack);
                return;
            }
        }
    }

    @Override
    default void clear() {
        getInventory().clear();
    }
}
