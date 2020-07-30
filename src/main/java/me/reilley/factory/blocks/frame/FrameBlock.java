package me.reilley.factory.blocks.frame;

import me.reilley.factory.Factory;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;

public class FrameBlock extends Block {
    public static final Identifier ID = new Identifier(Factory.MOD_ID, "frame_block");
    public static final BooleanProperty HARDENED = BooleanProperty.of("hardened");

    public FrameBlock() {
        super(FabricBlockSettings.of(Material.METAL).strength(-1.0F, 1000));
        setDefaultState(getStateManager().getDefaultState().with(HARDENED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(HARDENED);
    }
}
