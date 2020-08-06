package me.reilley.factory.registry;

import me.reilley.factory.Factory;
import me.reilley.factory.block.PowerConduitBlock;
import me.reilley.factory.block.ElectricFurnaceBlock;
import me.reilley.factory.block.GeneratorBlock;
import me.reilley.factory.block.MaceratorBlock;
import me.reilley.factory.block.QuarryBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FactoryItem {
    public static final Item COAL_DUST = register("coal_dust", new Item(new Item.Settings().group(Factory.ITEM_GROUP)));
    public static final Item DIAMOND_DUST = register("diamond_dust", new Item(new Item.Settings().group(Factory.ITEM_GROUP)));
    public static final Item ELECTRIC_FURNACE = register(ElectricFurnaceBlock.ID, new BlockItem(FactoryBlock.ELECTRIC_FURNACE, new Item.Settings().group(Factory.ITEM_GROUP)));
    public static final Item EMERALD_DUST = register("emerald_dust", new Item(new Item.Settings().group(Factory.ITEM_GROUP)));
    public static final Item GENERATOR = register(GeneratorBlock.ID, new BlockItem(FactoryBlock.GENERATOR, new Item.Settings().group(Factory.ITEM_GROUP)));
    public static final Item GOLD_DUST = register("gold_dust", new Item(new Item.Settings().group(Factory.ITEM_GROUP)));
    public static final Item IRON_DUST = register("iron_dust", new Item(new Item.Settings().group(Factory.ITEM_GROUP)));
    public static final Item MACERATOR = register(MaceratorBlock.ID, new BlockItem(FactoryBlock.MACERATOR, new Item.Settings().group(Factory.ITEM_GROUP)));
    public static final Item POWER_CONDUIT = register(PowerConduitBlock.ID, new BlockItem(FactoryBlock.POWER_CONDUIT, new Item.Settings().group(Factory.ITEM_GROUP)));
    public static final Item QUARRY = register(QuarryBlock.ID, new BlockItem(FactoryBlock.QUARRY, new Item.Settings().group(Factory.ITEM_GROUP)));

    public static void initialize() {
    }

    public static <T extends Item> T register(String name, T item) {
        return Registry.register(Registry.ITEM, new Identifier(Factory.MOD_ID, name), item);
    }
}