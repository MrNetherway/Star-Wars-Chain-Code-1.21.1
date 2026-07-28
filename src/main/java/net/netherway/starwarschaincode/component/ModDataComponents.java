package net.netherway.starwarschaincode.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.netherway.starwarschaincode.StarWarsChainCode;

import java.util.function.UnaryOperator;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, StarWarsChainCode.MOD_ID);


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ACTIVATED = register("activated",
            builder -> builder.persistent(Codec.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> BLOCKING = register("blocking",
            builder -> builder.persistent(Codec.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY_AMOUNT = register("energy_amount",
            builder -> builder.persistent(Codec.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TIBANNA_AMOUNT = register("tibanna_amount",
            builder -> builder.persistent(Codec.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WeaponAttachmentData>> SCOPE_ITEM =
            DATA_COMPONENT_TYPES.register("scope_item", () ->
                    DataComponentType.<WeaponAttachmentData>builder()
                            .persistent(WeaponAttachmentData.CODEC)
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WeaponAttachmentData>> STOCK_ITEM =
            DATA_COMPONENT_TYPES.register("stock_item", () ->
                    DataComponentType.<WeaponAttachmentData>builder()
                            .persistent(WeaponAttachmentData.CODEC)
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WeaponAttachmentData>> BARREL_ITEM =
            DATA_COMPONENT_TYPES.register("barrel_item", () ->
                    DataComponentType.<WeaponAttachmentData>builder()
                            .persistent(WeaponAttachmentData.CODEC)
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WeaponAttachmentData>> HILT_PART_1 =
            DATA_COMPONENT_TYPES.register("hilt_part_1", () ->
                    DataComponentType.<WeaponAttachmentData>builder()
                            .persistent(WeaponAttachmentData.CODEC)
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WeaponAttachmentData>> HILT_PART_2 =
            DATA_COMPONENT_TYPES.register("hilt_part_2", () ->
                    DataComponentType.<WeaponAttachmentData>builder()
                            .persistent(WeaponAttachmentData.CODEC)
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WeaponAttachmentData>> HILT_PART_3 =
            DATA_COMPONENT_TYPES.register("hilt_part_3", () ->
                    DataComponentType.<WeaponAttachmentData>builder()
                            .persistent(WeaponAttachmentData.CODEC)
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WeaponAttachmentData>> KYBER_CRYSTAL =
            DATA_COMPONENT_TYPES.register("kyber_crystal", () ->
                    DataComponentType.<WeaponAttachmentData>builder()
                            .persistent(WeaponAttachmentData.CODEC)
                            .build());

    private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name,
                                                                                          UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
