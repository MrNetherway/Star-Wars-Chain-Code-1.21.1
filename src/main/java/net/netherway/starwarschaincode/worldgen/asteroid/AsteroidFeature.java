package net.netherway.starwarschaincode.worldgen.asteroid;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class AsteroidFeature extends Feature<NoneFeatureConfiguration> {

    public AsteroidFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {

        BlockPos pos = context.origin();

        RandomSource random = context.random();

        int radius = random.nextIntBetweenInclusive(8, 16);

        AsteroidGenerator.generate(
                (WorldGenLevel) context.level(),
                pos,
                radius
        );

        return true;
    }
}