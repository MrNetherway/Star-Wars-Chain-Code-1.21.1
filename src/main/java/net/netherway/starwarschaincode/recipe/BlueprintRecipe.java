package net.netherway.starwarschaincode.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class BlueprintRecipe implements Recipe<BlueprintRecipeInput> {

    public static final int WIDTH = 4;
    public static final int HEIGHT = 4;

    private final NonNullList<Ingredient> ingredients; // tamanho 16, Ingredient.EMPTY para slots vazios
    private final ItemStack result;

    public BlueprintRecipe(NonNullList<Ingredient> ingredients, ItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
    }

    @Override
    public boolean matches(BlueprintRecipeInput input, Level level) {
        if (input.width() != WIDTH || input.height() != HEIGHT) return false;
        for (int i = 0; i < ingredients.size(); i++) {
            if (!ingredients.get(i).test(input.getItem(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(BlueprintRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= WIDTH && height >= HEIGHT;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result;
    }

    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public RecipeSerializer<? extends Recipe<BlueprintRecipeInput>> getSerializer() {
        return ModRecipes.BLUEPRINT_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<BlueprintRecipeInput>> getType() {
        return ModRecipes.BLUEPRINT_TYPE.get();
    }
}