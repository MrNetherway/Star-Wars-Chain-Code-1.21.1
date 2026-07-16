package net.netherway.starwarschaincode.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.netherway.starwarschaincode.block.ModBlocks;
import net.netherway.starwarschaincode.item.ModItems;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.LAVA_REFINER.get());
        this.dropSelf(ModBlocks.CHARGED_CHAMBER.get());
        this.dropSelf(ModBlocks.BLUEPRINT_BUILDER.get());

        this.dropSelf(ModBlocks.ALUMINUM_BLOCK.get());

        add(ModBlocks.DOONIUM_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.DOONIUM_ORE.get(), ModItems.RAW_DOONIUM.get(), 2, 3));
        add(ModBlocks.BAUXITE_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.BAUXITE_ORE.get(), ModItems.RAW_BAUXITE.get(), 3, 5));
        add(ModBlocks.QUADANIUM_ORE.get(),
                block -> createOreDrop(ModBlocks.QUADANIUM_ORE.get(), ModItems.RAW_QUADANIUM.get()));
        add(ModBlocks.DOLOVITE_ORE.get(),
                block -> createOreDrop(ModBlocks.DOLOVITE_ORE.get(), ModItems.RAW_DOLOVITE.get()));
        add(ModBlocks.BAUXITE_DEEPSLATE_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.BAUXITE_DEEPSLATE_ORE.get(), ModItems.RAW_BAUXITE.get(), 3, 5));
    }

    protected LootTable.Builder createMultipleOreDrops(Block pBlock, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock, LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries()
                .stream()
                .map(entry -> (Block) entry.value())
                .toList();
    }
}
