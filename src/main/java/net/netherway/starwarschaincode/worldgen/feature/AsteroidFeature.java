package net.netherway.starwarschaincode.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.netherway.starwarschaincode.worldgen.asteroid.AsteroidGenerator;

public class AsteroidFeature extends Feature<NoneFeatureConfiguration> {

    public AsteroidFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {

        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();

        int radius = level.getRandom().nextIntBetweenInclusive(4, 8);

        AsteroidGenerator.generate((WorldGenLevel) level, pos, radius, random);

        return true;
    }
}