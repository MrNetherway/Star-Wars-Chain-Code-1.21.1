package net.netherway.starwarschaincode.entity.custom;

import net.minecraft.core.Direction;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.netherway.starwarschaincode.entity.ModEntities;
import net.netherway.starwarschaincode.faction.FactionMember;
import net.netherway.starwarschaincode.item.ModItems;

public class BlasterBoltEntity extends AbstractArrow {

    private float damage = 6.0f;
    private float fireDistance = 200;

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setFireDistance(float distance) {
        this.fireDistance = distance;
    }

    public BlasterBoltEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public BlasterBoltEntity(LivingEntity shooter, Level level) {
        super(ModEntities.BLASTER_BOLT.get(), shooter, level, new ItemStack(ModItems.DL_44.get()), null);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);

        if (this.isInWater() || this.level().getFluidState(this.blockPosition()).is(net.minecraft.tags.FluidTags.WATER)) {
            if (!this.level().isClientSide()) {
                ((net.minecraft.server.level.ServerLevel) this.level()).sendParticles(
                        net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE,
                        this.getX(), this.getY(), this.getZ(),
                        8, 0.1, 0.1, 0.1, 0.02
                );

                this.level().playSound(null, this.blockPosition(),
                        net.minecraft.sounds.SoundEvents.FIRE_EXTINGUISH,
                        net.minecraft.sounds.SoundSource.NEUTRAL, 0.7F, 1.0F);

                this.discard();
            }
            return;
        }

        if (this.tickCount > fireDistance) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {


        Entity entity = result.getEntity();
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), damage);

        if (!this.level().isClientSide()) {
            this.discard();
        }
    }

    @Override
    protected boolean tryPickup(net.minecraft.world.entity.player.Player player) {
        return false; // sem pickup, não deveria nem tentar "grudar"
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (!super.canHitEntity(entity))
            return false;

        Entity owner = this.getOwner();

        if (owner instanceof FactionMember shooter &&
                entity instanceof FactionMember target &&
                shooter.getFaction() == target.getFaction()) {
            return false;
        }

        return true;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        if (!this.level().isClientSide()) {
            this.discard();
        }
    }
}