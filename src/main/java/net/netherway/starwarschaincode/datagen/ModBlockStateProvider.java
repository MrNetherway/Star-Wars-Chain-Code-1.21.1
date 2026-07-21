package net.netherway.starwarschaincode.datagen;

import net.minecraft.client.model.Model;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.block.ModBlocks;
import net.netherway.starwarschaincode.block.custom.LavaRefinerBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, StarWarsChainCode.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        blockWithItem(ModBlocks.PLATFORM_CONTROLLER);
        blockWithItem(ModBlocks.PLATFORM_FILLER);
        blockWithItem(ModBlocks.TIBANNA_GAS);
        blueprintBuilderBlock();
        lavaRefinerBlock();
        chargedChamberBlock();

        blockWithItem(ModBlocks.DOONIUM_ORE);
        blockWithItem(ModBlocks.BAUXITE_ORE);
        blockWithItem(ModBlocks.QUADANIUM_ORE);
        blockWithItem(ModBlocks.DOLOVITE_ORE);
        blockWithItem(ModBlocks.BAUXITE_DEEPSLATE_ORE);

        blockWithItem(ModBlocks.ALUMINUM_BLOCK);
    }

    private void blueprintBuilderBlock() {
        var model = models().cube("blueprint_builder",
                modLoc("block/blueprint_builder_bottom"),
                modLoc("block/blueprint_builder_top"),
                modLoc("block/blueprint_builder_front"),
                modLoc("block/blueprint_builder_front"),
                modLoc("block/blueprint_builder_side"),
                modLoc("block/blueprint_builder_side")
        ).texture("particle", modLoc("block/blueprint_builder_front"));
        simpleBlock(ModBlocks.BLUEPRINT_BUILDER.get(), model);
        simpleBlockItem(ModBlocks.BLUEPRINT_BUILDER.get(), model);

    }

    private void chargedChamberBlock() {
        var model = models().cube("charged_chamber",
                modLoc("block/charged_chamber_bottom"),
                modLoc("block/charged_chamber_top"),
                modLoc("block/charged_chamber_side"),
                modLoc("block/charged_chamber_side"),
                modLoc("block/charged_chamber_side"),
                modLoc("block/charged_chamber_side")
        ).texture("particle", modLoc("block/charged_chamber_side"));
        simpleBlock(ModBlocks.CHARGED_CHAMBER.get(), model);
        simpleBlockItem(ModBlocks.CHARGED_CHAMBER.get(), model);

    }

    private void lavaRefinerBlock() {
        getVariantBuilder(ModBlocks.LAVA_REFINER.get()).forAllStates(state -> {
            if (state.getValue(LavaRefinerBlock.HAS_LAVA)) {
                return new ConfiguredModel[]{new ConfiguredModel(models().cube("lava_refiner_filled",
                        modLoc("block/lava_refiner_bottom"),
                        modLoc("block/lava_refiner_top_filled"),
                        modLoc("block/lava_refiner_side_filled"),
                        modLoc("block/lava_refiner_side_filled"),
                        modLoc("block/lava_refiner_side_filled"),
                        modLoc("block/lava_refiner_side_filled")
                ).texture("particle", modLoc("block/lava_refiner_side_filled")))};
            } else {
                return new ConfiguredModel[]{new ConfiguredModel(models().cube("lava_refiner",
                        modLoc("block/lava_refiner_bottom"),
                        modLoc("block/lava_refiner_top"),
                        modLoc("block/lava_refiner_side"),
                        modLoc("block/lava_refiner_side"),
                        modLoc("block/lava_refiner_side"),
                        modLoc("block/lava_refiner_side")
                ).texture("particle", modLoc("block/lava_refiner_side")))};
            }
        });

        simpleBlockItem(ModBlocks.LAVA_REFINER.get(), models().cube("lava_refiner",
                modLoc("block/lava_refiner_bottom"),
                modLoc("block/lava_refiner_top"),
                modLoc("block/lava_refiner_side"),
                modLoc("block/lava_refiner_side"),
                modLoc("block/lava_refiner_side"),
                modLoc("block/lava_refiner_side")
        ).texture("particle", modLoc("block/lava_refiner_side")));

    }




    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
