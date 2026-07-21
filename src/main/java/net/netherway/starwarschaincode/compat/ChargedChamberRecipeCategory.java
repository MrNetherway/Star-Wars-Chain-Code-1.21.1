package net.netherway.starwarschaincode.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.block.ModBlocks;
import net.netherway.starwarschaincode.item.ModItemTags;
import net.netherway.starwarschaincode.recipe.ChargedChamberRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChargedChamberRecipeCategory implements IRecipeCategory<ChargedChamberRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "charged_chamber");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID,
            "textures/gui/charged_chamber/charged_chamber_gui.png");

    public static final RecipeType<ChargedChamberRecipe> CHARGED_CHAMBER_RECIPE_RECIPE_TYPE =
            new RecipeType<>(UID, ChargedChamberRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public ChargedChamberRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CHARGED_CHAMBER));
    }

    @Override
    public RecipeType<ChargedChamberRecipe> getRecipeType() {
        return CHARGED_CHAMBER_RECIPE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.starwarschaincode.charged_chamber");
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
    public void setRecipe(IRecipeLayoutBuilder builder, ChargedChamberRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 57, 26).addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 103, 26).addItemStack(recipe.getResultItem(null));


        List<ItemStack> energyHolderStacks = new ArrayList<>();
        for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(ModItemTags.ENERGY_HOLDER)) {
            energyHolderStacks.add(new ItemStack(holder.value()));
        }

        builder.addSlot(RecipeIngredientRole.CATALYST, 80, 51)
                .addItemStacks(energyHolderStacks);
    }
}
