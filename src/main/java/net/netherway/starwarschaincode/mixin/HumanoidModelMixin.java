package net.netherway.starwarschaincode.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.netherway.starwarschaincode.item.custom.LightsaberItem;
import net.netherway.starwarschaincode.item.custom.WeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> {

    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void starwarschaincode$aimWeapon(T entity, float limbSwing, float limbSwingAmount,
                                             float ageInTicks, float netHeadYaw, float headPitch,
                                             CallbackInfo ci) {

        if (!(entity instanceof Player player)) return;

        HumanoidModel<T> self = (HumanoidModel<T>) (Object) this;

        boolean mainIsRight = player.getMainArm() == HumanoidArm.RIGHT;

        ItemStack mainHandItem = player.getMainHandItem();
        ItemStack offhandItem = player.getOffhandItem();

        ModelPart rightArmModel = self.rightArm;
        ModelPart leftArmModel = self.leftArm;

        ItemStack rightHandItem = mainIsRight ? mainHandItem : offhandItem;
        ItemStack leftHandItem = mainIsRight ? offhandItem : mainHandItem;

        boolean mainHandBlocking = mainHandItem.getItem() instanceof LightsaberItem
                && mainHandItem.getOrDefault(ModDataComponents.ACTIVATED.get(), false)
                && mainHandItem.getOrDefault(ModDataComponents.BLOCKING.get(), false);

        if (rightHandItem.getItem() instanceof WeaponItem || (mainIsRight && mainHandBlocking)) {
            rightArmModel.xRot = (float) Math.toRadians(headPitch) - (float) (Math.PI / 2);
            rightArmModel.yRot = (float) Math.toRadians(netHeadYaw);
            rightArmModel.zRot = 0f;
        }

        if (leftHandItem.getItem() instanceof WeaponItem || (!mainIsRight && mainHandBlocking)) {
            leftArmModel.xRot = (float) Math.toRadians(headPitch) - (float) (Math.PI / 2);
            leftArmModel.yRot = (float) Math.toRadians(netHeadYaw);
            leftArmModel.zRot = 0f;
        }
    }
}