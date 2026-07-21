package net.netherway.starwarschaincode.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.component.ModAttachments;
import net.netherway.starwarschaincode.entity.custom.ShipEntity;
import net.netherway.starwarschaincode.screen.custom.PlanetMapScreen;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID, value = Dist.CLIENT)
public class ShipInventoryKeyHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        if (mc.options.keyInventory.matches(event.getKey(), event.getScanCode()) && event.getAction() == org.lwjgl.glfw.GLFW.GLFW_PRESS) {
            if (mc.player.getData(ModAttachments.HYPERSPACE_TRAVEL).traveling) {
                return; // em viagem, não abre o mapa
            }
            if (mc.player.getVehicle() instanceof ShipEntity) {
                mc.setScreen(new PlanetMapScreen());
            }
        }
    }
}