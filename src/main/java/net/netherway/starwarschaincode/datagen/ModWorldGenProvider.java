package net.netherway.starwarschaincode.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.worldgen.ModBiomeModifiers;
import net.netherway.starwarschaincode.worldgen.ModConfiguredFeatures;
import net.netherway.starwarschaincode.worldgen.ModPlacedFeatures;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModWorldGenProvider extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder BUILDER =
            new RegistrySetBuilder()
                    .add(net.minecraft.core.registries.Registries.CONFIGURED_FEATURE,
                            ModConfiguredFeatures::bootstrap)
                    .add(net.minecraft.core.registries.Registries.PLACED_FEATURE,
                            ModPlacedFeatures::bootstrap)
                    .add(net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.BIOME_MODIFIERS,
                            ModBiomeModifiers::bootstrap);

    public ModWorldGenProvider(PackOutput output,
                               CompletableFuture<HolderLookup.Provider> lookup) {

        super(output,
                lookup,
                BUILDER,
                Set.of(StarWarsChainCode.MOD_ID));
    }
}