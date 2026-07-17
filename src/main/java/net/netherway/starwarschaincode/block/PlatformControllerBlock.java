package net.netherway.starwarschaincode.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
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

public class PlatformControllerBlock extends Block implements EntityBlock {

    public static final int SIZE = 6;

    public PlatformControllerBlock(Properties properties) {
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
        return new PlatformControllerBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        if (level.getBlockEntity(pos) instanceof PlatformControllerBlockEntity controller) {
            controller.openMenu(player);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    /** Checa se a área SIZE x SIZE a partir de origin (canto) está livre */
    public static boolean canPlaceArea(Level level, BlockPos origin) {
        for (int dx = 0; dx < SIZE; dx++) {
            for (int dz = 0; dz < SIZE; dz++) {
                BlockPos check = origin.offset(dx, 0, dz);
                BlockState state = level.getBlockState(check);
                if (!state.isAir() && !state.canBeReplaced()) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Coloca o controller + todos os fillers a partir do canto origin */
    public static void placeMultiblock(Level level, BlockPos origin, BlockState controllerState, BlockState fillerState) {
        for (int dx = 0; dx < SIZE; dx++) {
            for (int dz = 0; dz < SIZE; dz++) {
                BlockPos pos = origin.offset(dx, 0, dz);
                boolean isControllerCell = (dx == 0 && dz == 0); // controller no canto (0,0) da área

                if (isControllerCell) {
                    level.setBlock(pos, controllerState, 3);
                } else {
                    level.setBlock(pos, fillerState, 3);
                    if (level.getBlockEntity(pos) instanceof PlatformFillerBlockEntity filler) {
                        filler.setControllerPos(origin);
                    }
                }
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof PlatformControllerBlockEntity controller) {
                controller.breakMultiblock(); // remove todos os 35 fillers junto
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}