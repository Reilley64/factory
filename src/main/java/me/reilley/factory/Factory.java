package me.reilley.factory;

import me.reilley.factory.blocks.quarry.QuarryBlock;
import me.reilley.factory.blocks.quarry.QuarryBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Factory implements ModInitializer {
    public static final String MOD_ID = "factory";

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
            new Identifier(MOD_ID, "general"),
            () -> new ItemStack(Blocks.COBBLESTONE)
    );

    public static final Block QUARRY = new QuarryBlock();
    public static final Identifier QUARRY_IDENTIFIER = new Identifier(MOD_ID, "quarry");
    public static BlockEntityType<QuarryBlockEntity> QUARRY_ENTITY_TYPE;

    public static final Block FRAME_BLOCK = new QuarryBlock();
    public static final Identifier FRAME_BLOCK_IDENTIFIER = new Identifier(MOD_ID, "frame_block");

    public static final Identifier HELLO_ID = new Identifier("factory:hello");
    public static SoundEvent HELLO_EVENT = new SoundEvent(HELLO_ID);

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, QUARRY_IDENTIFIER, QUARRY);
        Registry.register(Registry.ITEM, QUARRY_IDENTIFIER, new BlockItem(QUARRY, new Item.Settings().group(ITEM_GROUP)));
        QUARRY_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, QUARRY_IDENTIFIER, BlockEntityType.Builder.create(QuarryBlockEntity::new, QUARRY).build(null));

        Registry.register(Registry.BLOCK, FRAME_BLOCK_IDENTIFIER, FRAME_BLOCK);
        Registry.register(Registry.ITEM, FRAME_BLOCK_IDENTIFIER, new BlockItem(FRAME_BLOCK, new Item.Settings().group(ITEM_GROUP)));

        Registry.register(Registry.SOUND_EVENT, Factory.HELLO_ID, HELLO_EVENT);
    }
}
