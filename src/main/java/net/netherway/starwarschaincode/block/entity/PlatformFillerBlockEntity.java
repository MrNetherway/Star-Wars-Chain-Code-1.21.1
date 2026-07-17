package net.netherway.starwarschaincode.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.netherway.starwarschaincode.block.entity.renderer.ModBlockEntities;

public class PlatformFillerBlockEntity extends BlockEntity {

    private BlockPos controllerPos;

    public PlatformFillerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLATFORM_FILLER_BE.get(), pos, state);
    }

    public void setControllerPos(BlockPos pos) {
        this.controllerPos = pos;
        setChanged();
    }

    public BlockPos getControllerPos() {
        return controllerPos;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (controllerPos != null) {
            tag.putLong("ControllerPos", controllerPos.asLong());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("ControllerPos")) {
            controllerPos = BlockPos.of(tag.getLong("ControllerPos"));
        }
    }
}