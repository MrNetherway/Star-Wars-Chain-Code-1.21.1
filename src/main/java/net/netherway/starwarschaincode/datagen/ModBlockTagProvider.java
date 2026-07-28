package net.netherway.starwarschaincode.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.block.ModBlockTags;
import net.netherway.starwarschaincode.block.ModBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, StarWarsChainCode.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.DOONIUM_ORE.get())
                .add(ModBlocks.BAUXITE_ORE.get())
                .add(ModBlocks.QUADANIUM_ORE.get())
                .add(ModBlocks.DOLOVITE_ORE.get())
                .add(ModBlocks.BAUXITE_DEEPSLATE_ORE.get());

        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.DOONIUM_ORE.get())
                .add(ModBlocks.BAUXITE_ORE.get())
                .add(ModBlocks.BAUXITE_DEEPSLATE_ORE.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.QUADANIUM_ORE.get())
                .add(ModBlocks.DOLOVITE_ORE.get());

        tag(ModBlockTags.ASTEROID_BLOCKS)
                .add(ModBlocks.DOONIUM_ORE.get())
                .add(ModBlocks.DOLOVITE_ORE.get())
                .add(ModBlocks.QUADANIUM_ORE.get())
                .add(ModBlocks.TIBANNA_GAS.get())
                .add(Blocks.BLACKSTONE);
    }
}
