package me.reilley.factory.blocks.quarry;

import me.reilley.factory.blocks.frame.FrameBlock;
import me.reilley.factory.misc.FactoryEnergy;
import me.reilley.factory.misc.FactoryInventory;
import me.reilley.factory.misc.RectangularPrismIterator;
import me.reilley.factory.registry.FactoryBlock;
import me.reilley.factory.registry.FactoryBlockEntityType;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.EmptyFluid;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class QuarryBlockEntity extends BlockEntity implements FactoryEnergy, FactoryInventory, NamedScreenHandlerFactory, Tickable {
    private final List<BlockPos> framePositions = new ArrayList<>();
    private DefaultedList<ItemStack> inventory;
    private double energy;
    private int viewerCount;
    // TODO: remove these for frame iterator
    private BlockPos targetBlock;
    private int minX;
    private int minZ;
    private int maxX;
    private int maxZ;

    public QuarryBlockEntity() {
        super(FactoryBlockEntityType.QUARRY);
        this.inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
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
        return this.world.getBlockState(this.pos).get(QuarryBlock.ACTIVE) ? 1 : 0;
    }

    @Override
    public double getMaxEnergyOutput() {
        return 0;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new QuarryBlockGuiDescription(syncId, inventory, ScreenHandlerContext.create(this.world, this.pos));
    }

    @Override
    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
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

    private void onInvOpenOrClose() {
        Block block = this.getCachedState().getBlock();
        if (block instanceof QuarryBlock) {
            this.world.addSyncedBlockEvent(this.pos, block, 1, this.viewerCount);
            this.world.updateNeighborsAlways(this.pos, block);
        }
    }

    @Override
    public void tick() {
        if (!this.world.isClient) {
            if (this.targetBlock == null) initializeFrameBuild();

            if (this.framePositions.size() != 64) {
                this.framePositions.add(this.targetBlock);
                while (getNextFramePos(this.targetBlock) != null) {
                    this.framePositions.add(getNextFramePos(this.targetBlock));
                    this.targetBlock = getNextFramePos(this.targetBlock);
                }
                return;
            }

            if (this.world.getBlockState(this.pos).get(QuarryBlock.ACTIVE)) {
                for (BlockPos blockPos : this.framePositions) {
                    if (!(this.world.getBlockState(blockPos).getBlock() instanceof FrameBlock)) {
                        BuildFrameTask buildFrameTask = new BuildFrameTask(blockPos);
                        if (energy >= buildFrameTask.getEnergyRequired()) buildFrameTask.run();
                        return;
                    }
                }

                RectangularPrismIterator rectangularPrismIterator = initializeDigging();
                while (rectangularPrismIterator.hasNext()) {
                    BlockPos blockPos = rectangularPrismIterator.next();
                    if (!(this.world.getBlockState(blockPos).getBlock() instanceof AirBlock
                            || this.world.getBlockState(blockPos).getHardness(this.world, blockPos) < 0.0F
                            || !(this.world.getBlockState(blockPos.add(0, 1, 0)).getBlock() instanceof AirBlock)
                            || (!(this.world.getBlockState(blockPos).getFluidState().getFluid() instanceof EmptyFluid)
                            && !this.world.getBlockState(blockPos).getFluidState().isStill()))) {
                        DigBlockTask digBlockTask = new DigBlockTask(blockPos);
                        if (energy >= digBlockTask.getEnergyRequired()) digBlockTask.run();
                        return;
                    }
                }

                QuarryBlock.setActive(false, this.world, this.pos);
            }
        }
    }

    private void initializeFrameBuild() {
        switch (this.world.getBlockState(this.pos).get(QuarryBlock.FACING)) {
            case NORTH:
                targetBlock = this.pos.add(8, 0, 1);
                minX = targetBlock.getX();
                minZ = targetBlock.getZ();
                maxX = minX - 16;
                maxZ = minZ + 16;
                break;

            case EAST:
                targetBlock = this.pos.add(-1, 0, 8);
                minX = targetBlock.getX();
                minZ = targetBlock.getZ();
                maxX = minX - 16;
                maxZ = minZ - 16;
                break;

            case SOUTH:
                targetBlock = this.pos.add(-8, 0, -1);
                minX = targetBlock.getX();
                minZ = targetBlock.getZ();
                maxX = minX + 16;
                maxZ = minZ - 16;
                break;

            case WEST:
                targetBlock = this.pos.add(1, 0, -8);
                minX = targetBlock.getX();
                minZ = targetBlock.getZ();
                maxX = minX + 16;
                maxZ = minZ + 16;
                break;
        }
    }

    private BlockPos getNextFramePos(BlockPos currentPos) {
        assert this.world != null;
        switch (this.world.getBlockState(this.pos).get(QuarryBlock.FACING)) {
            case NORTH:
                if (targetBlock.getX() == minX && targetBlock.getZ() == (minZ + 1))
                    return null;
                if (targetBlock.getX() > maxX && targetBlock.getZ() == minZ)
                    return currentPos.add(-1, 0, 0);
                if (targetBlock.getX() == maxX && targetBlock.getZ() < maxZ)
                    return currentPos.add(0, 0, 1);
                if (targetBlock.getZ() == maxZ && targetBlock.getX() < minX)
                    return currentPos.add(1, 0, 0);
                if (targetBlock.getX() == minX && targetBlock.getZ() > minZ)
                    return currentPos.add(0, 0, -1);

            case EAST:
                if (targetBlock.getZ() == minZ && targetBlock.getX() == (minX - 1))
                    return null;
                if (targetBlock.getZ() > maxZ && targetBlock.getX() == minX)
                    return currentPos.add(0, 0, -1);
                if (targetBlock.getZ() == maxZ && targetBlock.getX() > maxX)
                    return currentPos.add(-1, 0, 0);
                if (targetBlock.getX() == maxX && targetBlock.getZ() < minZ)
                    return currentPos.add(0, 0, 1);
                if (targetBlock.getZ() == minZ && targetBlock.getX() < minX)
                    return currentPos.add(1, 0, 0);

            case SOUTH:
                if (targetBlock.getX() == minX && targetBlock.getZ() == (minZ - 1))
                    return null;
                if (targetBlock.getX() < maxX && targetBlock.getZ() == minZ)
                    return currentPos.add(1, 0, 0);
                if (targetBlock.getX() == maxX && targetBlock.getZ() > maxZ)
                    return currentPos.add(0, 0, -1);
                if (targetBlock.getZ() == maxZ && targetBlock.getX() > minX)
                    return currentPos.add(-1, 0, 0);
                if (targetBlock.getX() == minX && targetBlock.getZ() < minZ)
                    return currentPos.add(0, 0, 1);

            case WEST:
                if (targetBlock.getZ() == minZ && targetBlock.getX() == (minX + 1))
                    return null;
                if (targetBlock.getZ() < maxZ && targetBlock.getX() == minX)
                    return currentPos.add(0, 0, 1);
                if (targetBlock.getZ() == maxZ && targetBlock.getX() < maxX)
                    return currentPos.add(1, 0, 0);
                if (targetBlock.getX() == maxX && targetBlock.getZ() > minZ)
                    return currentPos.add(0, 0, -1);
                if (targetBlock.getZ() == minZ && targetBlock.getX() > minX)
                    return currentPos.add(-1, 0, 0);
        }
        return null;
    }

    private RectangularPrismIterator initializeDigging() {
        switch (this.world.getBlockState(this.pos).get(QuarryBlock.FACING)) {
            case NORTH:
                return new RectangularPrismIterator(this.pos.add(7, 14, 2),
                        new BlockPos(this.pos.getX() + -7, 0, this.pos.getZ() + 16), Direction.WEST);

            case EAST:
                return new RectangularPrismIterator(this.pos.add(-2, 14, 7),
                        new BlockPos(this.pos.getX() + -16, 0, this.pos.getZ() + -7), Direction.NORTH);

            case SOUTH:
                return new RectangularPrismIterator(this.pos.add(-7, 14, -2),
                        new BlockPos(this.pos.getX() + 7, 0, this.pos.getZ() + -16), Direction.EAST);

            case WEST:
                return new RectangularPrismIterator(this.pos.add(2, 14, -7),
                        new BlockPos(this.pos.getX() + 16, 0, this.pos.getZ() + 7), Direction.SOUTH);
        }

        return null;
    }

    private abstract static class Task {
        public abstract int getEnergyRequired();

        public abstract void run();
    }

    private class DigBlockTask extends Task {
        private final BlockPos pos;

        public DigBlockTask(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public int getEnergyRequired() {
            return (int) (16 * (world.getBlockState(pos).getHardness(world, pos) * 2));
        }

        @Override
        public void run() {
            energy -= getEnergyRequired();
            LootContext.Builder builder = (new LootContext.Builder((ServerWorld) world)).random(world.random)
                    .parameter(LootContextParameters.POSITION, this.pos)
                    .parameter(LootContextParameters.TOOL, new ItemStack(Items.DIAMOND_PICKAXE))
                    .optionalParameter(LootContextParameters.BLOCK_ENTITY, world.getBlockEntity(this.pos));
            List<ItemStack> droppedItems = world.getBlockState(this.pos).getDroppedStacks(builder);
            if (canItemStacksBeAddedToInventory(droppedItems)) {
                for (ItemStack itemStack : droppedItems) addItemStackToInventory(itemStack);
                if (!(world.getBlockState(this.pos).getFluidState().getFluid() instanceof EmptyFluid)) {
                    world.setBlockState(this.pos, Blocks.COBBLESTONE.getDefaultState());
                    world.removeBlock(this.pos, false);
                } else world.breakBlock(this.pos, false);
            }
        }
    }

    private class BuildFrameTask extends Task {
        private final BlockPos pos;

        BuildFrameTask(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public int getEnergyRequired() {
            return 24;
        }

        @Override
        public void run() {
            energy -= getEnergyRequired();
            world.setBlockState(this.pos, FactoryBlock.FRAME.getDefaultState());
        }
    }
}
