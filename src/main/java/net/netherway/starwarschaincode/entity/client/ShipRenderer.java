package net.netherway.starwarschaincode.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.entity.custom.ShipEntity;
import net.netherway.starwarschaincode.entity.ShipType;

public class ShipRenderer extends EntityRenderer<ShipEntity> {

    public ShipRenderer(EntityRendererProvider.Context context) {
        super(context);
        ShipModelRegistry.init(context.getModelSet());
    }

    @Override
    public ResourceLocation getTextureLocation(ShipEntity entity) {
        ShipType type = entity.getShipType();
        return type != null ? type.texture() : MissingTextureAtlasSprite.getLocation();
    }

    @Override
    public void render(ShipEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        ShipType type = entity.getShipType();
        if (type == null) { super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight); return; }

        var model = ShipModelRegistry.get(type.id());
        if (model == null) { super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight); return; }

        poseStack.translate(0.0, -1.5, 1.0); // esse valor aqui é o que você precisa calibrar

        poseStack.pushPose();

        poseStack.mulPose(entity.getOrientation(partialTicks));

        var vertexConsumer = buffer.getBuffer(model.renderType(getTextureLocation(entity)));
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}