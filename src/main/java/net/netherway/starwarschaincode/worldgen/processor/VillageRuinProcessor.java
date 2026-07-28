package net.netherway.starwarschaincode.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.netherway.starwarschaincode.registry.ModStructureProcessors;
// sua classe de loot tables, se tiver

import javax.annotation.Nullable;

public class VillageRuinProcessor extends StructureProcessor {

    public static final MapCodec<VillageRuinProcessor> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.floatRange(0f, 1f).fieldOf("destroy_chance").forGetter(p -> p.destroyChance),
                    Codec.floatRange(0f, 1f).fieldOf("fire_chance").forGetter(p -> p.fireChance)
            ).apply(instance, VillageRuinProcessor::new));

    private final float destroyChance;
    private final float fireChance;

    public VillageRuinProcessor(float destroyChance, float fireChance) {
        this.destroyChance = destroyChance;
        this.fireChance = fireChance;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(
            LevelReader level,
            BlockPos offset,
            BlockPos pos,
            StructureTemplate.StructureBlockInfo originalInfo,
            StructureTemplate.StructureBlockInfo currentInfo,
            StructurePlaceSettings settings
    ) {
        RandomSource random = settings.getRandom(currentInfo.pos());
        BlockState state = currentInfo.state();

        // Não mexe em jigsaw, ar, ou bedrock de borda
        if (state.is(Blocks.JIGSAW) || state.isAir()) {
            return currentInfo;
        }

        // Trata baús: troca loot table
        if (state.is(Blocks.CHEST) || state.is(Blocks.BARREL)) {
            if (currentInfo.nbt() != null) {
                var newNbt = currentInfo.nbt().copy();
                newNbt.putString("LootTable", "starwarschaincode:chests/village_ruined");
                newNbt.remove("LootTableSeed");
                return new StructureTemplate.StructureBlockInfo(currentInfo.pos(), state, newNbt);
            }
            return currentInfo;
        }

        if (isVillagerAttractor(state)) {
            return new StructureTemplate.StructureBlockInfo(currentInfo.pos(), Blocks.AIR.defaultBlockState(), null);
        }

        // Dano estrutural aleatório em blocos "sólidos" de construção
        if (isDamageable(state) && random.nextFloat() < destroyChance) {
            if (random.nextFloat() < fireChance) {
                return new StructureTemplate.StructureBlockInfo(currentInfo.pos(), Blocks.FIRE.defaultBlockState(), null);
            }
            // Alterna entre remover (buraco) e carbonizar
            if (random.nextBoolean()) {
                return new StructureTemplate.StructureBlockInfo(currentInfo.pos(), Blocks.AIR.defaultBlockState(), null);
            } else {
                return new StructureTemplate.StructureBlockInfo(currentInfo.pos(), Blocks.COAL_BLOCK.defaultBlockState(), null);
            }
        }

        return currentInfo;
    }

    private boolean isVillagerAttractor(BlockState state) {
        return state.is(net.minecraft.tags.BlockTags.BEDS)
                || state.is(Blocks.COMPOSTER)
                || state.is(Blocks.LECTERN)
                || state.is(Blocks.CARTOGRAPHY_TABLE)
                || state.is(Blocks.SMITHING_TABLE)
                || state.is(Blocks.FLETCHING_TABLE)
                || state.is(Blocks.LOOM)
                || state.is(Blocks.STONECUTTER)
                || state.is(Blocks.BREWING_STAND)
                || state.is(Blocks.CAULDRON)
                || state.is(Blocks.BLAST_FURNACE)
                || state.is(Blocks.SMOKER)
                || state.is(Blocks.GRINDSTONE)
                || state.is(Blocks.BARREL); // barrel é usado por pescador como job site também
    }

    private boolean isDamageable(BlockState state) {
        return state.is(Blocks.OAK_PLANKS) || state.is(Blocks.OAK_LOG)
                || state.is(Blocks.COBBLESTONE) || state.is(Blocks.OAK_STAIRS)
                || state.is(Blocks.GLASS_PANE) || state.is(Blocks.OAK_FENCE);
        // adicione os blocos usados nas suas casas conforme for testando
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return ModStructureProcessors.VILLAGE_RUIN.get();
    }
}