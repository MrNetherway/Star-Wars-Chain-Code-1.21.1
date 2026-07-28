package net.netherway.starwarschaincode.network;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
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
import net.netherway.starwarschaincode.util.DelayedTaskScheduler;
import net.netherway.starwarschaincode.util.SaberBlockAnimState;
import net.netherway.starwarschaincode.util.WeaponAttachmentUtil;
import net.netherway.starwarschaincode.util.WeaponReloadState;
import software.bernie.geckolib.animatable.GeoItem;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID)
public class ModNetworking {

    private static final int SINGLE_RELOAD_TICKS = 20; // 1s — ajusta como quiser
    private static final int DUAL_RELOAD_TICKS = 35;   // um pouco mais pra recarregar as duas

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

        registrar.playToServer(ReloadWeaponPayload.TYPE, ReloadWeaponPayload.STREAM_CODEC, ModNetworking::handleReload);
        registrar.playToClient(ReloadCompletePayload.TYPE, ReloadCompletePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        net.netherway.starwarschaincode.client.ReloadAnimHandler.onReloadComplete(payload)));

        registrar.playToClient(PlayThirdPersonAnimPayload.TYPE, PlayThirdPersonAnimPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        net.netherway.starwarschaincode.client.ThirdPersonAnimHandler.play(payload)));

        registrar.playToClient(StopThirdPersonAnimPayload.TYPE, StopThirdPersonAnimPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        net.netherway.starwarschaincode.client.ThirdPersonAnimHandler.stop(payload)));
    }

    private static void handleReload(ReloadWeaponPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            boolean mainIsWeapon = payload.main() && player.getMainHandItem().getItem() instanceof WeaponItem;
            boolean offIsWeapon = payload.off() && player.getOffhandItem().getItem() instanceof WeaponItem;

            java.util.List<InteractionHand> hands = new java.util.ArrayList<>();
            if (mainIsWeapon && !WeaponReloadState.isReloading(player, InteractionHand.MAIN_HAND))
                hands.add(InteractionHand.MAIN_HAND);
            if (offIsWeapon && !WeaponReloadState.isReloading(player, InteractionHand.OFF_HAND))
                hands.add(InteractionHand.OFF_HAND);

            if (hands.isEmpty()) return;

            java.util.Set<Integer> excludedSlots = new java.util.HashSet<>();

            for (InteractionHand hand : hands) {
                WeaponReloadState.setReloading(player, hand, true);

                ItemStack weaponStack = player.getItemInHand(hand);

                boolean hadPowerPack = weaponStack.has(ModDataComponents.TIBANNA_AMOUNT.get());

                if (hadPowerPack) {
                    int amount = weaponStack.getOrDefault(ModDataComponents.TIBANNA_AMOUNT.get(), 0);

                    ItemStack ejected = new ItemStack(net.netherway.starwarschaincode.item.ModItems.POWER_PACK.get());
                    ejected.set(ModDataComponents.TIBANNA_AMOUNT.get(), amount);

                    weaponStack.remove(ModDataComponents.TIBANNA_AMOUNT.get());

                    int freeSlot = findFirstEmptySlot(player);
                    if (freeSlot >= 0) {
                        player.getInventory().setItem(freeSlot, ejected);
                        excludedSlots.add(freeSlot);
                    } else {
                        player.drop(ejected, false);
                    }

                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                            new PlayThirdPersonAnimPayload(player.getId(), "reload_eject", hand == InteractionHand.OFF_HAND));
                }
            }

            int delayTicks = hands.size() == 2 ? DUAL_RELOAD_TICKS : SINGLE_RELOAD_TICKS;

            net.netherway.starwarschaincode.util.DelayedTaskScheduler.schedule(player.serverLevel(), delayTicks, () -> {
                boolean mainInserted = false;
                boolean offInserted = false;

                for (InteractionHand hand : hands) {
                    ItemStack weaponStack = player.getItemInHand(hand);

                    if (weaponStack.getItem() instanceof WeaponItem) {
                        int foundSlot = findValidPowerPackSlot(player, excludedSlots);

                        if (foundSlot >= 0) {
                            ItemStack found = player.getInventory().getItem(foundSlot);
                            int amount = found.getOrDefault(ModDataComponents.TIBANNA_AMOUNT.get(), 0);
                            weaponStack.set(ModDataComponents.TIBANNA_AMOUNT.get(), amount);
                            found.shrink(1);

                            if (hand == InteractionHand.MAIN_HAND) mainInserted = true;
                            else offInserted = true;
                        }
                    }

                    WeaponReloadState.setReloading(player, hand, false);
                }

                boolean finalMainInserted = mainInserted;
                boolean finalOffInserted = offInserted;
                if (finalMainInserted) {
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                            new PlayThirdPersonAnimPayload(player.getId(), "reload_insert", false));
                }
                if (finalOffInserted) {
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                            new PlayThirdPersonAnimPayload(player.getId(), "reload_insert", true));
                }

