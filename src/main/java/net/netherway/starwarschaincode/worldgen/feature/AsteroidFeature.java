package net.netherway.starwarschaincode.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.netherway.starwarschaincode.entity.ModEntities;
import net.netherway.starwarschaincode.entity.custom.StormtrooperCommanderEntity;
import net.netherway.starwarschaincode.entity.custom.StormtrooperEntity;
import net.netherway.starwarschaincode.worldgen.asteroid.AsteroidGenerator;
import net.netherway.starwarschaincode.worldgen.asteroid.PendingGarrisonData;

public class AsteroidFeature extends Feature<NoneFeatureConfiguration> {

    private static final float COMMANDER_SPAWN_CHANCE = 100F;
    private static final int GUARD_RADIUS = 16;

    public AsteroidFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {

        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();

        int radius = level.getRandom().nextIntBetweenInclusive(4, 8);

        if (intersectsWater(level, pos, radius + 6)) {
            return false;
        }

        AsteroidGenerator.generate(level, pos, radius, random);

        if (random.nextFloat() < COMMANDER_SPAWN_CHANCE) {
            spawnGarrison(level, pos, radius);
        }

        return true;
    }

    private boolean intersectsWater(WorldGenLevel level, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x += 2) {
            for (int y = -radius; y <= radius; y += 2) {
                for (int z = -radius; z <= radius; z += 2) {
                    if (level.getFluidState(center.offset(x, y, z)).is(net.minecraft.tags.FluidTags.WATER)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void spawnGarrison(WorldGenLevel level, BlockPos center, int asteroidRadius) {
        PendingGarrisonData.get(level.getLevel().getDataStorage()).markPending(center, asteroidRadius);
    }
}