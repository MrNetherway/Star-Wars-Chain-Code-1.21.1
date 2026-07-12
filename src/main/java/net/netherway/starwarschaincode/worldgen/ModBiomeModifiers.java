package net.netherway.starwarschaincode.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.netherway.starwarschaincode.StarWarsChainCode;

public class ModBiomeModifiers {

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        Holder<PlacedFeature> asteroid =
                placedFeatures.getOrThrow(ModPlacedFeatures.ASTEROID);


        context.register(
                ADD_ASTEROIDS,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(asteroid),
                        GenerationStep.Decoration.LOCAL_MODIFICATIONS
                )
        );
    }

    public static final ResourceKey<BiomeModifier> ADD_ASTEROIDS =
            registerKey("add_asteroid");

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, name));
    }
}
