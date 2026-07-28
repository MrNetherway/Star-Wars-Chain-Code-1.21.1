package net.netherway.starwarschaincode.item.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.netherway.starwarschaincode.client.ScopeClientState;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.netherway.starwarschaincode.component.WeaponAttachmentData;
import net.netherway.starwarschaincode.item.AttachmentItem;
import net.netherway.starwarschaincode.item.custom.WeaponGeoModel;
import net.netherway.starwarschaincode.item.custom.WeaponItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.RenderUtil;

public class WeaponItemRenderer extends GeoItemRenderer<WeaponItem> {

    private static Vec3 muzzlePosition = Vec3.ZERO;

    public static Vec3 getMuzzlePosition() {
        return muzzlePosition;
    }

    public WeaponItemRenderer() {
        super(new WeaponGeoModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        // Some scoped: some a mão principal (é a única que ativa scope) não renderiza o modelo.
        if (displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND && ScopeClientState.isScoped()) {
            return;
        }

        super.renderByItem(stack, displayContext, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public void preRender(PoseStack poseStack, WeaponItem animatable, BakedGeoModel model,
                          @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick,
                packedLight, packedOverlay, colour);

        if (this.renderPerspective == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || this.renderPerspective == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            poseStack.scale(-1, 1, 1);
        }
    }

    @Override
    public void renderRecursively(PoseStack poseStack, WeaponItem animatable, GeoBone bone,
                                  RenderType renderType, MultiBufferSource bufferSource,
                                  VertexConsumer buffer, boolean isReRender, float partialTick,
                                  int packedLight, int packedOverlay, int colour) {

        boolean isFirstPerson = this.renderPerspective == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || this.renderPerspective == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;

        if (bone.getName().equals("arm")) {
            if (!isFirstPerson) {
                bone.setHidden(true);
            } else {
                bone.setHidden(false);

                AbstractClientPlayer player = Minecraft.getInstance().player;
                if (player != null) {
                    ResourceLocation skin = player.getSkin().texture();
                    RenderType skinRenderType = RenderType.entityTranslucent(skin);
                    VertexConsumer skinBuffer = bufferSource.getBuffer(skinRenderType);

                    super.renderRecursively(poseStack, animatable, bone, skinRenderType, bufferSource,
                            skinBuffer, isReRender, partialTick, packedLight, packedOverlay, colour);
                    return;
                }
            }
        }

        if (bone.getName().equals("pente")) {
            boolean hasAmmo = this.currentItemStack != null
                    && this.currentItemStack.has(ModDataComponents.TIBANNA_AMOUNT.get());
            bone.setHidden(!hasAmmo);
        }

        if (bone.getName().equals("scope_attachment")) {
            WeaponAttachmentData scopeData = currentItemStack.get(ModDataComponents.SCOPE_ITEM.get());

            String selected = "";

            if (scopeData != null) {
                selected = scopeData.itemId().getPath();
            }

            for (GeoBone child : bone.getChildBones()) {
                child.setHidden(!child.getName().equals(selected));
            }
        }

        if (bone.getName().equals("stock_attachment")) {
            WeaponAttachmentData stockData = currentItemStack.get(ModDataComponents.STOCK_ITEM.get());

            String selected = "";

            if (stockData != null) {
                selected = stockData.itemId().getPath();
            }

            for (GeoBone child : bone.getChildBones()) {
                child.setHidden(!child.getName().equals(selected));
            }
        }

        if (bone.getName().equals("barrel_attachment")) {
            WeaponAttachmentData barrelData = currentItemStack.get(ModDataComponents.BARREL_ITEM.get());

            String selected = "";

            if (barrelData != null) {
                selected = barrelData.itemId().getPath();
            }

            for (GeoBone child : bone.getChildBones()) {
                child.setHidden(!child.getName().equals(selected));
            }
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}