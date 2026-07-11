package net.netherway.starwarschaincode.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.netherway.starwarschaincode.item.custom.WeaponItem;
import net.netherway.starwarschaincode.network.ActivateAbilityPayload;
import net.netherway.starwarschaincode.network.FireBlasterPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = "starwarschaincode", value = Dist.CLIENT)
public class ClientAbilityHandler {

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
    }

    private static final Map<UUID, Long> lastUsed = new HashMap<>();

    @SubscribeEvent
    public static void onMouseClick(InputEvent.MouseButton.Pre event) {

        if (event.getButton() != 0)
            return;

        if (event.getAction() != 1)
            return;

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null)
            return;

        ItemStack stack = mc.player.getMainHandItem();

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
    }
}