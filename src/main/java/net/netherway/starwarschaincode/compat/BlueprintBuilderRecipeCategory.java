package net.netherway.starwarschaincode.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.block.ModBlocks;
import net.netherway.starwarschaincode.item.ModItems;
import net.netherway.starwarschaincode.recipe.BlueprintRecipe;
import org.jetbrains.annotations.Nullable;

public class BlueprintBuilderRecipeCategory implements IRecipeCategory<BlueprintRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "blueprint_builder");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID,
            "textures/gui/blueprint_builder/blueprint_builder_gui.png");

    public static final RecipeType<BlueprintRecipe> BLUEPRINT_BUILDER_RECIPE_TYPE =
            new RecipeType<>(UID, BlueprintRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public BlueprintBuilderRecipeCategory(IGuiHelper helper) {
        // ajusta a largura/altura conforme o tamanho real do teu PNG de GUI
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.BLUEPRINT_BUILDER));
    }

    @Override
    public RecipeType<BlueprintRecipe> getRecipeType() {
        return BLUEPRINT_BUILDER_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.starwarschaincode.blueprint_builder");
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
    public void setRecipe(IRecipeLayoutBuilder builder, BlueprintRecipe recipe, IFocusGroup focuses) {
        // grid 4x4 de inputs, mesmas coordenadas do BlueprintBuilderMenu (13 + col*18, 6 + row*18)
        for (int row = 0; row < BlueprintRecipe.HEIGHT; row++) {
            for (int col = 0; col < BlueprintRecipe.WIDTH; col++) {
                int index = col + row * BlueprintRecipe.WIDTH;
                builder.addSlot(RecipeIngredientRole.INPUT, 13 + col * 18, 6 + row * 18)
                        .addIngredients(recipe.getIngredients().get(index));
            }
        }

        // slot da pen (catalyst) — mesma posição do menu
        builder.addSlot(RecipeIngredientRole.CATALYST, 106, 33)
                .addItemStack(new ItemStack(ModItems.PEN.get()));

        // resultado — mesma posição do menu
        builder.addSlot(RecipeIngredientRole.OUTPUT, 154, 33)
                .addItemStack(recipe.getResultItem(null));
    }
}