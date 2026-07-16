package net.netherway.starwarschaincode.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.block.ModBlocks;
import net.netherway.starwarschaincode.item.ModItems;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.DOONIUM_BAR, 1)
                .requires(ModItems.BASE_DOONIUM, 1)
                .requires(Items.COPPER_INGOT, 2)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT)).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.QUADANIUM_BAR, 1)
                .requires(ModItems.BASE_QUADANIUM, 1)
                .requires(Items.IRON_INGOT, 1)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WIRE, 8)
                .pattern("CCC")
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.IRON_ROD, 4)
                .pattern("I")
                .pattern("I")
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLUEPRINT_READER, 1)
                .pattern("RSR")
                .pattern("IBI")
                .pattern("III")
                .define('I', Items.IRON_INGOT)
                .define('B', ModItems.READER)
                .define('R', Items.REDSTONE)
                .define('S', ModItems.SCREEN)
                .unlockedBy("has_redstone", has(Items.REDSTONE)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SCREEN, 1)
                .pattern("GGG")
                .pattern("GGG")
                .pattern("III")
                .define('I', Items.IRON_INGOT)
                .define('G', Blocks.GLASS)
                .unlockedBy("has_glass", has(Blocks.GLASS)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.READER, 1)
                .pattern("WRR")
                .pattern("WCR")
                .pattern("WWR")
                .define('W', ModItems.WIRE)
                .define('R', Items.REDSTONE)
                .define('C', Blocks.COPPER_BLOCK)
                .unlockedBy("has_redstone", has(Items.REDSTONE)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PEN, 1)
                .pattern("S")
                .pattern("I")
                .define('I', Items.IRON_INGOT)
                .define('S', Items.INK_SAC)
                .unlockedBy("has_ink_sack", has(Items.INK_SAC)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ALUMINUM_PLATE, 4)
                .pattern("AAA")
                .define('A', ModItems.ALUMINUM_BAR)
                .unlockedBy("has_aluminum_bar", has(ModItems.ALUMINUM_BAR)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COPPER_PLATE, 4)
                .pattern("AAA")
                .define('A', Items.COPPER_INGOT)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DOONIUM_PLATE, 4)
                .pattern("AAA")
                .define('A', ModItems.DOONIUM_BAR)
                .unlockedBy("has_doonium_bar", has(ModItems.DOONIUM_BAR)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.IRON_PLATE, 4)
                .pattern("AAA")
                .define('A', Items.IRON_INGOT)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.POLISHED_DOLOVITE_PLATE, 4)
                .pattern("AAA")
                .define('A', ModItems.POLISHED_DOLOVITE)
                .unlockedBy("has_polished_dolovite", has(ModItems.POLISHED_DOLOVITE)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DOONIUM_WING, 1)
                .pattern("DC")
                .define('D', ModItems.DOONIUM_PLATE)
                .define('C', ModItems.COPPER_PLATE)
                .unlockedBy("has_doonium_plate", has(ModItems.DOONIUM_PLATE)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ENERGY_CELL, 1)
                .pattern("RBR")
                .pattern("WWW")
                .define('R', Items.REDSTONE)
                .define('W', ModItems.WIRE)
                .define('B', Blocks.REDSTONE_BLOCK)
                .unlockedBy("has_redstone", has(Items.REDSTONE)).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PORTABLE_SOLAR_COLLECTOR, 1)
                .requires(ModItems.WIRE, 1)
                .requires(Items.IRON_INGOT, 1)
                .requires(ModItems.SOLAR_COLLECTOR_PANEL, 1)
                .requires(ModItems.SOLAR_CELL, 1)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SOLAR_CELL, 1)
                .pattern("BBB")
                .pattern("RIR")
                .pattern("III")
                .define('R', Items.REDSTONE)
                .define('I', Items.IRON_INGOT)
                .define('B', ModItems.ALUMINA)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.QUADANIUM_COATING, 4)
                .requires(ModItems.QUADANIUM_BAR, 1)
                .unlockedBy("has_quadanium_bar", has(ModItems.QUADANIUM_BAR)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SOLAR_COLLECTOR_PANEL, 1)
                .pattern(" Q ")
                .pattern("QPQ")
                .pattern(" Q ")
                .define('P', Blocks.GLASS_PANE)
                .define('Q', ModItems.QUADANIUM_COATING)
                .unlockedBy("has_quadanium_coating", has(ModItems.QUADANIUM_COATING)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLUEPRINT_BUILDER, 1)
                .pattern("WWW")
                .pattern("WAW")
                .pattern("WWW")
                .define('A', ModBlocks.ALUMINUM_BLOCK)
                .define('W', ItemTags.PLANKS)
                .unlockedBy("has_planks", has(ItemTags.PLANKS)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.LAVA_REFINER, 1)
                .pattern("IRI")
                .pattern("RBR")
                .pattern("III")
                .define('I', Items.IRON_INGOT)
                .define('R', ModItems.IRON_ROD)
                .define('B', Items.BUCKET)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CHARGED_CHAMBER, 1)
                .pattern("CCC")
                .pattern("AEA")
                .pattern("AAA")
                .define('C', Items.COPPER_INGOT)
                .define('A', ModItems.ALUMINUM_BAR)
                .define('E', ModItems.ENERGY_CELL)
                .unlockedBy("has_energy_cell", has(ModItems.ENERGY_CELL)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ALUMINUM_BLOCK, 1)
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModItems.ALUMINUM_BAR)
                .unlockedBy("has_aluminum_bar", has(ModItems.ALUMINUM_BAR)).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ALUMINUM_BAR, 9)
                .requires(ModBlocks.ALUMINUM_BLOCK, 1)
                .unlockedBy("has_aluminum_block", has(ModBlocks.ALUMINUM_BLOCK)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.DL_44, 1)
                .pattern("QQQ")
                .pattern("WT ")
                .pattern("W  ")
                .define('Q', ModItems.QUADANIUM_BAR)
                .define('W', ItemTags.PLANKS)
                .define('T', ModItems.TIBANNA_GAS_CAPSULE)
                .unlockedBy("has_quadanium_bar", has(ModItems.QUADANIUM_BAR)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TIBANNA_GAS_CAPSULE, 1)
                .pattern(" C ")
                .pattern("IEI")
                .pattern(" C ")
                .define('I', Items.IRON_INGOT)
                .define('E', ModItems.ENERGY_CELL)
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_energy_cell", has(ModItems.ENERGY_CELL)).save(recipeOutput);
    }

    protected static void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTIme, pGroup, "_from_smelting");

    }

    protected static void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput recipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.Factory<T> factory,
                                                                       List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, StarWarsChainCode.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }
}
