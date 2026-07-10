package net.netherway.starwarschaincode.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.netherway.starwarschaincode.network.ActivateAbilityPayload;

@EventBusSubscriber(modid = "starwarschaincode", value = Dist.CLIENT)
public class ClientAbilityHandler {
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (ModKeyMappings.RACE_ABILITY_1.consumeClick()) {
            PacketDistributor.sendToServer(new ActivateAbilityPayload(1));
        }
        while (ModKeyMappings.RACE_ABILITY_2.consumeClick()) {
            PacketDistributor.sendToServer(new ActivateAbilityPayload(2));
        }
    }
}