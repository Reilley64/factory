package me.reilley.factory.misc;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Iterator;

public class RectangularPrismIterator implements Iterator<BlockPos> {
    private final BlockPos minPos;
    private final BlockPos maxPos;
    private final Direction startingDirection;
    private BlockPos currentPos;
    private Direction currentDirection;

    public RectangularPrismIterator(BlockPos minPos, BlockPos maxPos, Direction startingDirection) {
        this.currentDirection = startingDirection;
        this.startingDirection = startingDirection;

        switch (this.currentDirection) {
            case NORTH:
                this.currentPos = minPos.add(0, 0, 1);
                break;

            case EAST:
                this.currentPos = minPos.add(-1, 0, 0);
                break;

            case SOUTH:
                this.currentPos = minPos.add(0, 0, -1);
                break;

            case WEST:
                this.currentPos = minPos.add(1, 0, 0);
                break;
        }

        this.minPos = minPos;
        this.maxPos = maxPos;
    }

    public BlockPos getMinPos() {
        return minPos;
    }

    public BlockPos getMaxPos() {
        return maxPos;
    }

    @Override
    public boolean hasNext() {
        return !(currentPos.getX() == maxPos.getX() && currentPos.getY() == maxPos.getY() && currentPos.getZ() == maxPos.getZ());
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
            currentPos = new BlockPos(minPos.getX(), maxPos.getY() > minPos.getY() ? currentPos.getY() + 1 : currentPos.getY() - 1, minPos.getZ());
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
