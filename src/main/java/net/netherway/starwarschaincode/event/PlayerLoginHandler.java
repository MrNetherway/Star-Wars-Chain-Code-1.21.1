package net.netherway.starwarschaincode.event;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.netherway.starwarschaincode.component.ModAttachments;
import net.netherway.starwarschaincode.race.RacePassives;

@EventBusSubscriber(modid = "starwarschaincode")
public class PlayerLoginHandler {
    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RacePassives.apply(player, player.getData(ModAttachments.PLAYER_RACE));
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RacePassives.apply(player, player.getData(ModAttachments.PLAYER_RACE));
        }
    }
}