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
    public static final EnumProperty<Mode> MODE = EnumProperty.of("mode", Mode.class);

    private final PowerConduitBlockShapeUtil frameShapeUtil;

    public PowerConduitBlock() {
        super(FabricBlockSettings.of(Material.STONE).strength(1, 8));
        setDefaultState(getStateManager().getDefaultState().with(EAST, false).with(WEST, false).with(NORTH, false)
                .with(SOUTH, false).with(UP, false).with(DOWN, false).with(MODE, Mode.NONE));
        frameShapeUtil = new PowerConduitBlockShapeUtil(this);
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

        return this.getDefaultState().with(DOWN, down).with(UP, up).with(NORTH, north).with(EAST, east)
                .with(SOUTH, south).with(WEST, west);
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

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, MODE);
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
    public BlockState getStateForNeighborUpdate(BlockState ourState, Direction ourFacing, BlockState otherState,
                                                WorldAccess worldIn, BlockPos ourPos, BlockPos otherPos) {
        Boolean value = canConnectTo(worldIn, otherPos, ourFacing.getOpposite());
        return ourState.with(getProperty(ourFacing), value);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext shapeContext) {
        return frameShapeUtil.getShape(state);
    }

    public ExtendedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof ExtendedScreenHandlerFactory ? (ExtendedScreenHandlerFactory) blockEntity : null;
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

    public enum Mode implements StringIdentifiable {
        NONE("none"),
        EXTRACT("extract"),
        INSERT("insert");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        public String toString() {
            return this.asString();
        }

        @Override
        public String asString() {
            return name;
        }
    }
}
