package net.netherway.starwarschaincode.entity.ai;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.netherway.starwarschaincode.entity.custom.StormtrooperCommanderEntity;

import java.util.EnumSet;
import java.util.List;

public class FollowCommanderGoal extends Goal {
    private final PathfinderMob mob;
    private final double speed;
    private final float stopDistance;
    private final float searchRadius;
    private StormtrooperCommanderEntity commander;
    private int recheckCooldown;

    public FollowCommanderGoal(PathfinderMob mob, double speed, float stopDistance, float searchRadius) {
        this.mob = mob;
        this.speed = speed;
        this.stopDistance = stopDistance;
        this.searchRadius = searchRadius;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (--this.recheckCooldown > 0) {
            return commander != null && commander.isAlive();
        }
        this.recheckCooldown = 100;

        List<StormtrooperCommanderEntity> nearby = this.mob.level().getEntitiesOfClass(
                StormtrooperCommanderEntity.class,
                this.mob.getBoundingBox().inflate(searchRadius)
        );

        if (nearby.isEmpty()) {
            this.commander = null;
            return false;
        }

        nearby.sort((a, b) -> Double.compare(this.mob.distanceToSqr(a), this.mob.distanceToSqr(b)));
        this.commander = nearby.get(0);
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return commander != null && commander.isAlive()
                && this.mob.distanceToSqr(commander) > (double)(stopDistance * stopDistance);
    }

    @Override
    public void tick() {
        if (commander != null) {
            this.mob.getNavigation().moveTo(commander, speed);
        }
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        this.commander = null;
        this.recheckCooldown = 0;
    }
}