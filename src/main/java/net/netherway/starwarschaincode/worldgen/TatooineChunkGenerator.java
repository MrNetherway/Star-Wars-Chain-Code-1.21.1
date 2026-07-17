package net.netherway.starwarschaincode.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.LevelHeightAccessor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TatooineChunkGenerator extends ChunkGenerator {

    public static final MapCodec<TatooineChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource)
            ).apply(instance, TatooineChunkGenerator::new));

    private static final int MIN_Y = -64;
    private static final int HEIGHT = 384;
    private static final int BASE_HEIGHT = 70;   // nível médio das dunas
    private static final int AMPLITUDE = 5;       // "suaves" — aumente pra dunas mais altas
    private static final double FREQUENCY = 0.02; // controla o "espaçamento" das dunas

    @Nullable
    private volatile ImprovedNoise duneNoise;

    public TatooineChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    private ImprovedNoise noise() {
        ImprovedNoise local = duneNoise;
        if (local == null) {
            synchronized (this) {
                local = duneNoise;
                if (local == null) {
                    local = new ImprovedNoise(RandomSource.create(1_469_857L)); // seed fixo, qualquer número
                    duneNoise = local;
                }
            }
        }
        return local;
    }

    private int surfaceHeight(int worldX, int worldZ) {
        double n = noise().noise(worldX * FREQUENCY, 0, worldZ * FREQUENCY);
        return BASE_HEIGHT + (int) Math.round(n * AMPLITUDE);
    }

    @Override
    public void applyCarvers(WorldGenRegion level, long seed, RandomState random, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk, GenerationStep.Carving step) {

    }

    @Override
    public void buildSurface(WorldGenRegion level, StructureManager structureManager, RandomState random, ChunkAccess chunk) {

    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion level) {

    }

    @Override
    public int getGenDepth() {
        return HEIGHT;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int startX = chunkPos.getMinBlockX();
        int startZ = chunkPos.getMinBlockZ();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = startX + x;
                int worldZ = startZ + z;
                int height = surfaceHeight(worldX, worldZ);

                BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
                for (int y = MIN_Y; y < height; y++) {
                    pos.set(worldX, y, worldZ);
                    BlockState state = (y >= height - 4) ? Blocks.SAND.defaultBlockState() : Blocks.STONE.defaultBlockState();
                    chunk.setBlockState(pos, state, false);
                }
            }
        }
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getSeaLevel() {
        return -64; // sem água/oceano por enquanto
    }

    @Override
    public int getMinY() {
        return MIN_Y;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState randomState) {
        return surfaceHeight(x, z);
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState domState) {
        int height = surfaceHeight(x, z);
        BlockState[] states = new BlockState[level.getHeight()];
        for (int i = 0; i < states.length; i++) {
            int y = level.getMinBuildHeight() + i;
            if (y >= height) {
                states[i] = Blocks.AIR.defaultBlockState();
            } else if (y >= height - 4) {
                states[i] = Blocks.SAND.defaultBlockState();
            } else {
                states[i] = Blocks.STONE.defaultBlockState();
            }
        }
        return new NoiseColumn(level.getMinBuildHeight(), states);
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState randomState, BlockPos pos) {
        info.add("Tatooine dune gen — height: " + surfaceHeight(pos.getX(), pos.getZ()));
    }
}