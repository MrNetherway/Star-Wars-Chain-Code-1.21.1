package net.netherway.starwarschaincode.server;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.component.ModAttachments;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID)
public class HyperspaceLockHandler {

    public static boolean isTraveling(Player player) {
        return player.getData(ModAttachments.HYPERSPACE_TRAVEL).traveling;
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (isTraveling(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (isTraveling(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (isTraveling(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (isTraveling(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (isTraveling(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onDismount(net.neoforged.neoforge.event.entity.EntityMountEvent event) {
        if (!event.isMounting() && event.getEntityMounting() instanceof Player player) {
            if (isTraveling(player)) {
                event.setCanceled(true);
            }
        }
    }
}