package net.netherway.starwarschaincode.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.component.ModAttachments;
import net.netherway.starwarschaincode.entity.custom.ShipEntity;
import net.netherway.starwarschaincode.network.ShipRotationPayload;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID, value = Dist.CLIENT)
public class ShipInputHandler {

    private static float lastPlayerYaw;
    private static float lastPlayerPitch;
    private static boolean wasRidingShip = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // primeiro checa se está numa nave, sem exigir "pronta" ainda
        if (!(mc.player.getVehicle() instanceof ShipEntity ship)) {
            wasRidingShip = false;
            return;
        }

        if (mc.player.getData(ModAttachments.HYPERSPACE_TRAVEL).traveling) {
            ship.setFlightInput(false, false, false, false, false, false);
            return;
        }


        boolean ready = ship.isFlightReady();

        if (ready && !wasRidingShip) {
            lastPlayerYaw = mc.player.getYRot();
            lastPlayerPitch = mc.player.getXRot();
        }
        wasRidingShip = ready;

        boolean rollLeft = mc.options.keyLeft.isDown();
        boolean rollRight = mc.options.keyRight.isDown();
        boolean thrustForward = mc.options.keyUp.isDown();
        boolean thrustBackward = mc.options.keyDown.isDown();
        boolean ascend = mc.options.keyJump.isDown();
        boolean descend = ModKeyMappings.SHIP_DESCEND.isDown();

        if (!ready && (thrustForward || ascend)) {
            net.minecraft.network.chat.Component reason = !ship.getComponentInventory().isFullyEquipped()
                    ? net.minecraft.network.chat.Component.translatable("message.starwarschaincode.ship.missing_components").withStyle(net.minecraft.ChatFormatting.RED)
                    : net.minecraft.network.chat.Component.translatable("message.starwarschaincode.ship.no_fuel").withStyle(net.minecraft.ChatFormatting.RED);
            mc.player.displayClientMessage(reason, true);
        }

        float currentYaw = mc.player.getYRot();
        float currentPitch = mc.player.getXRot();

        float deltaYaw = currentYaw - lastPlayerYaw;
        float deltaPitch = currentPitch - lastPlayerPitch;

        // Reseta a rotação do player pra um valor neutro, evitando que o pitch trave em ±90
        mc.player.setXRot(0);
        lastPlayerYaw = currentYaw; // yaw não precisa resetar, não tem clamp
        lastPlayerPitch = 0;

        ship.addLocalYaw(deltaYaw);
        ship.addLocalPitch(deltaPitch);
        ship.setFlightInput(rollLeft, rollRight, thrustForward, thrustBackward, ascend, descend);

        byte flags = ShipRotationPayload.packFlags(rollLeft, rollRight, thrustForward, thrustBackward, ascend, descend);
        PacketDistributor.sendToServer(new ShipRotationPayload(deltaYaw, deltaPitch, flags));
    }

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (mc.player.getData(ModAttachments.HYPERSPACE_TRAVEL).traveling) {
            // deixa passar apenas telas que a própria sequência de hyperespaço for abrir (nenhuma por enquanto)
            event.setCanceled(true);
        }
    }
}