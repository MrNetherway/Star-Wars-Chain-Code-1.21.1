package net.netherway.starwarschaincode.entity.client.ships;// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.entity.custom.ShipEntity;

public class Z95HeadhunterModel extends EntityModel<ShipEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "z95headhunter"), "main");
	private final ModelPart body;
	private final ModelPart wing1;
	private final ModelPart wing2;

	public Z95HeadhunterModel(ModelPart root) {
		this.body = root.getChild("body");
		this.wing1 = root.getChild("wing1");
		this.wing2 = root.getChild("wing2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -42.0F, -92.0F, 16.0F, 16.0F, 128.0F, new CubeDeformation(0.0F))
		.texOffs(288, 36).addBox(-8.0F, -60.0F, -92.0F, 16.0F, 18.0F, 40.0F, new CubeDeformation(0.0F))
		.texOffs(0, 377).addBox(-8.0F, -60.0F, -102.0F, 16.0F, 13.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(288, 0).addBox(-17.0F, -38.0F, -89.0F, 34.0F, 2.0F, 34.0F, new CubeDeformation(0.0F))
		.texOffs(268, 314).addBox(-8.0F, -58.0F, -54.0F, 16.0F, 18.0F, 40.0F, new CubeDeformation(-2.0F))
		.texOffs(268, 248).addBox(-8.0F, -42.0F, 34.0F, 16.0F, 16.0F, 50.0F, new CubeDeformation(-2.0F))
		.texOffs(0, 144).addBox(7.0F, -41.0F, -93.0F, 8.0F, 11.0F, 93.0F, new CubeDeformation(-1.0F))
		.texOffs(202, 144).addBox(-15.0F, -41.0F, -93.0F, 8.0F, 11.0F, 93.0F, new CubeDeformation(-1.0F)),
				PartPose.offsetAndRotation(0.0F, 0, 0.0F, (float) Math.PI, (float) Math.PI, 0.0F));

		PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 351).addBox(-30.0F, -13.0F, -2.0F, 26.0F, 6.0F, 20.0F, new CubeDeformation(-2.0F)), PartPose.offsetAndRotation(17.0F, -26.0F, 44.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition wing1 = partdefinition.addOrReplaceChild("wing1", CubeListBuilder.create().texOffs(0, 248).addBox(-35.0F, -58.0F, -93.0F, 25.0F, 25.0F, 42.0F, new CubeDeformation(-1.0F))
		.texOffs(288, 94).addBox(-63.0F, -40.0F, -89.0F, 32.0F, 2.0F, 34.0F, new CubeDeformation(0.0F))
		.texOffs(132, 315).addBox(-95.0F, -40.0F, -89.0F, 32.0F, 2.0F, 28.0F, new CubeDeformation(0.0F))
		.texOffs(252, 372).addBox(-117.0F, -40.0F, -89.0F, 22.0F, 2.0F, 22.0F, new CubeDeformation(0.0F))
		.texOffs(92, 375).addBox(-125.0F, -43.0F, -90.0F, 8.0F, 8.0F, 27.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0, 0.0F, (float) Math.PI, (float) Math.PI, 0.0F));

		PartDefinition wing2 = partdefinition.addOrReplaceChild("wing2", CubeListBuilder.create().texOffs(134, 248).addBox(10.0F, -58.0F, -93.0F, 25.0F, 25.0F, 42.0F, new CubeDeformation(-1.0F))
		.texOffs(0, 315).addBox(34.0F, -40.0F, -89.0F, 32.0F, 2.0F, 34.0F, new CubeDeformation(0.0F))
		.texOffs(132, 345).addBox(66.0F, -40.0F, -89.0F, 32.0F, 2.0F, 28.0F, new CubeDeformation(0.0F))
		.texOffs(340, 372).addBox(98.0F, -40.0F, -89.0F, 22.0F, 2.0F, 22.0F, new CubeDeformation(0.0F))
		.texOffs(162, 375).addBox(120.0F, -43.0F, -90.0F, 8.0F, 8.0F, 27.0F, new CubeDeformation(0.0F)),
		PartPose.offsetAndRotation(0.0F, 0, 0.0F, (float) Math.PI, (float) Math.PI, 0.0F)); // xRot = 180°

		return LayerDefinition.create(meshdefinition, 512, 512);
	}

	@Override
	public void setupAnim(ShipEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		// sem animação por enquanto (modelo estático)
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		wing1.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		wing2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}