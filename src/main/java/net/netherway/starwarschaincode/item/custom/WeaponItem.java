package net.netherway.starwarschaincode.item.custom;

import net.minecraft.world.item.Item;

public class WeaponItem extends Item {

    private final double damage;
    private final float projectileSpeed;
    private final int fireRate;

    public WeaponItem(Properties properties,
                      double damage,
                      float projectileSpeed,
                      int fireRate) {

        super(properties);

        this.damage = damage;
        this.projectileSpeed = projectileSpeed;
        this.fireRate = fireRate;
    }

    public double getDamage() {
        return damage;
    }

    public float getProjectileSpeed() {
        return projectileSpeed;
    }

    public int getFireRate() {
        return fireRate;
    }
}