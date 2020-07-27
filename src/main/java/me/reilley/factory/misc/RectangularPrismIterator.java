package me.reilley.factory.misc;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Iterator;

public class RectangularPrismIterator implements Iterator<BlockPos> {
    private BlockPos current;
    private final BlockPos min;
    private final BlockPos max;
    private Direction currentDirection;
    private final Direction startingDirection;

    public RectangularPrismIterator(BlockPos min, BlockPos max, Direction startingDirection) {
        this.current = min;
        this.min = min;
        this.max = max;
        this.currentDirection = startingDirection;
        this.startingDirection = startingDirection;
    }

    public BlockPos getCurrent() {
        return current;
    }

    public Direction getNextDirection() {
        switch (startingDirection) {
            case NORTH:
                switch (currentDirection) {
                    case NORTH:
                        if (current.getZ() == max.getZ()) return Direction.WEST;
                        break;

                    case SOUTH:
                        if (current.getZ() == min.getZ()) return Direction.WEST;
                        break;

                    case WEST:
                        if (current.getZ() == min.getZ()) return Direction.NORTH;
                        else if (current.getZ() == max.getZ()) return Direction.SOUTH;
                        break;
                }
                break;

            case EAST:
                switch (currentDirection) {
                    case NORTH:
                        if (current.getX() == min.getX()) return Direction.EAST;
                        else if (current.getX() == max.getX()) return Direction.WEST;
                        break;

                    case EAST:
                        if (current.getX() == max.getX()) return Direction.NORTH;
                        break;

                    case WEST:
                        if (current.getX() == min.getX()) return Direction.NORTH;
                        break;
                }
                break;

            case SOUTH:
                switch (currentDirection) {
                    case NORTH:
                        if (current.getZ() == min.getZ()) return Direction.EAST;
                        break;

                    case EAST:
                        if (current.getZ() == min.getZ()) return Direction.SOUTH;
                        else if (current.getZ() == max.getZ()) return Direction.NORTH;
                        break;

                    case SOUTH:
                        if (current.getZ() == max.getZ()) return Direction.EAST;
                        break;
                }
                break;

            case WEST:
                switch (currentDirection) {
                    case EAST:
                        if (current.getX() == min.getX()) return Direction.SOUTH;
                        break;

                    case SOUTH:
                        if (current.getX() == min.getX()) return Direction.WEST;
                        else if (current.getX() == max.getX()) return Direction.EAST;
                        break;

                    case WEST:
                        if (current.getX() == max.getX()) return Direction.SOUTH;
                        break;
                }
                break;
        }
        
        return currentDirection;
    }

    @Override
    public boolean hasNext() {
        Direction nextDirection = getNextDirection();
        BlockPos nextPos = current;

        if (current.getX() == max.getX() && current.getZ() == max.getZ()) {
            return nextPos.getY() - 1 >= 0;
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
                return nextPos.getX() < max.getX() && nextPos.getZ() < max.getZ();

            case EAST:
                return nextPos.getX() > max.getX() && nextPos.getZ() < max.getZ();

            case SOUTH:
                return nextPos.getX() > max.getX() && nextPos.getZ() > max.getZ();

            case WEST:
                return nextPos.getX() < max.getX() && nextPos.getZ() > max.getZ();
        }

        return false;
    }

    @Override
    public BlockPos next() {
        currentDirection = getNextDirection();

        if (current.getX() == max.getX() && current.getZ() == max.getZ()) {
            if (current.getY() - 1 < 0) current = null;
            else current = new BlockPos(min.getX(), current.getY() - 1, min.getZ());
        } else {
            switch (currentDirection) {
                case NORTH:
                    current = current.add(0, 0, -1);
                    break;

                case EAST:
                    current = current.add(1, 0, 0);
                    break;

                case SOUTH:
                    current = current.add(0, 0, 1);
                    break;

                case WEST:
                    current = current.add(-1, 0, 0);
                    break;
            }
        }
        
        return current;
    }
}
