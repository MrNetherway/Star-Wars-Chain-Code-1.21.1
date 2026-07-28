package net.netherway.starwarschaincode.client;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.component.ModAttachments;
import net.netherway.starwarschaincode.entity.ShipType;
import net.netherway.starwarschaincode.entity.custom.ShipEntity;
import net.netherway.starwarschaincode.attachment.HyperspaceTravelData;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID, value = Dist.CLIENT)
public class ClientShipCameraHandler {

    private static final float LAG_FACTOR = 0.08f;

    private static org.joml.Quaternionf prevSmoothedOrientation = new org.joml.Quaternionf();
    private static org.joml.Quaternionf smoothedOrientation = new org.joml.Quaternionf();
    private static boolean initialized = false;
    private static CameraType savedCameraType = null;
    private static boolean wasRiding = false;

    // ---- Config do zoom de hyperespaço ----
    private static final double MIN_FOV = 20.0;
    private static final double MAX_FOV = 140.0;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.options == null || mc.screen != null) return;

        boolean ridingShip = mc.player.getVehicle() instanceof ShipEntity;

        if (ridingShip) {
            if (!wasRiding) {
                savedCameraType = mc.options.getCameraType();
            }
            if (mc.options.getCameraType() != CameraType.THIRD_PERSON_BACK) {
                mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
            }
        } else if (wasRiding) {
            if (savedCameraType != null) {
                mc.options.setCameraType(savedCameraType);
            }
            savedCameraType = null;
        }
        wasRiding = ridingShip;

        if (!ridingShip) {
            initialized = false;
            return;
        }

        ShipEntity ship = (ShipEntity) mc.player.getVehicle();

        prevSmoothedOrientation = new org.joml.Quaternionf(smoothedOrientation);
        org.joml.Quaternionf target = ship.getOrientation();

        if (!initialized) {
            smoothedOrientation = new org.joml.Quaternionf(target);
            prevSmoothedOrientation = new org.joml.Quaternionf(target);
            initialized = true;
            return;
        }

        smoothedOrientation.slerp(target, LAG_FACTOR);
    }

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (!(mc.player.getVehicle() instanceof ShipEntity)) return;

        float partialTick = (float) event.getPartialTick();

        org.joml.Quaternionf interp = new org.joml.Quaternionf(prevSmoothedOrientation)
                .slerp(smoothedOrientation, partialTick);

        org.joml.Vector3f euler = interp.getEulerAnglesYXZ(new org.joml.Vector3f());

        event.setYaw((float) Math.toDegrees(-euler.y));
        event.setPitch((float) Math.toDegrees(euler.x));
        event.setRoll((float) Math.toDegrees(euler.z));
    }

    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // --- Prioridade 1: zoom de hyperespaço, se estiver viajando ---
        HyperspaceTravelData travelData = mc.player.getData(ModAttachments.HYPERSPACE_TRAVEL);
        if (travelData.traveling) {
            applyHyperspaceFov(event, mc, travelData);
            return; // ignora o boost de velocidade normal enquanto viaja
        }

        // --- Boost de FOV normal por velocidade da nave ---
        if (!(mc.player.getVehicle() instanceof ShipEntity ship)) return;

        double speed = ship.getDeltaMovement().length();
        ShipType type = ship.getShipType();
        if (type == null) return;

        double speedRatio = Mth.clamp(speed / type.maxSpeed(), 0.0, 1.0);
        double fovBoost = speedRatio * 45.0;

        event.setFOV(event.getFOV() + fovBoost);
    }

    private static void applyHyperspaceFov(ViewportEvent.ComputeFov event, Minecraft mc, HyperspaceTravelData data) {
        double partialTick = event.getPartialTick();
        double elapsed = (mc.level.getGameTime() - data.startGameTime) + partialTick;
        double baseFov = event.getFOV();

        int zoomInEnd = HyperspaceTravelData.ZOOM_IN_TICKS;
        int zoomOutEnd = zoomInEnd + HyperspaceTravelData.ZOOM_OUT_TICKS;
        int recoveryEnd = zoomOutEnd + HyperspaceTravelData.RECOVERY_TICKS;

        double targetFov;

        if (elapsed <= zoomInEnd) {
            double t = zoomInEnd <= 0 ? 1.0 : Mth.clamp(elapsed / zoomInEnd, 0.0, 1.0);
            targetFov = Mth.lerp(easeInOutQuad(t), baseFov, MIN_FOV);

        } else if (elapsed <= zoomOutEnd) {
            double phaseTicks = zoomOutEnd - zoomInEnd;
            double t = phaseTicks <= 0 ? 1.0 : Mth.clamp((elapsed - zoomInEnd) / phaseTicks, 0.0, 1.0);
            targetFov = Mth.lerp(easeInQuad(t), MIN_FOV, MAX_FOV);

        } else if (elapsed <= recoveryEnd) {
            double phaseTicks = recoveryEnd - zoomOutEnd;
            double t = phaseTicks <= 0 ? 1.0 : Mth.clamp((elapsed - zoomOutEnd) / phaseTicks, 0.0, 1.0);
            targetFov = Mth.lerp(easeOutQuad(t), MAX_FOV, baseFov);

        } else {
            // fase de travel: FOV já normal, fica parado assim
            targetFov = baseFov;
        }

        event.setFOV(targetFov);
    }

    private static double easeOutQuad(double t) {
        return 1 - (1 - t) * (1 - t);
    }

    private static double easeInOutQuad(double t) {
        return t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2;
    }

    private static double easeInQuad(double t) {
        return t * t;
    }
}