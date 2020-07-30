package me.reilley.factory;

import me.reilley.factory.blocks.conduits.power.PowerConduitBlock;
import me.reilley.factory.blocks.conduits.power.PowerConduitBlockEntity;
import me.reilley.factory.blocks.frame.FrameBlock;
import me.reilley.factory.blocks.generator.GeneratorBlockGuiDescription;
import me.reilley.factory.blocks.quarry.*;
import me.reilley.factory.blocks.generator.GeneratorBlock;
import me.reilley.factory.blocks.generator.GeneratorBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Factory implements ModInitializer {
    public static final String MOD_ID = "factory";

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
            new Identifier(MOD_ID, "general"),
            () -> new ItemStack(Factory.QUARRY)
    );

    public static final Block QUARRY = new QuarryBlock();
    public static BlockEntityType<QuarryBlockEntity> QUARRY_ENTITY_TYPE;
    public static ScreenHandlerType<QuarryBlockGuiDescription> QUARRY_SCREEN_HANDLER_TYPE;

    public static final Block FRAME_BLOCK = new FrameBlock();

    public static final Block GENERATOR = new GeneratorBlock();
    public static BlockEntityType<GeneratorBlockEntity> GENERATOR_ENTITY_TYPE;
    public static ScreenHandlerType<GeneratorBlockGuiDescription> GENERATOR_SCREEN_HANDLER_TYPE;

    public static final Block POWER_CONDUIT_BLOCK = new PowerConduitBlock();
    public static BlockEntityType<PowerConduitBlockEntity> POWER_CONDUIT_ENTITY_TYPE;

    public static final Identifier HELLO_ID = new Identifier(MOD_ID, "hello");
    public static SoundEvent HELLO_EVENT = new SoundEvent(HELLO_ID);

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, QuarryBlock.ID, QUARRY);
        Registry.register(Registry.ITEM, QuarryBlock.ID, new BlockItem(QUARRY, new Item.Settings().group(ITEM_GROUP)));
        QUARRY_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, QuarryBlock.ID,
                BlockEntityType.Builder.create(QuarryBlockEntity::new, QUARRY).build(null));
        QUARRY_SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerSimple(QuarryBlock.ID,
                (syncId, inventory) -> new QuarryBlockGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY));

        Registry.register(Registry.BLOCK, FrameBlock.ID, FRAME_BLOCK);
        Registry.register(Registry.ITEM, FrameBlock.ID, new BlockItem(FRAME_BLOCK,
                new Item.Settings().group(ITEM_GROUP)));

        Registry.register(Registry.BLOCK, GeneratorBlock.ID, GENERATOR);
        Registry.register(Registry.ITEM, GeneratorBlock.ID, new BlockItem(GENERATOR,
                new Item.Settings().group(ITEM_GROUP)));
        GENERATOR_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, GeneratorBlock.ID,
                BlockEntityType.Builder.create(GeneratorBlockEntity::new, GENERATOR).build(null));
        GENERATOR_SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerSimple(GeneratorBlock.ID,
                (syncId, inventory) -> new GeneratorBlockGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY));

        Registry.register(Registry.BLOCK, PowerConduitBlock.ID, POWER_CONDUIT_BLOCK);
        Registry.register(Registry.ITEM, PowerConduitBlock.ID, new BlockItem(POWER_CONDUIT_BLOCK, new Item.Settings().group(ITEM_GROUP)));
        POWER_CONDUIT_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, PowerConduitBlock.ID,
                BlockEntityType.Builder.create(PowerConduitBlockEntity::new, POWER_CONDUIT_BLOCK).build(null));

        Registry.register(Registry.SOUND_EVENT, Factory.HELLO_ID, HELLO_EVENT);
    }
}
