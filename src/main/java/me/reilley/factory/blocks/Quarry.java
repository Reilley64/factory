package me.reilley.factory.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

public class Quarry extends Block {
    public Quarry() {
        super(
                FabricBlockSettings.of(Material.METAL)
                        .breakByHand(false)
                        .breakByTool(FabricToolTags.PICKAXES)
                        .sounds(BlockSoundGroup.METAL)
                        .strength(5, 6)
        );
    }
}
