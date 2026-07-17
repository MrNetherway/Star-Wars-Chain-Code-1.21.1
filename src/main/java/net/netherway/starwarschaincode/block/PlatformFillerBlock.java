package net.netherway.starwarschaincode.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.netherway.starwarschaincode.block.entity.PlatformControllerBlockEntity;
import net.netherway.starwarschaincode.block.entity.PlatformFillerBlockEntity;
import org.jetbrains.annotations.Nullable;

public class PlatformFillerBlock extends Block implements EntityBlock {

    public PlatformFillerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return PlatformShapes.PLATFORM_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return PlatformShapes.PLATFORM_SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PlatformFillerBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        if (level.getBlockEntity(pos) instanceof PlatformFillerBlockEntity filler) {
            BlockPos controllerPos = filler.getControllerPos();
            if (controllerPos != null && level.getBlockEntity(controllerPos) instanceof PlatformControllerBlockEntity controller) {
                controller.openMenu(player);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof PlatformFillerBlockEntity filler) {
                BlockPos controllerPos = filler.getControllerPos();
                if (controllerPos != null && level.getBlockEntity(controllerPos) instanceof PlatformControllerBlockEntity controller) {
                    controller.onFillerBroken(pos); // remove o resto do multiblock
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}