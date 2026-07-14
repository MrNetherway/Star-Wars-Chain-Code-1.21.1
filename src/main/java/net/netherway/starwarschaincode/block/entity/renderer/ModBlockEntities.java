package net.netherway.starwarschaincode.block.entity.renderer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.block.ModBlocks;
import net.netherway.starwarschaincode.block.custom.ChargedChamberBlock;
import net.netherway.starwarschaincode.block.entity.ChargedChamberBlockEntity;
import net.netherway.starwarschaincode.block.entity.LavaRefinerBlockEntity;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, StarWarsChainCode.MOD_ID);

    public static final Supplier<BlockEntityType<LavaRefinerBlockEntity>> LAVA_REFINER_BE =
            BLOCK_ENTITIES.register("lava_refiner_be", () -> BlockEntityType.Builder.of(
                    LavaRefinerBlockEntity::new, ModBlocks.LAVA_REFINER.get()).build(null));

    public static final Supplier<BlockEntityType<ChargedChamberBlockEntity>> CHARGED_CHAMBER_BE =
            BLOCK_ENTITIES.register("charged_chamber_be", () -> BlockEntityType.Builder.of(
                    ChargedChamberBlockEntity::new, ModBlocks.CHARGED_CHAMBER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
