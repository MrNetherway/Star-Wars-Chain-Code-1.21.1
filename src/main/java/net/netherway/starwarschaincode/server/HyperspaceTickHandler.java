package net.netherway.starwarschaincode.server;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.component.ModAttachments;
import net.netherway.starwarschaincode.entity.custom.ShipEntity;
import net.netherway.starwarschaincode.attachment.HyperspaceTravelData;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID)
public class HyperspaceTickHandler {

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            HyperspaceTravelData data = player.getData(ModAttachments.HYPERSPACE_TRAVEL);
            if (!data.traveling) continue;

            long elapsed = player.level().getGameTime() - data.startGameTime;
            if (elapsed < data.durationTicks) continue;

            resolveTravel(player, data);
        }
    }

    private static void resolveTravel(ServerPlayer player, HyperspaceTravelData data) {
        ServerLevel targetLevel = player.server.getLevel(data.targetDimension);
        if (targetLevel == null) {
            player.setData(ModAttachments.HYPERSPACE_TRAVEL, HyperspaceTravelData.IDLE);
            return;
        }

        Entity vehicle = player.getVehicle();
        ShipEntity ship = vehicle instanceof ShipEntity s ? s : null;

        Vec3 destination = new Vec3(data.destX, data.destY, data.destZ);

        player.setData(ModAttachments.HYPERSPACE_TRAVEL, HyperspaceTravelData.IDLE);

        if (ship != null) {
            player.stopRiding();

            DimensionTransition shipTransition = new DimensionTransition(
                    targetLevel, destination, Vec3.ZERO, data.destYaw, 0F,
                    DimensionTransition.DO_NOTHING
            );
            Entity teleportedShip = ship.changeDimension(shipTransition);

            DimensionTransition playerTransition = new DimensionTransition(
                    targetLevel, destination, Vec3.ZERO, data.destYaw, 0F,
                    DimensionTransition.DO_NOTHING
            );
            Entity teleportedPlayer = player.changeDimension(playerTransition);

            if (teleportedShip instanceof ShipEntity newShip && teleportedPlayer instanceof ServerPlayer newPlayer) {
                newPlayer.startRiding(newShip, true);
            }
        } else {
            // player não está mais na nave por algum motivo (desync/queda) - teleporta só ele
            DimensionTransition playerTransition = new DimensionTransition(
                    targetLevel, destination, Vec3.ZERO, data.destYaw, 0F,
                    DimensionTransition.DO_NOTHING
            );
            player.changeDimension(playerTransition);
        }
    }
}