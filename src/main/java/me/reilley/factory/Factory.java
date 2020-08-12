package me.reilley.factory;

import me.reilley.factory.block.PowerConduitBlock;
import me.reilley.factory.recipe.CrushingRecipe;
import me.reilley.factory.registry.FactoryBlock;
import me.reilley.factory.registry.FactoryBlockEntityType;
import me.reilley.factory.registry.FactoryItem;
import me.reilley.factory.registry.FactoryScreenHandlerType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
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

    public static Identifier POWER_CONDUIT_MODE = new Identifier(MOD_ID, "power_conduit_mode");

    @Override
    public void onInitialize() {
        FactoryBlock.initialize();
        FactoryBlockEntityType.initialize();
        FactoryItem.initialize();
        FactoryScreenHandlerType.initialize();

        CRUSHING = Registry.register(Registry.RECIPE_SERIALIZER, CrushingRecipe.Serializer.ID, CrushingRecipe.Serializer.INSTANCE);
        Registry.register(Registry.SOUND_EVENT, Factory.HELLO_ID, HELLO_EVENT);

        ServerSidePacketRegistry.INSTANCE.register(POWER_CONDUIT_MODE, (packetContext, attachedData) -> {
            BlockPos blockPos = attachedData.readBlockPos();
            packetContext.getTaskQueue().execute(() -> {
                if (packetContext.getPlayer().world.canPlayerModifyAt(packetContext.getPlayer(), blockPos))
                    PowerConduitBlock.nextMode(packetContext.getPlayer().world, blockPos);
            });
        });
    }
}
