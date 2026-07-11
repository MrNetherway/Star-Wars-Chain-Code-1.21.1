package net.netherway.starwarschaincode.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.netherway.starwarschaincode.entity.custom.BlasterBoltEntity;
import net.netherway.starwarschaincode.item.ModItems;
import net.netherway.starwarschaincode.item.custom.WeaponItem;
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
        registrar.playToServer(
                FireBlasterPayload.TYPE,
                FireBlasterPayload.STREAM_CODEC,
                ModNetworking::handleFireBlaster
        );
    }

    private static void handleFireBlaster(FireBlasterPayload payload, IPayloadContext ctx) {

        if (!(ctx.player() instanceof ServerPlayer player))
            return;

        ItemStack stack = player.getMainHandItem();

        if (!(stack.getItem() instanceof WeaponItem weapon))
            return;

        BlasterBoltEntity bolt = new BlasterBoltEntity(player.level(), player);

        bolt.shoot(player, weapon.getProjectileSpeed());

        player.level().addFreshEntity(bolt);
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