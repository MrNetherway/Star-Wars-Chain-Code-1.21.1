package net.netherway.starwarschaincode.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.netherway.starwarschaincode.screen.custom.BlueprintBuilderMenu;

public class BlueprintBuilderBlock extends Block {

    public BlueprintBuilderBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            player.openMenu(new SimpleMenuProvider(
                    (containerId, inventory, p) -> new BlueprintBuilderMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)),
                    Component.translatable("container.starwarschaincode.blueprint_builder")
            ));
        }
        return InteractionResult.SUCCESS;
    }
}