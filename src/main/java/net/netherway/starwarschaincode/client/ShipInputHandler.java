package net.netherway.starwarschaincode.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.entity.custom.ShipEntity;
import net.netherway.starwarschaincode.network.ShipInputPayload;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID, value = Dist.CLIENT)
public class ShipInputHandler {

    private static boolean lastLeft, lastRight, lastUp, lastDown;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (!(mc.player.getVehicle() instanceof ShipEntity)) return;

        boolean left = mc.options.keyLeft.isDown();
        boolean right = mc.options.keyRight.isDown();
        boolean up = mc.options.keyUp.isDown();
        boolean down = mc.options.keyDown.isDown();

        if (left != lastLeft || right != lastRight || up != lastUp || down != lastDown) {
            lastLeft = left;
            lastRight = right;
            lastUp = up;
            lastDown = down;

            if (mc.player.getVehicle() instanceof ShipEntity ship) {
                ship.setInput(left, right, up, down); // aplica local imediatamente
            }
            PacketDistributor.sendToServer(new ShipInputPayload(left, right, up, down));
        }
    }
}