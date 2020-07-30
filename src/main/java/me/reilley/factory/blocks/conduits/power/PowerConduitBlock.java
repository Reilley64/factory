package me.reilley.factory.blocks.conduits.power;

import me.reilley.factory.Factory;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.BlockView;

public class PowerConduitBlock extends BlockWithEntity {
    public static final Identifier ID = new Identifier(Factory.MOD_ID, "power_conduit");

    public PowerConduitBlock() {
        super(FabricBlockSettings.of(Material.METAL).strength(5, 6));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return null;
    }
}
