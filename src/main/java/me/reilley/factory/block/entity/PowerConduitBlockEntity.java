package me.reilley.factory.block.entity;

import me.reilley.factory.block.PowerConduitBlock;
import me.reilley.factory.energy.FactoryEnergy;
import me.reilley.factory.registry.FactoryBlockEntityType;
import me.reilley.factory.screen.PowerConduitBlockGuiDescription;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;

public class PowerConduitBlockEntity extends BlockEntity implements FactoryEnergy, ExtendedScreenHandlerFactory, Tickable {
    private double energy = 0;

    public PowerConduitBlockEntity() {
        super(FactoryBlockEntityType.POWER_CONDUIT);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.energy = tag.getShort("Energy");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putShort("Energy", (short) this.energy);
        return tag;
    }

    @Override
    public double getEnergy() {
        return this.energy;
    }

    @Override
    public void setEnergy(double energy) {
        this.energy = energy;
    }

    @Override
    public double getEnergyCapacity() {
        return 512;
    }

    @Override
    public double getMaxEnergyInput() {
        return this.world.getBlockState(this.pos).get(PowerConduitBlock.MODE) == PowerConduitBlock.Mode.EXTRACT ? 128 : 0;
    }

    @Override
    public double getMaxEnergyOutput() {
        return this.world.getBlockState(this.pos).get(PowerConduitBlock.MODE) == PowerConduitBlock.Mode.INSERT ? 128 : 0;
    }

    @Override
    public void energyTick(World world, BlockPos pos) {
        ArrayList<PowerConduitBlockEntity> cables = new ArrayList<>();

        for (Direction side : Direction.values()) {
            BlockEntity blockEntity = getWorld().getBlockEntity(this.pos.offset(side));
            if (blockEntity instanceof FactoryEnergy) {
                if (blockEntity instanceof PowerConduitBlockEntity) cables.add((PowerConduitBlockEntity) blockEntity);
                else {
                    FactoryEnergy factoryEnergyBlockEntity = (FactoryEnergy) blockEntity;
                    if (factoryEnergyBlockEntity.getMaxEnergyInput() > 0)
                        factoryEnergyBlockEntity.insertEnergy(extractEnergy(
                                Math.min(
                                        Math.min(getMaxEnergyOutput(), factoryEnergyBlockEntity.getMaxEnergyInput()),
                                        factoryEnergyBlockEntity.getEnergyCapacity() - factoryEnergyBlockEntity.getEnergy()
                                )
                        ));
                }
            }
        }

        if (!cables.isEmpty()) {
            cables.add(this);
            cables.forEach(cableBlockEntity -> cableBlockEntity.setEnergy(cables.stream().mapToDouble(PowerConduitBlockEntity::getEnergy).sum() / cables.size()));
        }
    }

    @Override
    public void tick() {
        if (!this.world.isClient) energyTick(this.world, this.pos);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new PowerConduitBlockGuiDescription(syncId, inv, ScreenHandlerContext.create(this.world, this.pos), pos);
    }
}
