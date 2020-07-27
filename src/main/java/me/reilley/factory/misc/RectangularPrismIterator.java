package me.reilley.factory.misc;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Iterator;

public class RectangularPrismIterator implements Iterator<BlockPos> {
    private BlockPos currentPos;
    private final BlockPos minPos;
    private final BlockPos maxPos;
    private Direction currentDirection;
    private final Direction startingDirection;

    public RectangularPrismIterator(BlockPos minPos, BlockPos maxPos, Direction startingDirection) {
        this.currentPos = minPos;
        this.minPos = minPos;
        this.maxPos = maxPos;
        this.currentDirection = startingDirection;
        this.startingDirection = startingDirection;
    }

    @Override
    public boolean hasNext() {
        return currentPos != maxPos;
    }

    @Override
    public BlockPos next() {
        switch (startingDirection) {
            case NORTH:
                switch (currentDirection) {
                    case NORTH:
                        if (currentPos.getZ() == maxPos.getZ()) currentDirection = Direction.WEST;
                        break;

                    case SOUTH:
                        if (currentPos.getZ() == minPos.getZ()) currentDirection = Direction.WEST;
                        break;

                    case WEST:
                        if (currentPos.getZ() == minPos.getZ()) currentDirection = Direction.NORTH;
                        else if (currentPos.getZ() == maxPos.getZ()) currentDirection = Direction.SOUTH;
                        break;
                }
                break;

            case EAST:
                switch (currentDirection) {
                    case NORTH:
                        if (currentPos.getX() == minPos.getX()) currentDirection = Direction.EAST;
                        else if (currentPos.getX() == maxPos.getX()) currentDirection = Direction.WEST;
                        break;

                    case EAST:
                        if (currentPos.getX() == maxPos.getX()) currentDirection = Direction.NORTH;
                        break;

                    case WEST:
                        if (currentPos.getX() == minPos.getX()) currentDirection = Direction.NORTH;
                        break;
                }
                break;

            case SOUTH:
                switch (currentDirection) {
                    case NORTH:
                        if (currentPos.getZ() == minPos.getZ()) currentDirection = Direction.EAST;
                        break;

                    case EAST:
                        if (currentPos.getZ() == minPos.getZ()) currentDirection = Direction.SOUTH;
                        else if (currentPos.getZ() == maxPos.getZ()) currentDirection = Direction.NORTH;
                        break;

                    case SOUTH:
                        if (currentPos.getZ() == maxPos.getZ()) currentDirection = Direction.EAST;
                        break;
                }
                break;

            case WEST:
                switch (currentDirection) {
                    case EAST:
                        if (currentPos.getX() == minPos.getX()) currentDirection = Direction.SOUTH;
                        break;

                    case SOUTH:
                        if (currentPos.getX() == minPos.getX()) currentDirection = Direction.WEST;
                        else if (currentPos.getX() == maxPos.getX()) currentDirection = Direction.EAST;
                        break;

                    case WEST:
                        if (currentPos.getX() == maxPos.getX()) currentDirection = Direction.SOUTH;
                        break;
                }
                break;
        }

        if (currentPos.getX() == maxPos.getX() && currentPos.getZ() == maxPos.getZ()) {
            currentPos = new BlockPos(minPos.getX(), currentPos.getY() - 1, minPos.getZ());
            currentDirection = startingDirection;
            return currentPos;
        } else {
            switch (currentDirection) {
                case NORTH:
                    currentPos = currentPos.add(0, 0, -1);
                    break;

                case EAST:
                    currentPos = currentPos.add(1, 0, 0);
                    break;

                case SOUTH:
                    currentPos = currentPos.add(0, 0, 1);
                    break;

                case WEST:
                    currentPos = currentPos.add(-1, 0, 0);
                    break;
            }
        }

        return currentPos;
    }
}
