package me.reilley.factory.block.entity;

import blue.endless.jankson.annotation.Nullable;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import me.reilley.factory.block.ElectricFurnaceBlock;
import me.reilley.factory.block.PulverizerBlock;
import me.reilley.factory.block.QuarryBlock;
import me.reilley.factory.energy.FactoryEnergy;
import me.reilley.factory.inventory.FactoryInventory;
import me.reilley.factory.recipe.CrushingRecipe;
import me.reilley.factory.registry.FactoryBlockEntityType;
import me.reilley.factory.screen.PulverizerBlockGuiDescription;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class PulverizerBlockEntity extends BlockEntity implements FactoryEnergy, FactoryInventory, NamedScreenHandlerFactory, PropertyDelegateHolder, Tickable {
    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{1};
    private static final int[] SIDE_SLOTS = new int[]{1};
    private final PropertyDelegate propertyDelegate;
    private DefaultedList<ItemStack> inventory;
    private int viewerCount;
    private double energy = 0;
    private int crushTime = 0;
    private int crushTimeTotal = 0;
    private ItemStack inputStack;

    public PulverizerBlockEntity() {
        super(FactoryBlockEntityType.PULVERIZER);
        this.inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
        this.inputStack = ItemStack.EMPTY;
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                switch (index) {
                    case 0:
                        return (int) energy;

                    case 1:
                        return (int) getEnergyCapacity();

                    case 2:
                        return crushTime;

                    case 3:
                        return crushTimeTotal;
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
                        break;

                    case 2:
                        crushTime = value;
                        break;

                    case 3:
                        crushTimeTotal = value;
                        break;
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.fromTag(tag, this.inventory);
        this.inputStack = this.inventory.get(0);
        this.energy = tag.getShort("Energy");
        this.crushTime = tag.getShort("CrushTime");
        this.crushTimeTotal = tag.getShort("CrushTimeTotal");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        Inventories.toTag(tag, this.inventory);
        tag.putShort("Energy", (short) this.energy);
        tag.putShort("CrushTime", (short) this.crushTime);
        tag.putShort("CrushTimeTotal", (short) this.crushTimeTotal);
        return tag;
    }

    @Override
    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }

    @Override
    public double getEnergy() {
        return energy;
    }

    @Override
    public void setEnergy(double energy) {
        this.energy = energy;
    }

    @Override
    public double getEnergyCapacity() {
        return 1200;
    }

    @Override
    public double getMaxEnergyInput() {
        return 32;
    }

    @Override
    public double getMaxEnergyOutput() {
        return 0;
    }

    @Override
    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) return BOTTOM_SLOTS;
        else return side == Direction.UP ? TOP_SLOTS : SIDE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return this.isValid(slot, stack);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = this.inventory.get(slot);
        this.inventory.set(slot, stack);
        boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(itemStack) && ItemStack.areTagsEqual(stack, itemStack);
        if (stack.getCount() > this.getMaxCountPerStack()) stack.setCount(this.getMaxCountPerStack());
        if (slot == 0 && !bl) {
            this.crushTimeTotal = this.getCrushTime();
            this.crushTime = 0;
            this.markDirty();
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
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) return false;
        else {
            return player.squaredDistanceTo((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
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
    public boolean isValid(int slot, ItemStack stack) {
        return slot != 1;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new PulverizerBlockGuiDescription(syncId, inventory, ScreenHandlerContext.create(this.world, this.pos));
    }

    protected int getCrushTime() {
        return this.world.getRecipeManager().getFirstMatch(CrushingRecipe.Type.INSTANCE, this, this.world).map(CrushingRecipe::getCrushTime).orElse(200);
    }

    private void craftRecipe(@Nullable Recipe<?> recipe) {
        if (recipe != null && this.canAcceptRecipeOutput(recipe)) {
            ItemStack itemStack = this.inventory.get(0);
            ItemStack itemStack2 = this.inventory.get(1);
            if (itemStack2.isEmpty()) this.inventory.set(1, recipe.getOutput().copy());
            else if (itemStack2.getItem() == recipe.getOutput().getItem())
                itemStack2.increment(recipe.getOutput().getCount());
            itemStack.decrement(1);
        }
    }

    @Override
    public void tick() {
        if (!this.world.isClient) {
            if (this.inventory.get(0).isEmpty()) {
                this.crushTime = MathHelper.clamp(this.crushTime - 2, 0, this.crushTimeTotal);
            } else {
                CrushingRecipe recipe = this.world.getRecipeManager().getFirstMatch(CrushingRecipe.Type.INSTANCE, this, this.world).orElse(null);
                if (this.canAcceptRecipeOutput(recipe)) {
                    if (this.energy >= 2) {
                        extractEnergy(2);
                        this.crushTime++;

                        if (this.crushTime >= this.crushTimeTotal) {
                            this.crushTime = 0;
                            this.crushTimeTotal = this.getCrushTime();
                            this.craftRecipe(recipe);
                        }
                    }
                } else this.crushTime = 0;
            }

            if (this.world.getBlockState(this.pos).get(PulverizerBlock.ACTIVE) != crushTimeTotal > 0)
                PulverizerBlock.setActive(crushTimeTotal > 0, this.world, this.pos);
        }
    }

    protected boolean canAcceptRecipeOutput(Recipe<?> recipe) {
        if (!this.inventory.get(0).isEmpty() && recipe != null) {
            if (recipe.getOutput().isEmpty()) return false;
            else {
                if (this.inventory.get(1).isEmpty()) return true;
                else if (!this.inventory.get(1).isItemEqualIgnoreDamage(recipe.getOutput())) return false;
                else if (this.inventory.get(1).getCount() < this.getMaxCountPerStack() && this.inventory.get(1).getCount() < this.inventory.get(1).getMaxCount())
                    return true;
                else return this.inventory.get(1).getCount() < this.inventory.get(1).getMaxCount();
            }
        } else return false;
    }
}
