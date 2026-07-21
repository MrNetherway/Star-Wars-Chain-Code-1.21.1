package net.netherway.starwarschaincode.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.block.ModBlocks;
import net.netherway.starwarschaincode.recipe.BlueprintRecipe;
import net.netherway.starwarschaincode.recipe.ChargedChamberRecipe;
import net.netherway.starwarschaincode.recipe.LavaRefinerRecipe;
import net.netherway.starwarschaincode.recipe.ModRecipes;
import net.netherway.starwarschaincode.screen.custom.BlueprintBuilderScreen;
import net.netherway.starwarschaincode.screen.custom.ChargedChamberScreen;
import net.netherway.starwarschaincode.screen.custom.LavaRefinerScreen;

import java.util.List;

@JeiPlugin
public class JEIStarWarsChainCodePlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new LavaRefinerRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()));

        registration.addRecipeCategories(new ChargedChamberRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()));

        registration.addRecipeCategories(new BlueprintBuilderRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<LavaRefinerRecipe> lavaRefinerRecipes = recipeManager
                .getAllRecipesFor(ModRecipes.LAVA_REFINER_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(LavaRefinerRecipeCategory.LAVA_REFINER_RECIPE_RECIPE_TYPE, lavaRefinerRecipes);

        List<ChargedChamberRecipe> chargedChamberRecipes = recipeManager
                .getAllRecipesFor(ModRecipes.CHARGED_CHAMBER_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(ChargedChamberRecipeCategory.CHARGED_CHAMBER_RECIPE_RECIPE_TYPE, chargedChamberRecipes);

        List<BlueprintRecipe> blueprintRecipes = recipeManager
                .getAllRecipesFor(ModRecipes.BLUEPRINT_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(BlueprintBuilderRecipeCategory.BLUEPRINT_BUILDER_RECIPE_TYPE, blueprintRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(LavaRefinerScreen.class, 76, 26, 22, 20,
                LavaRefinerRecipeCategory.LAVA_REFINER_RECIPE_RECIPE_TYPE);

        registration.addRecipeClickArea(ChargedChamberScreen.class, 76, 26, 22, 20,
                ChargedChamberRecipeCategory.CHARGED_CHAMBER_RECIPE_RECIPE_TYPE);

        registration.addRecipeClickArea(BlueprintBuilderScreen.class, 128, 26, 22, 20,
                BlueprintBuilderRecipeCategory.BLUEPRINT_BUILDER_RECIPE_TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.LAVA_REFINER.asItem()),
                LavaRefinerRecipeCategory.LAVA_REFINER_RECIPE_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CHARGED_CHAMBER.asItem()),
                ChargedChamberRecipeCategory.CHARGED_CHAMBER_RECIPE_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.BLUEPRINT_BUILDER.asItem()),
                BlueprintBuilderRecipeCategory.BLUEPRINT_BUILDER_RECIPE_TYPE);
    }
}
