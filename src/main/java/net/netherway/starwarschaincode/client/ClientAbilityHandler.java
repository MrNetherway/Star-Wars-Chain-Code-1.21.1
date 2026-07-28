package net.netherway.starwarschaincode.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.netherway.starwarschaincode.item.ModItems;
import net.netherway.starwarschaincode.item.custom.LightsaberItem;
import net.netherway.starwarschaincode.item.custom.WeaponItem;
import net.netherway.starwarschaincode.network.*;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = "starwarschaincode", value = Dist.CLIENT)
public class ClientAbilityHandler {

    private static int lightsaberCharge = 0;
    private static boolean charging = false;
    private static boolean blocking = false;
    private static boolean reloadWasDown = false;
    // --- estado do sistema de duas armas ---
    private static final Map<UUID, Long> lastUsedMain = new HashMap<>();
    private static final Map<UUID, Long> lastUsedOff = new HashMap<>();
    // true = a próxima vez que atirar (com as duas na mão) deve ser a mão PRINCIPAL
    private static final Map<UUID, Boolean> nextIsMain = new HashMap<>();
    // adiciona esses dois campos junto dos outros Maps no topo da classe
    private static final Map<UUID, Boolean> lastMainWasWeapon = new HashMap<>();
    private static final Map<UUID, Boolean> lastOffWasWeapon = new HashMap<>();

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null)
            return;

        if (mc.screen != null)
            return;

        UUID id = mc.player.getUUID();

        boolean mainIsWeaponNow = mc.player.getMainHandItem().getItem() instanceof WeaponItem;
        boolean offIsWeaponNow = mc.player.getOffhandItem().getItem() instanceof WeaponItem;

        boolean mainWasWeapon = lastMainWasWeapon.getOrDefault(id, mainIsWeaponNow);
        boolean offWasWeapon = lastOffWasWeapon.getOrDefault(id, offIsWeaponNow);

        if (mainWasWeapon != mainIsWeaponNow || offWasWeapon != offIsWeaponNow) {
            resetTurn(id); // uma das mãos mudou (pegou ou largou arma) — reseta o turno
        }

        lastMainWasWeapon.put(id, mainIsWeaponNow);
        lastOffWasWeapon.put(id, offIsWeaponNow);

        while (ModKeyMappings.RACE_ABILITY_1.consumeClick()) {
            PacketDistributor.sendToServer(new ActivateAbilityPayload(1));
        }

        while (ModKeyMappings.RACE_ABILITY_2.consumeClick()) {
            PacketDistributor.sendToServer(new ActivateAbilityPayload(2));
        }

        while (ModKeyMappings.LIGHTSABER_ACTIVATE.consumeClick()) {
            if(blocking)
                return;
            boolean activated = false;

            activated |= tryActivate(mc, InteractionHand.MAIN_HAND, mc.player.getMainHandItem());
            activated |= tryActivate(mc, InteractionHand.OFF_HAND, mc.player.getOffhandItem());

            if (!activated)
                return;
        }

        if (ModKeyMappings.WEAPON_RELOAD.isDown()) {
            if (!reloadWasDown) {
                reloadWasDown = true;

                boolean mainIsWeapon = mc.player.getMainHandItem().getItem() instanceof WeaponItem;
                boolean offIsWeapon = mc.player.getOffhandItem().getItem() instanceof WeaponItem;

                if (mainIsWeapon || offIsWeapon) {
                    java.util.List<InteractionHand> hands = new java.util.ArrayList<>();

                    if (mainIsWeapon) {
                        ItemStack mainStack = mc.player.getMainHandItem();
                        boolean hadPowerPack = mainStack.has(ModDataComponents.TIBANNA_AMOUNT.get());
                        if (hadPowerPack) {
                            if(isFirstPersonView(mc)) {
                                ((WeaponItem) mainStack.getItem()).triggerAnim(mc.player, GeoItem.getId(mainStack), "weapon_controller", "reload_eject");
                            }
                        }
                        hands.add(InteractionHand.MAIN_HAND);
                    }
                    if (offIsWeapon) {
                        ItemStack offStack = mc.player.getOffhandItem();
                        boolean hadPowerPack = offStack.has(ModDataComponents.TIBANNA_AMOUNT.get());
                        if (hadPowerPack) {
                            if(isFirstPersonView(mc)) {
                                ((WeaponItem) offStack.getItem()).triggerAnim(mc.player, GeoItem.getId(offStack), "weapon_controller", "reload_eject");
                            }

                        }
                        hands.add(InteractionHand.OFF_HAND);
                    }

                    PacketDistributor.sendToServer(new ReloadWeaponPayload(mainIsWeapon, offIsWeapon));
                }
            }
        } else {
            reloadWasDown = false;
        }

        if (ModKeyMappings.LIGHTSABER_IMPULSE.isDown()) {

            ItemStack stack = mc.player.getMainHandItem();

            boolean blocking = stack.getOrDefault(ModDataComponents.BLOCKING.get(), false);
            boolean activated = stack.getOrDefault(ModDataComponents.ACTIVATED.get(), false);

            if (blocking || !activated)
                return;

            if (stack.getItem() instanceof LightsaberItem saber) {
                if (!charging) {
                    // só dispara uma vez, no início do charging
                    if (isFirstPersonView(mc)) {
                        saber.triggerAnim(mc.player, GeoItem.getId(stack), "lightsaber_controller", "saber_impulse");
                    }
                }

                charging = true;
                lightsaberCharge++;
            }

        } else if (charging) {
            mc.player.stopUsingItem();
            ItemStack stack = mc.player.getMainHandItem();

            if (stack.getItem() instanceof LightsaberItem saber) {
                if (lightsaberCharge >= 10) {
                    PacketDistributor.sendToServer(new LightsaberImpulsePayload(lightsaberCharge));
                }

                if (isFirstPersonView(mc)) {
                    saber.stopTriggeredAnim(mc.player, GeoItem.getId(stack), "lightsaber_controller", "saber_impulse");
                }
            }

            charging = false;
            lightsaberCharge = 0;
        }
    }

    @SubscribeEvent
    public static void onMouseClick(InputEvent.MouseButton.Pre event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null)
            return;

        if (mc.screen != null)
            return;

        ItemStack mainStack = mc.player.getMainHandItem();
        ItemStack offStack = mc.player.getOffhandItem();

        boolean mainIsWeapon = mainStack.getItem() instanceof WeaponItem;
        boolean offIsWeapon = offStack.getItem() instanceof WeaponItem;

        UUID id = mc.player.getUUID();

        // ===== RELEASE (soltar o botão) =====
        if (event.getAction() != 1) {
            if (event.getButton() == 1 && blocking) {
                mc.player.stopUsingItem();
                blocking = false;
                PacketDistributor.sendToServer(new BlockingPayload(false));
            }
            return;
        }

        // ===== PRESS (apertar o botão) — tudo que já existia =====

        // ===== BOTÃO ESQUERDO =====
        if (event.getButton() == 0) {

            if (mainIsWeapon && offIsWeapon) {
                event.setCanceled(true);
                fireTurn(mc, id);
                return;
            }

            if (mainIsWeapon) {
                event.setCanceled(true);
                tryFire(mc, id, InteractionHand.MAIN_HAND, mainStack);
                return;
            }

            if (mainStack.getItem() instanceof LightsaberItem) {
                boolean blockingNow = mainStack.getOrDefault(ModDataComponents.BLOCKING.get(), false);
                if (blockingNow) {
                    event.setCanceled(true);
                }
            }

            return;
        }

        // ===== BOTÃO DIREITO =====
        if (event.getButton() == 1) {

            if (!mainIsWeapon && offIsWeapon) {
                boolean mainIsFood = mainStack.getItem().components().has(net.minecraft.core.component.DataComponents.FOOD);

                if (mainIsFood) {
                    return;
                }

                event.setCanceled(true);
                tryFire(mc, id, InteractionHand.OFF_HAND, offStack);
                return;
            }

            if (mainIsWeapon && offIsWeapon) {
                event.setCanceled(true);
                return;
            }

            boolean activated = mainStack.getOrDefault(ModDataComponents.ACTIVATED.get(), false);

            if (!activated)
                return;

            if (mainStack.getItem() instanceof LightsaberItem) {
                mc.player.startUsingItem(InteractionHand.MAIN_HAND);
                blocking = true;
                PacketDistributor.sendToServer(new BlockingPayload(true));
            }
        }
    }

    /** Alterna entre mão principal e offhand a cada clique, respeitando fire rate de cada uma */
    private static void fireTurn(Minecraft mc, UUID id) {
        boolean useMain = nextIsMain.getOrDefault(id, true);

        ItemStack primaryStack = useMain ? mc.player.getMainHandItem() : mc.player.getOffhandItem();
        InteractionHand primaryHand = useMain ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;

        if (hasAmmo(primaryStack)) {
            boolean fired = tryFire(mc, id, primaryHand, primaryStack);
            if (fired) {
                nextIsMain.put(id, !useMain);
            }
            return;
        }

        // arma da vez está sem munição: tenta a outra mão direto, sem esperar
        ItemStack otherStack = useMain ? mc.player.getOffhandItem() : mc.player.getMainHandItem();
        InteractionHand otherHand = useMain ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;

        if (!(otherStack.getItem() instanceof WeaponItem))
            return;

        boolean fired = tryFire(mc, id, otherHand, otherStack);
        if (fired) {
            // turno passa a ser sempre a outra (a que tem munição), até a primeira ser recarregada
            nextIsMain.put(id, useMain);
        }
    }

    private static boolean hasAmmo(ItemStack stack) {
        if (!(stack.getItem() instanceof WeaponItem))
            return false;
        int ammo = stack.getOrDefault(ModDataComponents.TIBANNA_AMOUNT.get(), 0);
        return ammo > 0;
    }

    private static boolean isFirstPersonView(Minecraft mc) {
        return mc.options.getCameraType().isFirstPerson();
    }


    /** Tenta atirar respeitando o fire rate daquela mão específica. Retorna true se atirou. */
    private static boolean tryFire(Minecraft mc, UUID id, InteractionHand hand, ItemStack stack) {
        if (!(stack.getItem() instanceof WeaponItem weapon))
            return false;

        int ammo = stack.getOrDefault(ModDataComponents.TIBANNA_AMOUNT.get(), 0);
        if (ammo <= 0)
            return false; // sem munição, nem anima nem manda pro servidor

        Map<UUID, Long> lastUsedMap = hand == InteractionHand.MAIN_HAND ? lastUsedMain : lastUsedOff;

        long cooldownTicks = weapon.getFireRate() * 20L;
        long now = mc.player.level().getGameTime();
        long last = lastUsedMap.getOrDefault(id, 0L);

        if (now - last < cooldownTicks) {
            return false;
        }

        lastUsedMap.put(id, now);

        weapon.stopTriggeredAnim(mc.player, GeoItem.getId(stack), "weapon_controller", "shoot");

        if (isFirstPersonView(mc)) {
            weapon.triggerAnim(mc.player, GeoItem.getId(stack), "weapon_controller", "shoot");
        }

        PacketDistributor.sendToServer(new FireBlasterPayload(hand));
        return true;
    }

    private static boolean tryActivate(Minecraft mc, InteractionHand hand, ItemStack stack) {
        if (!(stack.getItem() instanceof LightsaberItem))
            return false;

        PacketDistributor.sendToServer(new ActivateSaberPayload(hand));
        return true;
    }

    /** Reseta o turno de alternância — chamar quando o jogador tirar uma das armas da mão */
    public static void resetTurn(UUID id) {
        nextIsMain.put(id, true);
    }
}