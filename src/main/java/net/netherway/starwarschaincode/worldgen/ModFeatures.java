package net.netherway.starwarschaincode.worldgen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.bus.api.IEventBus;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.worldgen.feature.AsteroidFeature;

import java.util.function.Supplier;

public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(BuiltInRegistries.FEATURE, StarWarsChainCode.MOD_ID);

    public static final Supplier<Feature<NoneFeatureConfiguration>> ASTEROID =
            FEATURES.register("asteroid",
                    () -> new AsteroidFeature(NoneFeatureConfiguration.CODEC));

    public static void register(IEventBus bus){
        FEATURES.register(bus);
    }
}