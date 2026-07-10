package net.netherway.starwarschaincode.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.netherway.starwarschaincode.race.RaceAbilities;
import net.netherway.starwarschaincode.race.RaceAttachments;
import net.netherway.starwarschaincode.race.RacePassives;

@EventBusSubscriber(modid = "starwarschaincode")
public class ModNetworking {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(SelectRacePayload.TYPE, SelectRacePayload.STREAM_CODEC, ModNetworking::handleSelectRace);
        registrar.playToServer(ActivateAbilityPayload.TYPE, ActivateAbilityPayload.STREAM_CODEC, ModNetworking::handleActivateAbility);
    }

    private static void handleSelectRace(SelectRacePayload payload, IPayloadContext ctx) {
        if (ctx.player() instanceof ServerPlayer player) {
            player.setData(RaceAttachments.PLAYER_RACE, payload.race());
            RacePassives.apply(player, payload.race());
        }
    }

    private static void handleActivateAbility(ActivateAbilityPayload payload, IPayloadContext ctx) {
        if (ctx.player() instanceof ServerPlayer player) {
            RaceAbilities.activate(player, payload.slot());
        }
    }
}