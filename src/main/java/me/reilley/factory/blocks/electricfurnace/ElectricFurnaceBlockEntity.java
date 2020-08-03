package me.reilley.factory.blocks.electricfurnace;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import me.reilley.factory.Factory;
import me.reilley.factory.blocks.quarry.QuarryBlock;
import me.reilley.factory.misc.FactoryEnergy;
import me.reilley.factory.misc.FactoryInventory;
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
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;

public class ElectricFurnaceBlockEntity extends BlockEntity implements FactoryEnergy, FactoryInventory, NamedScreenHandlerFactory, PropertyDelegateHolder, Tickable {
    private DefaultedList<ItemStack> inventory;
    private int viewerCount;
    private double energy = 0;
    private int cookTime = 0;
    private int cookTimeTotal = 0;
    private ItemStack inputStack;

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return (int) energy;

                case 1:
                    return cookTime;

                case 2:
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
                    cookTime = value;
                    break;

                case 2:
                    cookTimeTotal = value;
                    break;
            }
        }

        @Override
        public int size() {
            return 3;
        }
    };

    public ElectricFurnaceBlockEntity() {
        super(Factory.ELECTRIC_FURNACE_ENTITY_TYPE);
        this.inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
        this.inputStack = ItemStack.EMPTY;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.fromTag(tag, this.inventory);
        this.inputStack = this.inventory.get(0);
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
        return 100;
    }

    @Override
    public double getMaxEnergyInput() {
        return 1;
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
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new ElectricFurnaceBlockGuiDescription(syncId, inventory, ScreenHandlerContext.create(this.world, this.pos));
    }

    @Override
    public void tick() {
        if (!this.world.isClient) {
            if (!this.inventory.get(0).isItemEqual(inputStack)) {
                this.inputStack = ItemStack.EMPTY;
                this.cookTime = 0;
                this.cookTimeTotal = 0;
            }

            if (!this.inventory.get(0).isEmpty()) {
                Recipe<?> recipe = this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, this, this.world).orElse(null);

                if (this.cookTimeTotal == 0 && canAcceptRecipeOutput(recipe)) {
                    this.inputStack = this.inventory.get(0);
                    this.cookTimeTotal = this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, this, this.world)
                            .map(AbstractCookingRecipe::getCookTime).orElse(200);
                }

                if (this.energy > 1) {
                    this.energy--;
                    this.cookTime++;
                }

                if (cookTime == cookTimeTotal) {
                    if (this.inventory.get(1).isEmpty()) this.inventory.set(1, recipe.getOutput());
                    else this.inventory.get(1).increment(1);
                    this.inventory.get(0).decrement(1);
                    this.cookTime = 0;
                    this.cookTimeTotal = 0;
                }
            }
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