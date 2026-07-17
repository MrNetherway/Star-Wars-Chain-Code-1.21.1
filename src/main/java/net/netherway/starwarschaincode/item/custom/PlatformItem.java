package net.netherway.starwarschaincode.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.netherway.starwarschaincode.block.ModBlocks;
import net.netherway.starwarschaincode.block.PlatformControllerBlock;

public class PlatformItem extends BlockItem {

    public PlatformItem(Properties properties) {
        super(ModBlocks.PLATFORM_CONTROLLER.get(), properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        // Origin = posição clicada + 1 na direção da face clicada (comportamento padrão de colocar "sobre" o bloco)
        BlockPos clicked = context.getClickedPos();
        Direction face = context.getClickedFace();
        BlockPos origin = face == Direction.UP || face == Direction.DOWN
                ? clicked.relative(face)
                : clicked.relative(Direction.UP); // clique lateral também tenta colocar em cima

        if (level.isClientSide) {
            return InteractionResult.SUCCESS; // evita duplo processamento; server decide de fato
        }

        if (!PlatformControllerBlock.canPlaceArea(level, origin)) {
            player.displayClientMessage(
                    Component.translatable("message.starwarschaincode.platform_obstructed"), true);
            return InteractionResult.FAIL;
        }

        BlockState controllerState = ModBlocks.PLATFORM_CONTROLLER.get().defaultBlockState();
        BlockState fillerState = ModBlocks.PLATFORM_FILLER.get().defaultBlockState();

        PlatformControllerBlock.placeMultiblock(level, origin, controllerState, fillerState);

        level.playSound(null, origin, SoundEvents.NETHERITE_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);

        if (!player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}