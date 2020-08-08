package me.reilley.factory.block.shapeutil;

import me.reilley.factory.block.Battery;
import me.reilley.factory.block.FrameBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class BatteryShapeUtil {
    private final Battery battery;
    private final HashMap<BlockState, VoxelShape> shapes;

    public BatteryShapeUtil(Battery battery) {
        this.battery = battery;
        this.shapes = createStateShapeMap();
    }

    private HashMap<BlockState, VoxelShape> createStateShapeMap() {
        return Util.make(new HashMap<>(), map -> battery.getStateManager().getStates()
                .forEach(state -> map.put(state, getStateShape(state)))
        );
    }

    private VoxelShape getStateShape(BlockState state) {
        final double size = 5;
        final VoxelShape baseShape = Block.createCuboidShape(size, size, size, 16.0D - size, 16.0D - size, 16.0D - size);
        final List<VoxelShape> cylinderParts = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            int j = 0;
            if (i == 1) j = 6;
            if (i == 2) j = 4;
            if (i == 3) j = 3;
            double y = 0D;

            VoxelShape shape1 = Block.createCuboidShape(i, 0D, j, 16.0D - i, 16.0D, 16.0D - j);
            cylinderParts.add(shape1);
            if (i != j) {
                VoxelShape shape2 = Block.createCuboidShape(j, 0D, i, 16.0D - j, 16.0D, 16.0D - i);
                cylinderParts.add(shape2);
            }
        }
        return VoxelShapes.union(baseShape, cylinderParts.toArray(new VoxelShape[]{}));
    }

    public VoxelShape getShape(BlockState state) {
        return shapes.get(state);
    }
}