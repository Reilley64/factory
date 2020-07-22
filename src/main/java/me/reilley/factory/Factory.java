package me.reilley.factory;

import me.reilley.factory.blocks.Quarry;
import me.reilley.factory.blocks.QuarryEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Factory implements ModInitializer {
    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
            new Identifier("factory", "factory"),
            () -> new ItemStack(Blocks.COBBLESTONE)
    );

    public static final Block QUARRY = new Quarry();
    public static BlockEntityType<QuarryEntity> QUARRY_ENTITY;

    @Override
    public void onInitialize() {
        QUARRY_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("factory", "quarry"), BlockEntityType.Builder.create(QuarryEntity::new, QUARRY).build(null));
        Registry.register(Registry.ITEM, new Identifier("factory", "quarry"), new BlockItem(QUARRY, new Item.Settings().group(ITEM_GROUP)));
    }
}
