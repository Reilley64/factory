package me.reilley.factory.block;

import me.reilley.factory.block.entity.BatteryEntity;
import me.reilley.factory.block.entity.PowerConduitBlockEntity;
import me.reilley.factory.block.entity.PulverizerBlockEntity;
import me.reilley.factory.block.shapeutil.PowerConduitBlockShapeUtil;
import me.reilley.factory.energy.FactoryEnergy;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class PowerConduitBlock extends BlockWithEntity {
    public static final String ID = "powerconduit";

    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;
    public static final BooleanProperty UP = Properties.UP;
    public static final BooleanProperty DOWN = Properties.DOWN;
    public static final BooleanProperty NORTH_CONNECTION = BooleanProperty.of("north_connection");
    public static final BooleanProperty EAST_CONNECTION = BooleanProperty.of("east_connection");
    public static final BooleanProperty SOUTH_CONNECTION = BooleanProperty.of("south_connection");
    public static final BooleanProperty WEST_CONNECTION = BooleanProperty.of("west_connection");
    public static final BooleanProperty UP_CONNECTION = BooleanProperty.of("up_connection");
    public static final BooleanProperty DOWN_CONNECTION = BooleanProperty.of("down_connection");
    public static final EnumProperty<Mode> MODE = EnumProperty.of("mode", Mode.class);

    private final PowerConduitBlockShapeUtil frameShapeUtil;

    public PowerConduitBlock() {
        super(FabricBlockSettings.of(Material.STONE).strength(1, 8));
        setDefaultState(getStateManager().getDefaultState().with(EAST, false).with(WEST, false).with(NORTH, false)
                .with(SOUTH, false).with(UP, false).with(DOWN, false).with(NORTH_CONNECTION, false)
                .with(EAST_CONNECTION, false).with(SOUTH_CONNECTION, false).with(WEST_CONNECTION, false)
                .with(UP_CONNECTION, false).with(DOWN_CONNECTION, false).with(MODE, Mode.NONE));
        frameShapeUtil = new PowerConduitBlockShapeUtil(this);
    }

    public static void nextMode(World world, BlockPos pos) {
        switch (world.getBlockState(pos).get(MODE)) {
            case NONE:
                world.setBlockState(pos, world.getBlockState(pos).with(MODE, Mode.EXTRACT));
                break;

            case EXTRACT:
                world.setBlockState(pos, world.getBlockState(pos).with(MODE, Mode.INSERT));
                break;

            case INSERT:
                world.setBlockState(pos, world.getBlockState(pos).with(MODE, Mode.NONE));
                break;
        }
    }

    public BooleanProperty getProperty(Direction facing) {
        switch (facing) {
            case EAST:
                return EAST;

            case WEST:
                return WEST;

            case NORTH:
                return NORTH;

            case SOUTH:
                return SOUTH;

            case UP:
                return UP;

            case DOWN:
                return DOWN;

            default:
                return EAST;
        }
    }

    private BlockState makeConnections(World world, BlockPos pos) {
        Boolean down = canConnectTo(world, pos.offset(Direction.DOWN, 1), Direction.UP);
        Boolean up = canConnectTo(world, pos.up(), Direction.DOWN);
        Boolean north = canConnectTo(world, pos.north(), Direction.SOUTH);
        Boolean east = canConnectTo(world, pos.east(), Direction.WEST);
        Boolean south = canConnectTo(world, pos.south(), Direction.NORTH);
        Boolean west = canConnectTo(world, pos.west(), Direction.WEST);
        Boolean downConnection = canConnectToNonCable(world, pos.offset(Direction.DOWN, 1), Direction.UP);
        Boolean upConnection = canConnectToNonCable(world, pos.up(), Direction.DOWN);
        Boolean northConnection = canConnectToNonCable(world, pos.north(), Direction.SOUTH);
        Boolean eastConnection = canConnectToNonCable(world, pos.east(), Direction.WEST);
        Boolean southConnection = canConnectToNonCable(world, pos.south(), Direction.NORTH);
        Boolean westConnection = canConnectToNonCable(world, pos.west(), Direction.WEST);

        return this.getDefaultState().with(DOWN, down).with(UP, up).with(NORTH, north).with(EAST, east)
                .with(SOUTH, south).with(WEST, west).with(NORTH_CONNECTION, northConnection).with(EAST_CONNECTION, eastConnection)
                .with(SOUTH_CONNECTION, southConnection).with(WEST_CONNECTION, westConnection).with(UP_CONNECTION, upConnection)
                .with(DOWN_CONNECTION, downConnection);
    }

    private Boolean canConnectTo(WorldAccess world, BlockPos pos, Direction facing) {
        if (world.getBlockEntity(pos) instanceof FactoryEnergy) {
            if (world.getBlockEntity(pos) instanceof PulverizerBlockEntity && facing == Direction.UP)
                return Boolean.FALSE;
            if (world.getBlockEntity(pos) instanceof BatteryEntity && !(facing == Direction.UP || facing == Direction.DOWN))
                return Boolean.FALSE;
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private Boolean canConnectToNonCable(WorldAccess world, BlockPos pos, Direction facing) {
        if (world.getBlockEntity(pos) instanceof FactoryEnergy) {
            if (world.getBlockEntity(pos) instanceof PowerConduitBlockEntity)
                return Boolean.FALSE;
            if (world.getBlockEntity(pos) instanceof PulverizerBlockEntity && facing == Direction.UP)
                return Boolean.FALSE;
            if (world.getBlockEntity(pos) instanceof BatteryEntity && !(facing == Direction.UP || facing == Direction.DOWN))
                return Boolean.FALSE;
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public ExtendedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof ExtendedScreenHandlerFactory ? (ExtendedScreenHandlerFactory) blockEntity : null;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new PowerConduitBlockEntity();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return makeConnections(context.getWorld(), context.getBlockPos());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, NORTH_CONNECTION, EAST_CONNECTION, SOUTH_CONNECTION, WEST_CONNECTION,
                UP_CONNECTION, DOWN_CONNECTION, MODE);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState ourState, Direction ourFacing, BlockState otherState,
                                                WorldAccess worldIn, BlockPos ourPos, BlockPos otherPos) {
        Boolean value = canConnectTo(worldIn, otherPos, ourFacing.getOpposite());
        return ourState.with(getProperty(ourFacing), value);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            ExtendedScreenHandlerFactory extendedScreenHandlerFactory = this.createScreenHandlerFactory(state, world, pos);
            if (extendedScreenHandlerFactory != null) {
                player.openHandledScreen(extendedScreenHandlerFactory);
                PiglinBrain.onGuardedBlockBroken(player, true);
            }
            return ActionResult.CONSUME;
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext shapeContext) {
        return frameShapeUtil.getShape(state);
    }

    public enum Mode implements StringIdentifiable {
        NONE("none"),
        EXTRACT("extract"),
        INSERT("insert");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    }
}
