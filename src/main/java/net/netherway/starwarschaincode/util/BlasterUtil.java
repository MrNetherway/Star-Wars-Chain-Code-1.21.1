package net.netherway.starwarschaincode.util;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.netherway.starwarschaincode.entity.custom.BlasterBoltEntity;
import net.netherway.starwarschaincode.item.custom.WeaponItem;

public class BlasterUtil {

    public static void shootBlasterBolt(
            LivingEntity shooter,
            Vec3 direction,
            Vec3 spawnPos
    ) {

        ItemStack weaponStack = shooter.getMainHandItem();

        if (!(weaponStack.getItem() instanceof WeaponItem weapon))
            return;

        BlasterBoltEntity bolt =
                new BlasterBoltEntity(shooter, shooter.level());

        bolt.setDamage(WeaponAttachmentUtil.getEffectiveDamage(weaponStack));
        bolt.setFireDistance(WeaponAttachmentUtil.getEffectiveFireDistance(weaponStack));

        bolt.setPos(spawnPos);

        bolt.shoot(
                direction.x,
                direction.y,
                direction.z,
                weapon.getProjectileSpeed(),
                0F
        );

        shooter.level().addFreshEntity(bolt);

        shooter.playSound(
                SoundEvents.SNOW_GOLEM_SHOOT,
                1.0F,
                1.0F
        );
    }
}