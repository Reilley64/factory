package me.reilley.factory.block;

import me.reilley.factory.block.entity.BatteryEntity;
import me.reilley.factory.block.shapeutil.BatteryShapeUtil;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class Battery extends BlockWithEntity {
    public static final String ID = "battery";

    public static DirectionProperty FACING = DirectionProperty.of("facing", Direction.Type.HORIZONTAL);
    public static final IntProperty INDICATOR = IntProperty.of("indicator", 0, 7);

    private final BatteryShapeUtil batteryShapeUtil;

    public Battery() {
        super(FabricBlockSettings.of(Material.METAL).strength(5, 6));
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(INDICATOR, 0));
        batteryShapeUtil = new BatteryShapeUtil(this);
    }

    public static void setFacing(Direction facing, World world, BlockPos pos) {
        world.setBlockState(pos, world.getBlockState(pos).with(FACING, facing));
    }
    public static void setIndicator(double energyMax, double energyCurrent, World world, BlockPos pos) {
        int indicator = 0;
        if (energyMax != 0 && energyCurrent != 0) {
            int incrementAmount = ((int) energyMax) / 7;
            indicator = ((int) energyCurrent) / incrementAmount;
        }
        world.setBlockState(pos, world.getBlockState(pos).with(INDICATOR, Math.min(7, Math.max(indicator, 0))));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        setFacing(placer.getHorizontalFacing().getOpposite(), world, pos);
        setIndicator(1, 0, world, pos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, INDICATOR);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new BatteryEntity();
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Inventory) {
                ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            NamedScreenHandlerFactory namedScreenHandlerFactory = this.createScreenHandlerFactory(state, world, pos);
            if (namedScreenHandlerFactory != null) {
                player.openHandledScreen(namedScreenHandlerFactory);
                PiglinBrain.onGuardedBlockBroken(player, true);
            }
            return ActionResult.CONSUME;
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext shapeContext) {
        return batteryShapeUtil.getShape(state);
    }
}