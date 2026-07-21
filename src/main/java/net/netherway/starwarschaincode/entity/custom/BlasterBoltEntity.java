package net.netherway.starwarschaincode.entity.custom;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.netherway.starwarschaincode.entity.ModEntities;
import net.netherway.starwarschaincode.faction.FactionMember;

public class BlasterBoltEntity extends Projectile {

    private double damage = 6.0D;

    public BlasterBoltEntity(EntityType<? extends BlasterBoltEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BlasterBoltEntity(Level level, LivingEntity shooter) {
        this(ModEntities.BLASTER_BOLT.get(), level);

        this.setOwner(shooter);

        this.setPos(
                shooter.getX(),
                shooter.getEyeY(),
                shooter.getZ()
        );
    }

    public void shoot(LivingEntity shooter, float speed) {
        this.setOwner(shooter);

        Vec3 direction = shooter.getLookAngle();

        this.setDeltaMovement(direction.scale(speed));
    }

    public void shootFromRotation(LivingEntity shooter, double dx, double dy, double dz, float speed, float inaccuracy) {
        this.setOwner(shooter);

        Vec3 direction = new Vec3(dx, dy, dz).normalize();

        this.setPos(
                shooter.getX(),
                shooter.getEyeY() - 0.1D,
                shooter.getZ()
        );

        this.setDeltaMovement(direction.scale(speed));
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (!super.canHitEntity(target)) return false;

        Entity owner = this.getOwner();
        if (owner instanceof FactionMember ownerFaction && target instanceof FactionMember targetFaction) {
            return ownerFaction.getFaction() != targetFaction.getFaction();
        }
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 movement = this.getDeltaMovement();

        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(
                this,
                this::canHitEntity
        );

        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onHit(hitResult);
        }

        this.move(MoverType.SELF, movement);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        Entity target = result.getEntity();
        Entity owner = this.getOwner();

        if (target instanceof LivingEntity livingTarget && owner instanceof LivingEntity livingOwner) {
            livingTarget.hurt(
                    this.damageSources().mobAttack(livingOwner),
                    (float) this.damage
            );
        }

        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        this.discard();
    }
}