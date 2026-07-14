package net.netherway.starwarschaincode.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record ChargedChamberRecipe(Ingredient inputItem, ItemStack output) implements Recipe<ChargedChamberRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(ChargedChamberRecipeInput input, Level level) {
        if(level.isClientSide()) {
            return false;
        }

        return inputItem.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(ChargedChamberRecipeInput input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CHARGED_CHAMBER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CHARGED_CHAMBER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<ChargedChamberRecipe> {

        public static final MapCodec<ChargedChamberRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(ChargedChamberRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(ChargedChamberRecipe::output)
        ).apply(inst, ChargedChamberRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ChargedChamberRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, ChargedChamberRecipe::inputItem,
                        ItemStack.STREAM_CODEC, ChargedChamberRecipe::output,
                        ChargedChamberRecipe::new);

        @Override
        public MapCodec<ChargedChamberRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ChargedChamberRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
