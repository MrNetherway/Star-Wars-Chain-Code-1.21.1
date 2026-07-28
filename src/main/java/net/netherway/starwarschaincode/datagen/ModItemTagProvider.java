package net.netherway.starwarschaincode.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.block.ModBlockTags;
import net.netherway.starwarschaincode.block.ModBlocks;
import net.netherway.starwarschaincode.item.ModItemTags;
import net.netherway.starwarschaincode.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, StarWarsChainCode.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModItemTags.HYPERDRIVE_COMPONENT)
                .add(ModItems.HYPERDRIVE_BLUEPRINT.get());

        this.tag(ModItemTags.LIFE_SUPPORT_COMPONENT)
                .add(ModItems.LIFE_SUPPORT_BLUEPRINT.get());

        this.tag(ModItemTags.REPULSORLIFT_GENERATOR_COMPONENT)
                .add(ModItems.REPULSORLIFT_GENERATOR_BLUEPRINT.get());

        this.tag(ModItemTags.NAVICOMPUTER_COMPONENT)
                .add(ModItems.NAVICOMPUTER_BLUEPRINT.get());

        this.tag(ModItemTags.MAIN_REACTOR_COMPONENT)
                .add(ModItems.MAIN_REACTOR_BLUEPRINT.get());

        this.tag(ModItemTags.TIBANNA_ENERGY_COMPONENT)
                .add(ModItems.TIBANNA_GAS_CAPSULE.get());

        this.tag(ModItemTags.ENERGY_HOLDER)
                .add(ModItems.ENERGY_CELL.get())
                .add(ModItems.PORTABLE_SOLAR_COLLECTOR.get());
    }
}
