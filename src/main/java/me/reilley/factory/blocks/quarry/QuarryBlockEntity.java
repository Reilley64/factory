package me.reilley.factory.blocks.quarry;

import me.reilley.factory.Factory;
import me.reilley.factory.blocks.FactoryInventoryBlockEntity;
import me.reilley.factory.misc.RectangularPrismIterator;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.EmptyFluid;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class QuarryBlockEntity extends FactoryInventoryBlockEntity implements NamedScreenHandlerFactory, Tickable {
    private abstract static class Task {
        public abstract int getEnergyRequired();

        public abstract void runTask();
    }

    private class DigBlockTask extends Task {
        private final World world;

        DigBlockTask(World world) {
            this.world = world;
        }

        @Override
        public int getEnergyRequired() {
            return 20;
        }

        @Override
        public void runTask() {
            while (diggingIterator.getCurrent() != null
                    && (this.world.getBlockState(diggingIterator.getCurrent()).getBlock() instanceof AirBlock
                    || this.world.getBlockState(diggingIterator.getCurrent()).getHardness(this.world, diggingIterator.getCurrent()) < 0.0F
                    || !(this.world.getBlockState(diggingIterator.getCurrent().add(0, 1, 0)).getBlock() instanceof AirBlock)
                    || (!(this.world.getBlockState(diggingIterator.getCurrent()).getFluidState().getFluid() instanceof EmptyFluid)
                    && !this.world.getBlockState(diggingIterator.getCurrent()).getFluidState().isStill()))) {
                diggingIterator.next();
            }

            if (diggingIterator.getCurrent() == null) active = false;
            else {
                if (!this.world.isClient) {
                    BlockEntity blockEntity = this.world.getBlockEntity(diggingIterator.getCurrent());
                    LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.world)).random(this.world.random).parameter(LootContextParameters.POSITION, diggingIterator.getCurrent()).parameter(LootContextParameters.TOOL, new ItemStack(Items.DIAMOND_PICKAXE)).optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity);
                    List<ItemStack> droppedItems = this.world.getBlockState(diggingIterator.getCurrent()).getDroppedStacks(builder);
                    if (canItemStacksBeAddedToInventory(inventory, droppedItems)) {
                        for (ItemStack itemStack : droppedItems) addItemStackToInventory(inventory, itemStack);
                        if (!(this.world.getBlockState(diggingIterator.getCurrent()).getFluidState().getFluid() instanceof EmptyFluid)) {
                            this.world.setBlockState(diggingIterator.getCurrent(), Blocks.COBBLESTONE.getDefaultState());
                            this.world.removeBlock(diggingIterator.getCurrent(), false);
                        } else this.world.breakBlock(diggingIterator.getCurrent(), false);
                    }
                }
            }
        }
    }

    private class BuildFrameTask extends Task {
        private final World world;
        private final BlockPos pos;

        BuildFrameTask(World world, BlockPos pos) {
            this.world = world;
            this.pos = pos;
        }

        @Override
        public int getEnergyRequired() {
            return 10;
        }

        @Override
        public void runTask() {
            while (this.world.getBlockState(targetBlock).getBlock() instanceof FrameBlock) {
                targetBlock = getNextFramePos(targetBlock);
                if (targetBlock == null) {
                    System.out.println("Frame built");
                    frameBuilt = true;
                    break;
                }
            }

            if (targetBlock != null) {
                this.world.setBlockState(targetBlock, Factory.FRAME_BLOCK.getDefaultState());
                targetBlock = getNextFramePos(targetBlock);
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
    }

    private boolean active = true;
    private boolean frameBuilt = false;
    private BlockPos targetBlock;
    private int minX;
    private int minZ;
    private int maxX;
    private int maxZ;
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
        return new TranslatableText("factory.quarry");
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return this.createScreenHandler(syncId, inv);
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
                this.diggingIterator = new RectangularPrismIterator(this.pos.add(7, 14, 2), this.pos.add(-7, 0, 16), Direction.WEST);
                break;

            case EAST:
                this.diggingIterator = new RectangularPrismIterator(this.pos.add(-2, 14, 7), this.pos.add(-16, 0, -7), Direction.NORTH);
                break;

            case SOUTH:
                this.diggingIterator = new RectangularPrismIterator(this.pos.add(-7, 14, -2), this.pos.add(7, 0, -16), Direction.EAST);
                break;

            case WEST:
                this.diggingIterator = new RectangularPrismIterator(this.pos.add(2, 14, -7), this.pos.add(16, 0, 7), Direction.SOUTH);
                break;
        }
    }

    @Override
    public void tick() {
        if (this.active && this.world != null) {
            if (!this.frameBuilt) {
                if (this.targetBlock == null) initializeFrameBuild();
                else {
                    BuildFrameTask buildFrameTask = new BuildFrameTask(this.world, this.pos);
                    buildFrameTask.runTask();
                }
            } else  {
                if (this.diggingIterator == null) initializeDigging();
                else {
                    DigBlockTask digBlockTask = new DigBlockTask(this.world);
                    digBlockTask.runTask();
                }
            }
        }
    }
}
