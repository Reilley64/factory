package me.reilley.factory.blocks.conduits.power;

import me.reilley.factory.blocks.frame.FrameBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class PowerConduitBlockShapeUtil {
    private final PowerConduitBlock frameBlock;
    private final HashMap<BlockState, VoxelShape> shapes;

    public PowerConduitBlockShapeUtil(PowerConduitBlock frameBlock) {
        this.frameBlock = frameBlock;
        this.shapes = createStateShapeMap();
    }

    private HashMap<BlockState, VoxelShape> createStateShapeMap() {
        return Util.make(new HashMap<>(), map -> frameBlock.getStateManager().getStates()
                .forEach(state -> map.put(state, getStateShape(state)))
        );
    }

    private VoxelShape getStateShape(BlockState state) {
        final double size = 6;
        final VoxelShape baseShape = Block.createCuboidShape(size, size, size, 16.0D - size, 16.0D - size, 16.0D - size);
        final List<VoxelShape> connections = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            if (state.get(FrameBlock.PROPERTY_MAP.get(dir))) {
                double x = dir == Direction.WEST ? 0 : dir == Direction.EAST ? 16D : size;
                double z = dir == Direction.NORTH ? 0 : dir == Direction.SOUTH ? 16D : size;
                double y = dir == Direction.DOWN ? 0 : dir == Direction.UP ? 16D : size;

                VoxelShape shape = Block.createCuboidShape(x, y, z, 16.0D - size, 16.0D - size, 16.0D - size);
                connections.add(shape);
            }
        }
        return VoxelShapes.union(baseShape, connections.toArray(new VoxelShape[]{}));
    }

    public VoxelShape getShape(BlockState state) {
        return shapes.get(state);
    }
}