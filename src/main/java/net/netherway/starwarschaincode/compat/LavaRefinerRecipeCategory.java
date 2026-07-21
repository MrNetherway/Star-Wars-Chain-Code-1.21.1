package net.netherway.starwarschaincode.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.block.ModBlocks;
import net.netherway.starwarschaincode.recipe.LavaRefinerRecipe;
import org.jetbrains.annotations.Nullable;

public class LavaRefinerRecipeCategory implements IRecipeCategory<LavaRefinerRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "lava_refiner");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID,
            "textures/gui/lava_refiner/lava_refiner_gui.png");

    public static final RecipeType<LavaRefinerRecipe> LAVA_REFINER_RECIPE_RECIPE_TYPE =
            new RecipeType<>(UID, LavaRefinerRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public LavaRefinerRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.LAVA_REFINER));
    }

    @Override
    public RecipeType<LavaRefinerRecipe> getRecipeType() {
        return LAVA_REFINER_RECIPE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.starwarschaincode.lava_refiner");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LavaRefinerRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 57, 26).addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 103, 26).addItemStack(recipe.getResultItem(null));

        builder.addSlot(RecipeIngredientRole.CATALYST, 80, 51).addItemStack(new ItemStack(Items.LAVA_BUCKET));
    }
}
