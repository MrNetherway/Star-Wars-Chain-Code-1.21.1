package net.netherway.starwarschaincode.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.netherway.starwarschaincode.StarWarsChainCode;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, StarWarsChainCode.MOD_ID);

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, StarWarsChainCode.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LavaRefinerRecipe>> LAVA_REFINER_SERIALIZER =
            SERIALIZERS.register("lava_refiner", LavaRefinerRecipe.Serializer::new);
    public static  final DeferredHolder<RecipeType<?>, RecipeType<LavaRefinerRecipe>> LAVA_REFINER_TYPE =
            TYPES.register("lava_refiner", () -> new RecipeType<LavaRefinerRecipe>() {
                @Override
                public String toString() {
                    return "lava_refiner";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ChargedChamberRecipe>> CHARGED_CHAMBER_SERIALIZER =
            SERIALIZERS.register("charged_chamber", ChargedChamberRecipe.Serializer::new);
    public static  final DeferredHolder<RecipeType<?>, RecipeType<ChargedChamberRecipe>> CHARGED_CHAMBER_TYPE =
            TYPES.register("charged_chamber", () -> new RecipeType<ChargedChamberRecipe>() {
                @Override
                public String toString() {
                    return "charged_chamber";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, BlueprintRecipeSerializer> BLUEPRINT_SERIALIZER =
            SERIALIZERS.register("blueprint_builder.json", BlueprintRecipeSerializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<BlueprintRecipe>> BLUEPRINT_TYPE =
            TYPES.register("blueprint_builder.json", () -> new RecipeType<BlueprintRecipe>() {
                @Override
                public String toString() {
                    return "blueprint_builder.json";
                }
            });


    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
