package net.netherway.starwarschaincode.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.component.ModAttachments;
import net.netherway.starwarschaincode.entity.custom.ShipEntity;
import net.netherway.starwarschaincode.attachment.HyperspaceTravelData;
import org.joml.Matrix4f;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID, value = Dist.CLIENT)
public class HyperspaceOverlayHandler {

    private static final ResourceLocation OVERLAY_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "textures/gui/hyperspace_overlay.png");

    private static final float ROTATION_SPEED = 0.15f;
    private static final float QUAD_DISTANCE = 20f; // o quanto à frente da câmera o quad fica

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        HyperspaceTravelData data = mc.player.getData(ModAttachments.HYPERSPACE_TRAVEL);
        if (!data.traveling) return;

        int zoomOutEnd = HyperspaceTravelData.ZOOM_IN_TICKS + HyperspaceTravelData.ZOOM_OUT_TICKS;

        float partialTick = event.getPartialTick().getRealtimeDeltaTicks();
        double elapsed = (mc.level.getGameTime() - data.startGameTime) + partialTick;

        if (elapsed < zoomOutEnd) return;

        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();

        // ---- 1. Overlay: quad preso à câmera, sem depth test, cobre tudo ----
        drawCameraLockedOverlay(poseStack, camera, elapsed);

        // ---- 2. Nave: renderiza normalmente no mundo, por cima do overlay ----
        Entity vehicle = mc.player.getVehicle();
        if (vehicle instanceof ShipEntity ship) {
            renderShipOnTop(mc, poseStack, camera, ship, (float) partialTick);
        }
    }

    private static void drawCameraLockedOverlay(PoseStack poseStack, Camera camera, double elapsed) {
        RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, OVERLAY_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest(); // <-- faltava isso
        RenderSystem.disableCull();

        poseStack.pushPose();

        poseStack.mulPose(camera.rotation()); // PRIMEIRO orienta pro espaço da câmera
        poseStack.translate(0, 0, -QUAD_DISTANCE); // DEPOIS empurra pra frente, agora no eixo local certo

        float rotationDegrees = (float) (elapsed * ROTATION_SPEED) % 360f;
        poseStack.mulPose(new org.joml.Quaternionf().rotateZ((float) Math.toRadians(rotationDegrees))); // spin do próprio quad

        float halfSize = QUAD_DISTANCE * 7f; // tamanho do quad, grande o bastante pra cobrir a tela toda

        Matrix4f matrix = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(
                com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_TEX
        );

        buffer.addVertex(matrix, -halfSize, -halfSize, 0).setUv(0, 1);
        buffer.addVertex(matrix, halfSize, -halfSize, 0).setUv(1, 1);
        buffer.addVertex(matrix, halfSize, halfSize, 0).setUv(1, 0);
        buffer.addVertex(matrix, -halfSize, halfSize, 0).setUv(0, 0);

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        poseStack.popPose();

        RenderSystem.enableDepthTest(); // <-- adiciona aqui
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void renderShipOnTop(Minecraft mc, PoseStack poseStack, Camera camera,
                                        ShipEntity ship, float partialTick) {
        // limpa o depth buffer aqui pra garantir que a nave desenha por cima do quad,
        // já que o quad foi desenhado sem depth write
        RenderSystem.clear(org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        Vec3 camPos = camera.getPosition();
        double shipX = Mth.lerp(partialTick, ship.xOld, ship.getX());
        double shipY = Mth.lerp(partialTick, ship.yOld, ship.getY());
        double shipZ = Mth.lerp(partialTick, ship.zOld, ship.getZ());

        dispatcher.render(
                ship,
                shipX - camPos.x,
                shipY - camPos.y,
                shipZ - camPos.z,
                0f,
                partialTick,
                poseStack,
                bufferSource,
                dispatcher.getPackedLightCoords(ship, partialTick)
        );

        bufferSource.endBatch();
    }
}