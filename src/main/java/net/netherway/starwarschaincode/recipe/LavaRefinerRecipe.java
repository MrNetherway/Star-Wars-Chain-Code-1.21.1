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

public record LavaRefinerRecipe(Ingredient inputItem, ItemStack output) implements Recipe<LavaRefinerRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(LavaRefinerRecipeInput input, Level level) {
        if(level.isClientSide()) {
            return false;
        }

        return inputItem.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(LavaRefinerRecipeInput input, HolderLookup.Provider registries) {
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
        return ModRecipes.LAVA_REFINER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.LAVA_REFINER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<LavaRefinerRecipe> {

        public static final MapCodec<LavaRefinerRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(LavaRefinerRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(LavaRefinerRecipe::output)
        ).apply(inst, LavaRefinerRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, LavaRefinerRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, LavaRefinerRecipe::inputItem,
                        ItemStack.STREAM_CODEC, LavaRefinerRecipe::output,
                        LavaRefinerRecipe::new);

        @Override
        public MapCodec<LavaRefinerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, LavaRefinerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
