package net.netherway.starwarschaincode.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.block.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, StarWarsChainCode.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        blockWithItem(ModBlocks.LAVA_REFINER);
        blockWithItem(ModBlocks.CHARGED_CHAMBER);
        blockWithItem(ModBlocks.PLATFORM_CONTROLLER);
        blockWithItem(ModBlocks.PLATFORM_FILLER);
        blueprintBuilderBlock();

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
                modLoc("block/blueprint_builder_side"),
                modLoc("block/blueprint_builder_side"),
                modLoc("block/blueprint_builder_side")
        ).texture("particle", modLoc("block/blueprint_builder_front"));
        simpleBlock(ModBlocks.BLUEPRINT_BUILDER.get(), model);
        simpleBlockItem(ModBlocks.BLUEPRINT_BUILDER.get(), model);

    }


    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
