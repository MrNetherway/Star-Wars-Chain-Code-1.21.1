package net.netherway.starwarschaincode.entity.client.ships;// Made with Blockbench 5.1.5
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
		.texOffs(7, 145).addBox(-5.0F, -26.0F, -91.0F, 10.0F, 1.0F, 127.0F, new CubeDeformation(0.0F))
		.texOffs(338, 354).addBox(-8.0F, -60.0F, -92.0F, 16.0F, 18.0F, 40.0F, new CubeDeformation(0.0F))
		.texOffs(446, 447).addBox(-8.0F, -60.0F, -102.0F, 16.0F, 13.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(202, 354).addBox(-17.0F, -38.0F, -89.0F, 34.0F, 2.0F, 34.0F, new CubeDeformation(0.0F))
		.texOffs(202, 390).addBox(-8.0F, -58.0F, -54.0F, 16.0F, 18.0F, 40.0F, new CubeDeformation(-2.0F))
		.texOffs(202, 288).addBox(-8.0F, -42.0F, 34.0F, 16.0F, 16.0F, 50.0F, new CubeDeformation(-2.0F))
		.texOffs(0, 288).addBox(7.0F, -41.0F, -93.0F, 8.0F, 11.0F, 93.0F, new CubeDeformation(-1.0F))
		.texOffs(288, 0).addBox(-15.0F, -41.0F, -93.0F, 8.0F, 11.0F, 93.0F, new CubeDeformation(-1.0F)),
				PartPose.offsetAndRotation(0.0F, 0, 0.0F, (float) Math.PI, (float) Math.PI, 0.0F));

		PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(422, 212).addBox(-30.0F, -13.0F, -2.0F, 26.0F, 6.0F, 20.0F, new CubeDeformation(-2.0F)), PartPose.offsetAndRotation(17.0F, -26.0F, 44.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition wing1 = partdefinition.addOrReplaceChild("wing1", CubeListBuilder.create().texOffs(288, 171).addBox(-35.0F, -58.0F, -93.0F, 25.0F, 25.0F, 42.0F, new CubeDeformation(-1.0F))
		.texOffs(314, 412).mirror().addBox(-63.0F, -40.0F, -89.0F, 32.0F, 2.0F, 34.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(422, 104).mirror().addBox(-95.0F, -40.0F, -89.0F, 32.0F, 2.0F, 28.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 428).mirror().addBox(-117.0F, -40.0F, -89.0F, 22.0F, 2.0F, 22.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(132, 392).mirror().addBox(-125.0F, -43.0F, -90.0F, 8.0F, 8.0F, 27.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(288, 261).addBox(-125.0F, -43.0F, -66.0F, 8.0F, 8.0F, 15.0F, new CubeDeformation(-3.0F))
		.texOffs(212, 448).addBox(-127.0F, -45.0F, -57.0F, 12.0F, 12.0F, 6.0F, new CubeDeformation(-3.0F))
		.texOffs(334, 296).addBox(-35.0F, -58.0F, -119.0F, 25.0F, 25.0F, 33.0F, new CubeDeformation(-6.0F))
		.texOffs(422, 164).addBox(-37.0F, -60.0F, -112.0F, 29.0F, 29.0F, 19.0F, new CubeDeformation(-6.0F)),
				PartPose.offsetAndRotation(6.0F, -2, 0.0F, (float) Math.PI, (float) Math.PI, 0.0F));

		PartDefinition wing2 = partdefinition.addOrReplaceChild("wing2", CubeListBuilder.create().texOffs(288, 104).addBox(10.0F, -58.0F, -93.0F, 25.0F, 25.0F, 42.0F, new CubeDeformation(-1.0F))
		.texOffs(334, 238).addBox(10.0F, -58.0F, -119.0F, 25.0F, 25.0F, 33.0F, new CubeDeformation(-6.0F))
		.texOffs(314, 412).addBox(34.0F, -40.0F, -89.0F, 32.0F, 2.0F, 34.0F, new CubeDeformation(0.0F))
		.texOffs(422, 104).addBox(66.0F, -40.0F, -89.0F, 32.0F, 2.0F, 28.0F, new CubeDeformation(0.0F))
		.texOffs(0, 428).addBox(98.0F, -40.0F, -89.0F, 22.0F, 2.0F, 22.0F, new CubeDeformation(0.0F))
		.texOffs(132, 392).addBox(120.0F, -43.0F, -90.0F, 8.0F, 8.0F, 27.0F, new CubeDeformation(0.0F))
		.texOffs(176, 448).addBox(118.0F, -45.0F, -57.0F, 12.0F, 12.0F, 6.0F, new CubeDeformation(-3.0F))
		.texOffs(288, 238).addBox(120.0F, -43.0F, -66.0F, 8.0F, 8.0F, 15.0F, new CubeDeformation(-3.0F))
		.texOffs(422, 164).addBox(8.0F, -60.0F, -112.0F, 29.0F, 29.0F, 19.0F, new CubeDeformation(-6.0F)),
				PartPose.offsetAndRotation(-6.0F, -2, 0.0F, (float) Math.PI, (float) Math.PI, 0.0F));

		return LayerDefinition.create(meshdefinition, 1024, 1024);
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