package net.netherway.starwarschaincode.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class WeaponWorkbenchBlock extends Block {

    public WeaponWorkbenchBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            player.openMenu(new SimpleMenuProvider(
                    (containerId, inv, p) -> new net.netherway.starwarschaincode.screen.custom.WeaponWorkbenchMenu(containerId, inv),
                    Component.translatable("block.starwarschaincode.weapon_workbench")
            ));
        }
        return InteractionResult.SUCCESS;
    }
}