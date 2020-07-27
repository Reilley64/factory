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

    public BlockPos getCurrentPos() {
        return currentPos;
    }

    public Direction getNextDirection() {
        switch (startingDirection) {
            case NORTH:
                switch (currentDirection) {
                    case NORTH:
                        if (currentPos.getZ() == maxPos.getZ()) return Direction.WEST;
                        break;

                    case SOUTH:
                        if (currentPos.getZ() == minPos.getZ()) return Direction.WEST;
                        break;

                    case WEST:
                        if (currentPos.getZ() == minPos.getZ()) return Direction.NORTH;
                        else if (currentPos.getZ() == maxPos.getZ()) return Direction.SOUTH;
                        break;
                }
                break;

            case EAST:
                switch (currentDirection) {
                    case NORTH:
                        if (currentPos.getX() == minPos.getX()) return Direction.EAST;
                        else if (currentPos.getX() == maxPos.getX()) return Direction.WEST;
                        break;

                    case EAST:
                        if (currentPos.getX() == maxPos.getX()) return Direction.NORTH;
                        break;

                    case WEST:
                        if (currentPos.getX() == minPos.getX()) return Direction.NORTH;
                        break;
                }
                break;

            case SOUTH:
                switch (currentDirection) {
                    case NORTH:
                        if (currentPos.getZ() == minPos.getZ()) return Direction.EAST;
                        break;

                    case EAST:
                        if (currentPos.getZ() == minPos.getZ()) return Direction.SOUTH;
                        else if (currentPos.getZ() == maxPos.getZ()) return Direction.NORTH;
                        break;

                    case SOUTH:
                        if (currentPos.getZ() == maxPos.getZ()) return Direction.EAST;
                        break;
                }
                break;

            case WEST:
                switch (currentDirection) {
                    case EAST:
                        if (currentPos.getX() == minPos.getX()) return Direction.SOUTH;
                        break;

                    case SOUTH:
                        if (currentPos.getX() == minPos.getX()) return Direction.WEST;
                        else if (currentPos.getX() == maxPos.getX()) return Direction.EAST;
                        break;

                    case WEST:
                        if (currentPos.getX() == maxPos.getX()) return Direction.SOUTH;
                        break;
                }
                break;
        }
        
        return currentDirection;
    }

    @Override
    public boolean hasNext() {
        Direction nextDirection = getNextDirection();
        BlockPos nextPos = currentPos;

        if (currentPos.getX() == maxPos.getX() && currentPos.getZ() == maxPos.getZ()) {
            return nextPos.getY() - 1 < 0;
        } else {
            switch (nextDirection) {
                case NORTH:
                    nextPos = nextPos.add(0, 0, -1);
                    break;

                case EAST:
                    nextPos = nextPos.add(1, 0, 0);
                    break;

                case SOUTH:
                    nextPos = nextPos.add(0, 0, 1);
                    break;

                case WEST:
                    nextPos = nextPos.add(-1, 0, 0);
                    break;
            }
        }

        switch (startingDirection) {
            case NORTH:
                return nextPos.getX() < maxPos.getX() && nextPos.getZ() < maxPos.getZ();

            case EAST:
                return nextPos.getX() > maxPos.getX() && nextPos.getZ() < maxPos.getZ();

            case SOUTH:
                return nextPos.getX() > maxPos.getX() && nextPos.getZ() > maxPos.getZ();

            case WEST:
                return nextPos.getX() < maxPos.getX() && nextPos.getZ() > maxPos.getZ();
        }

        return false;
    }

    @Override
    public BlockPos next() {
        currentDirection = getNextDirection();

        if (currentPos.getX() == maxPos.getX() && currentPos.getZ() == maxPos.getZ()) {
            if (currentPos.getY() - 1 < 0) currentPos = null;
            else currentPos = new BlockPos(minPos.getX(), currentPos.getY() - 1, minPos.getZ());
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
