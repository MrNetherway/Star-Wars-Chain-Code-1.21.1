package net.netherway.starwarschaincode.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;

import java.util.EnumSet;

public class RangedBlasterAttackGoal<T extends PathfinderMob & RangedAttackMob> extends Goal {
    private final T mob;
    private final double moveSpeed;
    private final int attackIntervalTicks;
    private final float attackRadius;
    private int attackTime = -1;
    private int seeTime;
    private int strafeTicks = 0;
    private float strafeDirection = 1.0F;

    public RangedBlasterAttackGoal(T mob, double moveSpeed, int attackIntervalTicks, float attackRadius) {
        this.mob = mob;
        this.moveSpeed = moveSpeed;
        this.attackIntervalTicks = attackIntervalTicks;
        this.attackRadius = attackRadius;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public void start() {
        this.attackTime = this.mob.getRandom().nextIntBetweenInclusive(10, 30);
    }

    @Override
    public boolean canUse() {
        return this.mob.getTarget() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse() || !this.mob.getNavigation().isDone();
    }

    @Override
    public void stop() {
        this.mob.setAggressive(false);
        this.seeTime = 0;
        this.attackTime = -1;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) return;

        double dx = target.getX() - this.mob.getX();
        double dz = target.getZ() - this.mob.getZ();
        double distSqr = dx * dx + dz * dz;
        boolean canSee = this.mob.getSensing().hasLineOfSight(target);

        if (canSee) this.seeTime++; else this.seeTime = 0;

        boolean inRange = distSqr <= (double)(attackRadius * attackRadius);

        this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (!inRange || seeTime < 5) {
            this.mob.getNavigation().moveTo(target, moveSpeed);
        } else {
            this.mob.getNavigation().stop();

            // força o corpo a encarar o alvo, senão o strafe sai em linha reta
            float yaw = (float) (Mth.atan2(dz, dx) * (180F / Math.PI)) - 90F;
            this.mob.setYRot(yaw);
            this.mob.yBodyRot = yaw;
            this.mob.yHeadRot = yaw;

            this.strafeTicks++;
            if (this.strafeTicks % 20 == 0) {
                this.strafeDirection = this.mob.getRandom().nextBoolean() ? 1.0F : -1.0F;
            }
            this.mob.getMoveControl().strafe(0.0F, this.strafeDirection * 0.5F);
        }

        if (--this.attackTime <= 0) {
            if (inRange && canSee) {
                float distanceFactor = (float) Math.sqrt(distSqr) / attackRadius;
                this.mob.performRangedAttack(target, distanceFactor);
                this.attackTime = this.attackIntervalTicks;
            }
        }

        this.mob.setAggressive(inRange);
    }
}