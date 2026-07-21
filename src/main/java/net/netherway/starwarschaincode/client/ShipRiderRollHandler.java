package net.netherway.starwarschaincode.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.entity.custom.ShipEntity;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID, value = Dist.CLIENT)
public class ShipRiderRollHandler {

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderLivingEvent.Pre<?, ?> event) {
        if (!(event.getEntity() instanceof net.minecraft.world.entity.player.Player player)) return;
        if (player.getVehicle() instanceof ShipEntity) {
            event.setCanceled(true); // não renderiza o player enquanto pilotando
        }
    }
}