package net.netherway.starwarschaincode.faction;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.netherway.starwarschaincode.StarWarsChainCode;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID)
public class FactionDeathListener {

    private static final int REPUTATION_LOSS_ON_KILL = 3;

    @SubscribeEvent
    public static void onFactionMemberDeath(LivingDeathEvent event) {
        LivingEntity dead = event.getEntity();

        if (!(dead instanceof FactionMember member)) return;

        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof ServerPlayer killer)) return;

        FactionReputationHelper.decreaseFactionReputation(killer, member.getFaction(), REPUTATION_LOSS_ON_KILL);
    }
}