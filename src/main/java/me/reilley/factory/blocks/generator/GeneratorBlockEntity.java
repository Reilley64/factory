package me.reilley.factory.blocks.generator;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import me.reilley.factory.Factory;
import me.reilley.factory.blocks.quarry.QuarryBlock;
import me.reilley.factory.misc.FactoryEnergy;
import me.reilley.factory.misc.FactoryInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
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

public class GeneratorBlockEntity extends BlockEntity implements FactoryEnergy, FactoryInventory, NamedScreenHandlerFactory, PropertyDelegateHolder, Tickable {
    private DefaultedList<ItemStack> inventory;
    private int viewerCount;
    private int energy = 0;
    private int burnTime = 0;
    private int fuelTime = 0;

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return energy;

                case 1:
                    return burnTime;

                case 2:
                    return fuelTime;
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

                case 2:
                    fuelTime = value;
                    break;
            }
        }

        @Override
        public int size() {
            return 3;
        }
    };

    public GeneratorBlockEntity() {
        super(Factory.GENERATOR_ENTITY_TYPE);
        this.inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.fromTag(tag, this.inventory);
        this.energy = tag.getShort("Energy");
        this.burnTime = tag.getShort("BurnTime");
        this.fuelTime = tag.getShort("FuelTime");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        Inventories.toTag(tag, this.inventory);
        tag.putShort("Energy", (short) this.energy);
        tag.putShort("BurnTime", (short) this.burnTime);
        tag.putShort("FuelTime", (short) this.fuelTime);
        return tag;
    }

    @Override
    public int getEnergy() {
        return energy;
    }

    @Override
    public void setEnergy(int value) {
        this.energy = value;
    }

    @Override
    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
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
    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return this.isValid(slot, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
                    (double) this.pos.getZ() + 0.5D) <= 64.0D;
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

    @Override
    public boolean isValid(int slot, ItemStack itemStack) {
        return AbstractFurnaceBlockEntity.canUseAsFuel(itemStack);
    }

    private void onInvOpenOrClose() {
        Block block = this.getCachedState().getBlock();
        if (block instanceof QuarryBlock) {
            this.world.addSyncedBlockEvent(this.pos, block, 1, this.viewerCount);
            this.world.updateNeighborsAlways(this.pos, block);
        }
    }

    @Override
    public void tick() {
        if (burnTime == 0 && !inventory.get(0).isEmpty()) {
            GeneratorBlock.setActive(true, this.world, this.pos);
            fuelTime = AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(inventory.get(0).getItem(), 0);
            burnTime = fuelTime;
            inventory.get(0).decrement(1);
        } else if (burnTime > 0) {
            burnTime--;
            energy += 1;
            if (burnTime == 0) GeneratorBlock.setActive(false, this.world, this.pos);
        }
    }
}
