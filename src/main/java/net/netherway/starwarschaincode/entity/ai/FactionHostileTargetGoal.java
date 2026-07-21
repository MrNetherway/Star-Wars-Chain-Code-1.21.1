package net.netherway.starwarschaincode.entity.ai;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.netherway.starwarschaincode.faction.Faction;
import net.netherway.starwarschaincode.faction.FactionReputationHelper;

import java.util.function.Supplier;

public class FactionHostileTargetGoal<T extends Mob> extends NearestAttackableTargetGoal<Player> {
    private final Supplier<Faction> factionSupplier;

    public FactionHostileTargetGoal(T mob, Class<Player> targetClass, boolean mustSee, Supplier<Faction> factionSupplier) {
        super(mob, Player.class, mustSee);
        this.factionSupplier = factionSupplier;
    }

    @Override
    public boolean canUse() {
        if (!super.canUse()) return false;

        if (this.target instanceof ServerPlayer serverPlayer) {
            Faction faction = factionSupplier.get();
            return FactionReputationHelper.isHostile(serverPlayer, faction);
        }
        return false;
    }
}