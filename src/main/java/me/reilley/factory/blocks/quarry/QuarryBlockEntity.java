package me.reilley.factory.blocks.quarry;

import me.reilley.factory.Factory;
import me.reilley.factory.blocks.FactoryInventoryBlockEntity;
import me.reilley.factory.misc.RectangularPrismIterator;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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

public class QuarryBlockEntity extends FactoryInventoryBlockEntity implements NamedScreenHandlerFactory, Tickable {
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
            return 20;
        }

        @Override
        public void run() {
            LootContext.Builder builder = (new LootContext.Builder((ServerWorld) world)).random(world.random).parameter(LootContextParameters.POSITION, this.pos).parameter(LootContextParameters.TOOL, new ItemStack(Items.DIAMOND_PICKAXE)).optionalParameter(LootContextParameters.BLOCK_ENTITY, world.getBlockEntity(this.pos));
            List<ItemStack> droppedItems = world.getBlockState(this.pos).getDroppedStacks(builder);
            if (canItemStacksBeAddedToInventory(inventory, droppedItems)) {
                for (ItemStack itemStack : droppedItems) addItemStackToInventory(inventory, itemStack);
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
            return 10;
        }

        @Override
        public void run() {
            world.setBlockState(this.pos, Factory.FRAME_BLOCK.getDefaultState());
        }
    }

    private boolean active = true;
    private int delay = 20;
    private BlockPos targetBlock;
    private int minX;
    private int minZ;
    private int maxX;
    private int maxZ;
    private final List<BlockPos> framePositions = new ArrayList<>();
    private RectangularPrismIterator diggingIterator;

    public QuarryBlockEntity() {
        super(Factory.QUARRY_ENTITY_TYPE, 27);
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
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new QuarryBlockGuiDescription(syncId, inventory, ScreenHandlerContext.create(this.world, this.pos));
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

    private void initializeDigging() {
        switch (this.world.getBlockState(this.pos).get(QuarryBlock.FACING)) {
            case NORTH:
                this.diggingIterator = new RectangularPrismIterator(this.pos.add(7, 14, 2), new BlockPos(this.pos.getX() + -7, 0, this.pos.getZ() + 16), Direction.WEST);
                break;

            case EAST:
                this.diggingIterator = new RectangularPrismIterator(this.pos.add(-2, 14, 7), new BlockPos(this.pos.getX() + -16, 0, this.pos.getZ() + -7), Direction.NORTH);
                break;

            case SOUTH:
                this.diggingIterator = new RectangularPrismIterator(this.pos.add(-7, 14, -2), new BlockPos(this.pos.getX() + 7, 0, this.pos.getZ() + -16), Direction.EAST);
                break;

            case WEST:
                this.diggingIterator = new RectangularPrismIterator(this.pos.add(2, 14, -7), new BlockPos(this.pos.getX() + 16, 0, this.pos.getZ() + 7), Direction.SOUTH);
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

    @Override
    public void tick() {
        this.delay -= 1;
        if (this.targetBlock == null) initializeFrameBuild();
        if (this.diggingIterator == null) initializeDigging();

        if (this.framePositions.size() != 64) {
            this.framePositions.add(this.targetBlock);
            while (getNextFramePos(this.targetBlock) != null) {
                this.framePositions.add(getNextFramePos(this.targetBlock));
                this.targetBlock = getNextFramePos(this.targetBlock);
            }
            return;
        }

        if (this.active && this.delay == 0 && this.world != null && !this.world.isClient) {
            this.delay = 5;

            for (BlockPos blockPos : this.framePositions) {
                if (!(this.world.getBlockState(blockPos).getBlock() instanceof FrameBlock)) {
                    BuildFrameTask buildFrameTask = new BuildFrameTask(blockPos);
                    buildFrameTask.run();
                    return;
                }
            }

            while (this.diggingIterator.hasNext()) {
                BlockPos blockPos = this.diggingIterator.next();
                if (!(this.world.getBlockState(blockPos).getBlock() instanceof AirBlock
                        || this.world.getBlockState(blockPos).getHardness(this.world, blockPos) < 0.0F
                        || !(this.world.getBlockState(blockPos.add(0, 1, 0)).getBlock() instanceof AirBlock)
                        || (!(this.world.getBlockState(blockPos).getFluidState().getFluid() instanceof EmptyFluid)
                        && !this.world.getBlockState(blockPos).getFluidState().isStill()))) {
                    DigBlockTask digBlockTask = new DigBlockTask(blockPos);
                    digBlockTask.run();
                    return;
                }
            }

            this.active = false;
        }
    }
}
