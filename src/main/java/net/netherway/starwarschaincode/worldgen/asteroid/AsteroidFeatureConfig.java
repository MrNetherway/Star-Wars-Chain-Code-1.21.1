package net.netherway.starwarschaincode.worldgen.asteroid;

import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class AsteroidFeatureConfig implements FeatureConfiguration {

    public final int radius;

    public AsteroidFeatureConfig(int radius) {
        this.radius = radius;
    }
}