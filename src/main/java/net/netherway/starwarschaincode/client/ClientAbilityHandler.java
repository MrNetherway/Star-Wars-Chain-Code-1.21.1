package net.netherway.starwarschaincode.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.netherway.starwarschaincode.item.custom.LightsaberItem;
import net.netherway.starwarschaincode.item.custom.WeaponItem;
import net.netherway.starwarschaincode.network.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = "starwarschaincode", value = Dist.CLIENT)
public class ClientAbilityHandler {

    private static int lightsaberCharge = 0;
    private static boolean charging = false;
    private static boolean blocking = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null)
            return;

        while (ModKeyMappings.RACE_ABILITY_1.consumeClick()) {
            PacketDistributor.sendToServer(new ActivateAbilityPayload(1));
        }

        while (ModKeyMappings.RACE_ABILITY_2.consumeClick()) {
            PacketDistributor.sendToServer(new ActivateAbilityPayload(2));
        }

        while (ModKeyMappings.LIGHTSABER_ACTIVATE.consumeClick()) {
            if (!(mc.player.getMainHandItem().getItem() instanceof LightsaberItem)) { return; }
            PacketDistributor.sendToServer(new ActivateSaberPayload());
        }

        if (ModKeyMappings.LIGHTSABER_IMPULSE.isDown()) {

            ItemStack stack = mc.player.getMainHandItem();

            boolean blocking = stack.getOrDefault(
                    ModDataComponents.BLOCKING.get(),
                    false
            );

            boolean activated = stack.getOrDefault(
                    ModDataComponents.ACTIVATED.get(),
                    false
            );

            if(blocking || !activated)
                return;

            if (stack.getItem() instanceof LightsaberItem) {
                mc.player.startUsingItem(InteractionHand.MAIN_HAND);
                charging = true;
                lightsaberCharge++;
            }

        } else if (charging) {
            mc.player.stopUsingItem();
            ItemStack stack = mc.player.getMainHandItem();

            if (stack.getItem() instanceof LightsaberItem && lightsaberCharge >= 10) {
                PacketDistributor.sendToServer(
                        new LightsaberImpulsePayload(lightsaberCharge)
                );
            }

            charging = false;
            lightsaberCharge = 0;
        }
    }

    private static final Map<UUID, Long> lastUsed = new HashMap<>();

    @SubscribeEvent
    public static void onMouseClick(InputEvent.MouseButton.Pre event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null)
            return;

        ItemStack stack = mc.player.getMainHandItem();

        if(event.getButton() == 0){
            if(event.getAction() == 1){

                if (stack.getItem() instanceof WeaponItem) {

                    event.setCanceled(true);

                    long COOLDOWN_TICKS = ((WeaponItem) stack.getItem()).getFireRate() * 20L;
                    long now = mc.player.level().getGameTime();
                    long last = lastUsed.getOrDefault(mc.player.getUUID(), 0L);

                    if (now - last < COOLDOWN_TICKS) {
                        long remaining = (COOLDOWN_TICKS - (now - last)) / 20;
                        return;
                    }

                    lastUsed.put(mc.player.getUUID(), now);


                    PacketDistributor.sendToServer(new FireBlasterPayload());
                }

                if (stack.getItem() instanceof LightsaberItem) {

                    boolean blocking = stack.getOrDefault(
                            ModDataComponents.BLOCKING.get(),
                            false
                    );

                    if (blocking) {
                        event.setCanceled(true);
                        return;
                    }
                }

            }
            else{

            }
        }
        else {
            boolean activated = stack.getOrDefault(
                    ModDataComponents.ACTIVATED.get(),
                    false
            );

            if(!activated)
                return;

            if(event.getAction() == 1) {
                if(stack.getItem() instanceof LightsaberItem){
                    mc.player.startUsingItem(InteractionHand.MAIN_HAND);
                    blocking = true;
                    PacketDistributor.sendToServer(new BlockingPayload(true));
                }
            }
            else{
                if(stack.getItem() instanceof LightsaberItem && blocking){
                    mc.player.stopUsingItem();
                    blocking = false;
                    PacketDistributor.sendToServer(new BlockingPayload(false));
                }
            }
        }
    }
}