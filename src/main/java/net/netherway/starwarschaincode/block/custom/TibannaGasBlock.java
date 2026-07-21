package net.netherway.starwarschaincode.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.netherway.starwarschaincode.item.ModItems;

public class TibannaGasBlock extends Block {
    private static final VoxelShape FULL_SHAPE = Block.box(0, 0, 0, 16, 16, 16);
    private int maxTibanna = 100;

    public TibannaGasBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity != null) {
                double entityBottom = entity.getBoundingBox().minY;
                double blockTop = pos.getY() + 1.0;

                // só colide se a entidade estiver "pousando"/em cima do bloco
                if (entityBottom < blockTop - 0.05) {
                    return Shapes.empty();
                }
            }
        }
        return FULL_SHAPE;
    }

    // opcional: também deixa a hitbox de seleção (outline) igual à colisão,
    // pra não ficar destacando o bloco quando vc atravessa ele de lado
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return FULL_SHAPE; // mantém a forma visual/seleção cheia, só a colisão que muda
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            if (stack.is(ModItems.TIBANNA_GAS_CAPSULE)) {
                int tibannaAmount = stack.getOrDefault(ModDataComponents.TIBANNA_AMOUNT, 0);

                if (tibannaAmount < 100 && tibannaAmount >= 0) {
                    stack.set(ModDataComponents.TIBANNA_AMOUNT, tibannaAmount + (maxTibanna - tibannaAmount));
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }
}