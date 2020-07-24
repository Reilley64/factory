package me.reilley.factory.blocks.quarry;

import me.reilley.factory.Factory;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.EmptyFluid;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class QuarryBlockEntity extends LootableContainerBlockEntity implements Tickable {
    private abstract static class Task {
        private int energyRequired;

        public Task(int energyRequired) {
            energyRequired = energyRequired;
        }

        public abstract void runTask();
    }

    private class DigBlockTask extends Task {
        private final World world;
        private final BlockPos pos;

        public DigBlockTask(World world, BlockPos pos) {
            super(20);
            this.world = world;
            this.pos = pos;
        }

        @Override
        public void runTask() {
            while (this.world.getBlockState(targetBlock).getBlock() == null
                    || this.world.getBlockState(targetBlock).getBlock() instanceof AirBlock
                    || this.world.getBlockState(targetBlock).getHardness(this.world, targetBlock) < 0.0F
                    || !(this.world.getBlockState(targetBlock.add(0, 1, 0)).getBlock() instanceof AirBlock)
                    || (!(this.world.getBlockState(targetBlock).getFluidState().getFluid() instanceof EmptyFluid)
                    && !this.world.getBlockState(targetBlock).getFluidState().isStill())) {
                if (targetBlock.getY() < 1) {
                    active = false;
                    return;
                }
                targetBlock = getNextBlockPos(targetBlock);
            }

            if (!this.world.isClient) {
                BlockEntity blockEntity = this.world.getBlockEntity(targetBlock);
                LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.world)).random(this.world.random).parameter(LootContextParameters.POSITION, targetBlock).parameter(LootContextParameters.TOOL, new ItemStack(Items.DIAMOND_PICKAXE)).optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity);
                List<ItemStack> droppedItems = this.world.getBlockState(targetBlock).getDroppedStacks(builder);
                if (canItemStacksBeAddedToInventory(inventory, droppedItems)) {
                    for (ItemStack itemStack : droppedItems) addItemStackToInventory(inventory, itemStack);
                    if (!(this.world.getBlockState(targetBlock).getFluidState().getFluid() instanceof EmptyFluid)) {
                        this.world.setBlockState(targetBlock, Blocks.COBBLESTONE.getDefaultState());
                        this.world.removeBlock(targetBlock, false);
                    } else {
                        this.world.breakBlock(targetBlock, false);
                    }
                }
            }
        }

        private BlockPos getNextBlockPos(BlockPos currentPos) {
            switch (this.world.getBlockState(this.pos).get(QuarryBlock.FACING)) {
                case NORTH:
                    switch (diggingDirection) {
                        case EAST:
                            if (currentPos.getX() == minX) diggingDirection = DiggingDirection.SOUTH;
                            break;

                        case SOUTH:
                            if (currentPos.getX() == minX) diggingDirection = DiggingDirection.WEST;
                            else if (currentPos.getX() == maxX) diggingDirection = DiggingDirection.EAST;
                            break;

                        case WEST:
                            if (currentPos.getX() == maxX) diggingDirection = DiggingDirection.SOUTH;
                            break;
                    }
                    break;

                case EAST:
                    switch (diggingDirection) {
                        case NORTH:
                            if (currentPos.getZ() == maxZ) diggingDirection = DiggingDirection.WEST;
                            break;

                        case SOUTH:
                            if (currentPos.getZ() == minZ) diggingDirection = DiggingDirection.WEST;
                            break;

                        case WEST:
                            if (currentPos.getZ() == minZ) diggingDirection = DiggingDirection.NORTH;
                            else if (currentPos.getZ() == maxZ) diggingDirection = DiggingDirection.SOUTH;
                            break;
                    }
                    break;

                case SOUTH:
                    switch (diggingDirection) {
                        case NORTH:
                            if (currentPos.getX() == minX) diggingDirection = DiggingDirection.EAST;
                            else if (currentPos.getX() == maxX) diggingDirection = DiggingDirection.WEST;
                            break;

                        case EAST:
                            if (currentPos.getX() == maxX) diggingDirection = DiggingDirection.NORTH;
                            break;

                        case WEST:
                            if (currentPos.getX() == minX) diggingDirection = DiggingDirection.NORTH;
                            break;
                    }
                    break;

                case WEST:
                    switch (diggingDirection) {
                        case NORTH:
                            if (currentPos.getZ() == minZ) diggingDirection = DiggingDirection.EAST;
                            break;

                        case EAST:
                            if (currentPos.getZ() == minZ) diggingDirection = DiggingDirection.SOUTH;
                            else if (currentPos.getZ() == maxZ) diggingDirection = DiggingDirection.NORTH;
                            break;

                        case SOUTH:
                            if (currentPos.getZ() == maxZ) diggingDirection = DiggingDirection.EAST;
                            break;
                    }
                    break;
            }

            if (currentPos.getX() == maxX && currentPos.getZ() == maxZ) {
                return new BlockPos(minX, currentPos.getY() - 1, minZ);
            } else {
                switch (diggingDirection) {
                    case NORTH:
                        return currentPos.add(0, 0, -1);

                    case EAST:
                        return currentPos.add(1, 0, 0);

                    case SOUTH:
                        return currentPos.add(0, 0, 1);

                    case WEST:
                        return currentPos.add(-1, 0, 0);
                }
            }

            return currentPos;
        }
    }

    private class BuildFrameTask extends Task {
        private final World world;
        private final BlockPos pos;

        public BuildFrameTask(World world, BlockPos pos) {
            super(10);
            this.world = world;
            this.pos = pos;
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
                    //Starts minX - minX > maxX - minZ < maxZ
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
                    //Starts minZ - minX > maxX - minZ > maxZ
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
                    //Starts minX - minX < maxX - minZ > maxZ
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
                    //Starts minZ - minX < maxX - minZ < maxZ
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

    private enum DiggingDirection {
        NORTH, EAST, SOUTH, WEST
    }

    private DefaultedList<ItemStack> inventory;
    protected int viewerCount;
    private int delay = 20;
    private boolean frameBuilt = false;
    private boolean active = true;
    private BlockPos targetBlock;
    private DiggingDirection diggingDirection;
    private int minX;
    private int minZ;
    private int maxX;
    private int maxZ;

    public QuarryBlockEntity() {
        super(Factory.QUARRY_ENTITY_TYPE);
        this.inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(tag)) {
            Inventories.fromTag(tag, this.inventory);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (!this.serializeLootTable(tag)) {
            Inventories.toTag(tag, this.inventory);
        }
        return tag;
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("factory.quarry");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    @Override
    public int size() {
        return 27;
    }

    @Override
    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1) {
            this.viewerCount = data;
            return true;
        } else {
            return super.onSyncedBlockEvent(type, data);
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

    protected void onInvOpenOrClose() {
        Block block = this.getCachedState().getBlock();
        if (block instanceof QuarryBlock) {
            this.world.addSyncedBlockEvent(this.pos, block, 1, this.viewerCount);
            this.world.updateNeighborsAlways(this.pos, block);
        }
    }

    @Override
    public void tick() {
        if (world != null) {
            if (targetBlock == null) {
                if (!frameBuilt) {
                    getFrameStart();
                } else {
                    getDiggingStart();
                }
            } else {
                if (active) {
                    if (!frameBuilt) {
                        BuildFrameTask buildFrameTask = new BuildFrameTask(this.getWorld(), this.getPos());
                        buildFrameTask.runTask();
                    } else {
                        DigBlockTask digBlockTask = new DigBlockTask(this.getWorld(), this.getPos());
                        digBlockTask.runTask();
                    }
                }
            }
        }
    }

    private void getFrameStart() {
        switch (this.world.getBlockState(this.getPos()).get(QuarryBlock.FACING)) {
            case NORTH:
                targetBlock = this.getPos().add(8, 0, 1);
                minX = targetBlock.getX();
                minZ = targetBlock.getZ();
                maxX = minX - 16;
                maxZ = minZ + 16;
                break;

            case EAST:
                targetBlock = this.getPos().add(-1, 0, 8);
                minX = targetBlock.getX();
                minZ = targetBlock.getZ();
                maxX = minX - 16;
                maxZ = minZ - 16;
                break;

            case SOUTH:
                targetBlock = this.getPos().add(-8, 0, -1);
                minX = targetBlock.getX();
                minZ = targetBlock.getZ();
                maxX = minX + 16;
                maxZ = minZ - 16;
                break;

            case WEST:
                targetBlock = this.getPos().add(1, 0, -8);
                minX = targetBlock.getX();
                minZ = targetBlock.getZ();
                maxX = minX + 16;
                maxZ = minZ + 16;
                break;
        }
    }

    private void getDiggingStart() {
        switch (this.world.getBlockState(this.pos).get(QuarryBlock.FACING)) {
            case NORTH:
                targetBlock = this.pos.add(7, 14, 2);
                diggingDirection = DiggingDirection.WEST;
                minX = targetBlock.getX();
                minZ = targetBlock.getZ();
                maxX = minX - 14;
                maxZ = minZ + 14;
                break;

            case EAST:
                targetBlock = pos.add(-2, 14, 7);
                diggingDirection = DiggingDirection.NORTH;
                minX = targetBlock.getX();
                minZ = targetBlock.getZ();
                maxX = minX - 14;
                maxZ = minZ - 14;
                break;

            case SOUTH:
                targetBlock = pos.add(-7, 14, -2);
                diggingDirection = DiggingDirection.EAST;
                minX = targetBlock.getX();
                minZ = targetBlock.getZ();
                maxX = minX + 14;
                maxZ = minZ - 14;
                break;

            case WEST:
                targetBlock = pos.add(2, 14, -7);
                diggingDirection = DiggingDirection.SOUTH;
                minX = targetBlock.getX();
                minZ = targetBlock.getZ();
                maxX = minX + 14;
                maxZ = minZ + 14;
                break;
        }
    }

    private boolean canItemStacksBeAddedToInventory(DefaultedList<ItemStack> inventory, List<ItemStack> itemStacks) {
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

    private void addItemStackToInventory(DefaultedList<ItemStack> inventory, ItemStack itemStack) {
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
}
