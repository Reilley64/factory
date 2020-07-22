package me.reilley.factory.blocks;

import me.reilley.factory.Factory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;

public class QuarryEntity extends BlockEntity implements Tickable {
    public QuarryEntity() {
        super(Factory.QUARRY_ENTITY);
    }


    public void tick(){
        this.removeBlock();
    }

    private void removeBlock() {
        BlockPos targetBlock = this.getPos().north(1);
        assert this.world != null;
        if (!this.world.isClient) {
            this.world.removeBlock(targetBlock, false);
        }
    }
}
