package net.netherway.starwarschaincode.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class GuardPositionGoal extends Goal {
    private final PathfinderMob mob;
    private final double speed;
    private BlockPos guardPos;
    private int radius;

    public GuardPositionGoal(PathfinderMob mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public void setGuardPosition(BlockPos pos, int radius) {
        this.guardPos = pos;
        this.radius = radius;
    }

    @Override
    public boolean canUse() {
        if (guardPos == null) return false;
        return this.mob.getTarget() == null
                && this.mob.distanceToSqr(guardPos.getX(), guardPos.getY(), guardPos.getZ()) > (double)(radius * radius);
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.getTarget() == null
                && !this.mob.getNavigation().isDone()
                && this.mob.distanceToSqr(guardPos.getX(), guardPos.getY(), guardPos.getZ()) > (double)(radius * radius) / 4.0D;
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(guardPos.getX(), guardPos.getY(), guardPos.getZ(), speed);
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
    }
}