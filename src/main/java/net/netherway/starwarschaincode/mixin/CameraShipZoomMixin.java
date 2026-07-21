package net.netherway.starwarschaincode.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.netherway.starwarschaincode.entity.ShipType;
import net.netherway.starwarschaincode.entity.custom.ShipEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
public abstract class CameraShipZoomMixin {

    @Shadow
    protected abstract float getMaxZoom(float original);

    @Redirect(
            method = "setup",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"
            )
    )
    private float starwarschaincode$modifyThirdPersonDistance(Camera camera, float original) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() instanceof ShipEntity ship) {
            ShipType type = ship.getShipType();
            if (type != null) {
                original = type.cameraDistance();
            }
        }
        return this.getMaxZoom(original);
    }
}