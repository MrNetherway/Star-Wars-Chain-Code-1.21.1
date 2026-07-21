package net.netherway.starwarschaincode.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.block.ModBlocks;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, StarWarsChainCode.MOD_ID);

    public static final Supplier<CreativeModeTab> UTILITY_ITEMS_TAB = CREATIVE_MODE_TAB.register("utility_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.ENERGY_CELL.get()))
                    .title(Component.translatable("creativetab.starwarschaincode.utility_items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.ENERGY_CELL);
                        output.accept(ModItems.ALUMINUM_PLATE);
                        output.accept(ModItems.COPPER_PLATE);
                        output.accept(ModItems.DOONIUM_PLATE);
                        output.accept(ModItems.IRON_PLATE);
                        output.accept(ModItems.POLISHED_DOLOVITE_PLATE);
                        output.accept(ModItems.BLUEPRINT_READER);
                        output.accept(ModItems.DOONIUM_WING);
                        output.accept(ModItems.IRON_ROD);
                        output.accept(ModItems.PEN);
                        output.accept(ModItems.PORTABLE_SOLAR_COLLECTOR);
                        output.accept(ModItems.QUADANIUM_COATING);
                        output.accept(ModItems.READER);
                        output.accept(ModItems.SCREEN);
                        output.accept(ModItems.SOLAR_CELL);
                        output.accept(ModItems.SOLAR_COLLECTOR_PANEL);
                        output.accept(ModItems.WIRE);
                        output.accept(ModItems.TIBANNA_GAS_CAPSULE);
                    }).build());

    public static final Supplier<CreativeModeTab> LONG_RANGE_WEAPON_TAB = CREATIVE_MODE_TAB.register("long_range_weapon_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.DL_44.get()))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "utility_items_tab"))
                    .title(Component.translatable("creativetab.starwarschaincode.long_range_weapons"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.DL_44);
                    }).build());

    public static final Supplier<CreativeModeTab> SHORT_RANGE_WEAPON_TAB = CREATIVE_MODE_TAB.register("short_range_weapon_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.LIGHTSABER.get()))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "long_range_weapon_tab"))
                    .title(Component.translatable("creativetab.starwarschaincode.short_range_weapons"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.LIGHTSABER);
                    }).build());

    public static final Supplier<CreativeModeTab> FUNCTIONAL_BLOCK_TAB = CREATIVE_MODE_TAB.register("functional_block_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.LAVA_REFINER))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "short_range_weapon_tab"))
                    .title(Component.translatable("creativetab.starwarschaincode.functional_blocks"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(ModBlocks.LAVA_REFINER);
                        output.accept(ModBlocks.CHARGED_CHAMBER);
                        output.accept(ModBlocks.BLUEPRINT_BUILDER);
                        output.accept(ModBlocks.PLATFORM_CONTROLLER);
                        output.accept(ModBlocks.TIBANNA_GAS);

                    }).build());

    public static final Supplier<CreativeModeTab> NON_FUNCTIONAL_BLOCK_TAB = CREATIVE_MODE_TAB.register("non_functional_block_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.ALUMINUM_BLOCK))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "functional_block_tab"))
                    .title(Component.translatable("creativetab.starwarschaincode.non_functional_blocks"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(ModBlocks.ALUMINUM_BLOCK);

                    }).build());

    public static final Supplier<CreativeModeTab> ORE_TAB = CREATIVE_MODE_TAB.register("ore_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.DOONIUM_ORE))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "functional_block_tab"))
                    .title(Component.translatable("creativetab.starwarschaincode.ores_tab"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(ModBlocks.DOONIUM_ORE);
                        output.accept(ModBlocks.BAUXITE_DEEPSLATE_ORE);
                        output.accept(ModBlocks.BAUXITE_ORE);
                        output.accept(ModBlocks.DOLOVITE_ORE);
                        output.accept(ModBlocks.QUADANIUM_ORE);
                        output.accept(ModItems.ALUMINA);
                        output.accept(ModItems.ALUMINUM_BAR);
                        output.accept(ModItems.DOONIUM_BAR);
                        output.accept(ModItems.QUADANIUM_BAR);
                        output.accept(ModItems.RAW_BAUXITE);
                        output.accept(ModItems.RAW_DOONIUM);
                        output.accept(ModItems.RAW_DOLOVITE);
                        output.accept(ModItems.RAW_QUADANIUM);
                        output.accept(ModItems.BASE_DOONIUM);
                        output.accept(ModItems.BASE_QUADANIUM);
                        output.accept(ModItems.POLISHED_DOLOVITE);

                    }).build());

    public static final Supplier<CreativeModeTab> BLUEPRINT_TAB = CREATIVE_MODE_TAB.register("blueprint_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.HYPERDRIVE_BLUEPRINT.get()))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "ore_tab"))
                    .title(Component.translatable("creativetab.starwarschaincode.blueprints_tab"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(ModItems.LIFE_SUPPORT_BLUEPRINT);
                        output.accept(ModItems.NAVICOMPUTER_BLUEPRINT);
                        output.accept(ModItems.MAIN_REACTOR_BLUEPRINT);
                        output.accept(ModItems.HYPERDRIVE_BLUEPRINT);
                        output.accept(ModItems.Z_95_HEADHUNTER_BLUEPRINT);
                        output.accept(ModItems.REPULSORLIFT_GENERATOR_BLUEPRINT);

                    }).build());



    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