// isso estava faltando: avisa o PRÓPRIO jogador (GeckoLib da arma em 1ª pessoa)
                PacketDistributor.sendToPlayer(player, new ReloadCompletePayload(
                        hands.contains(InteractionHand.MAIN_HAND),
                        hands.contains(InteractionHand.OFF_HAND),
                        finalMainInserted,
                        finalOffInserted
                ));
            });
        });
    }

    private static int findFirstEmptySlot(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            if (player.getInventory().items.get(i).isEmpty()) return i;
        }
        return -1;
    }

    private static int findValidPowerPackSlot(ServerPlayer player, java.util.Set<Integer> excludedSlots) {
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            if (excludedSlots.contains(i)) continue;

            ItemStack stack = player.getInventory().items.get(i);
            if (!stack.isEmpty()
                    && stack.getItem() instanceof net.netherway.starwarschaincode.item.custom.PowerPackItem
                    && stack.getOrDefault(ModDataComponents.TIBANNA_AMOUNT.get(), 0) > 0) {
                return i;
            }
        }
        return -1;
    }

    private static ItemStack findValidPowerPack(ServerPlayer player, java.util.Set<ItemStack> excluded) {
        for (ItemStack stack : player.getInventory().items) {
            if (!stack.isEmpty()
                    && stack.getItem() instanceof net.netherway.starwarschaincode.item.custom.PowerPackItem
                    && !excluded.contains(stack)
                    && stack.getOrDefault(ModDataComponents.TIBANNA_AMOUNT.get(), 0) > 0) {
                return stack;
            }
        }
        return null;
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

        boolean activated = stack.getOrDefault(ModDataComponents.ACTIVATED.get(), false);

        if (!activated)
            return;

        stack.set(ModDataComponents.BLOCKING.get(), payload.isBlocking());

        if (payload.isBlocking()) {
            SaberBlockAnimState.startBlock(player, stack);
        } else {
            SaberBlockAnimState.stopBlock(player, stack);
        }
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
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player))
                return;

            ItemStack stack = player.getItemInHand(payload.hand());

            if (!(stack.getItem() instanceof LightsaberItem saber))
                return;

            boolean activated = stack.getOrDefault(ModDataComponents.ACTIVATED.get(), false);
            boolean newActivated = !activated;

            // Garante que ESTE ItemStack tenha um ID único
            long id = GeoItem.getOrAssignId(stack, player.serverLevel());

            String animName = newActivated
                    ? "lightsaber_activate"
                    : "lightsaber_deactivate";

            saber.triggerAnim(player, id, "lightsaber_controller", animName);

            if(newActivated) {
                stack.set(ModDataComponents.ACTIVATED.get(), true);
            } else {
                DelayedTaskScheduler.schedule(player.serverLevel(), 10, () -> {
                    stack.set(ModDataComponents.ACTIVATED.get(), false);
                });
            }
        });
    }

    private static void handleFireBlaster(FireBlasterPayload payload, IPayloadContext ctx) {

        if (!(ctx.player() instanceof ServerPlayer player))
            return;

        ItemStack stack = player.getItemInHand(payload.hand());

        if (!(stack.getItem() instanceof WeaponItem weapon))
            return;

        GeoItem.getOrAssignId(stack, player.serverLevel());

        if (WeaponReloadState.isReloading(player, payload.hand()))
            return; // não atira enquanto recarrega

        int ammo = stack.getOrDefault(ModDataComponents.TIBANNA_AMOUNT.get(), 0);
        if (ammo <= 0)
            return; // sem pente ou vazio

        stack.set(ModDataComponents.TIBANNA_AMOUNT.get(), ammo - 1);

        BlasterBoltEntity bolt = new BlasterBoltEntity(player, player.level());
        bolt.setDamage(WeaponAttachmentUtil.getEffectiveDamage(stack));
        bolt.setFireDistance(WeaponAttachmentUtil.getEffectiveFireDistance(stack));

        // posição de saída: olhos do player + offset lateral (direita ou esquerda)
        float yaw = player.getYRot() * ((float) Math.PI / 180F);
        Vec3 right = new Vec3(Math.cos(yaw), 0, Math.sin(yaw));

        float sideSign = payload.hand() == InteractionHand.MAIN_HAND ? -1f : 1f;
        Vec3 sideOffset = right.scale(0.3 * sideSign);

        Vec3 spawnPos = player.getEyePosition().add(sideOffset).add(0, -.1,0);
        bolt.setPos(spawnPos);

        bolt.shootFromRotation(player, player.getXRot()-1 , player.getYRot(), 0.0F,
                weapon.getProjectileSpeed(), 0.0F);

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                new PlayThirdPersonAnimPayload(player.getId(), "shoot", payload.hand() == InteractionHand.OFF_HAND));
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