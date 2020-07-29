package me.reilley.factory.blocks.generator;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import me.reilley.factory.Factory;
import me.reilley.factory.blocks.FactoryInventoryBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.*;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

public class GeneratorBlockEntity extends FactoryInventoryBlockEntity implements NamedScreenHandlerFactory, PropertyDelegateHolder, Tickable {
    private int energy = 0;
    private int burnTime = 0;

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return energy;

                case 1:
                    return burnTime;
            }

            return -1;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    energy = value;
                    break;

                case 1:
                    burnTime = value;
                    break;
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.fromTag(tag, this.inventory);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        Inventories.toTag(tag, this.inventory);
        return tag;
    }

    @Override
    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }

    public GeneratorBlockEntity() {
        super(Factory.GENERATOR_ENTITY_TYPE, 1);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new GeneratorBlockGuiDescription(syncId, inventory, ScreenHandlerContext.create(this.world, this.pos));
    }

    @Override
    public boolean isValid(int slot, ItemStack itemStack) {
        return AbstractFurnaceBlockEntity.canUseAsFuel(itemStack);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return this.isValid(slot, stack);
    }

    @Override
    public void tick() {
        if (burnTime == 0 && !inventory.get(0).isEmpty()) {
            burnTime += AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(inventory.get(0).getItem(), 0);
            inventory.get(0).decrement(1);
        } else if (burnTime > 0) {
            burnTime--;
            energy += 2;
        }
    }
}
