package me.reilley.factory;

import me.reilley.factory.blocks.macerator.MaceratorBlock;
import me.reilley.factory.blocks.macerator.MaceratorBlockEntity;
import me.reilley.factory.recipes.CrushingRecipe;
import me.reilley.factory.registry.FactoryBlock;
import me.reilley.factory.registry.FactoryBlockEntityType;
import me.reilley.factory.registry.FactoryItem;
import me.reilley.factory.registry.FactoryScreenHandlerType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Factory implements ModInitializer {
    public static final String MOD_ID = "factory";

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
            new Identifier(MOD_ID, "general"),
            () -> new ItemStack(FactoryBlock.QUARRY)
    );

    public static RecipeSerializer<CrushingRecipe> CRUSHING;
    public static final Identifier HELLO_ID = new Identifier(MOD_ID, "hello");
    public static SoundEvent HELLO_EVENT = new SoundEvent(HELLO_ID);

    @Override
    public void onInitialize() {
        FactoryBlock.initialize();
        FactoryBlockEntityType.initialize();
        FactoryItem.initialize();
        FactoryScreenHandlerType.initialize();

        CRUSHING = Registry.register(Registry.RECIPE_SERIALIZER, CrushingRecipe.Serializer.ID, CrushingRecipe.Serializer.INSTANCE);
        Registry.register(Registry.SOUND_EVENT, Factory.HELLO_ID, HELLO_EVENT);
    }
}
