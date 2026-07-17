package net.netherway.starwarschaincode.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.netherway.starwarschaincode.entity.custom.BlasterBoltEntity;
import net.netherway.starwarschaincode.entity.custom.ShipEntity;
import net.netherway.starwarschaincode.item.custom.LightsaberItem;
import net.netherway.starwarschaincode.item.custom.WeaponItem;
import net.netherway.starwarschaincode.race.RaceAbilities;
import net.netherway.starwarschaincode.component.ModAttachments;
import net.netherway.starwarschaincode.race.RacePassives;
import net.netherway.starwarschaincode.screen.custom.PlatformMenu;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID)
public class ModNetworking {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(SelectRacePayload.TYPE, SelectRacePayload.STREAM_CODEC, ModNetworking::handleSelectRace);
        registrar.playToServer(ActivateAbilityPayload.TYPE, ActivateAbilityPayload.STREAM_CODEC, ModNetworking::handleActivateAbility);
        registrar.playToServer(FireBlasterPayload.TYPE, FireBlasterPayload.STREAM_CODEC, ModNetworking::handleFireBlaster);
        registrar.playToServer(ActivateSaberPayload.TYPE, ActivateSaberPayload.STREAM_CODEC, ModNetworking::handleSaberActivate);
        registrar.playToServer(LightsaberImpulsePayload.TYPE, LightsaberImpulsePayload.STREAM_CODEC, ModNetworking::handleLightsaberImpulse);
        registrar.playToServer(BlockingPayload.TYPE, BlockingPayload.STREAM_CODEC, ModNetworking::handleBlocking);
        registrar.playToServer(
                ShipInputPayload.TYPE,
                ShipInputPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    var player = context.player();
                    if (player.getVehicle() instanceof ShipEntity ship) {
                        ship.setInput(payload.left(), payload.right(), payload.up(), payload.down());
                    }
                })
        );
        registrar.playToServer(
                SelectShipTabPayload.TYPE,
                SelectShipTabPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player().containerMenu instanceof PlatformMenu menu) {
                        menu.selectShip(payload.shipIndex());
                    }
                })
        );
    }

    private static void handleBlocking(BlockingPayload payload, IPayloadContext ctx) {

        if (!(ctx.player() instanceof ServerPlayer player))
            return;

        ItemStack stack = player.getMainHandItem();

        if (!(stack.getItem() instanceof LightsaberItem))
            return;

        boolean activated = stack.getOrDefault(
                ModDataComponents.ACTIVATED.get(),
                false
        );

        if (!activated)
            return;

        stack.set(
                ModDataComponents.BLOCKING.get(),
                payload.isBlocking()
        );


    }

    private static void handleLightsaberImpulse(LightsaberImpulsePayload payload, IPayloadContext ctx) {

        ctx.enqueueWork(() -> {

            if (!(ctx.player() instanceof ServerPlayer player))
                return;

            ItemStack stack = player.getMainHandItem();

            if (!(stack.getItem() instanceof LightsaberItem))
                return;

            boolean activated = stack.getOrDefault(
                    ModDataComponents.ACTIVATED.get(),
                    false
            );

            boolean blocking = stack.getOrDefault(
                    ModDataComponents.BLOCKING.get(),
                    false
            );

            if (!activated || blocking)
                return;

            float yaw = player.getYRot();
            float pitch = player.getXRot();

            float x = -Mth.sin(yaw * ((float)Math.PI / 180F)) * Mth.cos(pitch * ((float)Math.PI / 180F));
            float y = -Mth.sin(pitch * ((float)Math.PI / 180F));
            float z = Mth.cos(yaw * ((float)Math.PI / 180F)) * Mth.cos(pitch * ((float)Math.PI / 180F));

            float len = Mth.sqrt(x * x + y * y + z * z);

            x *= 1.7F / len;
            y *= 1.7F / len;
            z *= 1.7F / len;

            player.push(x, y, z);

            System.out.println(player.getDeltaMovement());

            player.hasImpulse = true;
            player.hurtMarked = true;

            player.startAutoSpinAttack(20, 8.0F, stack);

            if (player.onGround()) {
                player.move(MoverType.SELF, new Vec3(0, 1.2, 0));
            }

            player.level().playSound(
                    null,
                    player,
                    SoundEvents.TRIDENT_THROW.value(),
                    SoundSource.PLAYERS,
                    1F,
                    1F
            );
        });
    }

    private static void handleSaberActivate(ActivateSaberPayload payload, IPayloadContext ctx) {
        if (!(ctx.player() instanceof ServerPlayer player))
            return;

        ItemStack stack = player.getMainHandItem();

        if (!(stack.getItem() instanceof LightsaberItem))
            return;

        boolean activated = stack.getOrDefault(
                ModDataComponents.ACTIVATED,
                false
        );

        stack.set(
                ModDataComponents.ACTIVATED,
                !activated
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
            player.setData(ModAttachments.PLAYER_RACE, payload.race());
            RacePassives.apply(player, payload.race());
        }
    }

    private static void handleActivateAbility(ActivateAbilityPayload payload, IPayloadContext ctx) {
        if (ctx.player() instanceof ServerPlayer player) {
            RaceAbilities.activate(player, payload.slot());
        }
    }
}