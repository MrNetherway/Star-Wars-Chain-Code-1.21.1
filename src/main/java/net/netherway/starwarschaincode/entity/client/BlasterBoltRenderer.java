package net.netherway.starwarschaincode.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.entity.custom.BlasterBoltEntity;

public class BlasterBoltRenderer extends EntityRenderer<BlasterBoltEntity> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    StarWarsChainCode.MOD_ID,
                    "textures/entity/blasterboltentity.png"
            );

    private final BlasterBoltModel<BlasterBoltEntity> model;

    public BlasterBoltRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new BlasterBoltModel<>(context.bakeLayer(BlasterBoltModel.LAYER_LOCATION));
    }

    @Override
    public void render(
            BlasterBoltEntity entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entity.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));

        model.renderToBuffer(
                poseStack,
                buffer.getBuffer(RenderType.entityCutout(TEXTURE)),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                0xFFFFFFFF
        );

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BlasterBoltEntity entity) {
        return TEXTURE;
    }
}