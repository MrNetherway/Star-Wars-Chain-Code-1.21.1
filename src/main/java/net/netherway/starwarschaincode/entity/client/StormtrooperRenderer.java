package net.netherway.starwarschaincode.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.entity.custom.StormtrooperCommanderEntity;
import net.netherway.starwarschaincode.entity.custom.StormtrooperEntity;

public class StormtrooperRenderer extends HumanoidMobRenderer<StormtrooperEntity, PlayerModel<StormtrooperEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    StarWarsChainCode.MOD_ID,
                    "textures/entity/stormtrooper.png"
            );

    public StormtrooperRenderer(EntityRendererProvider.Context context) {
        super(
                context,
                new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false),
                0.5F
        );
    }

    @Override
    public void render(StormtrooperEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        HumanoidModel.ArmPose pose = entity.isAggressive() ? HumanoidModel.ArmPose.CROSSBOW_HOLD : HumanoidModel.ArmPose.EMPTY;
        this.getModel().rightArmPose = pose;
        this.getModel().leftArmPose = pose;
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(StormtrooperEntity entity) {
        return TEXTURE;
    }
}