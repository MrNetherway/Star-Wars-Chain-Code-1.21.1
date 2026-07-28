package net.netherway.starwarschaincode.item.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.netherway.starwarschaincode.component.WeaponAttachmentData;
import net.netherway.starwarschaincode.item.ModItems;
import net.netherway.starwarschaincode.item.custom.LightsaberGeoItem;
import net.netherway.starwarschaincode.item.custom.LightsaberItem;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class LightsaberItemRenderer extends GeoItemRenderer<LightsaberItem> {

    public LightsaberItemRenderer() {
        super(new LightsaberGeoItem());
    }

    @Override
    public RenderType getRenderType(LightsaberItem animatable,
                                    ResourceLocation texture,
                                    MultiBufferSource bufferSource,
                                    float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    private ResourceLocation getHiltTexture(String boneName) {

        if (boneName.startsWith("tragedy_1"))
            return ResourceLocation.fromNamespaceAndPath("starwarschaincode", "textures/item/tragedy_1.png");

        if (boneName.startsWith("wisdom_1"))
            return ResourceLocation.fromNamespaceAndPath("starwarschaincode", "textures/item/wisdom_1.png");

        return null;
    }

    private static final java.util.Map<ResourceLocation, RenderType> OUT_RENDER_TYPE_CACHE = new java.util.HashMap<>();
    private static final java.util.Map<ResourceLocation, RenderType> INNER_RENDER_TYPE_CACHE = new java.util.HashMap<>();

    private static RenderType getOutRenderTypeNoDepthWrite(ResourceLocation texture) {
        return OUT_RENDER_TYPE_CACHE.computeIfAbsent(texture, tex ->
                RenderType.create(
                        "starwarschaincode_blade_out",
                        com.mojang.blaze3d.vertex.DefaultVertexFormat.NEW_ENTITY,
                        com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS,
                        256,
                        true,
                        true,
                        RenderType.CompositeState.builder()
                                .setShaderState(RenderType.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                                .setTextureState(new net.minecraft.client.renderer.RenderStateShard.TextureStateShard(tex, false, false))
                                .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                                .setCullState(RenderType.NO_CULL)
                                .setLightmapState(RenderType.LIGHTMAP)
                                .setOverlayState(RenderType.OVERLAY)
                                .setWriteMaskState(RenderType.COLOR_WRITE)
                                .createCompositeState(true)
                )
        );
    }

    private static RenderType getInnerRenderTypeFullbright(ResourceLocation texture) {
        return INNER_RENDER_TYPE_CACHE.computeIfAbsent(texture, tex ->
                RenderType.create(
                        "starwarschaincode_blade_inner",
                        com.mojang.blaze3d.vertex.DefaultVertexFormat.NEW_ENTITY,
                        com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS,
                        256,
                        true,
                        true,
                        RenderType.CompositeState.builder()
                                .setShaderState(RenderType.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                                .setTextureState(new net.minecraft.client.renderer.RenderStateShard.TextureStateShard(tex, false, false))
                                .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                                .setCullState(RenderType.NO_CULL)
                                .setLightmapState(RenderType.LIGHTMAP)
                                .setOverlayState(RenderType.OVERLAY)
                                .setWriteMaskState(RenderType.COLOR_DEPTH_WRITE) // inner escreve depth normal
                                .createCompositeState(true)
                )
        );
    }

    private boolean secondPass = false;

    @Override
    public void renderRecursively(
            PoseStack poseStack,
            LightsaberItem animatable,
            GeoBone bone,
            RenderType renderType,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            boolean isReRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            int colour
    ) {

        // ----- Ponto de entrada: bone raiz "saber" -----
        // Faz 2 passadas completas: primeiro tudo (sem "inner"), depois só "inner".
        if (bone.getName().equals("saber") && !secondPass) {

            GeoBone bladeBone = findBoneRecursive(bone, "blade");

            if (bladeBone != null) {
                boolean activated = currentItemStack.getOrDefault(
                        ModDataComponents.ACTIVATED.get(),
                        false
                );

                bladeBone.setHidden(!activated);
            }

            GeoBone innerBone = findBoneRecursive(bone, "inner");
            GeoBone outBone = findBoneRecursive(bone, "out");
            GeoBone hiltBone = findBoneRecursive(bone, "hilt");

            // PASS 1: tudo, com "inner" escondido
            if (innerBone != null) innerBone.setHidden(true);

            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer,
                    isReRender, partialTick, packedLight, packedOverlay, colour);

            if (bufferSource instanceof MultiBufferSource.BufferSource actualBufferSource) {
                actualBufferSource.endBatch(renderType);
            }

            // PASS 2: só o "inner" — pega um buffer NOVO, o antigo já foi drenado pelo endBatch
            if (innerBone != null) innerBone.setHidden(false);
            if (outBone != null) outBone.setHidden(true);
            if (hiltBone != null) hiltBone.setHidden(true);

            VertexConsumer freshBuffer = bufferSource.getBuffer(renderType);

            secondPass = true;
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, freshBuffer,
                    isReRender, partialTick, packedLight, packedOverlay, colour);
            secondPass = false;

            if (outBone != null) outBone.setHidden(false);
            if (hiltBone != null) hiltBone.setHidden(false);

            return;
        }

        if (bone.getName().equals("hilt_part_1")) {
            WeaponAttachmentData data = currentItemStack.get(ModDataComponents.HILT_PART_1.get());
            String selected = data != null ? data.itemId().getPath() : "";
            for (GeoBone child : bone.getChildBones()) {
                child.setHidden(!child.getName().equals(selected));
            }
        }

        if (bone.getName().equals("hilt_part_2")) {
            WeaponAttachmentData data = currentItemStack.get(ModDataComponents.HILT_PART_2.get());
            String selected = data != null ? data.itemId().getPath() : "";
            for (GeoBone child : bone.getChildBones()) {
                child.setHidden(!child.getName().equals(selected));
            }
        }

        if (bone.getName().equals("hilt_part_3")) {
            WeaponAttachmentData data = currentItemStack.get(ModDataComponents.HILT_PART_3.get());
            String selected = data != null ? data.itemId().getPath() : "";
            for (GeoBone child : bone.getChildBones()) {
                child.setHidden(!child.getName().equals(selected));
            }
        }

        if (bone.getName().equals("out")) {
            WeaponAttachmentData data = currentItemStack.get(ModDataComponents.KYBER_CRYSTAL.get());
            if (data != null && data.itemId().equals(ModItems.BLUE_KYBER_CRYSTAL.getId())) {
                colour = 0xFF0000FF;
            }else if(data != null && data.itemId().equals(ModItems.GREEN_KYBER_CRYSTAL.getId())){
                colour = 0xFF00FF55;
            }else if(data != null && data.itemId().equals(ModItems.RED_KYBER_CRYSTAL.getId())){
                colour = 0xFFFF3030;
            }else if(data != null && data.itemId().equals(ModItems.PURPLE_KYBER_CRYSTAL.getId())){
                colour = 0xFF9B30FF;
            }else if(data != null && data.itemId().equals(ModItems.CYAN_KYBER_CRYSTAL.getId())){
                colour = 0xFF40F8FF;
            }else if(data != null && data.itemId().equals(ModItems.ORANGE_KYBER_CRYSTAL.getId())){
                colour = 0xFFFF8C1A;
            }else if(data != null && data.itemId().equals(ModItems.YELLOW_KYBER_CRYSTAL.getId())){
                colour = 0xFFFFE000;
            }else if(data != null && data.itemId().equals(ModItems.WHITE_KYBER_CRYSTAL.getId())){
                colour = 0xFFFFFFFF;
            }else if(data != null && data.itemId().equals(ModItems.MAGENTA_KYBER_CRYSTAL.getId())){
                colour = 0xFFFF40D0;
            }

            ResourceLocation texture = getTextureLocation(animatable);
            RenderType outRenderType = getOutRenderTypeNoDepthWrite(texture);
            VertexConsumer outBuffer = bufferSource.getBuffer(outRenderType);

            super.renderRecursively(poseStack, animatable, bone, outRenderType, bufferSource, outBuffer,
                    isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, colour);
            return;
        }

        if (bone.getName().equals("inner")) {
            ResourceLocation texture = getTextureLocation(animatable);
            RenderType innerRenderType = getInnerRenderTypeFullbright(texture);
            VertexConsumer innerBuffer = bufferSource.getBuffer(innerRenderType);

            super.renderRecursively(poseStack, animatable, bone, innerRenderType, bufferSource, innerBuffer,
                    isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, 0xFFFFFFFF);
            return;
        }

        if (bone.getName().equals("blade")) {
            packedLight = LightTexture.FULL_BRIGHT;
        }

        ResourceLocation texture = getHiltTexture(bone.getName());

        if (texture != null) {
            RenderType customRender = RenderType.entityTranslucent(texture);
            VertexConsumer customBuffer = bufferSource.getBuffer(customRender);

            super.renderRecursively(poseStack, animatable, bone, customRender, bufferSource, customBuffer,
                    isReRender, partialTick, packedLight, packedOverlay, colour);
            return;
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    private GeoBone findBoneRecursive(GeoBone parent, String name) {
        for (GeoBone child : parent.getChildBones()) {
            if (child.getName().equals(name)) return child;
            GeoBone found = findBoneRecursive(child, name);
            if (found != null) return found;
        }
        return null;
    }
}