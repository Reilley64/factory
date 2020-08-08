package me.reilley.factory.block;

import me.reilley.factory.block.entity.BatteryEntity;
import me.reilley.factory.block.entity.PowerConduitBlockEntity;
import me.reilley.factory.block.entity.PulverizerBlockEntity;
import me.reilley.factory.block.shapeutil.PowerConduitBlockShapeUtil;
import me.reilley.factory.energy.FactoryEnergy;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.HashMap;
import java.util.Map;

public class PowerConduitBlock extends BlockWithEntity {
    public static final String ID = "powerconduit";

    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty WEST = BooleanProperty.of("west");
    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");

    public static final Map<Direction, BooleanProperty> PROPERTY_MAP = Util.make(new HashMap<>(), map -> {
        map.put(Direction.EAST, EAST);
        map.put(Direction.WEST, WEST);
        map.put(Direction.NORTH, NORTH);
        map.put(Direction.SOUTH, SOUTH);
        map.put(Direction.UP, UP);
        map.put(Direction.DOWN, DOWN);
    });

    private final PowerConduitBlockShapeUtil frameShapeUtil;

    public PowerConduitBlock() {
        super(FabricBlockSettings.of(Material.STONE).strength(1, 8));
        setDefaultState(getStateManager().getDefaultState().with(EAST, false).with(WEST, false).with(NORTH, false)
                .with(SOUTH, false).with(UP, false).with(DOWN, false));
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
        builder.add(EAST, WEST, NORTH, SOUTH, UP, DOWN);
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
}
