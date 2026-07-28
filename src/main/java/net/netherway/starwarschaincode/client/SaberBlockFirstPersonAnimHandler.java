package net.netherway.starwarschaincode.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.netherway.starwarschaincode.item.custom.LightsaberItem;
import net.netherway.starwarschaincode.item.custom.SaberBlockAnimNames;
import software.bernie.geckolib.animatable.GeoItem;

@EventBusSubscriber(modid = "starwarschaincode", value = Dist.CLIENT)
public class SaberBlockFirstPersonAnimHandler {

    private static boolean active = false;
    private static String currentAnim = null;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ItemStack stack = mc.player.getMainHandItem();

        boolean shouldBeActive = mc.options.getCameraType().isFirstPerson()
                && stack.getItem() instanceof LightsaberItem
                && stack.getOrDefault(ModDataComponents.ACTIVATED.get(), false)
                && stack.getOrDefault(ModDataComponents.BLOCKING.get(), false);

        if (shouldBeActive && !active) {
            // começou agora: sorteia uma animação
            currentAnim = SaberBlockAnimNames.NAMES[mc.player.getRandom().nextInt(SaberBlockAnimNames.NAMES.length)];

            long id = GeoItem.getId(stack);
            ((LightsaberItem) stack.getItem()).triggerAnim(mc.player, id, "block_controller", currentAnim);

            active = true;
        } else if (!shouldBeActive && active) {
            // parou (soltou botão, trocou câmera, trocou de item, etc)
            if (stack.getItem() instanceof LightsaberItem saber && currentAnim != null) {
                long id = GeoItem.getId(stack);
                saber.stopTriggeredAnim(mc.player, id, "block_controller", currentAnim);
            }

            active = false;
            currentAnim = null;
        }
    }
}