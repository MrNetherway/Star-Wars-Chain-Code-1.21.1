package net.netherway.starwarschaincode.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.netherway.starwarschaincode.faction.Faction;
import net.netherway.starwarschaincode.faction.FactionMember;

import java.util.function.Supplier;

public class FactionHurtByTargetGoal extends HurtByTargetGoal {
    private final Supplier<Faction> factionSupplier;

    public FactionHurtByTargetGoal(Mob mob, Supplier<Faction> factionSupplier) {
        super((PathfinderMob) mob);
        this.factionSupplier = factionSupplier;
    }

    @Override
    public boolean canUse() {
        if (!super.canUse()) return false;

        LivingEntity attacker = this.mob.getLastHurtByMob();
        if (attacker instanceof FactionMember member && member.getFaction() == factionSupplier.get()) {
            return false;
        }
        return true;
    }
}