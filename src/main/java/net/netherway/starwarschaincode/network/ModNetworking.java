package net.netherway.starwarschaincode.network;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.netherway.starwarschaincode.entity.ShipComponentInventory;
import net.netherway.starwarschaincode.entity.ShipType;
import net.netherway.starwarschaincode.entity.custom.BlasterBoltEntity;
import net.netherway.starwarschaincode.entity.custom.ShipEntity;
import net.netherway.starwarschaincode.item.custom.LightsaberItem;
import net.netherway.starwarschaincode.item.custom.WeaponItem;
import net.netherway.starwarschaincode.planet.PlanetTravelUtil;
import net.netherway.starwarschaincode.race.RaceAbilities;
import net.netherway.starwarschaincode.component.ModAttachments;
import net.netherway.starwarschaincode.race.RacePassives;
import net.netherway.starwarschaincode.screen.custom.PlatformMenu;
import net.minecraft.world.entity.Entity;
import net.netherway.starwarschaincode.planet.PlanetData;
import net.netherway.starwarschaincode.planet.ModPlanets;
import net.netherway.starwarschaincode.server.HyperspaceLockHandler;
import net.netherway.starwarschaincode.attachment.HyperspaceTravelData;

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
                ShipRotationPayload.TYPE,
                ShipRotationPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    var player = context.player();

                    if (net.netherway.starwarschaincode.server.HyperspaceLockHandler.isTraveling(player)) {
                        return; // ignora qualquer input de nave durante a viagem
                    }

                    if (player.getVehicle() instanceof ShipEntity ship) {
                        ship.addLocalYaw(payload.deltaYaw());
                        ship.addLocalPitch(payload.deltaPitch());
                        ship.setFlightInput(payload.rollLeft(), payload.rollRight(),
                                payload.thrustForward(), payload.thrustBackward(),
                                payload.ascend(), payload.descend());
                    }
                })
        );
        registrar.playToServer(
                SelectShipTabPayload.TYPE,
                SelectShipTabPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player().containerMenu instanceof PlatformMenu menu) {
                        menu.refreshAvailableShips(); // garante que o server viu naves criadas depois da abertura do menu
                        menu.selectShipById(payload.shipId());
                    }
                })
        );
        registrar.playToServer(TravelToPlanetPayload.TYPE, TravelToPlanetPayload.STREAM_CODEC, ModNetworking::handle);
    }

    public static void handle(TravelToPlanetPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();

            if (HyperspaceLockHandler.isTraveling(player)) {
                return; // já em viagem
            }

            if (!(player.getVehicle() instanceof ShipEntity ship)) {
                return;
            }

            PlanetData planet = ModPlanets.byId(payload.planetId());
            if (planet == null) return;

            ServerLevel targetLevel = player.server.getLevel(planet.dimension());
            if (targetLevel == null) return;

            if (ship.level() == targetLevel) return;

            PlanetData current = getCurrentPlanet(ship);
            if (current == null) return;

            double distance = PlanetTravelUtil.distanceBetween(current, planet);
            int cost = PlanetTravelUtil.calculateTibannaCost(distance);

            if (!consumeTibanna(ship, cost)) {
                player.displayClientMessage(
                        Component.translatable("message.starwarschaincode.ship.no_fuel", cost)
                                .withStyle(ChatFormatting.RED),
                        true
                );
                return;
            }

            int durationTicks = PlanetTravelUtil.calculateTravelTicks(distance);
            ship.zeroMotion();

            HyperspaceTravelData data = new HyperspaceTravelData(
                    true,
                    planet.dimension(),
                    planet.spawnX(), planet.spawnY(), planet.spawnZ(),
                    ship.getYRot(),
                    player.level().getGameTime(),
                    durationTicks,
                    ship.getId()
            );
            player.setData(ModAttachments.HYPERSPACE_TRAVEL, data);
        });
    }

    private static boolean consumeTibanna(ShipEntity ship, int cost) {
        ShipComponentInventory inventory = ship.getComponentInventory();
        int energySlot = inventory.findSlotByType(ShipType.ComponentType.ENERGY);
        if (energySlot < 0) return false;

        ItemStack tibannaStack = inventory.getItem(energySlot);
        if (tibannaStack.isEmpty()) return false;

        Integer current = tibannaStack.get(ModDataComponents.TIBANNA_AMOUNT);
        if (current == null || current < cost) return false;

        tibannaStack.set(ModDataComponents.TIBANNA_AMOUNT, current - cost);
        inventory.setItem(energySlot, tibannaStack);
        return true;
    }

    private static PlanetData getCurrentPlanet(ShipEntity ship) {
        var dim = ship.level().dimension();
        return ModPlanets.all().values().stream()
                .filter(p -> p.dimension().equals(dim))
                .findFirst().orElse(null);
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