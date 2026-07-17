package net.netherway.starwarschaincode.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.netherway.starwarschaincode.StarWarsChainCode; // ajuste pro seu MODID holder
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.bus.api.IEventBus;

import java.util.function.Supplier;

public class ModChunkGenerators {

    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, StarWarsChainCode.MOD_ID);

    public static final Supplier<MapCodec<TatooineChunkGenerator>> TATOOINE =
            CHUNK_GENERATORS.register("tatooine", () -> TatooineChunkGenerator.CODEC);

    public static void register(IEventBus modEventBus) {
        CHUNK_GENERATORS.register(modEventBus);
    }
}