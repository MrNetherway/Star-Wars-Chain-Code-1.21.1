package net.netherway.starwarschaincode.client;

import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import com.zigythebird.playeranimcore.math.ModMatrix4f;
import com.zigythebird.playeranimcore.math.Vec3f;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.netherway.starwarschaincode.item.custom.LightsaberItem;
import net.netherway.starwarschaincode.item.custom.WeaponItem;
import org.jetbrains.annotations.NotNull;

public class WeaponAimModifier extends AbstractModifier {

    private final AbstractClientPlayer player;
    private final boolean isLeft;

    public WeaponAimModifier(AbstractClientPlayer player, boolean isLeft) {
        this.player = player;
        this.isLeft = isLeft;
    }

    @Override
    public PlayerAnimBone get3DTransform(@NotNull PlayerAnimBone bone) {
        bone = super.get3DTransform(bone);

        String expectedBoneName = isLeft ? "left_arm" : "right_arm";
        if (!bone.getName().equals(expectedBoneName))
            return bone;

        boolean mainIsRight = player.getMainArm() == HumanoidArm.RIGHT;
        ItemStack relevant = isLeft
                ? (mainIsRight ? player.getOffhandItem() : player.getMainHandItem())
                : (mainIsRight ? player.getMainHandItem() : player.getOffhandItem());

        boolean isWeaponAim = relevant.getItem() instanceof WeaponItem;

        boolean isSaberBlocking = relevant.getItem() instanceof LightsaberItem
                && relevant.getOrDefault(ModDataComponents.ACTIVATED.get(), false)
                && relevant.getOrDefault(ModDataComponents.BLOCKING.get(), false);

        if (!isWeaponAim && !isSaberBlocking)
            return bone;

        float pitch = (float) Math.toRadians(player.getXRot()) - (float) (Math.PI / 2);
        float yaw = (float) Math.toRadians(player.yHeadRot - player.yBodyRot);

        // Monta a rotação de mira (mundo) POR FORA da rotação que a animação
        // já colocou no bone, em vez de somar Euler direto (que quebra quando
        // a animação já girou o braço bastante nos próprios eixos).
        ModMatrix4f matrix = new ModMatrix4f();
        matrix.rotateY(yaw);           // aim yaw — aplicado por último (mundo)
        matrix.rotateX(pitch);          // aim pitch — aplicado por último (mundo)
        matrix.rotateZ(bone.rotZ);      // rotação local da animação (ordem vanilla Z,Y,X)
        matrix.rotateY(bone.rotY);
        matrix.rotateX(bone.rotX);

        Vec3f euler = matrix.getEulerRotation();
        bone.updateRotation(euler.x(), euler.y(), euler.z());

        return bone;
    }

    @Override
    public boolean canRemove() {
        return false;
    }
}