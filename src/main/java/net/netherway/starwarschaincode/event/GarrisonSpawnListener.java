package net.netherway.starwarschaincode.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.entity.ModEntities;
import net.netherway.starwarschaincode.entity.custom.StormtrooperCommanderEntity;
import net.netherway.starwarschaincode.entity.custom.StormtrooperEntity;
import net.netherway.starwarschaincode.worldgen.asteroid.PendingGarrisonData;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID)
public class GarrisonSpawnListener {

    private static final int GUARD_RADIUS = 16;
    private static final int CHECK_INTERVAL_TICKS = 20;


    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        long gameTime = event.getServer().overworld().getGameTime();
        if (gameTime % CHECK_INTERVAL_TICKS != 0) return;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            PendingGarrisonData data = PendingGarrisonData.get(level.getDataStorage());

            List<PendingGarrisonData.PendingGarrison> toSpawn = new ArrayList<>();
            for (PendingGarrisonData.PendingGarrison garrison : data.all()) {
                ChunkPos chunkPos = new ChunkPos(garrison.center());
                boolean fullyLoaded = level.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false) != null;
                if (fullyLoaded) {
                    toSpawn.add(garrison);
                }
            }

            for (PendingGarrisonData.PendingGarrison garrison : toSpawn) {
                spawnGarrison(level, garrison.center(), garrison.radius());
                data.remove(garrison);
            }
        }
    }

    private static void spawnGarrison(ServerLevel level, BlockPos center, int asteroidRadius) {
        BlockPos spawnPos = findSurfacePos(level, center, asteroidRadius);
        if (spawnPos == null) {
            return;
        }

        StormtrooperCommanderEntity commander = ModEntities.STORMTROOPER_COMMANDER.get().create(level);
        if (commander == null) return;

        commander.moveTo(spawnPos, 0.0F, 0.0F);
        commander.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.STRUCTURE, null);
        commander.setGuardPosition(center, GUARD_RADIUS);
        level.addFreshEntity(commander);

        int troopCount = level.getRandom().nextIntBetweenInclusive(2, 6);

        for (int i = 0; i < troopCount; i++) {
            BlockPos columnXZ = center.offset(
                    level.getRandom().nextIntBetweenInclusive(-3, 3),
                    0,
                    level.getRandom().nextIntBetweenInclusive(-3, 3)
            );

            BlockPos trooperPos = findSurfacePos(level, columnXZ, asteroidRadius);
            if (trooperPos == null) continue;

            StormtrooperEntity trooper = ModEntities.STORMTROOPER.get().create(level);
            if (trooper == null) continue;

            trooper.moveTo(trooperPos, 0.0F, 0.0F);
            trooper.finalizeSpawn(level, level.getCurrentDifficultyAt(trooperPos), MobSpawnType.STRUCTURE, null);
            level.addFreshEntity(trooper);
        }
    }

    private static BlockPos findSurfacePos(ServerLevel level, BlockPos columnXZ, int asteroidRadius) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(
                columnXZ.getX(), columnXZ.getY() + asteroidRadius + 4, columnXZ.getZ()
        );

        int minY = columnXZ.getY() - asteroidRadius - 4;

        while (mutable.getY() > minY) {
            if (level.getBlockState(mutable).isSolid()) {
                return mutable.above().immutable();
            }
            mutable.move(0, -1, 0);
        }

        return null;
    }
}