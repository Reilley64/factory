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

import java.util.List;

public class QuarryBlockEntity extends LootableContainerBlockEntity implements Tickable {
    private enum DiggingDirection {
        NORTH, EAST, SOUTH, WEST
    }

    private DefaultedList<ItemStack> inventory;
    protected int viewerCount;
    private int delay = 20;
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
        --this.delay;
        if (this.delay <= 0) {
            this.removeBlock();
            delay = 5;
        }
    }

    private void removeBlock() {
        assert this.world != null;

        if (targetBlock == null) {
            this.world.setBlockState(this.getPos().add(0, 1, 0), new FrameBlock().getDefaultState());
            switch (this.world.getBlockState(this.getPos()).get(QuarryBlock.FACING)) {
                case NORTH:
                    targetBlock = this.getPos().add(7, 14, 2);
                    diggingDirection = DiggingDirection.WEST;
                    minX = targetBlock.getX();
                    minZ = targetBlock.getZ();
                    maxX = minX - 14;
                    maxZ = minZ + 14;
                    break;

                case EAST:
                    targetBlock = this.getPos().add(-2, 14, 7);
                    diggingDirection = DiggingDirection.NORTH;
                    minX = targetBlock.getX();
                    minZ = targetBlock.getZ();
                    maxX = minX - 14;
                    maxZ = minZ - 14;
                    break;

                case SOUTH:
                    targetBlock = this.getPos().add(-7, 14, -2);
                    diggingDirection = DiggingDirection.EAST;
                    minX = targetBlock.getX();
                    minZ = targetBlock.getZ();
                    maxX = minX + 14;
                    maxZ = minZ - 14;
                    break;

                case WEST:
                    targetBlock = this.getPos().add(2, 14, -7);
                    diggingDirection = DiggingDirection.SOUTH;
                    minX = targetBlock.getX();
                    minZ = targetBlock.getZ();
                    maxX = minX + 14;
                    maxZ = minZ + 14;
                    break;
            }
        } else {
            while (this.world.getBlockState(targetBlock).getBlock() == null
                    || this.world.getBlockState(targetBlock).getBlock() instanceof AirBlock
                    || (!(this.world.getBlockState(targetBlock).getFluidState().getFluid() instanceof EmptyFluid)
                    && !this.world.getBlockState(targetBlock).getFluidState().isStill())) {
                targetBlock = getNextBlockPos(targetBlock);
            }
        }

        if (!this.world.isClient) {
            BlockEntity blockEntity = this.world.getBlockEntity(targetBlock);
            LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.world)).random(this.world.random).parameter(LootContextParameters.POSITION, targetBlock).parameter(LootContextParameters.TOOL, new ItemStack(Items.DIAMOND_PICKAXE)).optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity);
            List<ItemStack> droppedItems = this.world.getBlockState(targetBlock).getDroppedStacks(builder);
            if (canItemStacksBeAddedToInventory(inventory, droppedItems)) {
                for (ItemStack itemStack : droppedItems) addItemStackToInventory(inventory, itemStack);
                System.out.println(targetBlock.toString());
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
        switch (this.world.getBlockState(this.getPos()).get(QuarryBlock.FACING)) {
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
