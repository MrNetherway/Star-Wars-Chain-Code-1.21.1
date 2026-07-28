package net.netherway.starwarschaincode.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.worldgen.processor.VillageRuinProcessor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModStructureProcessors {
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSOR_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, StarWarsChainCode.MOD_ID);

    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<VillageRuinProcessor>> VILLAGE_RUIN =
            PROCESSOR_TYPES.register(
                    "village_ruin",
                    () -> () -> VillageRuinProcessor.CODEC
            );
}