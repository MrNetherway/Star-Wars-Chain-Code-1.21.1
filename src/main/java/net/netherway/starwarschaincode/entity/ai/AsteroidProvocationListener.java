package net.netherway.starwarschaincode.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.block.ModBlockTags;
import net.netherway.starwarschaincode.faction.Faction;
import net.netherway.starwarschaincode.faction.FactionMember;

import java.util.List;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID)
public class AsteroidProvocationListener {

    private static final double ALERT_RADIUS = 24.0D;

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (player.isCreative() || player.isSpectator()) return;
        if (!event.getState().is(ModBlockTags.ASTEROID_BLOCKS)) return;

        alertNearbyImperials(player.level(), event.getPos(), player);
    }

    @SubscribeEvent
    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.isCreative() || player.isSpectator()) return;
        if (event.getLevel().isClientSide) return;
        if (!event.getLevel().getBlockState(event.getPos()).is(ModBlockTags.ASTEROID_BLOCKS)) return;

        alertNearbyImperials(player.level(), event.getPos(), player);
    }

    private static void alertNearbyImperials(Level level, BlockPos pos, ServerPlayer player) {
        List<Mob> nearby = level.getEntitiesOfClass(
                Mob.class,
                net.minecraft.world.phys.AABB.ofSize(pos.getCenter(), ALERT_RADIUS * 2, ALERT_RADIUS * 2, ALERT_RADIUS * 2),
                mob -> mob instanceof FactionMember member && member.getFaction() == Faction.EMPIRE
        );

        for (Mob mob : nearby) {
            mob.setTarget(player);
        }
    }
}