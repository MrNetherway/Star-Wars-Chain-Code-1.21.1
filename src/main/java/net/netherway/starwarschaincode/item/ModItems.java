package net.netherway.starwarschaincode.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.item.custom.*;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(StarWarsChainCode.MOD_ID);

    public static final DeferredItem<Item> RAW_DOONIUM = ITEMS.registerSimpleItem("raw_doonium");
    public static final DeferredItem<Item> BASE_DOONIUM = ITEMS.registerSimpleItem("base_doonium");
    public static final DeferredItem<Item> DOONIUM_BAR = ITEMS.registerSimpleItem("doonium_bar");
    public static final DeferredItem<Item> RAW_BAUXITE = ITEMS.registerSimpleItem("raw_bauxite");
    public static final DeferredItem<Item> ALUMINA = ITEMS.registerSimpleItem("alumina");
    public static final DeferredItem<Item> ALUMINUM_BAR = ITEMS.registerSimpleItem("aluminum_bar");
    public static final DeferredItem<Item> RAW_QUADANIUM = ITEMS.registerSimpleItem("raw_quadanium");
    public static final DeferredItem<Item> BASE_QUADANIUM = ITEMS.registerSimpleItem("base_quadanium");
    public static final DeferredItem<Item> QUADANIUM_BAR = ITEMS.registerSimpleItem("quadanium_bar");
    public static final DeferredItem<Item> RAW_DOLOVITE = ITEMS.registerSimpleItem("raw_dolovite");
    public static final DeferredItem<Item> POLISHED_DOLOVITE = ITEMS.registerSimpleItem("polished_dolovite");

    public static final DeferredItem<Item> TATOOINE_MODEL = ITEMS.registerItem("tatooine_model",
            properties -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> EARTH_MODEL = ITEMS.registerItem("earth_model",
            properties -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> IRON_ROD = ITEMS.registerSimpleItem("iron_rod");
    public static final DeferredItem<Item> BLUEPRINT_READER = ITEMS.registerSimpleItem("blueprint_reader");
    public static final DeferredItem<Item> WIRE = ITEMS.registerSimpleItem("wire");
    public static final DeferredItem<Item> SCREEN = ITEMS.registerSimpleItem("screen");
    public static final DeferredItem<Item> READER = ITEMS.registerSimpleItem("reader");
    public static final DeferredItem<Item> ALUMINUM_PLATE = ITEMS.registerSimpleItem("aluminum_plate");
    public static final DeferredItem<Item> DOONIUM_WING = ITEMS.registerSimpleItem("doonium_wing");
    public static final DeferredItem<Item> QUADANIUM_COATING = ITEMS.registerSimpleItem("quadanium_coating");
    public static final DeferredItem<Item> SOLAR_COLLECTOR_PANEL = ITEMS.registerSimpleItem("solar_collector_panel");
    public static final DeferredItem<Item> COPPER_PLATE = ITEMS.registerSimpleItem("copper_plate");
    public static final DeferredItem<Item> DOONIUM_PLATE = ITEMS.registerSimpleItem("doonium_plate");
    public static final DeferredItem<Item> IRON_PLATE = ITEMS.registerSimpleItem("iron_plate");
    public static final DeferredItem<Item> POLISHED_DOLOVITE_PLATE = ITEMS.registerSimpleItem("polished_dolovite_plate");

    public static final DeferredItem<Item> Z_95_HEADHUNTER_BLUEPRINT = ITEMS.registerItem("z_95_headhunter_blueprint",
            properties -> new Item(new Item.Properties().stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));
    public static final DeferredItem<Item> HYPERDRIVE_BLUEPRINT = ITEMS.registerItem("hyperdrive_blueprint",
            properties -> new Item(new Item.Properties().stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));
    public static final DeferredItem<Item> REPULSORLIFT_GENERATOR_BLUEPRINT = ITEMS.registerItem("repulsorlift_generator_blueprint",
            properties -> new Item(new Item.Properties().stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));
    public static final DeferredItem<Item> NAVICOMPUTER_BLUEPRINT = ITEMS.registerItem("navicomputer_blueprint",
            properties -> new Item(new Item.Properties().stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));
    public static final DeferredItem<Item> LIFE_SUPPORT_BLUEPRINT = ITEMS.registerItem("life_support_blueprint",
            properties -> new Item(new Item.Properties().stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));
    public static final DeferredItem<Item> MAIN_REACTOR_BLUEPRINT = ITEMS.registerItem("main_reactor_blueprint",
            properties -> new Item(new Item.Properties().stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));

    public static final DeferredItem<Item> PORTABLE_SOLAR_COLLECTOR = ITEMS.registerItem("portable_solar_collector",
            properties -> new PortableSolarCollectorItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> TIBANNA_GAS_CAPSULE = ITEMS.registerItem("tibanna_gas_capsule",
            properties -> new TibannaGasCapsuleItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> PEN = ITEMS.registerItem("pen",
            properties -> new Item(properties), new Item.Properties().stacksTo(1).durability(250));
    public static final DeferredItem<Item> SOLAR_CELL = ITEMS.registerItem("solar_cell",
            properties -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> ENERGY_CELL = ITEMS.registerItem("energy_cell",
            properties -> new EnergyCellItem(new Item.Properties().stacksTo(1)));


    public static final DeferredItem<Item> DL_44 = ITEMS.registerItem("dl_44",
            properties -> new WeaponItem(properties, 12, 2.5f, 1), new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> LIGHTSABER = ITEMS.registerItem("lightsaber",
            properties -> new LightsaberItem(properties.attributes(LightsaberItem.createAttributes()), 12), new Item.Properties().stacksTo(1));


    public static final DeferredItem<Item> PLATFORM = ITEMS.registerItem("platform_controller",
            properties -> new PlatformItem(properties), new Item.Properties().stacksTo(1));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
