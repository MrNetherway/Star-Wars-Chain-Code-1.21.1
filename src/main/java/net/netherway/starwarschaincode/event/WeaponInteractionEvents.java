package net.netherway.starwarschaincode.event;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.item.custom.WeaponItem;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID)
public class WeaponInteractionEvents {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();

        ItemStack offhand = player.getOffhandItem();
        if (!(offhand.getItem() instanceof WeaponItem)) {
            return; // sem arma na offhand, comportamento normal
        }

        Block block = event.getLevel().getBlockState(event.getPos()).getBlock();
        BlockEntity be = event.getLevel().getBlockEntity(event.getPos());

        boolean isContainer = be instanceof BaseContainerBlockEntity || be instanceof Container;
        boolean isButtonOrLever = block instanceof ButtonBlock || block instanceof LeverBlock;

        if (isContainer || isButtonOrLever) {
            return; // deixa interagir normal
        }

        // cancela interação da mão principal (sabre, bloco, etc)
        event.setUseItem(net.neoforged.neoforge.common.util.TriState.FALSE);
        event.setUseBlock(net.neoforged.neoforge.common.util.TriState.FALSE);
    }
}