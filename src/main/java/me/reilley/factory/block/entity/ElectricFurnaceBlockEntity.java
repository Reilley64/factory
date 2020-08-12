package me.reilley.factory.block.entity;

import blue.endless.jankson.annotation.Nullable;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import me.reilley.factory.block.ElectricFurnaceBlock;
import me.reilley.factory.block.QuarryBlock;
import me.reilley.factory.energy.FactoryEnergy;
import me.reilley.factory.inventory.FactoryInventory;
import me.reilley.factory.registry.FactoryBlockEntityType;
import me.reilley.factory.screen.ElectricFurnaceBlockGuiDescription;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
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

public class ElectricFurnaceBlockEntity extends BlockEntity implements FactoryEnergy, FactoryInventory, NamedScreenHandlerFactory, PropertyDelegateHolder, Tickable {
    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{1};
    private static final int[] SIDE_SLOTS = new int[]{1};
    private final PropertyDelegate propertyDelegate;
    private DefaultedList<ItemStack> inventory;
    private int viewerCount;
    private double energy = 0;
    private int cookTime = 0;
    private int cookTimeTotal = 0;

    public ElectricFurnaceBlockEntity() {
        super(FactoryBlockEntityType.ELECTRIC_FURNACE);
        this.inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                switch (index) {
                    case 0:
                        return (int) energy;

                    case 1:
                        return (int) getEnergyCapacity();

                    case 2:
                        return cookTime;

                    case 3:
                        return cookTimeTotal;
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
                        cookTime = value;
                        break;

                    case 3:
                        cookTimeTotal = value;
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
        this.energy = tag.getShort("Energy");
        this.cookTime = tag.getShort("CookTime");
        this.cookTimeTotal = tag.getShort("CookTimeTotal");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        Inventories.toTag(tag, this.inventory);
        tag.putShort("Energy", (short) this.energy);
        tag.putShort("BurnTime", (short) this.cookTime);
        tag.putShort("FuelTime", (short) this.cookTimeTotal);
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
        return 416;
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
            this.cookTimeTotal = this.getCookTime();
            this.cookTime = 0;
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
        return new ElectricFurnaceBlockGuiDescription(syncId, inventory, ScreenHandlerContext.create(this.world, this.pos));
    }

    protected int getCookTime() {
        return this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, this, this.world).map(AbstractCookingRecipe::getCookTime).orElse(200);
    }

    private void craftRecipe(@Nullable Recipe<?> recipe) {
        if (recipe != null && this.canAcceptRecipeOutput(recipe)) {
            ItemStack itemStack = this.inventory.get(0);
            ItemStack itemStack2 = recipe.getOutput();
            ItemStack itemStack3 = this.inventory.get(1);
            if (itemStack3.isEmpty()) this.inventory.set(1, itemStack2.copy());
            else if (itemStack3.getItem() == itemStack2.getItem()) itemStack3.increment(1);
            itemStack.decrement(1);
        }
    }

    @Override
    public void tick() {
        if (!this.world.isClient) {
            if (this.inventory.get(0).isEmpty()) {
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
            } else {
                SmeltingRecipe recipe = this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, this, this.world).orElse(null);
                if (this.canAcceptRecipeOutput(recipe)) {
                    if (this.energy >= 3) {
                        extractEnergy(3);
                        this.cookTime += 2;

                        if (this.cookTime >= this.cookTimeTotal) {
                            this.cookTime = 0;
                            this.cookTimeTotal = this.getCookTime();
                            this.craftRecipe(recipe);
                            this.inventory.get(0).decrement(1);
                        }
                    }
                } else this.cookTime = 0;
            }

            if (this.world.getBlockState(this.pos).get(ElectricFurnaceBlock.ACTIVE) != cookTimeTotal > 0)
                ElectricFurnaceBlock.setActive(cookTimeTotal > 0, this.world, this.pos);
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
