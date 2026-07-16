package net.netherway.starwarschaincode.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class BlueprintRecipeSerializer implements RecipeSerializer<BlueprintRecipe> {

    // "" no JSON vira Ingredient.EMPTY; qualquer outro valor eh um item id resolvido direto no registro
    private static final com.mojang.serialization.Codec<Ingredient> SLOT_CODEC = com.mojang.serialization.Codec.STRING.flatXmap(
            s -> {
                if (s.isEmpty()) {
                    return com.mojang.serialization.DataResult.success(Ingredient.EMPTY);
                }
                net.minecraft.resources.ResourceLocation rl = net.minecraft.resources.ResourceLocation.tryParse(s);
                if (rl == null || !net.minecraft.core.registries.BuiltInRegistries.ITEM.containsKey(rl)) {
                    return com.mojang.serialization.DataResult.error(() -> "Unknown item: " + s);
                }
                net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(rl);
                return com.mojang.serialization.DataResult.success(Ingredient.of(item));
            },
            ingredient -> {
                // direcao de encode (Ingredient -> String) nao eh usada para carregar receitas,
                // so serviria pra datagen; deixamos um erro generico pra nao depender de API instavel
                if (ingredient.isEmpty()) {
                    return com.mojang.serialization.DataResult.success("");
                }
                return com.mojang.serialization.DataResult.error(() -> "Encoding BlueprintRecipe ingredients back to JSON is not supported");
            }
    );

    public static final MapCodec<BlueprintRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SLOT_CODEC.listOf().fieldOf("ingredients").forGetter(r -> java.util.List.copyOf(r.getIngredients())),
            ItemStack.CODEC.fieldOf("result").forGetter(r -> r.getResultItem(null))
    ).apply(instance, (ingredientList, result) -> {
        NonNullList<Ingredient> ingredients = NonNullList.withSize(BlueprintRecipe.WIDTH * BlueprintRecipe.HEIGHT, Ingredient.EMPTY);
        for (int i = 0; i < ingredientList.size() && i < ingredients.size(); i++) {
            ingredients.set(i, ingredientList.get(i));
        }
        return new BlueprintRecipe(ingredients, result);
    }));

    public static final StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, BlueprintRecipe> STREAM_CODEC =
            StreamCodec.of(
                    (buf, recipe) -> {
                        buf.writeVarInt(recipe.getIngredients().size());
                        for (Ingredient ing : recipe.getIngredients()) {
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing);
                        }
                        ItemStack.STREAM_CODEC.encode(buf, recipe.getResultItem(null));
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);
                        for (int i = 0; i < size; i++) {
                            ingredients.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                        }
                        ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                        return new BlueprintRecipe(ingredients, result);
                    }
            );

    @Override
    public MapCodec<BlueprintRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, BlueprintRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}