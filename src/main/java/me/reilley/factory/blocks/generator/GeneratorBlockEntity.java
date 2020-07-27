package me.reilley.factory.blocks.generator;

import me.reilley.factory.Factory;
import me.reilley.factory.blocks.FactoryInventoryBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;

public class GeneratorBlockEntity extends FactoryInventoryBlockEntity implements NamedScreenHandlerFactory, Tickable {
    private int energy;

    public GeneratorBlockEntity() {
        super(Factory.GENERATOR_ENTITY_TYPE, 1);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("factory.generator");
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return this.createScreenHandler(syncId, inv);
    }

    @Override
    public void tick() {
        energy += 20;
        System.out.println(energy);
    }
}
